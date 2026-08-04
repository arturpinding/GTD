package com.example.gtd;

import android.content.SharedPreferences;

public class CalendarEntry {
    private String text;
    private String time;
    private Date date;
    private SharedPreferences sharedPreferences;

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

}
