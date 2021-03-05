package com.bctecnica.stormy.mainUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bctecnica.stormy.R;
import com.bctecnica.stormy.weather.AlertDialogFragment;
import com.bctecnica.stormy.weather.DailyForecast;
import com.bctecnica.stormy.weather.Day;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = MainActivity.class.getSimpleName();

    private Button trackingButton;
    private TextView timerValue;

    private boolean isMyLocationChecked = false;

    private static DecimalFormat df = new DecimalFormat("0.00");

    private ArrayList<StrikeItem> mExampleList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Used to run and format stop watch
    Handler customHandler = new Handler();
    long startTime=0L,timeInMilliseconds=0L,timeSwapBuff=0L,updateTime=0L;

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
            updateTime = timeSwapBuff+timeInMilliseconds;
            int secs=(int)(updateTime/1000);
            secs%=60;
            int milliseconds=(int)(updateTime/10);
            milliseconds%=100;
            timerValue.setText(String.format("%02d",secs)+":"
                    +String.format("%02d",milliseconds));
            customHandler.postDelayed(this,0);
        }
    };


    private Day[] day;

    double latitude = 0.0;
    double longitude = 0.0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("lat",0);
        longitude = intent.getDoubleExtra("long",0);

        trackingButton = findViewById(R.id.btn_strike);
        timerValue = findViewById(R.id.text_timerValue);

        mExampleList = new ArrayList<>();
        buildRecyclerView();

        trackingButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // start your timer
                timerValue.setVisibility(View.VISIBLE);
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread,0);

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // stop your timer.
                timerValue.setVisibility(View.INVISIBLE);
                insertItem(0);
                customHandler.removeCallbacks(updateTimerThread);
                timerValue.setText("00:00");
                mLayoutManager.scrollToPosition(0);
            }
            return false;
        });

        getForecast(latitude, longitude);

    }

    // --RecyclerView--
    // Adds strike details to RV when button is released
    public void insertItem(int position) {
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        double distanceConversion = (updateTime * 0.343)/1000;
        String distance = (df.format(distanceConversion) + " km");
        mExampleList.add(position, new StrikeItem(time , distance));
        mAdapter.notifyItemInserted(position);
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new StrikeAdapter(mExampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void getForecast(double latitude, double longitude) {

        // Server request with location
        String APIKey = "bd3c617cdb63b3fbafb2f8c8df8173b1";

        // Built request string for server
        String forecastURL = "https://api.openweathermap.org/data/2.5/onecall?lat="
                + latitude + "&lon=" + longitude +"&appid=" + APIKey;

        // Sends request to server if network is available
        if(isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();

            // Runs asynchronously and waits for response from server then passes data to app
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            day = getDailyForecast(jsonData);

                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO caught", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON error caught", e);
                    }
                }
            });
        }
    }

    // Used to create and array of data for the hourly forecast by looping through the json data
    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONArray daily = forecast.getJSONArray("daily");

        Day[] days = new Day[daily.length()];

        for (int i = 0; i < daily.length(); i++){

            JSONObject eachDay = daily.getJSONObject(i);
            JSONArray weather = eachDay.getJSONArray("weather");
            JSONObject weatherObject = weather.getJSONObject(0);

            Day day = new Day();

            day.setIcon(weatherObject.getString("icon"));
            day.setTime(eachDay.getLong("dt"));

            days[i] = day;
        }

        return days;
    }

    // Used to check if a user is connected to a network
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        else{
            Toast.makeText(this, "No network is currently available", Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    // Error pop-up box if network request fails
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getSupportFragmentManager(), "error");
    }

    // --Menu--
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"bctecnica@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Stormy app");
        try {
            startActivity(Intent.createChooser(i, "Send e-mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    // Passes the hours array as an intent to be displayed as a recycler view
    public void dailyOnClick(View view){

        List<Day> days = Arrays.asList(day);

        Intent intent = new Intent(this, DailyForecast.class);
        intent.putExtra("DailyList",(Serializable) days);
        intent.putExtra("long", longitude);
        intent.putExtra("lat", latitude);

        startActivity(intent);
    }

}