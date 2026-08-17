package com.example.gtd;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashSet;
import java.util.UUID;

public class GtdRepository {
    public static final String INBOX = "inbox";
    public static final String PROJECTS = "projects";
    public static final String NEXT_ACTIONS = "next_actions";
    public static final String WAITING_FOR = "waiting_for";
    public static final String SOMEDAY_MAYBE = "someday_maybe";
    public static final String REFERENCE = "reference";

    private static final String PREFS_NAME = "GtdPrefs";
    private static final String ITEMS_KEY = "items";
    private static final String INBOX_MIGRATED_KEY = "inbox_migrated_v1";
    private static final String CORRUPTED_ITEMS_BACKUP_KEY = "items_corrupted_backup";
    private final SharedPreferences preferences;

    public GtdRepository(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        migrateLegacyInbox(context);
    }

    public ArrayList<GtdItem> getItems(String list) {
        ArrayList<GtdItem> result = new ArrayList<>();
        for (GtdItem item : loadAll()) {
            if (item.getList().equals(list)) result.add(item);
        }
        sortForDisplay(result);
        return result;
    }

    public ArrayList<GtdItem> getTodayItems() {
        ArrayList<GtdItem> result = new ArrayList<>();
        for (GtdItem item : loadAll()) {
            if (item.isToday() && !item.isCompleted()) result.add(item);
        }
        Collections.sort(result, Comparator.comparingLong(GtdItem::getCreatedAt));
        return result;
    }

    public void add(String title, String list, boolean today) {
        add(title, list, today, null);
    }

    public void add(String title, String list, boolean today, String projectId) {
        ArrayList<GtdItem> items = loadAll();
        items.add(new GtdItem(UUID.randomUUID().toString(), title.trim(), list,
                false, today, System.currentTimeMillis(), projectId));
        saveAll(items);
    }

    public void update(GtdItem changedItem) {
        ArrayList<GtdItem> items = loadAll();
        boolean projectWasMoved = false;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(changedItem.getId())) {
                projectWasMoved = PROJECTS.equals(items.get(i).getList())
                        && !PROJECTS.equals(changedItem.getList());
                items.set(i, changedItem);
                break;
            }
        }
        if (projectWasMoved) {
            for (GtdItem candidate : items) {
                if (changedItem.getId().equals(candidate.getProjectId())) candidate.setProjectId(null);
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
        if (PROJECTS.equals(item.getList())) {
            for (GtdItem candidate : items) {
                if (item.getId().equals(candidate.getProjectId())) candidate.setProjectId(null);
            }
        }
        saveAll(items);
    }

    public GtdItem getById(String id) {
        if (id == null) return null;
        for (GtdItem item : loadAll()) {
            if (id.equals(item.getId())) return item;
        }
        return null;
    }

    public GtdItem firstIncompleteActionForProject(String projectId) {
        for (GtdItem item : getItems(NEXT_ACTIONS)) {
            if (!item.isCompleted() && projectId.equals(item.getProjectId())) return item;
        }
        return null;
    }

    public int countProjectsWithoutNextAction() {
        int count = 0;
        for (GtdItem project : getItems(PROJECTS)) {
            if (!project.isCompleted() && firstIncompleteActionForProject(project.getId()) == null) count++;
        }
        return count;
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
        HashSet<String> removedProjectIds = new HashSet<>();
        for (GtdItem item : items) {
            if (item.isCompleted() && PROJECTS.equals(item.getList())) {
                removedProjectIds.add(item.getId());
            }
        }
        items.removeIf(GtdItem::isCompleted);
        if (!removedProjectIds.isEmpty()) {
            for (GtdItem item : items) {
                if (removedProjectIds.contains(item.getProjectId())) item.setProjectId(null);
            }
        }
        saveAll(items);
        return before - items.size();
    }

    public int countCompleted() {
        int count = 0;
        for (GtdItem item : loadAll()) {
            if (item.isCompleted()) count++;
        }
        return count;
    }

    public void clearAll() {
        ArrayList<GtdItem> items = loadAll();
        items.removeIf(item -> !INBOX.equals(item.getList()));
        saveAll(items);
    }

    private void sortForDisplay(ArrayList<GtdItem> items) {
        Collections.sort(items, (left, right) -> {
            if (left.isCompleted() != right.isCompleted()) return left.isCompleted() ? 1 : -1;
            return Long.compare(left.getCreatedAt(), right.getCreatedAt());
        });
    }

    private ArrayList<GtdItem> loadAll() {
        ArrayList<GtdItem> items = new ArrayList<>();
        String stored = preferences.getString(ITEMS_KEY, "[]");
        try {
            JSONArray array = new JSONArray(stored == null ? "[]" : stored);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    items.add(new GtdItem(
                            object.getString("id"),
                            object.getString("title"),
                            object.getString("list"),
                            object.optBoolean("completed", false),
                            object.optBoolean("today", false),
                            object.optLong("createdAt", 0L),
                            emptyToNull(object.optString("projectId", ""))));
                } catch (JSONException ignored) {
                    // Preserve every valid record even if one item is damaged.
                }
            }
        } catch (JSONException ignored) {
            preferences.edit().putString(CORRUPTED_ITEMS_BACKUP_KEY, stored).apply();
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
                if (item.getProjectId() != null) object.put("projectId", item.getProjectId());
                array.put(object);
            } catch (JSONException e) {
                throw new IllegalStateException("Unable to save GTD items", e);
            }
        }
        preferences.edit().putString(ITEMS_KEY, array.toString()).apply();
    }

    private String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private void migrateLegacyInbox(Context context) {
        if (preferences.getBoolean(INBOX_MIGRATED_KEY, false)) return;
        SharedPreferences legacy = context.getSharedPreferences("InboxPrefs", Context.MODE_PRIVATE);
        String stored = legacy.getString("items", "");
        if (stored != null && !stored.isEmpty()) {
            ArrayList<GtdItem> items = loadAll();
            long createdAt = System.currentTimeMillis();
            for (String title : stored.split("\\|\\|")) {
                String trimmed = title.trim();
                if (!trimmed.isEmpty()) {
                    items.add(new GtdItem(UUID.randomUUID().toString(), trimmed, INBOX,
                            false, false, createdAt++));
                }
            }
            saveAll(items);
        }
        preferences.edit().putBoolean(INBOX_MIGRATED_KEY, true).apply();
        legacy.edit().remove("items").apply();
    }
}
