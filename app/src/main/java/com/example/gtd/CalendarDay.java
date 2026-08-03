package com.example.gtd;

import java.util.ArrayList;

public class CalendarDay {
    private int dayNumber;
    private String month;
    private int year;
    private boolean inDisplayedMonth;
    private boolean today;
    ArrayList<CalendarEntry> entries;


    public CalendarDay(int dayNumber, String month, int year, boolean inDisplayedMonth, boolean today, ArrayList<CalendarEntry> entries) {
        this.dayNumber = dayNumber;
        this.month = month;
        this.year = year;
        this.inDisplayedMonth = inDisplayedMonth;
        this.today = today;
        this.entries = entries;
    }

    public int getDayNumber()  {
        return dayNumber;
    }

    public String getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public boolean isInDisplayedMonth() {
        return inDisplayedMonth;
    }

    public boolean isToday() {
        return today;
    }

    public ArrayList<CalendarEntry> getEntries() {
        return entries;
    }
}
