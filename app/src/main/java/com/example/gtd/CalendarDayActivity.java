package com.example.gtd;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CalendarDayActivity extends Activity {
    public static final String EXTRA_DAY = "com.example.gtd.EXTRA_DAY";
    public static final String EXTRA_MONTH = "com.example.gtd.EXTRA_MONTH";
    public static final String EXTRA_YEAR = "com.example.gtd.EXTRA_YEAR";
    private static final String PREFS_NAME = "CalendarPrefs";
    private static final String ENTRIES_KEY = "entries";
    private SharedPreferences sharedPreferences;
    ArrayList<CalendarEntry> calendarEntries = new ArrayList<>();
    private static final String[] MONTH_NAMES = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_day_activity);

        // Get the date from the intent
        int day = getIntent().getIntExtra(EXTRA_DAY, -1);
        int month = getIntent().getIntExtra(EXTRA_MONTH, -1);
        int year = getIntent().getIntExtra(EXTRA_YEAR, -1);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadEntries();
        Date date = new Date(day, month, year);

        RecyclerView recyclerView = findViewById(R.id.calendarDayRecyclerView);
        CalendarEntryAdapter adapter = new CalendarEntryAdapter(this, calendarEntries);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /* This is unnecessary because we are loading entries from SharedPreferences in loadEntries() method.
    private void setUpEntries() {

    }
    */
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
                Date todate = new Date(3, 7, 2026); //todo
                if (date.equals(todate)) {
                    calendarEntries.add(new CalendarEntry(text, time, date));
                }
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
