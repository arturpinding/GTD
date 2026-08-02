package com.example.gtd;

public class CalendarDay {
    private final int day;
    private final String month;
    private final int year;
    private final String weekday;

    public CalendarDay(int day, String month, int year, String weekday) {
        // mis kuupäeval on mis nädalapäev arvutab välja adapter / calendarActivity
        this.day = day;
        this.month = month;
        this.year = year;
        this.weekday = weekday;
    }

    public int getDay() {
        return day;
    }
    public String getMonth() {
        return month;
    }
    public int getYear() {
        return year;
    }
    public String getWeekday() {
        return weekday;
    }
}
