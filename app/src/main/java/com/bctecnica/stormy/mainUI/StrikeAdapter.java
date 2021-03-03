package com.bctecnica.stormy.mainUI;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bctecnica.stormy.R;

import java.util.ArrayList;

public class StrikeAdapter extends RecyclerView.Adapter<StrikeAdapter.ExampleViewHolder> {
    private ArrayList<StrikeItem> exampleList;
    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_time;
        public TextView textView_distance;
        public ExampleViewHolder(View itemView) {
            super(itemView);
            textView_time = itemView.findViewById(R.id.text_recyclerView_time);
            textView_distance = itemView.findViewById(R.id.text_recyclerView_distance);
        }
    }
    public StrikeAdapter(ArrayList<StrikeItem> exampleList) {
        this.exampleList = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.strike_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        StrikeItem currentItem = exampleList.get(position);
        holder.textView_time.setText(currentItem.getText1());
        holder.textView_distance.setText(currentItem.getText2());
    }
    @Override
    public int getItemCount() {
        return exampleList.size();
    }
}