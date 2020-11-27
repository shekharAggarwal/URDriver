package com.urdriver.urdriver;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.retrofit.IURDriver;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText edtUserEmail, edtPassword;
    ImageView logo;
    Button btnLogin;
    TextView txtForgetPassword, txtCreateAccount;
    IURDriver mService;

    ConnectivityReceiver connectivityReceiver;
    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Common.setBack(this);

        Paper.init(this);

        mService = Common.getAPI();

        edtUserEmail = findViewById(R.id.edtUserEmail);
        edtPassword = findViewById(R.id.edtPassword);
        logo = findViewById(R.id.logo);
        rootLayout = findViewById(R.id.root_layout);
        btnLogin = findViewById(R.id.btnLogin);
        txtForgetPassword = findViewById(R.id.txtForgetPassword);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtUserEmail.getText().toString().trim().isEmpty()
                        || !edtUserEmail.getText().toString().contains("@")) {
                    edtUserEmail.setError("check your registered Email");
                    edtUserEmail.requestFocus();
                    btnLogin.setEnabled(true);
                    return;
                }
                if (edtPassword.getText().toString().trim().isEmpty()
                        || edtPassword.getText().toString().trim().replaceAll(" ", "").isEmpty() ||
                        edtPassword.getText().toString().trim().length() < 6) {
                    edtPassword.setError("check your Password");
                    edtPassword.requestFocus();
                    btnLogin.setEnabled(true);
                    return;
                }
                if (!edtUserEmail.getText().toString().isEmpty() && !edtPassword.getText().toString().isEmpty())
                    LoginUser();
            }
        });


        txtForgetPassword.setOnClickListener(view -> {

            Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(logo, "imageTransition");
            ActivityOptions options;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, options.toBundle());
            } else
                startActivity(intent);

        });

        txtCreateAccount.setOnClickListener(view -> {

            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);

        });
    }

    private void LoginUser() {
        mService.getDriverInfo(edtUserEmail.getText().toString(), edtPassword.getText().toString())
                .enqueue(new Callback<Driver>() {
                    @Override
                    public void onResponse(Call<Driver> call, Response<Driver> response) {
                        Common.currentDriver = response.body();
                        if (Common.currentDriver != null) {
                            if (Common.currentDriver.getError_msg() == null) {
                                if (Common.currentDriver.getDriverStatus() == 10) {
                                    Paper.book().write("email", edtUserEmail.getText().toString());
                                    Paper.book().write("password", edtPassword.getText().toString());
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, DriversRequest.class));
                                } else if (Common.currentDriver.getDriverStatus() == 1 || Common.currentDriver.getDriverStatus() == 5) {
                                    Paper.book().write("email", edtUserEmail.getText().toString());
                                    Paper.book().write("password", edtPassword.getText().toString());
                                    startActivitySchedule();
                                } else if (Common.currentDriver.getDriverStatus() == 0) {
                                    Toast.makeText(LoginActivity.this, "Account is not verified", Toast.LENGTH_SHORT).show();
                                } else if (Common.currentDriver.getDriverStatus() == 2) {
                                    Toast.makeText(LoginActivity.this, "Account is not Exists", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(rootLayout, "" + Common.currentDriver.getError_msg(),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(rootLayout, "Error occurred try again!",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Driver> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void startActivitySchedule() {
        Intent intent = new Intent(LoginActivity.this, ScheduleActivity.class);
        finish();
        startActivity(intent);
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