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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText edtPhoneNumber;
    Button btnForget;
    TextView txtBackLogin;
    IURDriver mService;
    RelativeLayout rootLayout;

    ConnectivityReceiver connectivityReceiver;
    SpinKitView spin_kit;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        Common.setBack(this);
        Common.StartFirebaseLogin(ForgotActivity.this);
        mService = Common.getAPI();
        rootLayout = findViewById(R.id.root_layout);


        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        btnForget = findViewById(R.id.btnForget);
        txtBackLogin = findViewById(R.id.txtBackLogin);
        spin_kit = findViewById(R.id.spin_kit);

        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Sprite fadingCircle = new FadingCircle();
                spin_kit.setIndeterminateDrawable(fadingCircle);
                spin_kit.setVisibility(View.VISIBLE);
                btnForget.setEnabled(false);
                if (edtPhoneNumber.getText().toString().isEmpty() || edtPhoneNumber.getText().toString().length() < 10) {
                    edtPhoneNumber.setError("Enter Correct Phone Number");
                    edtPhoneNumber.requestFocus();
                    spin_kit.setVisibility(View.GONE);
                    btnForget.setEnabled(true);
                    return;
                }

                mService.checkDriver(edtPhoneNumber.getText().toString())
                        .enqueue(new Callback<CheckDriverResponse>() {
                            @Override
                            public void onResponse(Call<CheckDriverResponse> call, Response<CheckDriverResponse> response) {
                                CheckDriverResponse checkUserResponse = response.body();
                                if (checkUserResponse.getError_msg().equals("ok")) {
                                    Common.fromActivity = "fog";
                                    Common.AuthPhone = edtPhoneNumber.getText().toString();
                                    Common.sendOTP(ForgotActivity.this, edtPhoneNumber.getText().toString());
                                    spin_kit.setVisibility(View.GONE);
                                    btnForget.setEnabled(true);
                                } else {
                                    Snackbar.make(rootLayout, "" + checkUserResponse.getError_msg(),
                                            Snackbar.LENGTH_LONG).show();
                                    spin_kit.setVisibility(View.GONE);
                                    btnForget.setEnabled(true);
                                }
                            }

                            @Override
                            public void onFailure(Call<CheckDriverResponse> call, Throwable t) {
                                Log.d("ERROR", t.getMessage());
                                spin_kit.setVisibility(View.GONE);
                                btnForget.setEnabled(true);
                            }
                        });
            }
        });

        txtBackLogin.setOnClickListener(new View.OnClickListener() {
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
