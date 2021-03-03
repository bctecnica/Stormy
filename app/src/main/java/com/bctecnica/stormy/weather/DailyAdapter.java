package com.bctecnica.stormy.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bctecnica.stormy.R;
import com.bctecnica.stormy.databinding.DailyListItemBinding;

import java.util.List;

// Adapter Used to link the hourly data to the recycler view
public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {
    private List<Day> days;
    private Context context;

    public DailyAdapter(List<Day> days, Context context) {
        this.days = days;
        this.context = context;
    }

    @NonNull
    @Override
    public DailyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DailyListItemBinding binding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.daily_list_item,
                        parent,
                        false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Day day = days.get(position);
        holder.dailyListItemBinding.setDay(day);

    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // Binding variable
        public DailyListItemBinding dailyListItemBinding;

        // Constructor to do look ups for each subview
        public ViewHolder(DailyListItemBinding dailyLayoutBinding){
            super(dailyLayoutBinding.getRoot());
            dailyListItemBinding = dailyLayoutBinding;
        }
    }
}
