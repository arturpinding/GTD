package com.example.gtd;

import java.util.ArrayList;

public class CalendarDay {
    private Date date;
    private boolean inDisplayedMonth;
    private boolean today;
    ArrayList<CalendarEntry> entries;


    public CalendarDay(Date date, boolean inDisplayedMonth, boolean today, ArrayList<CalendarEntry> entries) {
        this.date = date;
        this.inDisplayedMonth = inDisplayedMonth;
        this.today = today;
        this.entries = entries;
    }

    public Date getDate() {
        return date;
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
