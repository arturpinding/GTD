package com.example.gtd;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Main extends Activity {

    ArrayList<MenuItem> menuItems = new ArrayList<>();
    int[] menuItemsIcons= {R.drawable.ic_calendar, R.drawable.ic_inbox, R.drawable.ic_today, R.drawable.ic_projects, R.drawable.ic_next_actions, R.drawable.ic_waiting_for, R.drawable.ic_someday_maybe, R.drawable.ic_settings, R.drawable.ic_weekly_review, R.drawable.ic_settings, R.drawable.ic_settings, R.drawable.ic_settings, R.drawable.ic_settings};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        setUpMenuItems();
        MenuRW_Adapter adapter = new MenuRW_Adapter(this, menuItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*
        btn_inbox = findViewById(R.id.btn_inbox);

        btn_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Inbox.class);
                startActivity(intent);
            }


        });
        */

    }


    private void setUpMenuItems() {
        String[] menuItemsNames = getResources().getStringArray(R.array.menu_items_txt);

        for (int i = 0; i < menuItemsNames.length; i++) {
            menuItems.add(new MenuItem(menuItemsNames[i], menuItemsIcons[i]));
        }
    }




}
