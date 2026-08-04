package com.example.gtd;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
        //setime asjad mis peaks olema calendar_day-s.
        holder.dayNumber.setText(String.valueOf(calendarDays.get(pos).getDate().getNum()));
        if (calendarDays.get(pos).getEntries().size() > 1) {
            holder.entry1.setText(calendarDays.get(pos).getEntries().get(0).getText());
            holder.entry2.setText(calendarDays.get(pos).getEntries().get(1).getText());
        } else if (calendarDays.get(pos).getEntries().size() == 1) {
            holder.entry1.setText(calendarDays.get(pos).getEntries().get(0).getText());
            holder.entry2.setText("");
        } else {
            holder.entry1.setText("");
            holder.entry2.setText("");
        }
        holder.cardView.setOnClickListener(v -> {
            Date date = calendarDays.get(pos).getDate();
            Intent intent = new Intent(context, CalendarDayActivity.class);
            intent.putExtra(CalendarDayActivity.EXTRA_DAY, date.getNum());
            intent.putExtra(CalendarDayActivity.EXTRA_MONTH, date.getMonth());
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
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dayNumber = itemView.findViewById(R.id.dayNumber);
            entry1 = itemView.findViewById(R.id.entry1);
            entry2 = itemView.findViewById(R.id.entry2);
            cardView = itemView.findViewById(R.id.calendarDayCardView);
        }
    }

}

