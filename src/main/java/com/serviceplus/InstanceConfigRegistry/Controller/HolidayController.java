package com.serviceplus.InstanceConfigRegistry.Controller;

import com.serviceplus.InstanceConfigRegistry.Entity.HolidayDocument;
import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.ErrorDetails;
import com.serviceplus.InstanceConfigRegistry.ExceptionHandler.SPRuntimeError;
import com.serviceplus.InstanceConfigRegistry.Service.HolidayMasterService;
import com.serviceplus.InstanceConfigRegistry.dto.HolidayRequest;
import com.serviceplus.InstanceConfigRegistry.dto.HolidayResponse;
import com.serviceplus.InstanceConfigRegistry.dto.UserSessionObject;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;

import static com.serviceplus.InstanceConfigRegistry.util.Utility.getUserSessionDetails;

@RestController
@RequestMapping("/a/api/v1")
public class HolidayController {

    private final HolidayMasterService holidayMasterService;

    public HolidayController(HolidayMasterService holidayMasterService) {
        this.holidayMasterService = holidayMasterService;
    }

    /*
     * Used by Processing service on Redis cache miss.
     *
     * GET /a/api/v1/holidays/0/2026
     * GET /a/api/v1/holidays/TENANT001/2026
     */
    @GetMapping("/holidays")
    public ResponseEntity<HolidayDocument> getHolidays(
            @RequestParam("tenantId") String tenantId,
            @RequestParam("year") Integer year) {

        if (tenantId == null || tenantId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (year == null || year <= 0) {
            return ResponseEntity.badRequest().build();
        }

        return holidayMasterService
                .getHolidays(tenantId.trim(), year)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
     * Generate Sundays as global holidays.
     * Global tenantId = "0".
     *
     * POST /a/api/v1/holidays/global/sundays/2026
     */
    @PostMapping("/holidays/global/sundays")
    public ResponseEntity<HolidayDocument> generateSundays( @RequestBody(required = false) Map<String, Integer> request) {

        Integer year = request == null ? null : request.get("year");

        if (year == null || year <= 0) {
            return ResponseEntity.badRequest().build();
        }

        HolidayDocument savedDocument =holidayMasterService.generateAndInsertSundays(year);

        return ResponseEntity.ok(savedDocument);
    }

    /*
     * DISPLAY TENANT HOLIDAYS
     *
     * GET /a/api/v1/admin/holidays
     * GET /a/api/v1/admin/holidays?year=2026
     */
    @GetMapping("/admin/holidays")
    public ResponseEntity<List<HolidayResponse>> listTenantHolidays(@RequestParam(required = false) Integer year,HttpServletRequest httpRequest) {
        String tenantId = resolveTenantId(httpRequest);
        List<HolidayResponse> holidays =holidayMasterService.getTenantHolidays(tenantId,year);
        return ResponseEntity.ok(holidays);
    }

    /*
     * SAVE + UPDATE HOLIDAYS
     *
     * New date      -> insert
     * Existing date -> update description
     *
     * POST /a/api/v1/admin/holidays
     */
    
    @PostMapping("/admin/holidays")
    public ResponseEntity<Map<String, String>> saveOrUpdateTenantHolidays(
            @RequestBody(required = false) List<HolidayRequest> requests,
            HttpServletRequest httpRequest) {

        if (requests == null || requests.isEmpty()) {
            throw new SPRuntimeError("Holiday list is required",HttpStatus.BAD_REQUEST);
            }

        String tenantId = resolveTenantId(httpRequest);

        String message =holidayMasterService.addTenantHolidays(tenantId,requests);

        return ResponseEntity.ok(Map.of("message", message));
    }
    /*
     * DELETE HOLIDAY USING POST
     *
     * POST /a/api/v1/admin/holidays/delete
     *
     * Body:
     * {
     *     "date": "2026-01-26"
     * }
     */
    @PostMapping("/admin/holidays/delete")
    public ResponseEntity<Map<String, String>> deleteTenantHoliday(
            @RequestBody(required = false) HolidayRequest request,
            HttpServletRequest httpRequest) {

        if (request == null|| request.getDate() == null|| request.getDate().isBlank()) {

            throw new SPRuntimeError("Holiday date is required",HttpStatus.BAD_REQUEST);
        }

        String tenantId = resolveTenantId(httpRequest);

        holidayMasterService.deleteTenantHoliday(tenantId,request.getDate());

        return ResponseEntity.ok(Map.of("message","Holiday deleted successfully")
        );
    }

    /*
     * Tenant ID always comes from USER-DETAILS.
     * Frontend must not send tenantId in request body.
     */
    private String resolveTenantId(HttpServletRequest request) {

        UserSessionObject user;

        try {
            user = getUserSessionDetails(request);

        } catch (RuntimeException ex) {

            throw new SPRuntimeError("Invalid user session",HttpStatus.UNAUTHORIZED);
        }

        if (user == null|| user.getTenantId() == null|| user.getTenantId().isBlank()) {

            throw new SPRuntimeError("User session tenant is required",HttpStatus.UNAUTHORIZED);
        }
        String tenantId = user.getTenantId().trim();

        if ("0".equals(tenantId)) {throw new SPRuntimeError("A tenant session is required",HttpStatus.FORBIDDEN);
        }
        return tenantId;
    }

    /*
     * Invalid JSON / query parameter handling.
     */
    @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorDetails> invalidRequest(Exception ex) {

        return ResponseEntity.badRequest().body(
                new ErrorDetails("Invalid holiday request; use date and description only, ISO dates and a numeric year",HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value())
        );
    }
}