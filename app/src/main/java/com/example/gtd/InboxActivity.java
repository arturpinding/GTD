package com.example.gtd;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class InboxActivity extends BaseActivity implements GtdItemAdapter.Listener {
    private final ArrayList<GtdItem> items = new ArrayList<>();
    private GtdRepository repository;
    private GtdItemAdapter adapter;
    private EditText input;
    private TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_activity);
        setTitle(R.string.inbox);
        repository = new GtdRepository(this);
        input = findViewById(R.id.editText);
        emptyView = findViewById(R.id.inboxEmpty);

        RecyclerView recyclerView = findViewById(R.id.inboxRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GtdItemAdapter(items, false, false, this);
        recyclerView.setAdapter(adapter);

        Button captureButton = findViewById(R.id.button);
        captureButton.setOnClickListener(view -> capture());
        input.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                capture();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    private void capture() {
        String title = input.getText().toString().trim();
        if (title.isEmpty()) {
            input.setError(getString(R.string.enter_something));
            return;
        }
        repository.add(title, GtdRepository.INBOX, false);
        input.setText("");
        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(input.getWindowToken(), 0);
        reload();
    }

    private void reload() {
        items.clear();
        items.addAll(repository.getItems(GtdRepository.INBOX));
        adapter.notifyDataSetChanged();
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemChanged(GtdItem item) {
        repository.update(item);
        reload();
    }

    @Override
    public void onItemActionRequested(GtdItem item) {
        String[] actions = {
                getString(R.string.make_next_action),
                getString(R.string.make_today_action),
                getString(R.string.make_project),
                getString(R.string.make_waiting_for),
                getString(R.string.add_to_calendar),
                getString(R.string.make_someday_maybe),
                getString(R.string.make_reference),
                getString(R.string.edit_capture),
                getString(R.string.delete)
        };
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.clarify_capture, item.getTitle()))
                .setItems(actions, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            organize(item, GtdRepository.NEXT_ACTIONS, false, R.string.next_actions);
                            break;
                        case 1:
                            organize(item, GtdRepository.NEXT_ACTIONS, true, R.string.today);
                            break;
                        case 2:
                            organize(item, GtdRepository.PROJECTS, false, R.string.projects);
                            break;
                        case 3:
                            organize(item, GtdRepository.WAITING_FOR, false, R.string.waiting_for);
                            break;
                        case 4:
                            chooseCalendarDate(item);
                            break;
                        case 5:
                            organize(item, GtdRepository.SOMEDAY_MAYBE, false, R.string.someday_maybe);
                            break;
                        case 6:
                            organize(item, GtdRepository.REFERENCE, false, R.string.reference);
                            break;
                        case 7:
                            editCapture(item);
                            break;
                        default:
                            confirmDelete(item);
                    }
                })
                .show();
    }

    @Override
    public String getItemSubtitle(GtdItem item) {
        return getString(R.string.tap_to_clarify);
    }

    private void organize(GtdItem item, String destination, boolean today, int destinationLabel) {
        item.setList(destination);
        item.setToday(today);
        item.setCompleted(false);
        repository.update(item);
        Toast.makeText(this, getString(R.string.moved_to, getString(destinationLabel)),
                Toast.LENGTH_SHORT).show();
        reload();
    }

    private void chooseCalendarDate(GtdItem item) {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, (picker, year, month, day) -> {
            CalendarStorage storage = new CalendarStorage(this);
            storage.add(new CalendarEntry(item.getTitle(), "", new Date(day, month, year)));
            repository.delete(item);
            Toast.makeText(this, R.string.added_to_calendar, Toast.LENGTH_SHORT).show();
            reload();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void editCapture(GtdItem item) {
        EditText editText = new EditText(this);
        editText.setText(item.getTitle());
        editText.setSelectAllOnFocus(true);
        int padding = getResources().getDimensionPixelSize(R.dimen.dialog_input_padding);
        editText.setPadding(padding, padding / 2, padding, padding / 2);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.edit_capture)
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
