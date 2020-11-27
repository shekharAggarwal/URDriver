package com.urdriver.urdriver;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;

import io.paperdb.Paper;

public class YourAccount extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    ConnectivityReceiver connectivityReceiver;
    TextView txtName, txtPhone, txtEmail;
    ImageView ic_profile, UserReview, updateCab, changePassword, cancelCab;
    Toolbar toolbar;
    MenuItem menuItem;
    BottomNavigationView navView;
    Menu menu;
    LinearLayout logout;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_schedule:
                    Intent intent1 = new Intent(YourAccount.this, ScheduleActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    return true;
                case R.id.navigation_myTrip:
                    Intent intent = new Intent(YourAccount.this, MyTripActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                case R.id.navigation_map:
                    String Id = Paper.book().read("TripId");
                    if (Id != null) {
                        Intent intent2 = new Intent(YourAccount.this, StartTrip.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent2);
                    } else {
                        Toast.makeText(YourAccount.this, "No Trip Is Started", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.navigation_profile:
//                    Toast.makeText(YourAccount.this, "Account", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_account);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Common.setTop(this);

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        updateCab = findViewById(R.id.updateCab);
        ic_profile = findViewById(R.id.ic_profile);
        UserReview = findViewById(R.id.UserReview);
        cancelCab = findViewById(R.id.cancelCab);
        logout = findViewById(R.id.logout);
        changePassword = findViewById(R.id.changePassword);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        menu = navView.getMenu();
        menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        Paper.init(this);

        if (Common.currentDriver != null) {
            txtName.setText(Common.currentDriver.getName());
            txtPhone.setText(Common.currentDriver.getPhone());
            txtEmail.setText(Common.currentDriver.getEmail());
        }
        ic_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(YourAccount.this, ProfileActivity.class));
            }
        });
        UserReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(YourAccount.this, ViewRating.class));
            }
        });

        cancelCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(YourAccount.this, CancelledCabActivity.class));
            }
        });

        updateCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(YourAccount.this, UpdateCab.class));
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.fromActivity = "cpwd";
                Common.AuthPhone = Common.currentDriver.getPhone();
                startActivity(new Intent(YourAccount.this, ChangePasswordActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().delete("email");
                Paper.book().delete("password");
                Paper.book().destroy();
                Intent intent = new Intent(YourAccount.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        menu = navView.getMenu();
        menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        MyApplication.getInstance().setConnectivityListener(this);
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
}
