package com.example.gtd;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CalendarActivity extends Activity {

    private Date today = new Date(3, 7, 2026); //todo
    private ArrayList<CalendarDay> calendarDays = new ArrayList<>();
    private ArrayList<CalendarEntry> calendarEntries = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "CalendarPrefs";
    private static final String ENTRIES_KEY = "entries";
    private static final String[] MONTH_NAMES = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadEntries();

        RecyclerView recyclerView = findViewById(R.id.calendarRecyclerView);
        setUpCalendarDays();
        CalendarDayAdapter adapter = new CalendarDayAdapter(this, calendarDays);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
    }

    private void setUpCalendarDays() {
        Date date = today.monday();
        for (int i = 0; i < 400; i++) {
            //laeme ära 400 päeva.
            boolean inDisplayedMonth = date.getMonth() == today.getMonth();
            boolean isToday = date.equals(today);
            ArrayList<CalendarEntry> entries = new ArrayList<>();

            for (CalendarEntry entry : calendarEntries) {
                if (entry.getDate().equals(date)) {
                    entries.add(entry);
                }
            }
            calendarDays.add(new CalendarDay(date, inDisplayedMonth, isToday, entries));
            date = date.nextDay();
        }
    }

    private void loadEntries() {
        calendarEntries.clear();
        String json = sharedPreferences.getString(ENTRIES_KEY, "[]");

        try {
            JSONArray entriesJson = new JSONArray(json);
            for (int i = 0; i < entriesJson.length(); i++) {
                JSONObject entryJson = entriesJson.getJSONObject(i);
                String text = entryJson.getString("text");
                String time = entryJson.getString("time");
                JSONObject dateJson = entryJson.getJSONObject("date");
                int day = dateJson.getInt("day");
                String monthName = dateJson.getString("month");
                int year = dateJson.getInt("year");
                Date date = new Date(day, getMonthIndex(monthName), year);
                calendarEntries.add(new CalendarEntry(text, time, date));
            }
        } catch (JSONException e) {
            // Ignore malformed saved data and start with an empty list.
            calendarEntries.clear();
        }
    }

    private void saveEntries() {
        JSONArray entriesJson = new JSONArray();

        try {
            for (CalendarEntry entry : calendarEntries) {
                JSONObject entryJson = new JSONObject();
                entryJson.put("text", entry.getText());
                entryJson.put("time", entry.getTime());
                Date date = entry.getDate();
                JSONObject dateJson = new JSONObject();
                dateJson.put("day", date.getNum());
                dateJson.put("month", date.getMonth());
                dateJson.put("year", date.getYear());
                entryJson.put("date", dateJson);
                entriesJson.put(entryJson);
            }

            sharedPreferences.edit()
                    .putString(ENTRIES_KEY, entriesJson.toString())
                    .apply();
        } catch (JSONException e) {
            throw new IllegalStateException("Unable to save calendar entries", e);
        }
    }

    private int getMonthIndex(String monthName) throws JSONException {
        for (int i = 0; i < MONTH_NAMES.length; i++) {
            if (MONTH_NAMES[i].equals(monthName)) {
                return i;
            }
        }
        throw new JSONException("Invalid month: " + monthName);
    }
}
