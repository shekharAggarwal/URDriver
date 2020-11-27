package com.urdriver.urdriver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.retrofit.IURDriver;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText edtOtp;
    Button btnOtp;
    TextView txtResend;
    CountDownTimer countDownTimer;
    TextView timer;
    boolean isRunning = false;

    ConnectivityReceiver connectivityReceiver;

    IURDriver mService;
    SpinKitView spin_kit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        Common.setBack(this);
        mService = Common.getAPI();
        edtOtp = findViewById(R.id.edtOtp);
        btnOtp = findViewById(R.id.btnOtp);
        timer = findViewById(R.id.timer);
        txtResend = findViewById(R.id.txtResend);
        spin_kit = findViewById(R.id.spin_kit);

        verifyOtp();
        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sprite fadingCircle = new FadingCircle();
                spin_kit.setIndeterminateDrawable(fadingCircle);
                spin_kit.setVisibility(View.VISIBLE);
                btnOtp.setEnabled(false);

                if (edtOtp.getText().toString().length() < 6 || edtOtp.getText().toString().isEmpty()) {
                    edtOtp.setError("Check OTP");
                    edtOtp.requestFocus();
                    spin_kit.setVisibility(View.GONE);
                    btnOtp.setEnabled(true);
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Common.verificationCode, edtOtp.getText().toString());
                SigninWithPhone(credential);
            }
        });

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.sendOTP(OTPActivity.this, Common.AuthPhone);
            }
        });

    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if (Common.fromActivity.equals("reg")) {
                                if (isRunning)
                                    countDownTimer.cancel();
                                Intent intent = new Intent(OTPActivity.this, UploadPapers.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                spin_kit.setVisibility(View.GONE);
                                btnOtp.setEnabled(true);

                            }
                            else if (Common.fromActivity.equals("fog")) {
                                if (isRunning)
                                    countDownTimer.cancel();
                                Intent intent = new Intent(OTPActivity.this, ChangePasswordActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                spin_kit.setVisibility(View.GONE);
                                btnOtp.setEnabled(true);
                            }
                            else if (Common.fromActivity.equals("pro")) {
                                if (isRunning)
                                    countDownTimer.cancel();
                                mService.updateDriver(Common.currentDriver.getId(),
                                        Common.name,
                                        Common.currentDriver.getPhone(),
                                        Common.image,
                                        Common.phone,
                                        Common.email)
                                        .enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if (response.body().equalsIgnoreCase("OK")) {
                                                    mService.getDriverInfo(Common.email, Common.currentDriver.getPassword()).enqueue(new Callback<Driver>() {
                                                        @Override
                                                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                                                            Common.currentDriver = response.body();
                                                            if (Common.currentDriver.getError_msg() == null) {
                                                                Intent intent = new Intent(OTPActivity.this, ProfileActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                                spin_kit.setVisibility(View.GONE);
                                                                btnOtp.setEnabled(true);
                                                            } else {
                                                                Toast.makeText(OTPActivity.this, "" + Common.currentDriver.getError_msg(), Toast.LENGTH_SHORT).show();
                                                                spin_kit.setVisibility(View.GONE);
                                                                btnOtp.setEnabled(true);
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Driver> call, Throwable t) {
                                                            Log.d("Error", t.getMessage());
                                                            Toast.makeText(OTPActivity.this, "Try again later", Toast.LENGTH_SHORT).show();
                                                            spin_kit.setVisibility(View.GONE);
                                                            btnOtp.setEnabled(true);
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                Log.d("Error", t.getMessage());
                                                Toast.makeText(OTPActivity.this, "Try again later", Toast.LENGTH_SHORT).show();
                                                spin_kit.setVisibility(View.GONE);
                                                btnOtp.setEnabled(true);
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(OTPActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                            spin_kit.setVisibility(View.GONE);
                            btnOtp.setEnabled(true);
                        }
                    }
                });
    }

    private void verifyOtp() {
        countDownTimer = new CountDownTimer(60000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                isRunning = true;
                if (millisUntilFinished / 1000 == 10)
                    timer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                timer.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Toast.makeText(OTPActivity.this, "Time Out!!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
        if (isRunning)
            countDownTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isRunning)
            countDownTimer.cancel();
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
}
