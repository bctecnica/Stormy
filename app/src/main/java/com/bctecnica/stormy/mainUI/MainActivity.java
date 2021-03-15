package com.bctecnica.stormy.mainUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bctecnica.stormy.R;
import com.bctecnica.stormy.weather.AlertDialogFragment;
import com.bctecnica.stormy.weather.DailyForecast;
import com.bctecnica.stormy.weather.Day;

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

    private SharedPreferences.Editor editor;
    private static final String PREFS_FILE = "com.bctecnica.stormyapp.preferences";
    private static final String KEY_MILESCHECKED = "KEY_MILESCHECKED";
    private boolean isConvertKmToMilesChecked = false;

    private Button trackingButton;
    private TextView timerValue;

    private static DecimalFormat df = new DecimalFormat("0.00");
    private long startTime=0L,timeInMilliseconds=0L,timeSwapBuff=0L,updateTime=0L;
    Handler timerHandler = new Handler();

    private ArrayList<StrikeItem> mExampleList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Vibrator vibrator;

    private Day[] day;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private String userLocation;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        // Activity setup
        // Restores total count variable
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isConvertKmToMilesChecked = sharedPreferences.getBoolean(KEY_MILESCHECKED, false);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("lat",0);
        longitude = intent.getDoubleExtra("long",0);
        userLocation = intent.getStringExtra("location");

        trackingButton = findViewById(R.id.btn_strike);
        timerValue = findViewById(R.id.txt_timer_value);

        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        VibrationEffect effect = VibrationEffect.createOneShot(500, 150);

        mExampleList = new ArrayList<>();
        buildRecyclerView();

        // Logic for tracking button
        trackingButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // start your timer
                timerValue.setVisibility(View.VISIBLE);
                startTime = SystemClock.uptimeMillis();
                timerHandler.postDelayed(updateTimer,0);

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // stop your timer.
                timerValue.setVisibility(View.INVISIBLE);
                insertItem(0);
                timerHandler.removeCallbacks(updateTimer);
                timerValue.setText("00:00");
                mLayoutManager.scrollToPosition(0);
                vibrator.vibrate(effect);
            }
            return false;
        });

        // Gets forecast Json from open weather API
        getForecast(latitude, longitude);

    }

    // Used to run and format stop watch
    Runnable updateTimer = new Runnable() {
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
            timerHandler.postDelayed(this,0);
        }
    };

    // --RecyclerView--
    // Adds strike details to RV when button is released
    public void insertItem(int position) {
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String distance;

        if(isConvertKmToMilesChecked){
            double distanceInMiles = (updateTime * 0.343) / 1000 * 0.62;
            distance = (df.format(distanceInMiles) + " miles");
        }else {
            double distanceInKm = (updateTime * 0.343) / 1000;
            distance = (df.format(distanceInKm) + " km");
        }

        mExampleList.add(position, new StrikeItem(time , distance));
        mAdapter.notifyItemInserted(position);
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_strike);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new StrikeAdapter(mExampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    // --forecast from open weather API--
    private void getForecast(double latitude, double longitude) {

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

    // Used to create and array of data for the daily forecast by looping through the returned json data
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.kmToMilesSwitch);
        checkable.setChecked(isConvertKmToMilesChecked);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kmToMilesSwitch:
                isConvertKmToMilesChecked = !item.isChecked();
                item.setChecked(isConvertKmToMilesChecked);
                return true;
            case R.id.feedbackMenu:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"bctecnica@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Stormy app");
                try {
                    startActivity(Intent.createChooser(i, "Send e-mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Saves if user has selected to use miles over KM
    @Override
    protected void onPause() {
        super.onPause();

        editor.putBoolean(KEY_MILESCHECKED, isConvertKmToMilesChecked);
        editor.apply();
    }

    // Passes the hours array as an intent to be displayed as a recycler view
    public void dailyForecastOnClick(View view){

        List<Day> days = Arrays.asList(day);

        Intent intent = new Intent(this, DailyForecast.class);
        intent.putExtra("DailyList",(Serializable) days);
        intent.putExtra("location", userLocation);

        startActivity(intent);
    }

}