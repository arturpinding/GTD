package com.example.gtd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GtdItemAdapter extends RecyclerView.Adapter<GtdItemAdapter.ViewHolder> {
    public interface Listener {
        void onItemChanged(GtdItem item);
        void onItemDeleteRequested(GtdItem item);
    }

    private final ArrayList<GtdItem> items;
    private final boolean checkable;
    private final boolean showTodayButton;
    private final Listener listener;

    public GtdItemAdapter(ArrayList<GtdItem> items, boolean checkable,
                          boolean showTodayButton, Listener listener) {
        this.items = items;
        this.checkable = checkable;
        this.showTodayButton = showTodayButton;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gtd_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GtdItem item = items.get(position);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setText(item.getTitle());
        holder.checkBox.setChecked(item.isCompleted());
        holder.checkBox.setVisibility(checkable ? View.VISIBLE : View.GONE);
        holder.title.setText(item.getTitle());
        holder.title.setVisibility(checkable ? View.GONE : View.VISIBLE);

        holder.todayButton.setVisibility(showTodayButton ? View.VISIBLE : View.GONE);
        holder.todayButton.setImageResource(item.isToday()
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
        holder.todayButton.setContentDescription(item.isToday()
                ? "Remove from Today" : "Add to Today");

        holder.checkBox.setOnCheckedChangeListener((button, checked) -> {
            item.setCompleted(checked);
            listener.onItemChanged(item);
        });
        holder.todayButton.setOnClickListener(view -> {
            item.setToday(!item.isToday());
            listener.onItemChanged(item);
        });
        holder.itemView.setOnLongClickListener(view -> {
            listener.onItemDeleteRequested(item);
            return true;
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBox;
        final TextView title;
        final ImageButton todayButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.gtdItemCheck);
            title = itemView.findViewById(R.id.gtdItemTitle);
            todayButton = itemView.findViewById(R.id.gtdItemTodayButton);
        }
    }
}
