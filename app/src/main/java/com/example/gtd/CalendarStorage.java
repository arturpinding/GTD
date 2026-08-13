package com.example.gtd;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CalendarStorage {
    private static final String PREFS_NAME = "CalendarPrefs";
    private static final String ENTRIES_KEY = "entries";
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
        try {
            JSONArray array = new JSONArray(preferences.getString(ENTRIES_KEY, "[]"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                JSONObject dateObject = object.getJSONObject("date");
                Date date = new Date(dateObject.getInt("day"),
                        monthIndex(dateObject.getString("month")), dateObject.getInt("year"));
                entries.add(new CalendarEntry(object.getString("text"),
                        object.optString("time", ""), date));
            }
        } catch (JSONException ignored) {
            entries.clear();
        }
        return entries;
    }

    public ArrayList<CalendarEntry> entriesFor(Date date) {
        ArrayList<CalendarEntry> result = new ArrayList<>();
        for (CalendarEntry entry : loadEntries()) {
            if (date.equals(entry.getDate())) result.add(entry);
        }
        return result;
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
                dateObject.put("month", entry.getDate().getMonth());
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
