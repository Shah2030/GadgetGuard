package com.example.gadgetguard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class GadgetUsageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Object> items;

    private static final int TYPE_DATE_HEADER = 0;
    private static final int TYPE_USAGE_ITEM = 1;

    public GadgetUsageAdapter(List<Object> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_DATE_HEADER : TYPE_USAGE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_DATE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_date_header, parent, false);
            return new DateHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_usage_entry, parent, false);
            return new UsageEntryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateHeaderViewHolder) {
            ((DateHeaderViewHolder) holder).dateText.setText((String) items.get(position));
        } else if (holder instanceof UsageEntryViewHolder) {
            GadgetUsageEntry entry = (GadgetUsageEntry) items.get(position);
            ((UsageEntryViewHolder) holder).appText.setText(entry.getApp());
            ((UsageEntryViewHolder) holder).timeText.setText(entry.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;

        DateHeaderViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateHeaderText);
        }
    }

    static class UsageEntryViewHolder extends RecyclerView.ViewHolder {
        TextView appText, timeText;

        UsageEntryViewHolder(View itemView) {
            super(itemView);
            appText = itemView.findViewById(R.id.appText);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }
}
