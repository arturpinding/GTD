package com.example.gtd;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarDayAdapter extends RecyclerView.Adapter<CalendarDayAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<CalendarDay> calendarDays;

    public CalendarDayAdapter(Context context, ArrayList<CalendarDay> calendarDays) {
        this.context = context;
        this.calendarDays = calendarDays;
    }

    @NonNull
    @Override
    public CalendarDayAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.calendar_day, parent, false);
        return new CalendarDayAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarDayAdapter.MyViewHolder holder, int pos) {
        CalendarDay calendarDay = calendarDays.get(pos);
        //setime asjad mis peaks olema calendar_day-s.
        holder.dayNumber.setText(String.valueOf(calendarDay.getDate().getNum()));
        if (calendarDay.getEntries().size() > 1) {
            holder.entry1.setText(calendarDay.getEntries().get(0).getText());
            holder.entry2.setText(calendarDay.getEntries().get(1).getText());
        } else if (calendarDay.getEntries().size() == 1) {
            holder.entry1.setText(calendarDay.getEntries().get(0).getText());
            holder.entry2.setText("");
        } else {
            holder.entry1.setText("");
            holder.entry2.setText("");
        }
        if (calendarDay.getEntries().size() > 2) {
            holder.entry3.setText(calendarDay.getEntries().get(2).getText());
        } else {
            holder.entry3.setText("");
        }
        if (calendarDay.isToday()) {
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.gtd_accent_soft));
            holder.cardView.setStrokeColor(context.getColor(R.color.gtd_accent));
            holder.cardView.setStrokeWidth(2);
            holder.dayNumber.setTextColor(context.getColor(R.color.gtd_accent_dark));
        } else {
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.gtd_surface));
            holder.cardView.setStrokeColor(context.getColor(R.color.gtd_outline));
            holder.cardView.setStrokeWidth(1);
            holder.dayNumber.setTextColor(context.getColor(R.color.gtd_text_primary));
        }
        holder.cardView.setOnClickListener(v -> {
            Date date = calendarDay.getDate();
            Intent intent = new Intent(context, CalendarDayActivity.class);
            intent.putExtra(CalendarDayActivity.EXTRA_DAY, date.getNum());
            intent.putExtra(CalendarDayActivity.EXTRA_MONTH, date.getMonthIndex());
            intent.putExtra(CalendarDayActivity.EXTRA_YEAR, date.getYear());
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return calendarDays.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //defineerime view elemendid mis on calendar_day.xml-s

        TextView dayNumber;
        TextView entry1;
        TextView entry2;
        TextView entry3;
        MaterialCardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dayNumber = itemView.findViewById(R.id.dayNumber);
            entry1 = itemView.findViewById(R.id.entry1);
            entry2 = itemView.findViewById(R.id.entry2);
            entry3 = itemView.findViewById(R.id.entry3);
            cardView = itemView.findViewById(R.id.calendarDayCardView);
        }
    }

}
