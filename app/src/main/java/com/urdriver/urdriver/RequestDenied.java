package com.urdriver.urdriver;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.gson.Gson;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDenied extends CrashActivity {

    Toolbar toolbar;

    CheckBox ckb_1, ckb_2, ckb_3, ckb_4, ckb_5, ckb_6, ckb_7, ckb_8, ckb_9, ckb_10, ckb_11;
    EditText edit_12;
    Button btnSubmit;
    String DataArr = "";
    List<String> WhyList = new ArrayList<>();
    IURDriver mService;
    SpinKitView spin_kit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_denied);

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

        ckb_1 = findViewById(R.id.ckb_1);
        ckb_2 = findViewById(R.id.ckb_2);
        ckb_3 = findViewById(R.id.ckb_3);
        ckb_4 = findViewById(R.id.ckb_4);
        ckb_5 = findViewById(R.id.ckb_5);
        ckb_6 = findViewById(R.id.ckb_6);
        ckb_7 = findViewById(R.id.ckb_7);
        ckb_8 = findViewById(R.id.ckb_8);
        ckb_9 = findViewById(R.id.ckb_9);
        ckb_10 = findViewById(R.id.ckb_10);
        ckb_11 = findViewById(R.id.ckb_11);
        edit_12 = findViewById(R.id.edit_12);
        btnSubmit = findViewById(R.id.btnSubmit);
        spin_kit = findViewById(R.id.spin_kit);

        mService = Common.getAPI();


        ckb_11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    edit_12.setVisibility(View.VISIBLE);
                else
                    edit_12.setVisibility(View.GONE);

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sprite fadingCircle = new FadingCircle();
                spin_kit.setIndeterminateDrawable(fadingCircle);
                spin_kit.setVisibility(View.VISIBLE);
                btnSubmit.setEnabled(false);
                getData();
            }
        });
    }

    private void getData() {

        if (ckb_1.isChecked()) {
            WhyList.add(ckb_1.getText().toString());
        }
        if (ckb_2.isChecked()) {
            WhyList.add(ckb_2.getText().toString());
        }
        if (ckb_3.isChecked()) {
            WhyList.add(ckb_3.getText().toString());
        }
        if (ckb_4.isChecked()) {
            WhyList.add(ckb_4.getText().toString());
        }
        if (ckb_5.isChecked()) {
            WhyList.add(ckb_5.getText().toString());
        }
        if (ckb_6.isChecked()) {
            WhyList.add(ckb_6.getText().toString());
        }
        if (ckb_7.isChecked()) {
            WhyList.add(ckb_7.getText().toString());
        }
        if (ckb_8.isChecked()) {
            WhyList.add(ckb_8.getText().toString());
        }
        if (ckb_9.isChecked()) {
            WhyList.add(ckb_9.getText().toString());
        }
        if (ckb_10.isChecked()) {
            WhyList.add(ckb_10.getText().toString());
        }
        if (ckb_11.isChecked()) {
            WhyList.add(edit_12.getText().toString());
        }

        DataArr = new Gson().toJson(WhyList);
        Log.d("ERROR", DataArr);

        if (WhyList.size() != 0)
            sendRequestToServer();
        else {
            Toast.makeText(this, "Select Reason", Toast.LENGTH_SHORT).show();
            spin_kit.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
        }
    }

    private void sendRequestToServer() {
        mService.updateDriverStatus(Common.driverRequestModel.getPhone(), "2", DataArr)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body() != null) {
                            if (response.body().equalsIgnoreCase("ok")) {
                                Toast.makeText(RequestDenied.this, "Request Denied", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RequestDenied.this, DriversRequest.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(intent);
                                spin_kit.setVisibility(View.GONE);
                                btnSubmit.setEnabled(true);
                            } else {
                                Toast.makeText(RequestDenied.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RequestDenied.this, DriversRequest.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(intent);
                                spin_kit.setVisibility(View.GONE);
                                btnSubmit.setEnabled(true);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("ERROR", t.getMessage());
                        Toast.makeText(RequestDenied.this, "Try Again", Toast.LENGTH_SHORT).show();
                        spin_kit.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                    }
                });
    }

    @Override
    public void onBackPressed() {

    }

}
