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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.urdriver.urdriver.Adapter.TripAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.Trip;
import com.urdriver.urdriver.model.UserDetails;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class InfoUserForAdmin extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    TextView txtName, txtEmail, txtPhoneNumber, txtTrip;
    IURDriver mService;
    Toolbar toolbar;
    RecyclerView recycler_trips;

    ConnectivityReceiver connectivityReceiver;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user_for_admin);

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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtTrip = findViewById(R.id.txtTrips);
        recycler_trips = findViewById(R.id.recycler_trips);
        recycler_trips.setLayoutManager(new LinearLayoutManager(this));
        recycler_trips.setHasFixedSize(true);

        mService = Common.getAPI();

        if (getIntent().getStringExtra("PHONE") != null) {
            userDetails = new Gson().fromJson(getIntent().getStringExtra("PHONE"), UserDetails.class);
            txtName.setText(userDetails.getName());
            txtEmail.setText(userDetails.getEmail());
            txtPhoneNumber.setText(userDetails.getPhone());
            compositeDisposable.add(mService.getTripDataUser("2", userDetails.getPhone())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Trip>>() {
                        @Override
                        public void accept(List<Trip> trips) throws Exception {
                            if (trips.size() != 0) {
                                txtTrip.setText("" + trips.size());
                                TripAdapter tripAdapter = new TripAdapter(InfoUserForAdmin.this, trips);
                                recycler_trips.setAdapter(tripAdapter);
                            } else {
                                txtTrip.setText("0");
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("Error", throwable.getMessage());
                            Toast.makeText(InfoUserForAdmin.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }));
        }
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
    protected void onResume() {
        super.onResume();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }
}
