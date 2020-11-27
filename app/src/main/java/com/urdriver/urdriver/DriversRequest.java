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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.urdriver.urdriver.Adapter.PagerViewAdminAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.retrofit.IURDriver;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriversRequest extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Toolbar toolbar;
    TextView one_way, round_way;
    ViewPager viewPager;
    PagerViewAdminAdapter pagerViewAdminAdapter;

    IURDriver mService;

    int t = 0;
    ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_request);

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
        updateToken();
        one_way = findViewById(R.id.one);
        round_way = findViewById(R.id.round);
        viewPager = findViewById(R.id.pagerView);

        pagerViewAdminAdapter = new PagerViewAdminAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerViewAdminAdapter);

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

        one_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });

        round_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        MyApplication.getInstance().setConnectivityListener(this);
        viewPager.removeAllViews();
        pagerViewAdminAdapter = new PagerViewAdminAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerViewAdminAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewPager.removeAllViews();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu_admin, menu);
        MenuItem view = menu.findItem(R.id.action_logout);
        MenuItem changePassword = menu.findItem(R.id.action_change_password);
        MenuItem cancelCab = menu.findItem(R.id.action_cancelled_cab);
        MenuItem btnDriver = menu.findItem(R.id.action_cab_driver);
        MenuItem btnUser = menu.findItem(R.id.action_user_data);
        view.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Paper.init(DriversRequest.this);
                Paper.book().delete("email");
                Paper.book().delete("password");
                Intent intent = new Intent(DriversRequest.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
                return true;
            }
        });
        cancelCab.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(DriversRequest.this, CancelCabActivity.class);
                startActivity(intent);
                return true;
            }
        });
        changePassword.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Common.AuthPhone = Common.currentDriver.getPhone();
                Common.fromActivity = "cpwd";
                startActivity(new Intent(DriversRequest.this, ChangePasswordActivity.class));
                return true;
            }
        });

        btnDriver.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(DriversRequest.this, InfoActivity.class);
                intent.putExtra("TEXT", "DRIVER");
                startActivity(intent);
                return true;
            }
        });
        btnUser.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(DriversRequest.this, InfoActivity.class);
                intent.putExtra("TEXT", "USER");
                startActivity(intent);
                return true;
            }
        });
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
//            compositeDisposable.dispose();
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
        viewPager.removeAllViews();
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
                        Toast.makeText(DriversRequest.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }

    private void onChangeTab(int position) {
        if (position == 0) {
            one_way.setBackgroundResource(R.drawable.cab_tab_left_background);
            round_way.setBackgroundResource(R.drawable.cab_tab_left_background_with_shadow);

        }
        if (position == 1) {
            round_way.setBackgroundResource(R.drawable.cab_tab_right_background);
            one_way.setBackgroundResource(R.drawable.cab_tab_background_with_shadow);

        }
    }

}
