package com.example.gtd;

public class CalendarEntry {
    private int id;
    private String text;
    private String date;
    private String time;

    public CalendarEntry(int id, String text, String date, String time) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

}
