package com.serviceplus.InstanceConfigRegistry.dto;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class HolidayResponse {

    private final String date;
    private final String day;
    private final int year;
    private final String description;

    public HolidayResponse(LocalDate date, String description) {
        this.date = date.toString();
        this.day = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        this.year = date.getYear();
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }
}
