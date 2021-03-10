package com.bctecnica.stormy.mainUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.bctecnica.stormy.R;

public class Opening extends AppCompatActivity {

    double latitude = 50.7150;
    double longitude = 1.9872;
    String userLocation;

    Button start;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening);

        Spinner locationSpinner = findViewById(R.id.spinner_location);
        start = findViewById(R.id.btn_start);

        //--Spinner dropdown list of locations--
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Spinner_items,
                R.layout.spinner_layout
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);

        locationSpinner.setAdapter(adapter);

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                switch (position) {
                    case 0:
                        //Central England - London
                        latitude = 51.4540;
                        longitude = -0.1052;
                        userLocation = "London";
                        break;
                    case 1:
                        // North England - York
                        latitude = 53.8978;
                        longitude = -1.03970;
                        userLocation = "York";
                        break;
                    case 2:
                        // South England - Poole
                        latitude = 50.7150;
                        longitude = -1.9872;
                        userLocation = "Poole";
                        break;
                    case 3:
                        // West England - Exeter
                        latitude = 50.7051;
                        longitude = -3.5257;
                        userLocation = "Exeter";
                        break;
                    case 4:
                        // East England - Norwich
                        latitude = 52.5830;
                        longitude = 1.2982;
                        userLocation = "Norwich";
                        break;
                    case 5:
                        // Wales - Newtown
                        latitude = 52.4961;
                        longitude = -3.3173;
                        userLocation = "Newton";
                        break;
                    case 6:
                        // Ireland - Clara
                        latitude = 53.3243;
                        longitude = -7.6078;
                        userLocation = "Clara";
                        break;
                    case 7:
                        // Northern Ireland - Belfast
                        latitude = 54.5465;
                        longitude = -5.9072;
                        userLocation = "Belfast";
                        break;
                    case 8:
                        // South Scotland - Glasgow
                        latitude = 55.8012;
                        longitude = -4.2589;
                        userLocation = "Glasgow";
                        break;
                    case 9:
                        // North Scotland - Inverness
                        latitude = 57.4331;
                        longitude = -4.1929;
                        userLocation = "Inverness";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        // Passes user selected location for API call
        start.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("location", userLocation);
            startActivity(intent);
        });

    }
}
