package com.example.gtd;

public class GtdItem {
    private final String id;
    private String title;
    private String list;
    private final long createdAt;
    private String projectId;
    private boolean completed;
    private boolean today;

    public GtdItem(String id, String title, String list, boolean completed, boolean today, long createdAt) {
        this(id, title, list, completed, today, createdAt, null);
    }

    public GtdItem(String id, String title, String list, boolean completed, boolean today,
                   long createdAt, String projectId) {
        this.id = id;
        this.title = title;
        this.list = list;
        this.completed = completed;
        this.today = today;
        this.createdAt = createdAt;
        this.projectId = projectId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getList() { return list; }
    public boolean isCompleted() { return completed; }
    public boolean isToday() { return today; }
    public long getCreatedAt() { return createdAt; }
    public String getProjectId() { return projectId; }
    public void setTitle(String title) { this.title = title; }
    public void setList(String list) { this.list = list; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setToday(boolean today) { this.today = today; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
}
