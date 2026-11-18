package com.serviceplus.InstanceConfigRegistry.Service;

import com.serviceplus.InstanceConfigRegistry.Entity.HolidayDocument;
import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError;
import com.serviceplus.InstanceConfigRegistry.Kafka.Service.KafkaOutboxService;
import com.serviceplus.InstanceConfigRegistry.Repository.HolidayRepository;
import com.serviceplus.InstanceConfigRegistry.dto.HolidayRequest;
import com.serviceplus.InstanceConfigRegistry.dto.HolidayResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class HolidayMasterService {

    private static final String GLOBAL_TENANT_ID = "0";
    private static final String HOLIDAY_SYNC_TOPIC = "holiday_sync_topic";

    private final HolidayRepository holidayRepository;
    private final KafkaOutboxService kafkaOutboxService;

    public HolidayMasterService(
            HolidayRepository holidayRepository,
            KafkaOutboxService kafkaOutboxService) {

        this.holidayRepository = holidayRepository;
        this.kafkaOutboxService = kafkaOutboxService;
    }

    /*
     * Generate global Sundays.
     */
    public HolidayDocument generateAndInsertSundays(int year) {

        if (year < 1 || year > 9999) {
            throw new SPRuntimeError("Invalid year",HttpStatus.BAD_REQUEST);
        }

        HolidayDocument document =loadOrCreate(GLOBAL_TENANT_ID, year);

        boolean changed = false;

        LocalDate date = LocalDate.of(year, 1, 1);

        while (date.getYear() == year) {

            if (date.getDayOfWeek() == DayOfWeek.SUNDAY&& findEntry(document, date).isEmpty()) {

                HolidayDocument.HolidayEntry holiday =new HolidayDocument.HolidayEntry();
                holiday.setDate(date);
                holiday.setWeekday(date.getDayOfWeek().name());
                holiday.setDescription("Sunday");

                document.getHolidays().add(holiday);
                changed = true;
            }

            date = date.plusDays(1);
        }
        if (changed) {
            saveAndPublish(document);
        }
        return document;
    }

    /*
     * Display tenant holidays.
     */
    public List<HolidayResponse> getTenantHolidays(String tenantId,Integer year) {

        if (year != null && (year < 1 || year > 9999)) {
            throw new SPRuntimeError("Invalid year",HttpStatus.BAD_REQUEST);
        }

        List<HolidayDocument> documents = year == null ? holidayRepository.findByTenantId(tenantId)
                        : getHolidays(tenantId, year)
                                .map(List::of)
                                .orElseGet(List::of);

        return documents.stream()
                .filter(document -> document.getHolidays() != null)
                .flatMap(document -> document.getHolidays().stream())
                .filter(entry -> entry.getDate() != null)
                .sorted(Comparator.comparing(HolidayDocument.HolidayEntry::getDate))
                .map(entry ->new HolidayResponse(entry.getDate(),entry.getDescription())).toList();
    }

    /*
     * SAVE + UPDATE
     *
     * New date      -> insert
     * Existing date -> update description
     */
    public String addTenantHolidays(String tenantId,List<HolidayRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new SPRuntimeError("Holiday list is required",HttpStatus.BAD_REQUEST);
        }

        Map<Integer, HolidayDocument> documentsByYear = new HashMap<>();
        Set<Integer> changedYears = new HashSet<>();

        boolean saved = false;
        boolean updated = false;

        for (HolidayRequest request : requests) {

            HolidayDocument.HolidayEntry newEntry =
                    validatedEntry(request);

            int year = newEntry.getDate().getYear();

            HolidayDocument document =documentsByYear.computeIfAbsent(year,y -> loadOrCreate(tenantId, y));

            HolidayDocument.HolidayEntry existingEntry =
                    findEntry(document,newEntry.getDate()).orElse(null);

            if (existingEntry != null) {

                if (!Objects.equals(
                        existingEntry.getDescription(),
                        newEntry.getDescription())) {

                    existingEntry.setDescription(newEntry.getDescription());

                    existingEntry.setWeekday(newEntry.getWeekday());

                    changedYears.add(year);
                    updated = true;
                }

            } else {

                document.getHolidays().add(newEntry);

                changedYears.add(year);
                saved = true;
            }
        }

        for (Integer year : changedYears) {
            saveAndPublish(documentsByYear.get(year));
        }

        if (saved && updated) {
            return "Holiday saved and updated successfully";
        }

        if (saved) {
            return "Holiday saved successfully";
        }

        if (updated) {
            return "Holiday updated successfully";
        }

        return "No changes found";
    }

    /*
     * DELETE
     */
    public void deleteTenantHoliday(String tenantId,String date) {

        LocalDate holidayDate =parseHolidayDate(date);
        HolidayDocument document =requireDocument(tenantId,holidayDate);

        HolidayDocument.HolidayEntry entry =findEntry(document, holidayDate).orElseThrow(this::holidayNotFound);
        document.getHolidays().remove(entry);

        saveAndPublish(document);
    }

    /*
     * Used by Processing service.
     */
    public Optional<HolidayDocument> getHolidays(String tenantId,int year) {

        return holidayRepository.findById(buildDocumentId(tenantId, year));
    }

    private HolidayDocument.HolidayEntry validatedEntry(HolidayRequest request) {

        if (request == null) {
        	throw new SPRuntimeError("Holiday is required",HttpStatus.BAD_REQUEST);
        }

        LocalDate date =parseHolidayDate(request.getDate());

        if (request.getDescription() == null || request.getDescription().isBlank()) {

            throw new SPRuntimeError("Holiday description is required",HttpStatus.BAD_REQUEST);
        }
        HolidayDocument.HolidayEntry entry =new HolidayDocument.HolidayEntry();

        entry.setDate(date);
        entry.setWeekday(date.getDayOfWeek().name());
        entry.setDescription(request.getDescription().trim());

        return entry;
    }

    private LocalDate parseHolidayDate(String value) {

        if (value == null|| !value.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {

            throw new SPRuntimeError("Date must use yyyy-MM-dd",HttpStatus.BAD_REQUEST);
        }

        try {
            return LocalDate.parse(value,DateTimeFormatter.ISO_LOCAL_DATE);

        } catch (DateTimeParseException ex) {
            throw new SPRuntimeError("Invalid holiday date",HttpStatus.BAD_REQUEST);
        }
    }

    private HolidayDocument loadOrCreate(String tenantId,int year) {

        HolidayDocument document =getHolidays(tenantId, year).orElseGet(() ->
                                createHolidayDocument(
                                        tenantId,
                                        year
                                ));

        if (document.getHolidays() == null) {
            document.setHolidays(new ArrayList<>());
        }

        return document;
    }

    private HolidayDocument requireDocument(String tenantId,LocalDate date) {

        return getHolidays(tenantId,date.getYear()).orElseThrow(this::holidayNotFound);
    }

    private Optional<HolidayDocument.HolidayEntry> findEntry(HolidayDocument document,LocalDate date) {

        if (document.getHolidays() == null) {
            return Optional.empty();
        }

        return document.getHolidays()
                .stream()
                .filter(entry ->
                        date.equals(entry.getDate()))
                .findFirst();
    }

    private SPRuntimeError holidayNotFound() {
        return new SPRuntimeError(
                "Holiday not found",
                HttpStatus.NOT_FOUND
        );
    }

    private HolidayDocument createHolidayDocument(String tenantId,int year) {

        HolidayDocument document =new HolidayDocument();

        document.setId(buildDocumentId(tenantId, year));
        document.setTenantId(tenantId);
        document.setYear(year);
        document.setHolidays(
                new ArrayList<>());

        document.setUpdatedAt(Instant.now());

        return document;
    }

    private String buildDocumentId(String tenantId,int year) {
        return tenantId + ":" + year;
    }

    private void saveAndPublish(HolidayDocument document) {

        document.getHolidays().sort(Comparator.comparing(HolidayDocument.HolidayEntry::getDate));

        document.setUpdatedAt(Instant.now());

        holidayRepository.save(document);

        publishHolidayChangedEvent(document.getTenantId(),document.getYear());
    }

    private void publishHolidayChangedEvent(String tenantId,int year) {

        Map<String, Object> event =
                Map.of("eventType","HOLIDAY_CONFIG_CHANGED","tenantId",tenantId,"year",year);

        kafkaOutboxService.saveEvent(HOLIDAY_SYNC_TOPIC,event);
    }
}