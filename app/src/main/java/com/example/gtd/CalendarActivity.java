package com.example.gtd;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarActivity extends Activity {

    private Date today = new Date(3, 7, 2026); //todo
    private ArrayList<CalendarDay> calendarDays = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

        RecyclerView recyclerView = findViewById(R.id.calendarRecyclerView);
        setUpCalendarDays();
        CalendarDayAdapter adapter = new CalendarDayAdapter(this, calendarDays);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
    }

    private void setUpCalendarDays() {
        Date date = today.monday();
        for (int i = 0; i < 400; i++) {
            //laeme ära 400 päeva.
            boolean inDisplayedMonth = date.getMonth() == today.getMonth();
            boolean isToday = date.equals(today);
            ArrayList<CalendarEntry> entries = new ArrayList<>();
            //todo lisada päris andmed
            entries.add(new CalendarEntry("Entry 1", "10:00"));
            entries.add(new CalendarEntry("Entry 2", "14:00"));
            calendarDays.add(new CalendarDay(date, inDisplayedMonth, isToday, entries));
            date = date.nextDay();
        }
    }
}
