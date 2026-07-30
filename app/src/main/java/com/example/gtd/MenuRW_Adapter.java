package com.example.gtd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuRW_Adapter extends RecyclerView.Adapter<MenuRW_Adapter.MyViewHolder> {
    private Context context;
    private ArrayList<MenuItem> menuItems;

    public MenuRW_Adapter(Context context, ArrayList<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public MenuRW_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new MenuRW_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuRW_Adapter.MyViewHolder holder, int position) {
        holder.activityName.setText(menuItems.get(position).getName());
        holder.activityIcon.setImageResource(menuItems.get(position).getIcon());
        holder.activityButton.setOnClickListener(v -> {
            context.startActivity(menuItems.get(position).getIntent());
        });

    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView activityIcon;
        TextView activityName;
        Button activityButton;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            activityIcon = itemView.findViewById(R.id.activityIcon);
            activityName = itemView.findViewById(R.id.activityName);
            activityButton = itemView.findViewById(R.id.activityButton);
        }
    }
}
