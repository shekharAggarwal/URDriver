package com.urdriver.urdriver;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.DriverForAdmin;
import com.urdriver.urdriver.retrofit.IURDriver;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverStatus extends CrashActivity implements OnMapReadyCallback, ConnectivityReceiver.ConnectivityReceiverListener {

    TextView txtName, txtPhoneNumber, txtCabBrand, txtCabNumber, txtNumberTrips, txtFrom,
            txtTo, txtStartTime, txtUserName, txtEmail, txtUserNumber;

    ConnectivityReceiver connectivityReceiver;

    IURDriver mService = Common.getAPI();
    private GoogleMap mMap;
    Toolbar toolbar;
    DatabaseReference drivers;
    GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_status);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.
                    TRANSPARENT);
        }

        Common.setTop(this);

        txtName = findViewById(R.id.txtName);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtCabBrand = findViewById(R.id.txtCabBrand);
        txtCabNumber = findViewById(R.id.txtCabNumber);
        txtNumberTrips = findViewById(R.id.txtNumberTrips);
        txtFrom = findViewById(R.id.txtFrom);
        txtTo = findViewById(R.id.txtTo);
        txtStartTime = findViewById(R.id.txtStartTime);
        txtUserName = findViewById(R.id.txtUserName);
        txtEmail = findViewById(R.id.txtEmail);
        txtUserNumber = findViewById(R.id.txtUserNumber);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (getIntent().getStringExtra("TEXT") != null) {
            mService.getDriverInfoForAdmin(getIntent().getStringExtra("TEXT"), "0")
                    .enqueue(new Callback<DriverForAdmin>() {
                @Override
                public void onResponse(Call<DriverForAdmin> call, Response<DriverForAdmin> response) {
                    if (response.body() != null) {
                        txtName.setText(response.body().getDriverName());
                        txtPhoneNumber.setText(response.body().getDriverPhone());
                        txtCabBrand.setText(response.body().getDriverCabBrand() + " " + response.body().getDriverCabModel());
                        txtCabNumber.setText(response.body().getDriverCabNumber());
                        txtNumberTrips.setText(response.body().getTotalTrip());
                        txtFrom.setText(response.body().getSourceAddress());
                        txtTo.setText(response.body().getDestinationAddress());
                        txtStartTime.setText(response.body().getStartTrip());
                        txtUserName.setText(response.body().getName());
                        txtEmail.setText(response.body().getEmail());
                        txtUserNumber.setText(response.body().getBookAccount());
                    } else {
                        Toast.makeText(DriverStatus.this, "Try Again", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }

                @Override
                public void onFailure(Call<DriverForAdmin> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                    Toast.makeText(DriverStatus.this, "Try Again", Toast.LENGTH_SHORT).show();
                    onBackPressed();

                }
            });
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        drivers = FirebaseDatabase.getInstance().getReference("Location");
        geoFire = new GeoFire(drivers);
        geoFire.getLocation(getIntent().getStringExtra("TEXT"), new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                mMap.clear();
                final LatLng your_location = new LatLng(location.latitude,
                        location.longitude);
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                        .flat(true)
                        .position(your_location)
                        .title("Cab Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(your_location, 17.0f));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            setContentView(R.layout.layout_no_internet);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.
                        FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.
                        FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.
                        TRANSPARENT);
            }
            Common.setTop(this);

            findViewById(R.id.btnTry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recreate();

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        /*register connection status listener*/
        MyApplication.getInstance().setConnectivityListener(this);
//        // register connection status listener
//        MyApplication.getInstance().setConnectivityListener(this);
    }
}
