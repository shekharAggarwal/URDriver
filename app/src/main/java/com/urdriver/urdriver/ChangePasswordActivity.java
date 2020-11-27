package com.urdriver.urdriver;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.material.snackbar.Snackbar;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.CheckDriverResponse;
import com.urdriver.urdriver.retrofit.IURDriver;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {


    ConnectivityReceiver connectivityReceiver;
    ImageView logo;
    EditText edtPassword, edtRePassword;
    Button btnChange;
    IURDriver mService;
    RelativeLayout rootLayout;
    SpinKitView spin_kit;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Common.setBack(this);
        rootLayout = findViewById(R.id.root_layout);

        logo = findViewById(R.id.logo);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        btnChange = findViewById(R.id.btnChange);
        spin_kit = findViewById(R.id.spin_kit);
        mService = Common.getAPI();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sprite fadingCircle = new FadingCircle();
                spin_kit.setIndeterminateDrawable(fadingCircle);
                spin_kit.setVisibility(View.VISIBLE);
                btnChange.setEnabled(false);

                if (edtPassword.getText().toString().isEmpty()) {
                    edtPassword.setError("Enter Your Password");
                    edtPassword.requestFocus();
                    spin_kit.setVisibility(View.GONE);
                    btnChange.setEnabled(true);
                    return;
                }
                if (edtPassword.getText().toString().length() < 6) {
                    edtPassword.setError("Password length should be more then 6");
                    edtPassword.requestFocus();
                    spin_kit.setVisibility(View.GONE);
                    btnChange.setEnabled(true);
                    return;
                }
                if (edtRePassword.getText().toString().isEmpty() ||
                        !edtRePassword.getText().toString().equals(edtPassword.getText().toString())) {
                    edtRePassword.setError("Re-Type your password");
                    edtRePassword.requestFocus();
                    spin_kit.setVisibility(View.GONE);
                    btnChange.setEnabled(true);
                    return;
                }
                if (Common.fromActivity.equalsIgnoreCase("cpwd")) {
                    mService.updateDriverPassword(edtPassword.getText().toString(), Common.AuthPhone)
                            .enqueue(new Callback<CheckDriverResponse>() {
                                @Override
                                public void onResponse(Call<CheckDriverResponse> call, Response<CheckDriverResponse> response) {
                                    CheckDriverResponse checkUserResponse = response.body();
                                    if (checkUserResponse.getError_msg().equals("ok")) {
                                        Paper.init(ChangePasswordActivity.this);
                                        Paper.book().write("password",edtPassword.getText().toString());
                                        Toast.makeText(ChangePasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                        spin_kit.setVisibility(View.GONE);
                                        btnChange.setEnabled(true);
                                    } else {
                                        Snackbar.make(rootLayout, "" + checkUserResponse.getError_msg(),
                                                Snackbar.LENGTH_LONG).show();
                                        spin_kit.setVisibility(View.GONE);
                                        btnChange.setEnabled(true);
                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckDriverResponse> call, Throwable t) {
                                    Log.d("ERROR", t.getMessage());
                                    spin_kit.setVisibility(View.GONE);
                                    btnChange.setEnabled(true);
                                }
                            });
                } else {
                    mService.updateDriverPassword(edtPassword.getText().toString(), Common.AuthPhone)
                            .enqueue(new Callback<CheckDriverResponse>() {
                                @Override
                                public void onResponse(Call<CheckDriverResponse> call, Response<CheckDriverResponse> response) {
                                    CheckDriverResponse checkUserResponse = response.body();
                                    if (checkUserResponse.getError_msg().equals("ok")) {
                                        Toast.makeText(ChangePasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                        spin_kit.setVisibility(View.GONE);
                                        btnChange.setEnabled(true);
                                    } else {
                                        Snackbar.make(rootLayout, "" + checkUserResponse.getError_msg(),
                                                Snackbar.LENGTH_LONG).show();
                                        spin_kit.setVisibility(View.GONE);
                                        btnChange.setEnabled(true);
                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckDriverResponse> call, Throwable t) {
                                    Log.d("ERROR", t.getMessage());
                                    spin_kit.setVisibility(View.GONE);
                                    btnChange.setEnabled(true);
                                }
                            });

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (Common.fromActivity.equalsIgnoreCase("cpwd")) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
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
