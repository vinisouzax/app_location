package com.movep.movep;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    private Button btnGetLocation;
    private TextView showLocation;
    private LocationManager locationManager;
    private String latitude, longitude, speed;
    private static boolean isRunning;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;
    private long FASTEST_INTERVAL = 2000;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        showLocation = findViewById(R.id.showLocation);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    if(!isRunning) {
                        btnGetLocation.setText("Parar");
                        getLocation();
                    }else{
                        btnGetLocation.setText("Iniciar");
                        isRunning = false;
                        stopLocationUpdates();
                    }
                }
            }
        });
    }

    public void stopLocationUpdates() {
        if (fusedLocationClient != null) {
            try {
                final Task<Void> voidTask = fusedLocationClient.removeLocationUpdates(locationCallback);
                if (voidTask.isSuccessful()) {
                    Log.d(TAG,"StopLocation updates successful! ");
                } else {
                    Log.d(TAG,"StopLocation updates unsuccessful! " + voidTask.toString());
                }
            }
            catch (SecurityException exp) {
                Log.d(TAG, " Security exception while removeLocationUpdates");
            }
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Habilitar GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            isRunning = true;
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    send_location(locationResult.getLastLocation());
                }
            };
            fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        }
    }

    public void send_location(Location locationGPS){
        RetrieveHttp http = new RetrieveHttp();
        JSONObject json;

        double lat = locationGPS.getLatitude();
        double longi = locationGPS.getLongitude();
        double spe = locationGPS.getSpeed();

        latitude = String.valueOf(lat);
        longitude = String.valueOf(longi);
        speed = String.valueOf(spe);

        String txt_locations = showLocation.getText().toString();
        Date date = new Date();
        showLocation.setText(txt_locations + "Data: " + date + "\n" + "Latitude: " + latitude + "\n"
                + "Longitude: " + longitude + "\n" + "Vel: " + speed + "\n\n");

        try {
            json = http.execute("http://tocti.com.br/directions/5/"+latitude+"/"+longitude+"/"+speed,
                    "GET", "",null).get();

            System.out.println(json);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}