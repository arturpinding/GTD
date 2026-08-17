package com.example.gtd;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends BaseActivity {
    private final ArrayList<MenuItem> menuItems = new ArrayList<>();
    private GtdRepository repository;
    private MenuRW_Adapter adapter;
    private EditText captureInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        repository = new GtdRepository(this);
        captureInput = findViewById(R.id.homeCaptureInput);

        setUpMenuItems();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new MenuRW_Adapter(this, menuItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.homeCaptureButton).setOnClickListener(view -> quickCapture());
        captureInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                quickCapture();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMenuSummaries();
    }

    private void quickCapture() {
        String title = captureInput.getText().toString().trim();
        if (title.isEmpty()) {
            captureInput.setError(getString(R.string.enter_something));
            return;
        }
        repository.add(title, GtdRepository.INBOX, false);
        captureInput.setText("");
        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(captureInput.getWindowToken(), 0);
        Toast.makeText(this, R.string.captured_to_inbox, Toast.LENGTH_SHORT).show();
        updateMenuSummaries();
    }

    private void setUpMenuItems() {
        menuItems.add(new MenuItem(getString(R.string.today), R.drawable.ic_today,
                new Intent(this, TodayActivity.class)));
        menuItems.add(new MenuItem(getString(R.string.inbox), R.drawable.ic_inbox,
                new Intent(this, InboxActivity.class)));
        menuItems.add(new MenuItem(getString(R.string.next_actions), R.drawable.ic_next_actions,
                new Intent(this, NextActionsActivity.class)));
        menuItems.add(new MenuItem(getString(R.string.projects), R.drawable.ic_projects,
                new Intent(this, ProjectsActivity.class)));
        menuItems.add(new MenuItem(getString(R.string.waiting_for), R.drawable.ic_waiting_for,
                new Intent(this, WaitingForActivity.class)));
        menuItems.add(new MenuItem(getString(R.string.calendar), R.drawable.ic_calendar,
                new Intent(this, CalendarActivity.class)));
        menuItems.add(new MenuItem(getString(R.string.someday_maybe), R.drawable.ic_someday_maybe,
                new Intent(this, SomedayMaybeActivity.class)));
        menuItems.add(new MenuItem(getString(R.string.reference), R.drawable.ic_reference,
                new Intent(this, ReferenceActivity.class)));
        menuItems.add(new MenuItem(getString(R.string.weekly_review), R.drawable.ic_weekly_review,
                new Intent(this, WeeklyReviewActivity.class)));
        menuItems.add(new MenuItem(getString(R.string.settings), R.drawable.ic_settings,
                new Intent(this, SettingsActivity.class)));
    }

    private void updateMenuSummaries() {
        if (adapter == null) return;
        Calendar now = Calendar.getInstance();
        Date today = new Date(now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.MONTH),
                now.get(Calendar.YEAR));
        int todayEvents = new CalendarStorage(this).entriesFor(today).size();
        int inbox = repository.count(GtdRepository.INBOX, false);
        int next = repository.count(GtdRepository.NEXT_ACTIONS, false);
        int projects = repository.count(GtdRepository.PROJECTS, false);
        int stalled = repository.countProjectsWithoutNextAction();
        int waiting = repository.count(GtdRepository.WAITING_FOR, false);
        int someday = repository.count(GtdRepository.SOMEDAY_MAYBE, false);
        int reference = repository.count(GtdRepository.REFERENCE, false);

        int focus = repository.getTodayItems().size();
        menuItems.get(0).setSummary(getString(R.string.summary_pair,
                getResources().getQuantityString(R.plurals.focus_action_count, focus, focus),
                getResources().getQuantityString(R.plurals.event_count_short,
                        todayEvents, todayEvents)));
        menuItems.get(1).setSummary(getResources().getQuantityString(
                R.plurals.inbox_capture_count, inbox, inbox));
        menuItems.get(2).setSummary(getResources().getQuantityString(
                R.plurals.active_item_count, next, next));
        menuItems.get(3).setSummary(getString(R.string.summary_pair,
                getResources().getQuantityString(R.plurals.active_project_count,
                        projects, projects),
                getResources().getQuantityString(R.plurals.projects_needing_action_count,
                        stalled, stalled)));
        menuItems.get(4).setSummary(getResources().getQuantityString(
                R.plurals.active_item_count, waiting, waiting));
        menuItems.get(5).setSummary(getResources().getQuantityString(
                R.plurals.today_event_count, todayEvents, todayEvents));
        menuItems.get(6).setSummary(getResources().getQuantityString(
                R.plurals.saved_item_count, someday, someday));
        menuItems.get(7).setSummary(getResources().getQuantityString(
                R.plurals.saved_item_count, reference, reference));
        menuItems.get(8).setSummary(getString(R.string.summary_pair,
                getResources().getQuantityString(R.plurals.inbox_item_count, inbox, inbox),
                getResources().getQuantityString(R.plurals.stalled_project_count,
                        stalled, stalled)));
        menuItems.get(9).setSummary("");
        adapter.notifyDataSetChanged();
    }
}
