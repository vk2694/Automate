package com.lsv.automate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.lsv.automate.data.Location;
import com.lsv.automate.data.LocationViewModel;

import org.w3c.dom.Text;

public class AutomationActivity extends AppCompatActivity {
    private Button loc_btn, saveDB;
    private Double saved_latitude, saved_longitude, db_lat, db_lng;
    private String uniqueid, labelNamedb;
    private Switch bluethoothSwitch, doNotDistrubeSwitch;
    private Boolean bluethooth, doNotDistrube, gpsProvider, networkProvider;
    private String TAG = "AutomationActivity";
    private LocationViewModel mLocationViewModel;
    private TextView labelName;
    private Integer id, mediaVolume;
    private SeekBar mediaSeekbar;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automation);
//        loc_btn = (Button) findViewById(R.id.location_btn);
        bluethoothSwitch = (Switch) findViewById(R.id.bluethoothSwitch);
        doNotDistrubeSwitch = (Switch) findViewById(R.id.donNotDistrubeSwitch);
        saveDB = (Button) findViewById(R.id.saveDB);
        labelName = (TextView) findViewById(R.id.labelNameId);
        mediaSeekbar = (SeekBar) findViewById(R.id.seekBar);
//        Get User Current location
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        getLocation();

//        data model
        mLocationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        mLocationViewModel.getID(1).observe(this, location -> {
            if (location != null) {
                bluethooth = location.getBluethooth();
                if (bluethooth) {
                    bluethoothSwitch.setChecked(true);
                }
                doNotDistrube = location.getDonotdistrub();
                if (doNotDistrube) {
                    doNotDistrubeSwitch.setChecked(true);
                }
                db_lat = location.getLat();
                db_lng = location.getLng();
//                Toast.makeText(this, "Existing Location latitude "+ saved_latitude+", longitude "+saved_longitude, Toast.LENGTH_LONG).show();
                labelNamedb = location.getLabel();
                if (labelNamedb != null)
                    labelName.setText(labelNamedb);
                id = location.getId();
                mediaVolume = location.getMediaVolume();
                if (mediaVolume != null) {
                    Integer volume = Math.round(mediaVolume * 20);
                    mediaSeekbar.setProgress(volume);
                }
            }
        });
        mediaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaVolume = seekBar.getProgress();
            }
        });
//        Log.w(TAG, String.valueOf(mediaVolume));
        // MARK: Getting intent value
       /* Bundle come_from = getIntent().getExtras();
        if (come_from != null) {
            uniqueid = come_from.getString("Unique_id");
            if (uniqueid.equals("saved_location")) {
                saved_latitude = come_from.getDouble("latitude");
                saved_longitude = come_from.getDouble("longitude");
            }
        }*/

        bluethoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bluethooth = true;
                } else {
                    bluethooth = false;
                }
            }
        });

        doNotDistrubeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    doNotDistrube = true;
                } else {
                    doNotDistrube = false;
                }
            }
        });

        /*loc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent maps_intent = new Intent(AutomationActivity.this, MapsActivity.class);
                startActivity(maps_intent);
            }
        });*/

        saveDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saved_latitude == null) {
                    saved_latitude = db_lat;
                }
                if (saved_longitude == null) {
                    saved_longitude = db_lng;
                }
                LocationViewModel locationViewModel = new ViewModelProvider(AutomationActivity.this).get(LocationViewModel.class);
                Integer volume = Math.round(mediaVolume/20);
                Location word = new Location(1, saved_latitude, saved_longitude, bluethooth, doNotDistrube, labelName.getText().toString(), volume);
                if(id != null) {
                    locationViewModel.update(word);
                } else {
                    locationViewModel.insert(word);
                }
                Toast.makeText(v.getContext(), "Changes are saved successfully", Toast.LENGTH_SHORT).show();
                Intent home = new Intent(AutomationActivity.this, MainActivity.class);
                startActivity(home);
                finish();
            }
        });
    }
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                AutomationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AutomationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            android.location.Location location = null;
            if (networkProvider) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else if (gpsProvider) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (location != null) {
                saved_latitude = location.getLatitude();
                saved_longitude = location.getLongitude();
            }
        }
    }
}
