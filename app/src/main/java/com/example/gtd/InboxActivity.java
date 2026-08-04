package com.example.gtd;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class InboxActivity extends Activity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView listView;
    private Button button;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "InboxPrefs";
    private static final String ITEMS_KEY = "items";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_activity);

        listView = findViewById(R.id.listView);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(view);
            }
        });

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        items = new ArrayList<>();
        loadItems();

        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(itemsAdapter);
        setUpListviewListener(); //sa saaks teha pika clicki et asja kustutada.


    }
    private void loadItems() {
        String json = sharedPreferences.getString(ITEMS_KEY, "");
        if (!json.isEmpty()) {
            String[] itemArray = json.split("\\|\\|");
            for (String item : itemArray) {
                if (!item.isEmpty()) {
                    items.add(item);
                }
            }
        }
    }

    private void saveItems() {
        String json = String.join("||", items);
        sharedPreferences.edit().putString(ITEMS_KEY, json).apply();
    }

    //kustutab elemendi nimekirjast kui sellel teha long click.jjj
    private void setUpListviewListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = getApplicationContext();
                Toast.makeText(context, "Item deleted", Toast.LENGTH_LONG).show();

                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                saveItems();

                return true;
            }
        });


    }

    private void addItem (View view) {
        EditText input = findViewById(R.id.editText);
        String inputText = input.getText().toString();

        if (!inputText.isEmpty()) {
            itemsAdapter.add(inputText);
            /* Oleks samuti saanud teha
            items.add(inputText);
            itemsAdapter.notifyDataSetChanged();
             */
            input.setText("");
            saveItems();
        } else {
            Context context = getApplicationContext();
            Toast.makeText(context, "Input is empty!", Toast.LENGTH_LONG).show();
        }

    }



}
