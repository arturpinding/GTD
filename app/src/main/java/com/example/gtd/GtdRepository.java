package com.example.gtd;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class GtdRepository {
    public static final String PROJECTS = "projects";
    public static final String NEXT_ACTIONS = "next_actions";
    public static final String WAITING_FOR = "waiting_for";
    public static final String SOMEDAY_MAYBE = "someday_maybe";
    public static final String REFERENCE = "reference";

    private static final String PREFS_NAME = "GtdPrefs";
    private static final String ITEMS_KEY = "items";
    private final SharedPreferences preferences;

    public GtdRepository(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public ArrayList<GtdItem> getItems(String list) {
        ArrayList<GtdItem> result = new ArrayList<>();
        for (GtdItem item : loadAll()) {
            if (item.getList().equals(list)) result.add(item);
        }
        return result;
    }

    public ArrayList<GtdItem> getTodayItems() {
        ArrayList<GtdItem> result = new ArrayList<>();
        for (GtdItem item : loadAll()) {
            if (item.isToday() && !item.isCompleted()) result.add(item);
        }
        return result;
    }

    public void add(String title, String list, boolean today) {
        ArrayList<GtdItem> items = loadAll();
        items.add(new GtdItem(UUID.randomUUID().toString(), title.trim(), list,
                false, today, System.currentTimeMillis()));
        saveAll(items);
    }

    public void update(GtdItem changedItem) {
        ArrayList<GtdItem> items = loadAll();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(changedItem.getId())) {
                items.set(i, changedItem);
                break;
            }
        }
        saveAll(items);
    }

    public void delete(GtdItem item) {
        ArrayList<GtdItem> items = loadAll();
        Iterator<GtdItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(item.getId())) {
                iterator.remove();
                break;
            }
        }
        saveAll(items);
    }

    public int count(String list, boolean includeCompleted) {
        int count = 0;
        for (GtdItem item : loadAll()) {
            if (item.getList().equals(list) && (includeCompleted || !item.isCompleted())) count++;
        }
        return count;
    }

    public int clearCompleted() {
        ArrayList<GtdItem> items = loadAll();
        int before = items.size();
        items.removeIf(GtdItem::isCompleted);
        saveAll(items);
        return before - items.size();
    }

    public void clearAll() {
        preferences.edit().remove(ITEMS_KEY).apply();
    }

    private ArrayList<GtdItem> loadAll() {
        ArrayList<GtdItem> items = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(preferences.getString(ITEMS_KEY, "[]"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                items.add(new GtdItem(
                        object.getString("id"),
                        object.getString("title"),
                        object.getString("list"),
                        object.optBoolean("completed", false),
                        object.optBoolean("today", false),
                        object.optLong("createdAt", 0L)));
            }
        } catch (JSONException ignored) {
            // A damaged preference should not crash the whole app.
        }
        return items;
    }

    private void saveAll(ArrayList<GtdItem> items) {
        JSONArray array = new JSONArray();
        for (GtdItem item : items) {
            JSONObject object = new JSONObject();
            try {
                object.put("id", item.getId());
                object.put("title", item.getTitle());
                object.put("list", item.getList());
                object.put("completed", item.isCompleted());
                object.put("today", item.isToday());
                object.put("createdAt", item.getCreatedAt());
                array.put(object);
            } catch (JSONException e) {
                throw new IllegalStateException("Unable to save GTD items", e);
            }
        }
        preferences.edit().putString(ITEMS_KEY, array.toString()).apply();
    }
}
