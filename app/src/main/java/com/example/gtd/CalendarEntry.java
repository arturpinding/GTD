package com.example.gtd;

public class CalendarEntry {
    private String text;
    private String time;
    private final Date date;

    public CalendarEntry(String text, String time, Date date) {
        this.text = text;
        this.time = time;
        this.date = date;
    }


    public String getText() {
        return text;
    }


    public String getTime() {
        return time;
    }

    public Date getDate() {
        return date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMinutesSinceMidnight() {
        if (time == null || !time.matches("\\d{2}:\\d{2}")) return Integer.MAX_VALUE;
        try {
            int hour = Integer.parseInt(time.substring(0, 2));
            int minute = Integer.parseInt(time.substring(3, 5));
            if (hour > 23 || minute > 59) return Integer.MAX_VALUE;
            return hour * 60 + minute;
        } catch (NumberFormatException ignored) {
            return Integer.MAX_VALUE;
        }
    }
}
