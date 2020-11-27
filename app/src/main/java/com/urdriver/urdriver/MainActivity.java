package com.urdriver.urdriver;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.retrofit.IURDriver;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    ImageView logo;
    IURDriver mService;
    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSION = 1001;
    ConnectivityReceiver connectivityReceiver;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Common.setBack(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION);
        }
        mService = Common.getAPI();
        Paper.init(this);

        logo = findViewById(R.id.logo);

        final ObjectAnimator animation = ObjectAnimator.ofFloat(logo, "rotationY", 0.0f, 360f);
        animation.setDuration(3600);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

        String email = Paper.book().read("email");
        String password = Paper.book().read("password");

        if (email != null && password != null)
            mService.getDriverInfo(email, password)
                    .enqueue(new Callback<Driver>() {
                        @Override
                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                            Common.currentDriver = response.body();
                            Log.d("ERROR", new Gson().toJson(Common.currentDriver));
                            if (Common.currentDriver != null)
                                if (Common.currentDriver.getError_msg() == null) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            animation.cancel();
                                            if (Common.currentDriver.getDriverStatus() == 10) {
                                                finish();
                                                startActivity(new Intent(MainActivity.this, DriversRequest.class));
                                            } else if (Common.currentDriver.getDriverStatus() == 1 || Common.currentDriver.getDriverStatus() == 5) {
                                                startActivitySchedule();
                                            } else if (Common.currentDriver.getDriverStatus() == 0) {
                                                Paper.book().delete("password");
                                                Paper.book().delete("email");
                                                Toast.makeText(MainActivity.this, "Account is not verified", Toast.LENGTH_SHORT).show();
                                                animation.cancel();
                                                Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                            } else if (Common.currentDriver.getDriverStatus() == 2) {
                                                Paper.book().delete("password");
                                                Paper.book().delete("email");
                                                Toast.makeText(MainActivity.this, "Account is not Exists", Toast.LENGTH_SHORT).show();
                                                animation.cancel();
                                                Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                                animation.cancel();
                                                Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                            }
                                        }
                                    }, 3600);
                                } else {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            animation.cancel();
                                            Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                                            startActivity(mainIntent);
                                            finish();
                                        }
                                    }, 3600);
                                }
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("Error", t.getMessage());
//                        recreate();
                        }
                    });
        else
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animation.cancel();
                    Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }, 3600);
    }

    private void startActivitySchedule() {
        Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
        finish();
        startActivity(intent);
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
