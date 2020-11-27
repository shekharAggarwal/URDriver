package com.urdriver.urdriver;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.material.snackbar.Snackbar;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.CheckDriverResponse;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.retrofit.IURDriver;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {


    EditText edtUserName, edtUserEmail, edtPhoneNumber, edtPassword, edtAadhar;
    Button btnRegister;
    public static ImageView logo;
    IURDriver mService;
    ConnectivityReceiver connectivityReceiver;
    Toolbar toolbar;
    RelativeLayout rootLayout;
    SpinKitView spin_kit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Common.setTop(this);

        Common.StartFirebaseLogin(RegisterActivity.this);
        mService = Common.getAPI();
        rootLayout = findViewById(R.id.root_layout);

        edtUserName = findViewById(R.id.edtUserName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtUserEmail = findViewById(R.id.edtUserEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtAadhar = findViewById(R.id.edtAadhar);
        logo = findViewById(R.id.logo);
        btnRegister = findViewById(R.id.btnRegister);
        spin_kit = findViewById(R.id.spin_kit);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sprite fadingCircle = new FadingCircle();
                spin_kit.setIndeterminateDrawable(fadingCircle);
                spin_kit.setVisibility(View.VISIBLE);
                btnRegister.setEnabled(false);
                check_data();
            }
        });

    }

    private void check_data() {
        if (edtUserName.getText().toString().isEmpty()) {
            edtUserName.setError("Enter Your Name");
            edtUserName.requestFocus();
            btnRegister.setEnabled(true);
            spin_kit.setVisibility(View.GONE);
            return;
        }
        /*if (edtUserName.getText().toString().length() < 6) {
            edtUserName.setError("Username length should be more then 6");
            edtUserName.requestFocus();
            btnRegister.setEnabled(true);
            spin_kit.setVisibility(View.GONE);
            return;
        }*/
//        Matcher m = pattern.matcher(edtUserName.getText().toString());
//        boolean b = m.find();
//        if (edtUserName.getText().toString().contains("[\'\"]")) {
//            edtUserName.setError("Username Can't contain special Characters");
//            edtUserName.requestFocus();
//            return;
//        }
        if (edtPassword.getText().toString().isEmpty()) {
            edtPassword.setError("Enter Your Password");
            edtPassword.requestFocus();
            btnRegister.setEnabled(true);
            spin_kit.setVisibility(View.GONE);
            return;
        }
        if (edtPassword.getText().toString().length() < 6) {
            edtPassword.setError("Password length should be more then 6");
            btnRegister.setEnabled(true);
            edtPassword.requestFocus();
            spin_kit.setVisibility(View.GONE);
            return;
        }

        if (edtUserEmail.getText().toString().isEmpty()) {
            edtUserEmail.setError("Enter Your Email");
            edtUserEmail.requestFocus();
            btnRegister.setEnabled(true);
            spin_kit.setVisibility(View.GONE);
            return;
        }

        if (!edtUserEmail.getText().toString().contains("@")) {
            edtUserEmail.setError("Enter Your Email");
            btnRegister.setEnabled(true);
            edtUserEmail.requestFocus();
            spin_kit.setVisibility(View.GONE);
            return;
        }
//        Matcher n = pattern.matcher(edtPassword.getText().toString());
//        boolean bo = n.find();
//        if (bo) {
//            edtPassword.setError("Password Can't contain special Characters");
//            edtPassword.requestFocus();
//            return;
//        }
        if (edtPhoneNumber.getText().toString().isEmpty() || edtPhoneNumber.getText().toString().length() < 10) {
            edtPhoneNumber.setError("Check Phone Number");
            btnRegister.setEnabled(true);
            edtPhoneNumber.requestFocus();
            spin_kit.setVisibility(View.GONE);
            return;
        }
        if (edtAadhar.getText().toString().isEmpty() || edtAadhar.getText().toString().length() < 12) {
            edtAadhar.setError("Check Aadhar Number");
            edtAadhar.requestFocus();
            spin_kit.setVisibility(View.GONE);
            return;
        }


        mService.CheckDriverExists(edtUserEmail.getText().toString(), edtPhoneNumber.getText().toString())
                .enqueue(new Callback<CheckDriverResponse>() {
                    @Override
                    public void onResponse(Call<CheckDriverResponse> call, Response<CheckDriverResponse> response) {
                        if (response != null) {
                            CheckDriverResponse checkUserResponse = response.body();
//                            Toast.makeText(RegisterActivity.this, ""+checkUserResponse.getError_msg(), Toast.LENGTH_SHORT).show();
                            if (checkUserResponse.getError_msg().equals("ok")) {
                                Common.fromActivity = "reg";
                                Common.AuthPhone = edtPhoneNumber.getText().toString();
                                Common.register = new Driver(edtUserName.getText().toString(),
                                        edtUserEmail.getText().toString(),
                                        edtPhoneNumber.getText().toString(),
                                        edtPassword.getText().toString(),
                                        edtAadhar.getText().toString(),
                                        " ",
                                        " ",
                                        0,
                                        null);
                                Common.sendOTP(RegisterActivity.this, edtPhoneNumber.getText().toString());
                                spin_kit.setVisibility(View.GONE);
                                btnRegister.setEnabled(true);
                            } else {
                                Snackbar.make(rootLayout, "" + checkUserResponse.getError_msg(),
                                        Snackbar.LENGTH_LONG).show();
                                btnRegister.setEnabled(true);
                                spin_kit.setVisibility(View.GONE);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<CheckDriverResponse> call, Throwable t) {
                        spin_kit.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
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
