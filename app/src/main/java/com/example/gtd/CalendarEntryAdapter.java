package com.example.gtd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarEntryAdapter extends RecyclerView.Adapter<CalendarEntryAdapter.MyViewHolder> {
    /*

CalendarEntryAdapter.java
Adapter for the selected day’s full entry list.
It should:
◦
Inflate calendar_entry_row.xml.
◦
Display each entry’s text.
◦
Update when another day is selected.
     */

    private final Context context;
    private final ArrayList<CalendarEntry> calendarEntries;

    public CalendarEntryAdapter(Context context, ArrayList<CalendarEntry> calendarEntries) {
        this.context = context;
        this.calendarEntries = calendarEntries;
    }

    @NonNull
    @Override
    public CalendarEntryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.calendar_entry, parent, false);
        return new CalendarEntryAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CalendarEntryAdapter.MyViewHolder holder, int position) {
        //setime asjad mis peaks olema calendar_entry-s.
        holder.entryText.setText(calendarEntries.get(position).getText());
        holder.entryTime.setText(calendarEntries.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return calendarEntries.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //defineerime view elemendid mis on calendar_entry.xml-s
        TextView entryText;
        TextView entryTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //leiame view elemendid itemView-st
            entryText = itemView.findViewById(R.id.entryText);
            entryTime = itemView.findViewById(R.id.entryTime);
        }

    }


}

