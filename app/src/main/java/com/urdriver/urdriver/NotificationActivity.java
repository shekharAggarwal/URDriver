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

import com.urdriver.urdriver.Adapter.NotificationAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.RequestData;
import com.urdriver.urdriver.model.RequestDataOneWay;
import com.urdriver.urdriver.model.RequestDataRoundWay;
import com.urdriver.urdriver.retrofit.IURDriver;

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

public class NotificationActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    RecyclerView recycler_notification;
    List<RequestData> requestData = new ArrayList<>();
    IURDriver mService = Common.getAPI();
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Calendar calendar = Calendar.getInstance();
    Toolbar toolbar;
    ConnectivityReceiver connectivityReceiver;
    String date;
    int Noti = 0;
    boolean isCheck = true;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout ln2;

    int c1 = 0, c2 = 0, c3 = 0, c4 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

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

//        Common.setBack(this);
        recycler_notification = findViewById(R.id.recycler_notification);
        recycler_notification.setLayoutManager(new LinearLayoutManager(this));
        recycler_notification.setHasFixedSize(true);
        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        ln2 = findViewById(R.id.ln2);

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
                            getNotification();

                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            Toast.makeText(NotificationActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    getNotification();

                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                c1 = 0;
                c2 = 0;
                c3 = 0;
                c4 = 0;
                if (Common.currentDriver == null) {
                    String email = Paper.book().read("email");
                    String password = Paper.book().read("password");

                    mService.getDriverInfo(email, password).enqueue(new Callback<Driver>() {
                        @Override
                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                            Common.currentDriver = response.body();
                            getNotification();

                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            Toast.makeText(NotificationActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    getNotification();

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
        if (!connectivityReceiver.isOrderedBroadcast()) {
//            unregisterReceiver(connectivityReceiver);
            registerReceiver(connectivityReceiver, intentFilter);
        } else {
            unregisterReceiver(connectivityReceiver);
            registerReceiver(connectivityReceiver, intentFilter);
        }

        MyApplication.getInstance().setConnectivityListener(this);
        getNotification();
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

    private void getOneWayNotSeen() {
        if (c1 == 0) {
            c1 = 1;
            recycler_notification.removeAllViews();
            requestData.clear();
            requestData = new ArrayList<>();

            compositeDisposable.add(mService.getRequestDataOneWay(Common.currentDriver.getPhone(), date, 0, 4)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<RequestDataOneWay>>() {
                        @Override
                        public void accept(List<RequestDataOneWay> requestDataOneWays) throws Exception {
                            for (int i = 0; i < requestDataOneWays.size(); i++) {
                                RequestData request = new RequestData(requestDataOneWays.get(i).getId(),
                                        requestDataOneWays.get(i).getFullName(),
                                        requestDataOneWays.get(i).getPhoneNumber(),
                                        requestDataOneWays.get(i).getEmail(),
                                        requestDataOneWays.get(i).getSourceAddress(),
                                        requestDataOneWays.get(i).getDestinationAddress(),
                                        requestDataOneWays.get(i).getPickupDate(),
                                        "0000-00-00",
                                        requestDataOneWays.get(i).getPickupTime(),
                                        requestDataOneWays.get(i).getSource(),
                                        requestDataOneWays.get(i).getDestination(),
                                        requestDataOneWays.get(i).getCabs(),
                                        requestDataOneWays.get(i).getBookAccount(),
                                        requestDataOneWays.get(i).getCabFare(),
                                        requestDataOneWays.get(i).getCabDriver(),
                                        requestDataOneWays.get(i).getCabStatus(),
                                        requestDataOneWays.get(i).getCabModel(),
                                        requestDataOneWays.get(i).getCabTnxId(),
                                        "0",
                                        requestDataOneWays.get(i).getRequestTime());
                                requestData.add(request);
                            }
                            getOneWayNotAccepted();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            getOneWayNotAccepted();
                        }
                    }));
        }
    }

    private void getOneWayNotAccepted() {
        if (c2 == 0) {
            c2 = 1;
            compositeDisposable.add(mService.getRequestDataOneWay(Common.currentDriver.getPhone(), date, 2, 4)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<RequestDataOneWay>>() {
                        @Override
                        public void accept(List<RequestDataOneWay> requestDataOneWays) throws Exception {
                            for (int i = 0; i < requestDataOneWays.size(); i++) {
                                RequestData request = new RequestData(requestDataOneWays.get(i).getId(),
                                        requestDataOneWays.get(i).getFullName(),
                                        requestDataOneWays.get(i).getPhoneNumber(),
                                        requestDataOneWays.get(i).getEmail(),
                                        requestDataOneWays.get(i).getSourceAddress(),
                                        requestDataOneWays.get(i).getDestinationAddress(),
                                        requestDataOneWays.get(i).getPickupDate(),
                                        "0000-00-00",
                                        requestDataOneWays.get(i).getPickupTime(),
                                        requestDataOneWays.get(i).getSource(),
                                        requestDataOneWays.get(i).getDestination(),
                                        requestDataOneWays.get(i).getCabs(),
                                        requestDataOneWays.get(i).getBookAccount(),
                                        requestDataOneWays.get(i).getCabFare(),
                                        requestDataOneWays.get(i).getCabDriver(),
                                        requestDataOneWays.get(i).getCabStatus(),
                                        requestDataOneWays.get(i).getCabModel(),
                                        requestDataOneWays.get(i).getCabTnxId(),
                                        "0",
                                        requestDataOneWays.get(i).getRequestTime());
                                requestData.add(request);
                            }
                            getRoundWayNotAccepted();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            getRoundWayNotAccepted();
                        }
                    }));
        }
    }

    private void getRoundWayNotAccepted() {
        if (c3 == 0) {
            c3 = 1;
            compositeDisposable.add(mService.getRequestDataRoundWay(Common.currentDriver.getPhone(), date, 2, 5)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<RequestDataRoundWay>>() {
                        @Override
                        public void accept(List<RequestDataRoundWay> requestDataRoundWays) throws Exception {
                            for (int i = 0; i < requestDataRoundWays.size(); i++) {
                                RequestData request = new RequestData(requestDataRoundWays.get(i).getId(),
                                        requestDataRoundWays.get(i).getFullName(),
                                        requestDataRoundWays.get(i).getPhoneNumber(),
                                        requestDataRoundWays.get(i).getEmail(),
                                        requestDataRoundWays.get(i).getSourceAddress(),
                                        requestDataRoundWays.get(i).getDestinationAddress(),
                                        requestDataRoundWays.get(i).getPickupDate(),
                                        requestDataRoundWays.get(i).getDropDate(),
                                        requestDataRoundWays.get(i).getPickupTime(),
                                        requestDataRoundWays.get(i).getSource(),
                                        requestDataRoundWays.get(i).getDestination(),
                                        requestDataRoundWays.get(i).getCabs(),
                                        requestDataRoundWays.get(i).getBookAccount(),
                                        requestDataRoundWays.get(i).getCabFare(),
                                        requestDataRoundWays.get(i).getCabDriver(),
                                        requestDataRoundWays.get(i).getCabStatus(),
                                        requestDataRoundWays.get(i).getCabModel(),
                                        requestDataRoundWays.get(i).getCabTnxId(),
                                        "1",
                                        requestDataRoundWays.get(i).getRequestTime());
                                requestData.add(request);
                            }
                            getRoundWayNotSeen();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            getRoundWayNotSeen();
                        }
                    }));
        }
    }

    private void getRoundWayNotSeen() {
        if (c4 == 0) {
            c4 = 1;
            compositeDisposable.add(mService.getRequestDataRoundWay(Common.currentDriver.getPhone(), date, 0, 5)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<RequestDataRoundWay>>() {
                                   @Override
                                   public void accept(List<RequestDataRoundWay> requestDataRoundWays) throws Exception {
                                       for (int i = 0; i < requestDataRoundWays.size(); i++) {
                                           RequestData request = new RequestData(requestDataRoundWays.get(i).getId(),
                                                   requestDataRoundWays.get(i).getFullName(),
                                                   requestDataRoundWays.get(i).getPhoneNumber(),
                                                   requestDataRoundWays.get(i).getEmail(),
                                                   requestDataRoundWays.get(i).getSourceAddress(),
                                                   requestDataRoundWays.get(i).getDestinationAddress(),
                                                   requestDataRoundWays.get(i).getPickupDate(),
                                                   requestDataRoundWays.get(i).getDropDate(),
                                                   requestDataRoundWays.get(i).getPickupTime(),
                                                   requestDataRoundWays.get(i).getSource(),
                                                   requestDataRoundWays.get(i).getDestination(),
                                                   requestDataRoundWays.get(i).getCabs(),
                                                   requestDataRoundWays.get(i).getBookAccount(),
                                                   requestDataRoundWays.get(i).getCabFare(),
                                                   requestDataRoundWays.get(i).getCabDriver(),
                                                   requestDataRoundWays.get(i).getCabStatus(),
                                                   requestDataRoundWays.get(i).getCabModel(),
                                                   requestDataRoundWays.get(i).getCabTnxId(),
                                                   "1",
                                                   requestDataRoundWays.get(i).getRequestTime());
                                           requestData.add(request);
                                       }
                                       swipeRefreshLayout.setRefreshing(false);
                                       NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationActivity.this, requestData);
                                       recycler_notification.setAdapter(notificationAdapter);
                                   }
                               },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Log.d("ERROR", throwable.getMessage());
                                    swipeRefreshLayout.setRefreshing(false);
                                    NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationActivity.this, requestData);
                                    recycler_notification.setAdapter(notificationAdapter);
                                }
                            }));
        }
    }

    private void getNotification() {
        Noti = 0;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;

        int month1 = calendar.get(Calendar.MONTH) + 1;
        int day1 = calendar.get(Calendar.DAY_OF_MONTH);
        String date1;
        if (month < 10) {
            date = calendar.get(Calendar.YEAR) + "-" + "0" + month + "-" + day;
            date1 = calendar.get(Calendar.YEAR) + "-" + "0" + month1 + "-" + day1;
        } else {
            date = calendar.get(Calendar.YEAR) + "-" + month + "-" + day;
            date1 = calendar.get(Calendar.YEAR) + "-" + month1 + "-" + day1;
        }
        Log.d("date", date);
        Log.d("date", Common.currentDriver.getPhone());

        mService.getCount(Common.currentDriver.getPhone(), "0", date).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Noti = Noti + Integer.parseInt(response.body());
                getRoundWayNotification();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Error", t.getMessage());
                getRoundWayNotification();
            }
        });
    }

    private void getRoundWayNotification() {
        mService.getCount(Common.currentDriver.getPhone(), "1", date).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Noti = Noti + Integer.parseInt(response.body());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Noti == 0) {
                            ln2.setVisibility(View.VISIBLE);
                            recycler_notification.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            ln2.setVisibility(View.GONE);
                            recycler_notification.setVisibility(View.VISIBLE);
                            if (isCheck)
                                getOneWayNotSeen();
                            else {
                                isCheck = false;
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
