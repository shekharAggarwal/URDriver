package com.urdriver.urdriver;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.nex3z.notificationbadge.NotificationBadge;
import com.urdriver.urdriver.Adapter.PagerViewAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.Trip;
import com.urdriver.urdriver.model.cabDetails;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.Calendar;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    TextView today, upcoming;
    ViewPager viewPager;
    ConnectivityReceiver connectivityReceiver;
    PagerViewAdapter pagerViewAdapter;
    Toolbar toolbar;
    MenuItem menuItem;
    BottomNavigationView navView;
    Menu menu;
    IURDriver mService = Common.getAPI();
    ImageView notification_icon;
    SwitchCompat switch_layout;
    NotificationBadge badge;
    int Noti = 0;
    String date;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_schedule:
//                    Toast.makeText(ScheduleActivity.this, "schedule", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_myTrip:
                    Intent intent = new Intent(ScheduleActivity.this, MyTripActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                case R.id.navigation_map:
                    String Id = Paper.book().read("TripId");
                    if (Id != null) {
                        Intent intent2 = new Intent(ScheduleActivity.this, StartTrip.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent2);
                    } else {
                        Toast.makeText(ScheduleActivity.this, "No Trip Is Started", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.navigation_profile:
                    Intent intent1 = new Intent(ScheduleActivity.this, YourAccount.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Paper.init(ScheduleActivity.this);
        Common.setTop(this);

        toolbar = findViewById(R.id.toolbar);
        Common.terms = findViewById(R.id.terms);
        setSupportActionBar(toolbar);
        today = findViewById(R.id.today);
        upcoming = findViewById(R.id.upcoming);
        viewPager = findViewById(R.id.pagerView);

        if (Common.currentDriver == null) {
            String email = Paper.book().read("email");
            String password = Paper.book().read("password");

            mService.getDriverInfo(email, password)
                    .enqueue(new Callback<Driver>() {
                        @Override
                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                            Common.currentDriver = response.body();
                            assert Common.currentDriver != null;
                            if (Common.currentDriver.getError_msg() == null) {
                                if (Common.currentDriver != null)
                                    mService.getcabbydriver(Common.currentDriver.getPhone()).enqueue(new Callback<cabDetails>() {
                                        @Override
                                        public void onResponse(Call<cabDetails> call, Response<cabDetails> response) {
                                            Common.cabDetail = response.body();
                                            mService.getDriverTripDetail(Common.currentDriver.getPhone(), "4")
                                                    .enqueue(new Callback<Trip>() {
                                                        @Override
                                                        public void onResponse(Call<Trip> call, Response<Trip> response) {
                                                            if (response.body() != null) {
                                                                if (response.body().getTripStatus().equals("3")) {
                                                                    Paper.book().write("TripId", response.body().getId());
                                                                    Paper.book().write("TripPhone", Common.currentDriver.getPhone());
                                                                }

                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Trip> call, Throwable t) {
                                                            Log.d("ERROR", t.getMessage());
//                                                            Toast.makeText(ScheduleActivity.this, "Error in fetching data from server", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onFailure(Call<cabDetails> call, Throwable t) {
                                            Log.d("Error", t.getMessage());
                                        }
                                    });
                                updateToken();
                                final String Id = Paper.book().read("TripId");
                                final String TripPhone = Paper.book().read("TripPhone");
                                if (Common.currentDriver.getPhone().equals(TripPhone))
                                    if (Id != null) {
                                        Intent intent2 = new Intent(ScheduleActivity.this, StartTrip.class);
                                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent2);
                                    }
                            }
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("Error", t.getMessage());
//                        recreate();
                        }
                    });
        } else {
            updateToken();
            if (Common.currentDriver != null)
                mService.getcabbydriver(Common.currentDriver.getPhone()).enqueue(new Callback<cabDetails>() {
                    @Override
                    public void onResponse(Call<cabDetails> call, Response<cabDetails> response) {
                        Common.cabDetail = response.body();
                        mService.getDriverTripDetail(Common.currentDriver.getPhone(), "4")
                                .enqueue(new Callback<Trip>() {
                                    @Override
                                    public void onResponse(Call<Trip> call, Response<Trip> response) {
                                        if (response.body() != null) {
                                            if (response.body().getTripStatus().equals("3")) {
                                                Paper.book().write("TripId", response.body().getId());
                                                Paper.book().write("TripPhone", Common.currentDriver.getPhone());
                                            }

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Trip> call, Throwable t) {
                                        Log.d("ERROR", t.getMessage());
//                                        Toast.makeText(ScheduleActivity.this, "Error in fetching data from server", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<cabDetails> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                    }
                });
            final String Id = Paper.book().read("TripId");
            final String TripPhone = Paper.book().read("TripPhone");
            if (Common.currentDriver.getPhone().equals(TripPhone))
                if (Id != null) {
                    Intent intent2 = new Intent(ScheduleActivity.this, StartTrip.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent2);
                }

        }

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        menu = navView.getMenu();
        menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerViewAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onChangeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });

        upcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });

    }

    private void onChangeTab(int position) {
        if (position == 0) {
            today.setBackgroundResource(R.drawable.cab_tab_left_background);
            upcoming.setBackgroundResource(R.drawable.cab_tab_left_background_with_shadow);

        }
        if (position == 1) {
            upcoming.setBackgroundResource(R.drawable.cab_tab_right_background);
            today.setBackgroundResource(R.drawable.cab_tab_background_with_shadow);

        }
    }

    private void updateToken() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        IURDriver mService = Common.getAPI();
                        mService.updateToken(Common.currentDriver.getPhone(),
                                instanceIdResult.getToken(),
                                "1")
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        Log.d("DEBUG2", response.body());
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.d("DEBUG1", t.getMessage());

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScheduleActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        View view = menu.findItem(R.id.action_notification).getActionView();
        View s = menu.findItem(R.id.myswitch).getActionView();
        badge = view.findViewById(R.id.badge);
        updateNotification();
        switch_layout = s.findViewById(R.id.switchForActionBar);
        if (Common.currentDriver.getDriverStatus() == 1)
            switch_layout.setChecked(true);
        else if (Common.currentDriver.getDriverStatus() == 5)
            switch_layout.setChecked(false);
        switch_layout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mService.UpdateDriverOnOff("1", Common.currentDriver.getPhone())
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.body() != null)
                                        if (response.body().equals("ok")) {
                                            Common.currentDriver.setDriverStatus(1);
                                            Toast.makeText(ScheduleActivity.this, "Now you are online", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ScheduleActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                        }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("ERROR", t.getMessage());
                                }
                            });
                } else {
                    mService.UpdateDriverOnOff("5", Common.currentDriver.getPhone())
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.body() != null)
                                        if (response.body().equals("ok")) {
                                            Common.currentDriver.setDriverStatus(5);
                                            Toast.makeText(ScheduleActivity.this, "Now you are offline", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ScheduleActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                        }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("ERROR", t.getMessage());
                                }
                            });
                }
            }
        });
        notification_icon = view.findViewById(R.id.img_notification);
        notification_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScheduleActivity.this, NotificationActivity.class));
            }
        });
        return true;

    }

    private void updateNotification() {

        if (badge == null) return;
        Noti = 0;
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (month < 10)
            date = calendar.get(Calendar.YEAR) + "-" + "0" + month + "-" + day;
        else
            date = calendar.get(Calendar.YEAR) + "-" + month + "-" + day;

        mService.getCount(Common.currentDriver.getPhone(), "0", date).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null)
                    Noti = Noti + Integer.parseInt(response.body());
                getCountNotification();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                getCountNotification();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        menu = navView.getMenu();
        menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        updateNotification();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        MyApplication.getInstance().setConnectivityListener(this);

        viewPager.removeAllViews();
        pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerViewAdapter);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getCountNotification() {
        mService.getCount(Common.currentDriver.getPhone(), "1", date).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null)
                    Noti = Noti + Integer.parseInt(response.body());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Noti == 0) {
                            badge.setVisibility(View.INVISIBLE);
                        } else {
                            badge.setVisibility(View.VISIBLE);
                            badge.setText(String.valueOf(Noti));
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
        viewPager.removeAllViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewPager.removeAllViews();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }
}
