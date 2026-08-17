package com.example.gtd;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CalendarStorage {
    private static final String PREFS_NAME = "CalendarPrefs";
    private static final String ENTRIES_KEY = "entries";
    private static final String CORRUPTED_ENTRIES_BACKUP_KEY = "entries_corrupted_backup";
    private static final String[] MONTH_NAMES = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private final SharedPreferences preferences;

    public CalendarStorage(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public ArrayList<CalendarEntry> loadEntries() {
        ArrayList<CalendarEntry> entries = new ArrayList<>();
        String stored = preferences.getString(ENTRIES_KEY, "[]");
        try {
            JSONArray array = new JSONArray(stored == null ? "[]" : stored);
            for (int i = 0; i < array.length(); i++) {
                try {
                JSONObject object = array.getJSONObject(i);
                JSONObject dateObject = object.getJSONObject("date");
                Date date = new Date(dateObject.getInt("day"),
                        dateObject.has("monthIndex")
                                ? dateObject.getInt("monthIndex")
                                : monthIndex(dateObject.getString("month")),
                        dateObject.getInt("year"));
                entries.add(new CalendarEntry(object.getString("text"),
                        object.optString("time", ""), date));
                } catch (JSONException ignored) {
                    // Keep other valid entries if a single stored record is damaged.
                }
            }
        } catch (JSONException ignored) {
            preferences.edit().putString(CORRUPTED_ENTRIES_BACKUP_KEY, stored).apply();
        }
        return entries;
    }

    public ArrayList<CalendarEntry> entriesFor(Date date) {
        ArrayList<CalendarEntry> result = new ArrayList<>();
        for (CalendarEntry entry : loadEntries()) {
            if (date.equals(entry.getDate())) result.add(entry);
        }
        Collections.sort(result, Comparator.comparingInt(CalendarEntry::getMinutesSinceMidnight));
        return result;
    }

    public void add(CalendarEntry entry) {
        ArrayList<CalendarEntry> entries = loadEntries();
        entries.add(entry);
        saveEntries(entries);
    }

    public void saveEntries(ArrayList<CalendarEntry> entries) {
        JSONArray array = new JSONArray();
        try {
            for (CalendarEntry entry : entries) {
                JSONObject object = new JSONObject();
                object.put("text", entry.getText());
                object.put("time", entry.getTime());
                JSONObject dateObject = new JSONObject();
                dateObject.put("day", entry.getDate().getNum());
                dateObject.put("monthIndex", entry.getDate().getMonthIndex());
                dateObject.put("year", entry.getDate().getYear());
                object.put("date", dateObject);
                array.put(object);
            }
        } catch (JSONException e) {
            throw new IllegalStateException("Unable to save calendar entries", e);
        }
        preferences.edit().putString(ENTRIES_KEY, array.toString()).apply();
    }

    private int monthIndex(String monthName) throws JSONException {
        for (int i = 0; i < MONTH_NAMES.length; i++) {
            if (MONTH_NAMES[i].equals(monthName)) return i;
        }
        throw new JSONException("Invalid month: " + monthName);
    }
}
