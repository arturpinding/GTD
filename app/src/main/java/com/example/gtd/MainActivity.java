package com.example.gtd;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    ArrayList<MenuItem> menuItems = new ArrayList<>();
    int[] menuItemsIcons= {R.drawable.ic_calendar, R.drawable.ic_inbox, R.drawable.ic_today, R.drawable.ic_projects, R.drawable.ic_next_actions, R.drawable.ic_waiting_for, R.drawable.ic_someday_maybe, R.drawable.ic_settings, R.drawable.ic_weekly_review, R.drawable.ic_settings};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        setUpMenuItems();
        MenuRW_Adapter adapter = new MenuRW_Adapter(this, menuItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    private void setUpMenuItems() {
        String[] menuItemsNames = getResources().getStringArray(R.array.menu_items_txt);
        
        Intent[] menuItemsIntents = {
                new Intent(MainActivity.this, CalendarActivity.class),
                new Intent(MainActivity.this, InboxActivity.class),
                new Intent(MainActivity.this, TodayActivity.class),
                new Intent(MainActivity.this, ProjectsActivity.class),
                new Intent(MainActivity.this, NextActionsActivity.class),
                new Intent(MainActivity.this, WaitingForActivity.class),
                new Intent(MainActivity.this, SomedayMaybeActivity.class),
                new Intent(MainActivity.this, ReferenceActivity.class),
                new Intent(MainActivity.this, WeeklyReviewActivity.class),
                new Intent(MainActivity.this, SettingsActivity.class)
        };

        for (int i = 0; i < menuItemsNames.length; i++) {
            menuItems.add(new MenuItem(menuItemsNames[i], menuItemsIcons[i], menuItemsIntents[i]));
        }
    }
}
