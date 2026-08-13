package com.example.gtd;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarActivity extends Activity {
    private final ArrayList<CalendarDay> calendarDays = new ArrayList<>();
    private final ArrayList<CalendarEntry> calendarEntries = new ArrayList<>();
    private CalendarDayAdapter adapter;
    private CalendarStorage storage;
    private Date today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);
        setTitle(R.string.calendar);

        Calendar systemDate = Calendar.getInstance();
        today = new Date(systemDate.get(Calendar.DAY_OF_MONTH),
                systemDate.get(Calendar.MONTH), systemDate.get(Calendar.YEAR));
        storage = new CalendarStorage(this);

        RecyclerView recyclerView = findViewById(R.id.calendarRecyclerView);
        adapter = new CalendarDayAdapter(this, calendarDays);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendarEntries.clear();
        calendarEntries.addAll(storage.loadEntries());
        setUpCalendarDays();
        adapter.notifyDataSetChanged();
    }

    private void setUpCalendarDays() {
        calendarDays.clear();
        Date date = today.monday();
        for (int i = 0; i < 400; i++) {
            boolean inDisplayedMonth = date.getMonthIndex() == today.getMonthIndex()
                    && date.getYear() == today.getYear();
            ArrayList<CalendarEntry> entries = new ArrayList<>();
            for (CalendarEntry entry : calendarEntries) {
                if (entry.getDate().equals(date)) entries.add(entry);
            }
            calendarDays.add(new CalendarDay(date, inDisplayedMonth, date.equals(today), entries));
            date = date.nextDay();
        }
    }
}
