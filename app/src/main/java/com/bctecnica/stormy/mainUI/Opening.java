package com.bctecnica.stormy.mainUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bctecnica.stormy.R;

public class Opening extends AppCompatActivity {

    double latitude = 50.7150;
    double longitude = 1.9872;

    private Spinner spinner;
    private static final String[] paths = {"item 1", "item 2", "item 3"};

    Button start;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening);

        Spinner locationSpinner = (Spinner) findViewById(R.id.coloredSpinner_location);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Spinner_items,
                R.layout.color_spinner_layout
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
                        Toast.makeText(getApplicationContext(),"picked Central eng",Toast.LENGTH_SHORT).show();
                        latitude = 51.4540;
                        longitude = -0.1052;
                        break;
                    case 1:
                        // North England - York
                        Toast.makeText(getApplicationContext(),"picked north eng",Toast.LENGTH_SHORT).show();
                        latitude = 53.8978;
                        longitude = -1.03970;
                        break;
                    case 2:
                        // South England - Poole
                        Toast.makeText(getApplicationContext(),"picked south eng",Toast.LENGTH_SHORT).show();
                        latitude = 50.7150;
                        longitude = -1.9872;
                        break;
                    case 3:
                        // West England - Exeter
                        Toast.makeText(getApplicationContext(),"picked west eng",Toast.LENGTH_SHORT).show();
                        latitude = 50.7051;
                        longitude = -3.5257;
                        break;
                    case 4:
                        // East England - Norwich
                        Toast.makeText(getApplicationContext(),"picked east eng",Toast.LENGTH_SHORT).show();
                        latitude = 52.5830;
                        longitude = 1.2982;
                        break;
                    case 5:
                        // Wales - Newtown
                        Toast.makeText(getApplicationContext(),"picked wales",Toast.LENGTH_SHORT).show();
                        latitude = 52.4961;
                        longitude = -3.3173;
                        break;
                    case 6:
                        // Ireland - Clara
                        Toast.makeText(getApplicationContext(),"picked ireland",Toast.LENGTH_SHORT).show();
                        latitude = 53.3243;
                        longitude = -7.6078;
                        break;
                    case 7:
                        // Northern Ireland - Belfast
                        Toast.makeText(getApplicationContext(),"picked north ire",Toast.LENGTH_SHORT).show();
                        latitude = 54.5465;
                        longitude = -5.9072;
                        break;
                    case 8:
                        // South Scotland - Glasgow
                        Toast.makeText(getApplicationContext(),"picked south scot",Toast.LENGTH_SHORT).show();
                        latitude = 55.8012;
                        longitude = -4.2589;
                        break;
                    case 9:
                        // North Scotland - Inverness
                        Toast.makeText(getApplicationContext(),"picked north scot",Toast.LENGTH_SHORT).show();
                        latitude = 57.4331;
                        longitude = -4.1929;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        start = findViewById(R.id.start_BTN);

        start.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("long", longitude);
            intent.putExtra("lat", latitude);
            startActivity(intent);
        });

    }
}
