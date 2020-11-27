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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.urdriver.urdriver.Adapter.RefundAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.Trip;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.List;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelledCabActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Toolbar toolbar;
    RecyclerView recycler_trip;
    IURDriver mService = Common.getAPI();
    ConnectivityReceiver connectivityReceiver;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    LinearLayout ln1;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_cab);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Common.setTop(this);

        ln1 = findViewById(R.id.ln1);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        recycler_trip = findViewById(R.id.recycler_trip);
        recycler_trip.setLayoutManager(new LinearLayoutManager(this));
        recycler_trip.setHasFixedSize(true);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                if (Common.currentDriver == null) {
                    String email = Paper.book().read("email");
                    String password = Paper.book().read("password");

                    mService.getDriverInfo(email, password).enqueue(new Callback<Driver>() {
                        @Override
                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                            Common.currentDriver = response.body();
                            loadTrip();
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            Toast.makeText(CancelledCabActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loadTrip();
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if (Common.currentDriver == null) {
                    String email = Paper.book().read("email");
                    String password = Paper.book().read("password");

                    mService.getDriverInfo(email, password).enqueue(new Callback<Driver>() {
                        @Override
                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                            Common.currentDriver = response.body();
                            loadTrip();
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            Toast.makeText(CancelledCabActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loadTrip();
                }
            }
        });

    }

    private void loadTrip() {
        compositeDisposable.add(mService.getCancelCab(1, Common.currentDriver.getPhone())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Trip>>() {
                    @Override
                    public void accept(List<Trip> trips) throws Exception {
                        swipeRefreshLayout.setRefreshing(false);
                        RefundAdapter refundAdapter = new RefundAdapter(CancelledCabActivity.this, trips, getSupportFragmentManager());
                        recycler_trip.setAdapter(refundAdapter);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        recycler_trip.setVisibility(View.GONE);
                        ln1.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        Log.d("Error", throwable.getMessage());
                    }
                }));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

            findViewById(R.id.btnTry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recreate();

                }
            });
        }
    }

}
