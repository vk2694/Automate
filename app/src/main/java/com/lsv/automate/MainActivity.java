package com.lsv.automate;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lsv.automate.data.LocationViewModel;

import java.lang.annotation.Documented;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.lsv.automate.R.string.media_volume;
import static com.lsv.automate.R.string.service_enabled;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "MainActivity";
    private Double cur_latitude, cur_longitude, saved_latitude, saved_longitude;
    private Button loc_btn, permission_btn;
    private String uniqueid;
    private List<com.lsv.automate.data.Location> data;
    private Boolean bluetoothStatus, doNotDistrubStatus;
    private int automationCount;
    private Integer mediaVolume;
    private PopupWindow popupWindow;
    private Boolean click = true;
    private int LOCATION_PERMISSION_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch service_toggle = (Switch) findViewById(R.id.service_enable);
        service_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("com.lsv.automate", MODE_PRIVATE).edit();
                if (isChecked) {
                    editor.putString("service", "Yes");
                    startService(new Intent(MainActivity.this, LocationService.class));
//                    Toast.makeText(MainActivity.this, getString(service_enabled), Toast.LENGTH_LONG).show();
                } else {
                    stopService(new Intent(MainActivity.this, LocationService.class));
                    editor.putString("service", "No");
                    Toast.makeText(MainActivity.this, getString(R.string.service_disabled), Toast.LENGTH_LONG).show();
                }
                editor.apply();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("com.lsv.automate", MODE_PRIVATE);
        String service_status = sharedPreferences.getString("service", "No");
        if (service_status.equals("Yes")) {
            service_toggle.setChecked(true);
        } else {
            Toast.makeText(this, "Service not enabled", Toast.LENGTH_LONG).show();
        }

//       Broadcast Manager calling
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver, new IntentFilter("locationCoordinate"));


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (automationCount == 0) {
                    Intent call_automation = new Intent(MainActivity.this, AutomationActivity.class);
                    startActivity(call_automation);
                } else {
                    Toast.makeText(MainActivity.this, "We are working on Multiple Automation!", Toast.LENGTH_LONG).show();
                }
            }
        });
       /* Bundle come_from = getIntent().getExtras();
        if (come_from != null) {
            uniqueid = come_from.getString("Unique_id");
            if (uniqueid.equals("saved_location")) {
                saved_latitude = come_from.getDouble("latitude");
                saved_longitude = come_from.getDouble("longitude");
            }
        }*/
        RecyclerView recyclerView = findViewById(R.id.recylerViewCard);
        final AutomationDataAdapter adapter = new AutomationDataAdapter(new AutomationDataAdapter.AutomationDiff(), this);
        // Get a new or existing ViewModel from the ViewModelProvider.
        LocationViewModel mLocationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        mLocationViewModel.getAllWords().observe(this,
                adapter::submitList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mLocationViewModel.getID(1).observe(this, location -> {
            if (location != null) {
                saved_longitude = location.getLng();
                saved_latitude = location.getLat();
                bluetoothStatus = location.getBluethooth();
                automationCount = location.getId();
                mediaVolume = location.getMediaVolume();
                doNotDistrubStatus = location.getDonotdistrub();
            }
        });
        permission_btn = (Button) findViewById(R.id.permission_btn);
        permission_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Enable Fab button to add automation custom setting
                    fab.setVisibility(View.VISIBLE);
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    Toast.makeText(MainActivity.this, "You already granted the permission", Toast.LENGTH_SHORT).show();
                } else {
                    //Disable Fab button, Because we didn't get location permission
                    //Calling the request permission method
                    fab.setVisibility(View.GONE);
                    requestLocationPermission();
                }
            }
        });
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMISSION_CODE);
        return true;
    }

    private  void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Permission needed for access your location and change the customized settings for you!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

  /*  void showPopup(String forPermission) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        int width  = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focuable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focuable);

        TextView textView = popupView.findViewById(R.id.titleText);
        textView.setText("Give loation permission to access the current location to change the settings given by you!");

        Button button = popupView.findViewById(R.id.messageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "We received you permission", Toast.LENGTH_SHORT).show();
            }
        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
        popupWindow.setContentView(popupView);
;    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            configure_button();
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            cur_latitude = b.getDouble("latitude");
            cur_longitude = b.getDouble("longitude");
            checkRadius();
        }
    };

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NOTIFICATION_POLICY, Manifest.permission.ACCESS_BACKGROUND_LOCATION}
                    , 10);
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
    }

    void checkRadius() {
        if (saved_latitude != null && saved_longitude != null) {
            double distance;
            Location locationA = new Location("Point A");
            locationA.setLatitude(cur_latitude);
            locationA.setLongitude(cur_longitude);

            Location locationB = new Location("Point B");
            locationB.setLatitude(saved_latitude);
            locationB.setLongitude(saved_longitude);
            distance = locationA.distanceTo(locationB) / 1000;
            Log.w(TAG, String.valueOf(distance));
            if (distance <= 3) {
                if (bluetoothStatus) {
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter != null) {
                        if (!mBluetoothAdapter.isEnabled()) {
                            mBluetoothAdapter.enable();
                        }
                    }
                }
                if (doNotDistrubStatus) {
                    NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    if (n.isNotificationPolicyAccessGranted()) {
//                        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                        n.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                        try {
                            Log.w("check dnd status", String.valueOf(Settings.Global.getInt(getContentResolver(), "zen_mode")));
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Ask the user to grant access
                        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivityForResult(intent, 1);
                    }
                }
                if (mediaVolume != null && mediaVolume > 0) {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mediaVolume, 0);
                }
            }
        }
    }

    void editBtnClicked() {
        Intent automatePage = new Intent(MainActivity.this, AutomationActivity.class);
        startActivity(automatePage);
    }
}
