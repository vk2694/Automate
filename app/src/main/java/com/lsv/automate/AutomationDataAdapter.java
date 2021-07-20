package com.lsv.automate;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.lsv.automate.data.Location;

import java.util.Currency;

public class AutomationDataAdapter extends ListAdapter<Location, AutomationViewHolder> {
    Context context;
    public AutomationDataAdapter(@NonNull DiffUtil.ItemCallback<Location> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
    }

    @Override
    public AutomationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return AutomationViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(AutomationViewHolder holder, int position) {
        Location current = getItem(position);
        int count = 0;
        if (current.getDonotdistrub())
            count +=1;
        if (current.getBluethooth())
            count +=1;
        holder.bind(String.valueOf(current.getLabel()), String.valueOf(count), String.valueOf(current.getBluethooth()), String.valueOf(current.getDonotdistrub()), context);
    }

    static class AutomationDiff extends DiffUtil.ItemCallback<Location> {

        @Override
        public boolean areItemsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    }
}