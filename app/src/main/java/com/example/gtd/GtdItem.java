package com.example.gtd;

public class GtdItem {
    private final String id;
    private final String title;
    private final String list;
    private final long createdAt;
    private boolean completed;
    private boolean today;

    public GtdItem(String id, String title, String list, boolean completed, boolean today, long createdAt) {
        this.id = id;
        this.title = title;
        this.list = list;
        this.completed = completed;
        this.today = today;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getList() { return list; }
    public boolean isCompleted() { return completed; }
    public boolean isToday() { return today; }
    public long getCreatedAt() { return createdAt; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setToday(boolean today) { this.today = today; }
}
