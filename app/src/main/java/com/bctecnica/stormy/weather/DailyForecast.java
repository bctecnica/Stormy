package com.bctecnica.stormy.weather;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bctecnica.stormy.R;
import com.bctecnica.stormy.databinding.ActivityDailyForecastBinding;

import java.util.ArrayList;
import java.util.List;

// Used to get the forecast data and set it to the recycler view
public class DailyForecast extends AppCompatActivity {

    private TextView location;
    private DailyAdapter adapter;
    private ActivityDailyForecastBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);



        Intent intent = getIntent();
        List<Day> dayList = (ArrayList<Day>) intent.getSerializableExtra("DailyList");

        binding= DataBindingUtil.setContentView(this,
                R.layout.activity_daily_forecast);

        adapter = new DailyAdapter(dayList,this);

        binding.dailyListItems.setAdapter(adapter);

        binding.dailyListItems.setAdapter(adapter);
        binding.dailyListItems.setLayoutManager(new LinearLayoutManager(this));

        location = findViewById(R.id.textView_location);
        String passedLocation = intent.getStringExtra("location");
        location.setText(passedLocation);
    }
}