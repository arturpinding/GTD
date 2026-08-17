package com.example.gtd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarEntryAdapter extends RecyclerView.Adapter<CalendarEntryAdapter.MyViewHolder> {
    public interface Listener {
        void onEntryActionRequested(CalendarEntry entry);
    }
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
    private final Listener listener;
    private final boolean showActionButton;

    public CalendarEntryAdapter(Context context, ArrayList<CalendarEntry> calendarEntries) {
        this(context, calendarEntries, null, false);
    }

    public CalendarEntryAdapter(Context context, ArrayList<CalendarEntry> calendarEntries, Listener listener) {
        this(context, calendarEntries, listener, true);
    }

    public CalendarEntryAdapter(Context context, ArrayList<CalendarEntry> calendarEntries,
                                Listener listener, boolean showActionButton) {
        this.context = context;
        this.calendarEntries = calendarEntries;
        this.listener = listener;
        this.showActionButton = showActionButton;
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
        CalendarEntry entry = calendarEntries.get(position);
        holder.entryText.setText(entry.getText());
        holder.entryTime.setText(formatTime(entry));
        holder.entryTime.setVisibility(entry.getTime() == null || entry.getTime().isEmpty()
                ? View.GONE : View.VISIBLE);
        holder.moreButton.setVisibility(listener == null || !showActionButton
                ? View.GONE : View.VISIBLE);
        holder.moreButton.setContentDescription(context.getString(
                R.string.event_actions_for, entry.getText()));
        holder.moreButton.setOnClickListener(view -> {
            if (listener != null) listener.onEntryActionRequested(entry);
        });
        holder.itemView.setContentDescription(listener != null && !showActionButton
                ? context.getString(R.string.open_event, entry.getText()) : null);
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onEntryActionRequested(entry);
        });
        holder.itemView.setOnLongClickListener(view -> {
            if (listener != null) listener.onEntryActionRequested(entry);
            return listener != null;
        });
    }

    @Override
    public int getItemCount() {
        return calendarEntries.size();
    }

    private String formatTime(CalendarEntry entry) {
        int minutes = entry.getMinutesSinceMidnight();
        if (minutes == Integer.MAX_VALUE) return entry.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, minutes / 60);
        calendar.set(Calendar.MINUTE, minutes % 60);
        return android.text.format.DateFormat.getTimeFormat(context).format(calendar.getTime());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //defineerime view elemendid mis on calendar_entry.xml-s
        TextView entryText;
        TextView entryTime;
        ImageButton moreButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //leiame view elemendid itemView-st
            entryText = itemView.findViewById(R.id.entryText);
            entryTime = itemView.findViewById(R.id.entryTime);
            moreButton = itemView.findViewById(R.id.calendarEntryMoreButton);
        }

    }


}
