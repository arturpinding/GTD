package com.example.gtd;

import android.content.Intent;


public class MenuItem {
    private final String name;
    private final int iconResId;
    private final Intent intent;
    private String summary;


    public MenuItem(String name, int iconResId, Intent intent) {
        this(name, iconResId, intent, "");
    }

    public MenuItem(String name, int iconResId, Intent intent, String summary) {
        this.name = name;
        this.iconResId = iconResId;
        this.intent = intent;
        this.summary = summary;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return iconResId;
    }

    public Intent getIntent() {
        return intent;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
