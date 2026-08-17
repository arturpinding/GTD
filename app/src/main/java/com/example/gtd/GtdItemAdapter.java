package com.example.gtd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Paint;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GtdItemAdapter extends RecyclerView.Adapter<GtdItemAdapter.ViewHolder> {
    public interface Listener {
        void onItemChanged(GtdItem item);
        void onItemActionRequested(GtdItem item);
        default String getItemSubtitle(GtdItem item) { return ""; }
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
        String subtitle = listener.getItemSubtitle(item);
        holder.subtitle.setText(subtitle);
        holder.subtitle.setVisibility(subtitle == null || subtitle.isEmpty() ? View.GONE : View.VISIBLE);
        int checkFlags = holder.checkBox.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG;
        int titleFlags = holder.title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG;
        holder.checkBox.setPaintFlags(item.isCompleted()
                ? checkFlags | Paint.STRIKE_THRU_TEXT_FLAG : checkFlags);
        holder.title.setPaintFlags(item.isCompleted()
                ? titleFlags | Paint.STRIKE_THRU_TEXT_FLAG : titleFlags);

        holder.todayButton.setVisibility(showTodayButton ? View.VISIBLE : View.GONE);
        holder.todayButton.setImageResource(item.isToday()
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
        holder.todayButton.setContentDescription(item.isToday()
                ? holder.itemView.getContext().getString(R.string.remove_from_today)
                : holder.itemView.getContext().getString(R.string.add_to_today));
        holder.todayButton.setSelected(item.isToday());

        holder.checkBox.setOnCheckedChangeListener((button, checked) -> {
            item.setCompleted(checked);
            listener.onItemChanged(item);
        });
        holder.todayButton.setOnClickListener(view -> {
            item.setToday(!item.isToday());
            listener.onItemChanged(item);
        });
        holder.moreButton.setContentDescription(holder.itemView.getContext()
                .getString(R.string.item_actions_for, item.getTitle()));
        holder.moreButton.setOnClickListener(view -> listener.onItemActionRequested(item));
        holder.itemView.setOnClickListener(view -> listener.onItemActionRequested(item));
        holder.itemView.setOnLongClickListener(view -> {
            listener.onItemActionRequested(item);
            return true;
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBox;
        final TextView title;
        final TextView subtitle;
        final ImageButton todayButton;
        final ImageButton moreButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.gtdItemCheck);
            title = itemView.findViewById(R.id.gtdItemTitle);
            subtitle = itemView.findViewById(R.id.gtdItemSubtitle);
            todayButton = itemView.findViewById(R.id.gtdItemTodayButton);
            moreButton = itemView.findViewById(R.id.gtdItemMoreButton);
        }
    }
}
