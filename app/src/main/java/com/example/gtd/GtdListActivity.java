package com.example.gtd;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class GtdListActivity extends BaseActivity implements GtdItemAdapter.Listener {
    private GtdRepository repository;
    private ArrayList<GtdItem> items = new ArrayList<>();
    private GtdItemAdapter adapter;
    private TextView emptyView;
    private EditText input;

    protected abstract String listKey();
    protected abstract int screenTitle();
    protected abstract int screenDescription();
    protected abstract int inputHint();
    protected int emptyMessage() { return R.string.nothing_here_yet; }
    protected boolean itemsAreCheckable() { return true; }
    protected boolean canAddToToday() { return false; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gtd_list_activity);
        setTitle(screenTitle());
        repository = new GtdRepository(this);

        ((TextView) findViewById(R.id.gtdListTitle)).setText(screenTitle());
        ((TextView) findViewById(R.id.gtdListDescription)).setText(screenDescription());
        input = findViewById(R.id.gtdListInput);
        input.setHint(inputHint());
        emptyView = findViewById(R.id.gtdListEmpty);
        emptyView.setText(emptyMessage());

        RecyclerView recyclerView = findViewById(R.id.gtdListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GtdItemAdapter(items, itemsAreCheckable(), canAddToToday(), this);
        recyclerView.setAdapter(adapter);

        Button addButton = findViewById(R.id.gtdListAddButton);
        addButton.setOnClickListener(view -> addItem());
        input.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addItem();
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

    private void addItem() {
        String title = input.getText().toString().trim();
        if (title.isEmpty()) {
            input.setError(getString(R.string.enter_something));
            return;
        }
        repository.add(title, listKey(), false);
        input.setText("");
        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(input.getWindowToken(), 0);
        reload();
    }

    private void reload() {
        items.clear();
        items.addAll(repository.getItems(listKey()));
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
        ArrayList<String> actions = new ArrayList<>();
        actions.add(getString(R.string.edit));
        actions.add(getString(R.string.move_to));
        if (GtdRepository.PROJECTS.equals(item.getList())) {
            actions.add(getString(R.string.create_next_action));
        } else if (GtdRepository.NEXT_ACTIONS.equals(item.getList())) {
            actions.add(getString(R.string.assign_to_project));
        }
        actions.add(getString(R.string.delete));
        new AlertDialog.Builder(this)
                .setTitle(item.getTitle())
                .setItems(actions.toArray(new String[0]), (dialog, which) -> {
                    if (which == 0) {
                        showEditDialog(item);
                    } else if (which == 1) {
                        showMoveDialog(item);
                    } else if (which == actions.size() - 1) {
                        confirmDelete(item);
                    } else if (GtdRepository.PROJECTS.equals(item.getList())) {
                        showCreateNextActionDialog(item);
                    } else {
                        showProjectPicker(item);
                    }
                })
                .show();
    }

    @Override
    public String getItemSubtitle(GtdItem item) {
        if (GtdRepository.PROJECTS.equals(item.getList())) {
            if (item.isCompleted()) return getString(R.string.completed);
            GtdItem nextAction = repository.firstIncompleteActionForProject(item.getId());
            return nextAction == null
                    ? getString(R.string.needs_next_action)
                    : getString(R.string.next_action_summary, nextAction.getTitle());
        }
        if (GtdRepository.NEXT_ACTIONS.equals(item.getList()) && item.getProjectId() != null) {
            GtdItem project = repository.getById(item.getProjectId());
            if (project != null) return getString(R.string.project_summary, project.getTitle());
        }
        return "";
    }

    private void showEditDialog(GtdItem item) {
        EditText editText = new EditText(this);
        editText.setText(item.getTitle());
        editText.setSelectAllOnFocus(true);
        editText.setSingleLine(false);
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

    private void showMoveDialog(GtdItem item) {
        String[] keys = {
                GtdRepository.INBOX, GtdRepository.PROJECTS, GtdRepository.NEXT_ACTIONS,
                GtdRepository.WAITING_FOR, GtdRepository.SOMEDAY_MAYBE, GtdRepository.REFERENCE
        };
        String[] labels = {
                getString(R.string.inbox), getString(R.string.projects), getString(R.string.next_actions),
                getString(R.string.waiting_for), getString(R.string.someday_maybe), getString(R.string.reference)
        };
        ArrayList<String> visibleLabels = new ArrayList<>();
        ArrayList<String> visibleKeys = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            if (!keys[i].equals(item.getList())) {
                visibleKeys.add(keys[i]);
                visibleLabels.add(labels[i]);
            }
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.move_to)
                .setItems(visibleLabels.toArray(new String[0]), (dialog, which) -> {
                    String destination = visibleKeys.get(which);
                    item.setList(destination);
                    item.setCompleted(false);
                    if (!GtdRepository.NEXT_ACTIONS.equals(destination)) {
                        item.setToday(false);
                        item.setProjectId(null);
                    }
                    repository.update(item);
                    reload();
                })
                .show();
    }

    private void showCreateNextActionDialog(GtdItem project) {
        EditText editText = new EditText(this);
        editText.setHint(R.string.next_action_hint);
        int padding = getResources().getDimensionPixelSize(R.dimen.dialog_input_padding);
        editText.setPadding(padding, padding / 2, padding, padding / 2);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.next_action_for, project.getTitle()))
                .setView(editText)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.add, null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    String title = editText.getText().toString().trim();
                    if (title.isEmpty()) {
                        editText.setError(getString(R.string.enter_something));
                        return;
                    }
                    repository.add(title, GtdRepository.NEXT_ACTIONS, false, project.getId());
                    dialog.dismiss();
                    reload();
                }));
        dialog.show();
    }

    private void showProjectPicker(GtdItem action) {
        ArrayList<GtdItem> projects = repository.getItems(GtdRepository.PROJECTS);
        ArrayList<GtdItem> activeProjects = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        labels.add(getString(R.string.no_project));
        for (GtdItem project : projects) {
            if (!project.isCompleted()) {
                activeProjects.add(project);
                labels.add(project.getTitle());
            }
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.assign_to_project)
                .setItems(labels.toArray(new String[0]), (dialog, which) -> {
                    action.setProjectId(which == 0 ? null : activeProjects.get(which - 1).getId());
                    repository.update(action);
                    reload();
                })
                .show();
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
