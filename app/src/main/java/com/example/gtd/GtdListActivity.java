package com.example.gtd;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class GtdListActivity extends Activity implements GtdItemAdapter.Listener {
    private GtdRepository repository;
    private ArrayList<GtdItem> items = new ArrayList<>();
    private GtdItemAdapter adapter;
    private TextView emptyView;
    private EditText input;

    protected abstract String listKey();
    protected abstract int screenTitle();
    protected abstract int screenDescription();
    protected abstract int inputHint();
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

        RecyclerView recyclerView = findViewById(R.id.gtdListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GtdItemAdapter(items, itemsAreCheckable(), canAddToToday(), this);
        recyclerView.setAdapter(adapter);

        Button addButton = findViewById(R.id.gtdListAddButton);
        addButton.setOnClickListener(view -> addItem());
        input.setOnEditorActionListener((view, actionId, event) -> {
            addItem();
            return true;
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
    public void onItemDeleteRequested(GtdItem item) {
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
