package com.example.gtd;

import android.content.Intent;
import android.widget.Button;

public class MenuItem {
    String name;
    int iconResId;
    Intent intent;


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
