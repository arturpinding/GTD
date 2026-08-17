package com.example.gtd;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TodayActivity extends BaseActivity implements GtdItemAdapter.Listener {
    private final ArrayList<CalendarEntry> schedule = new ArrayList<>();
    private final ArrayList<GtdItem> focusItems = new ArrayList<>();
    private GtdRepository repository;
    private CalendarStorage calendarStorage;
    private GtdItemAdapter focusAdapter;
    private CalendarEntryAdapter scheduleAdapter;
    private TextView scheduleEmpty;
    private TextView focusEmpty;
    private EditText quickActionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_activity);
        setTitle(R.string.today);
        repository = new GtdRepository(this);
        calendarStorage = new CalendarStorage(this);

        ((TextView) findViewById(R.id.todayDate)).setText(
                DateFormat.getDateInstance(DateFormat.FULL).format(Calendar.getInstance().getTime()));
        scheduleEmpty = findViewById(R.id.todayScheduleEmpty);
        focusEmpty = findViewById(R.id.todayFocusEmpty);
        quickActionInput = findViewById(R.id.todayQuickActionInput);

        RecyclerView scheduleView = findViewById(R.id.todayScheduleRecyclerView);
        scheduleAdapter = new CalendarEntryAdapter(
                this, schedule, entry -> openTodayCalendar(), false);
        scheduleView.setAdapter(scheduleAdapter);
        scheduleView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView focusView = findViewById(R.id.todayFocusRecyclerView);
        focusAdapter = new GtdItemAdapter(focusItems, true, true, this);
        focusView.setAdapter(focusAdapter);
        focusView.setLayoutManager(new LinearLayoutManager(this));

        Button addButton = findViewById(R.id.todayQuickAddButton);
        addButton.setOnClickListener(view -> addQuickAction());
        quickActionInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addQuickAction();
                return true;
            }
            return false;
        });
        findViewById(R.id.todayAddEventButton).setOnClickListener(view -> openTodayCalendar());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.todayDate)).setText(
                DateFormat.getDateInstance(DateFormat.FULL).format(Calendar.getInstance().getTime()));
        reload();
    }

    private void addQuickAction() {
        String title = quickActionInput.getText().toString().trim();
        if (title.isEmpty()) {
            quickActionInput.setError(getString(R.string.enter_something));
            return;
        }
        repository.add(title, GtdRepository.NEXT_ACTIONS, true);
        quickActionInput.setText("");
        reload();
    }

    private void reload() {
        Calendar now = Calendar.getInstance();
        Date today = new Date(now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.MONTH), now.get(Calendar.YEAR));
        schedule.clear();
        schedule.addAll(calendarStorage.entriesFor(today));
        focusItems.clear();
        focusItems.addAll(repository.getTodayItems());
        scheduleAdapter.notifyDataSetChanged();
        focusAdapter.notifyDataSetChanged();
        scheduleEmpty.setVisibility(schedule.isEmpty() ? View.VISIBLE : View.GONE);
        focusEmpty.setVisibility(focusItems.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemChanged(GtdItem item) {
        repository.update(item);
        reload();
    }

    @Override
    public void onItemActionRequested(GtdItem item) {
        String[] actions = {
                getString(R.string.edit),
                getString(R.string.remove_from_today),
                getString(R.string.delete)
        };
        new AlertDialog.Builder(this)
                .setTitle(item.getTitle())
                .setItems(actions, (dialog, which) -> {
                    if (which == 0) {
                        editItem(item);
                    } else if (which == 1) {
                        item.setToday(false);
                        repository.update(item);
                        reload();
                    } else {
                        confirmDelete(item);
                    }
                })
                .show();
    }

    @Override
    public String getItemSubtitle(GtdItem item) {
        GtdItem project = repository.getById(item.getProjectId());
        return project == null ? "" : getString(R.string.project_summary, project.getTitle());
    }

    private void openTodayCalendar() {
        Calendar now = Calendar.getInstance();
        Intent intent = new Intent(this, CalendarDayActivity.class);
        intent.putExtra(CalendarDayActivity.EXTRA_DAY, now.get(Calendar.DAY_OF_MONTH));
        intent.putExtra(CalendarDayActivity.EXTRA_MONTH, now.get(Calendar.MONTH));
        intent.putExtra(CalendarDayActivity.EXTRA_YEAR, now.get(Calendar.YEAR));
        startActivity(intent);
    }

    private void editItem(GtdItem item) {
        EditText editText = new EditText(this);
        editText.setText(item.getTitle());
        editText.setSelectAllOnFocus(true);
        int padding = getResources().getDimensionPixelSize(R.dimen.dialog_input_padding);
        editText.setPadding(padding, padding / 2, padding, padding / 2);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.edit_item)
                .setView(editText)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.save, null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    String title = editText.getText().toString().trim();
                    if (title.isEmpty()) {
                        editText.setError(getString(R.string.enter_something));
                        return;
                    }
                    item.setTitle(title);
                    repository.update(item);
                    dialog.dismiss();
                    reload();
                }));
        dialog.show();
    }

    private void confirmDelete(GtdItem item) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_item_question, item.getTitle()))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    repository.delete(item);
                    reload();
                })
                .show();
    }
}
