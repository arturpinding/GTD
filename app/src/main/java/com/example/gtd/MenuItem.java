package com.example.gtd;

import android.content.Intent;


public class MenuItem {
    private final String name;
    private final int iconResId;
    private final Intent intent;


    public MenuItem(String name, int iconResId, Intent intent) {
        this.name = name;
        this.iconResId = iconResId;
        this.intent = intent;
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
}
