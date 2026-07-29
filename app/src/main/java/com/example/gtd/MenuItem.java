package com.example.gtd;

public class MenuItem {
    String name;
    int iconResId;

    public MenuItem(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return iconResId;
    }
}
