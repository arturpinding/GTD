package com.example.gtd;

public class CalendarEntry {
    private String text;
    private String time;

    public CalendarEntry(String text, String time) {
        this.text = text;
        this.time = time;
    }


    public String getText() {
        return text;
    }


    public String getTime() {
        return time;
    }

}
