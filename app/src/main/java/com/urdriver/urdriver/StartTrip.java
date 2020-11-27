package com.urdriver.urdriver;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.DataMessage;
import com.urdriver.urdriver.model.MyResponse;
import com.urdriver.urdriver.model.Token;
import com.urdriver.urdriver.model.Trip;
import com.urdriver.urdriver.retrofit.IFCMService;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartTrip extends CrashActivity implements OnMapReadyCallback, ConnectivityReceiver.ConnectivityReceiverListener {

    ConnectivityReceiver connectivityReceiver;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    Toolbar toolbar;
    IURDriver mService = Common.getAPI();
    Button btnDropOff;
    DatabaseReference drivers;
    GeoFire geoFire;
    private GoogleMap mMap;
    private Marker mCurrent;
    protected static final String TAG = "StartTrip";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private float v;

    private double lat = 0, lng = 0;
    private LatLng startPosition, endPosition;

    private float getBearing(LatLng startPosition, LatLng endPosition) {
        double lat = Math.abs(startPosition.latitude - endPosition.latitude);
        double lng = Math.abs(startPosition.longitude - endPosition.longitude);

        if (startPosition.latitude < endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (startPosition.latitude < endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Common.setTop(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drivers = FirebaseDatabase.getInstance().getReference("Location")/*.child(Common.currentDriver.getPhone())*/;
        geoFire = new GeoFire(drivers);
        Paper.init(this);
        if (Common.trip == null) {
            String Id = Paper.book().read("TripId");

            mService.getTrip(Id).enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    Common.trip = response.body();
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                }
            });

        }
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            buildLocationCallBack();
                            buildLocationRequest();
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(StartTrip.this);
                            //start update location
                            if (ActivityCompat.checkSelfPermission(StartTrip.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(StartTrip.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                            displayLocationSettingsRequest(StartTrip.this);
                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(StartTrip.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }).check();
        btnDropOff = findViewById(R.id.btnDropOff);

        btnDropOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogMeter();
            }
        });

    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(10f);

    }

    private void buildLocationCallBack() {

        locationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                displayLocation();
            }
        };

    }

    private void displayLocation() {
        //checking permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;


        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        Common.mLastLocation = location;
                        geoFire.setLocation(Common.currentDriver.getPhone(), new GeoLocation(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude()),
                                new GeoFire.CompletionListener() {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {

                                    }
                                });
                        if (Common.mLastLocation != null) {
                            final double latitude = Common.mLastLocation.getLatitude();
                            final double longitude = Common.mLastLocation.getLongitude();
                            if (endPosition != null) {
                                if ((!String.valueOf(endPosition.latitude).equalsIgnoreCase(String.valueOf(latitude)) ||
                                        !String.valueOf(endPosition.longitude).equalsIgnoreCase(String.valueOf(longitude)))) {
                                    startPosition = endPosition;
                                    endPosition = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());

                                    if (!String.valueOf(endPosition.latitude).equalsIgnoreCase(String.valueOf(startPosition.latitude)) &&
                                            !String.valueOf(endPosition.longitude).equalsIgnoreCase(String.valueOf(startPosition.longitude))) {
                                        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                                        valueAnimator.setDuration(2000);
                                        valueAnimator.setInterpolator(new LinearInterpolator());
                                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                v = valueAnimator.getAnimatedFraction();
                                                lng = v * endPosition.longitude + (1 - v) * startPosition.longitude;
                                                lat = v * endPosition.latitude + (1 - v) * startPosition.latitude;
                                                LatLng newPos = new LatLng(lat, lng);
                                                mCurrent.setPosition(newPos);
                                                mCurrent.setAnchor(0.5f, 0.5f);
                                                mCurrent.setRotation(getBearing(startPosition, newPos));
                                                CameraPosition currentPlace = new CameraPosition.Builder()
                                                        .target(newPos)
                                                        .bearing(getBearing(startPosition, newPos) + 0)
                                                        .zoom(17.0f)
                                                        .build();
                                                mMap.animateCamera(CameraUpdateFactory
                                                        .newCameraPosition(currentPlace));
                                            }
                                        });
                                        valueAnimator.start();
                                    }
                                }
                            } else {
                                if (mCurrent != null)
                                    mCurrent.remove();
                                mCurrent = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                        .position(new LatLng(latitude, longitude))
                                        .rotation(location.getBearing())
                                        .title("Your Location"));
                                CameraPosition currentPlace = new CameraPosition.Builder()
                                        .target(new LatLng(latitude, longitude))
                                        .bearing(Common.mLastLocation.getBearing() + 0).zoom(17.0f).build();
                                mMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(currentPlace));
                                startPosition = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());
                                endPosition = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ERROR", e.getMessage());
            }
        });


    }

    private void showDialogMeter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StartTrip.this);
        builder.setCancelable(false);
        builder.setTitle("Drop Trip");
        View view = LayoutInflater.from(StartTrip.this).inflate(R.layout.item_drop_trip, null);
        final EditText edtReading = view.findViewById(R.id.edtReading);
        final EditText edtTax = view.findViewById(R.id.edtTax);
        final EditText edtNight = view.findViewById(R.id.edtNight);
        builder.setView(view);

        builder.setPositiveButton("Drop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edtReading.getText().toString().replaceAll(" ", "").length() == 0) {
                    edtReading.setError("Enter Cab Meter Reading");
                    edtReading.requestFocus();
                    return;
                }

                if (edtNight.getText().toString().replaceAll(" ", "").length() == 0 || edtNight.getText().toString().contains("-")) {
                    edtNight.setError("Enter Night Stay");
                    edtNight.requestFocus();
                    return;
                }

                if (Integer.parseInt(edtReading.getText().toString()) < Integer.parseInt(Common.trip.getPickUpMeter()) || edtReading.getText().toString().contains("-")) {
                    edtReading.setError("Enter Cab Meter Reading");
                    edtReading.requestFocus();
                    return;
                }

                if (edtTax.getText().toString().replaceAll(" ", "").length() == 0 || edtTax.getText().toString().contains("-")) {
                    edtTax.setError("Enter Tax");
                    edtTax.requestFocus();
                    return;
                }

                Date dt = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String currentTime = sdf.format(dt);
                mService.updateTripData("4", Common.trip.getId(), edtTax.getText().toString(),
                        "2", currentTime, edtReading.getText().toString(), edtNight.getText().toString())
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                assert response.body() != null;
                                if (response.body().equals("OK")) {
                                    sendNotificationToUser(Common.trip.getBookAccount(), Common.trip.getId(), edtReading.getText().toString());
                                    Common.id = Common.trip.getId();
                                    finish();
                                    Paper.book().delete("TripId");
                                    Paper.book().delete("TripPhone");
                                    startActivity(new Intent(StartTrip.this, InvoiceActivity.class));
                                } else {
                                    Toast.makeText(StartTrip.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d("ERROR", t.getMessage());
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    @Override
    protected void onPause() {
        if (locationCallback != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        mMap.clear();
        super.onPause();

    }

    @Override
    protected void onStop() {
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        mMap.clear();
        super.onStop();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }

    private void sendNotificationToUser(String phone, final String id, final String Km) {
        Log.d("ERROR", phone);
        mService.getToken(phone, "0")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        //when we have Token , just send notification to this token

                        Map<String, String> contentSend = new HashMap<>();
                        contentSend.put("title", "Ride Completed");
                        contentSend.put("message", "Your ride is completed at " + Km + "KM");
                        contentSend.put("code", "invoice");
                        contentSend.put("Phone3", Common.currentDriver.getPhone());
                        contentSend.put("id", id);
                        DataMessage dataMessage = new DataMessage();
                        if (response.body().getToken() != null)
                            dataMessage.setTo(response.body().getToken());
                        dataMessage.setData(contentSend);

                        IFCMService ifcmService = Common.getGetFCMService();
                        ifcmService.sendNotification(dataMessage)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success == 1) {
                                                Log.d("ERROR", new Gson().toJson(response.body()));
                                                Toast.makeText(StartTrip.this, "Notification send", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("ERROR", new Gson().toJson(response.body()));
                                                Toast.makeText(StartTrip.this, "Notification send failed ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Toast.makeText(StartTrip.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
//                            Log.d("ERROR",t.getMessage());
                        Toast.makeText(StartTrip.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (locationCallback != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        /*register connection status listener*/
        MyApplication.getInstance().setConnectivityListener(this);
//        // register connection status listener
//        MyApplication.getInstance().setConnectivityListener(this);
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


    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(StartTrip.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


}
