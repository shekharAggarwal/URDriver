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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.urdriver.urdriver.Adapter.TripAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.Trip;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTripActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Toolbar toolbar;
    RecyclerView recycler_trip;
    IURDriver mService = Common.getAPI();
    TextView txtRids;
    String[] time = {"All", "This Year", "6 Months", "This Month", "This Week"};
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MaterialSpinner timeSpinner;
    List<Trip> tripList = new ArrayList<>();
    ConnectivityReceiver connectivityReceiver;
    SwipeRefreshLayout swipeRefreshLayout;
    String newDate;
    LinearLayout ln1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Common.setTop(this);

        txtRids = findViewById(R.id.txtRids);
        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);

        timeSpinner = findViewById(R.id.timeSpinner);
        timeSpinner.setItems(time);

        ln1 = findViewById(R.id.ln1);

        timeSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position == 0) {
                    loadTrip();
                } else if (position == 1) {
                    loadTrip(365);
                } else if (position == 2) {
                    loadTrip(180);
                } else if (position == 3) {
                    loadTrip(30);
                } else if (position == 4) {
                    loadTrip(7);
                } else
                    loadTrip();
            }
        });

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
                            timeSpinner.setSelectedIndex(0);
                            loadTrip();
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            Toast.makeText(MyTripActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    timeSpinner.setSelectedIndex(0);
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
                            timeSpinner.setSelectedIndex(0);
                            loadTrip();
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            Toast.makeText(MyTripActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    timeSpinner.setSelectedIndex(0);
                    loadTrip();
                }
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recycler_trip = findViewById(R.id.recycler_trip);
        recycler_trip.setLayoutManager(new LinearLayoutManager(this));
        recycler_trip.setHasFixedSize(true);

    }

    private void loadTrip(int day) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        //Number of Days to add
        c.add(Calendar.DAY_OF_MONTH, -day);
        //Date after adding the days to the given date
        newDate = sdf.format(c.getTime());
        swipeRefreshLayout.setRefreshing(true);
        tripList = new ArrayList<>();
        compositeDisposable.add(mService.getTripData("1", newDate, Common.currentDriver.getPhone())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Trip>>() {
                    @Override
                    public void accept(List<Trip> trips) throws Exception {

                        txtRids.setText(String.valueOf(trips.size()));
                        tripList = trips;
                        swipeRefreshLayout.setRefreshing(false);
                        recycler_trip.removeAllViews();
                        if (trips.size() == 0) {
                            recycler_trip.setVisibility(View.GONE);
                            ln1.setVisibility(View.VISIBLE);
                        } else {
                            recycler_trip.setVisibility(View.VISIBLE);
                            ln1.setVisibility(View.GONE);
                        }
                        TripAdapter tripAdapter = new TripAdapter(MyTripActivity.this, tripList);
                        recycler_trip.setAdapter(tripAdapter);

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

    private void loadTrip() {
        recycler_trip.removeAllViews();
        swipeRefreshLayout.setRefreshing(true);
        compositeDisposable.add(mService.getTripData("3", Common.currentDriver.getPhone())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Trip>>() {
                    @Override
                    public void accept(List<Trip> trips) throws Exception {
                        txtRids.setText(String.valueOf(trips.size()));
                        swipeRefreshLayout.setRefreshing(false);
                        if (trips.size() == 0) {
                            recycler_trip.setVisibility(View.GONE);
                            ln1.setVisibility(View.VISIBLE);
                        } else {
                            recycler_trip.setVisibility(View.VISIBLE);
                            ln1.setVisibility(View.GONE);
                        }
                        TripAdapter tripAdapter = new TripAdapter(MyTripActivity.this, trips);
                        recycler_trip.setAdapter(tripAdapter);
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
}
