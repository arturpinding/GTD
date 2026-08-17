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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
        holder.dayNumber.setText(String.valueOf(calendarDay.getDate().getNum()));
        int entryCount = calendarDay.getEntries().size();
        holder.entry1.setText(entryCount == 0 ? ""
                : getEventBadge(entryCount));
        holder.entry2.setVisibility(View.GONE);
        holder.entry3.setVisibility(View.GONE);
        if (calendarDay.isToday()) {
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.gtd_accent_soft));
            holder.cardView.setStrokeColor(context.getColor(R.color.gtd_accent));
            holder.cardView.setStrokeWidth(context.getResources()
                    .getDimensionPixelSize(R.dimen.calendar_today_stroke));
            holder.dayNumber.setTextColor(context.getColor(R.color.gtd_accent_dark));
        } else if (!calendarDay.isInDisplayedMonth()) {
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.gtd_surface_variant));
            holder.cardView.setStrokeColor(context.getColor(R.color.gtd_outline));
            holder.cardView.setStrokeWidth(context.getResources()
                    .getDimensionPixelSize(R.dimen.calendar_stroke));
            holder.dayNumber.setTextColor(context.getColor(R.color.gtd_text_secondary));
        } else {
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.gtd_surface));
            holder.cardView.setStrokeColor(context.getColor(R.color.gtd_outline));
            holder.cardView.setStrokeWidth(context.getResources()
                    .getDimensionPixelSize(R.dimen.calendar_stroke));
            holder.dayNumber.setTextColor(context.getColor(R.color.gtd_text_primary));
        }
        Date date = calendarDay.getDate();
        Calendar systemDate = new GregorianCalendar(date.getYear(), date.getMonthIndex(), date.getNum());
        String fullDate = DateFormat.getDateInstance(DateFormat.FULL).format(systemDate.getTime());
        int descriptionResource = calendarDay.isToday()
                ? R.plurals.calendar_day_accessibility_today
                : R.plurals.calendar_day_accessibility;
        String description = context.getResources().getQuantityString(
                descriptionResource, entryCount, fullDate, entryCount);
        holder.cardView.setContentDescription(description);
        holder.cardView.setSelected(calendarDay.isToday());
        holder.cardView.setOnClickListener(v -> {
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

    private String getEventBadge(int count) {
        return count == 1 ? context.getString(R.string.event_dot)
                : context.getString(R.string.event_dot_count, count);
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
