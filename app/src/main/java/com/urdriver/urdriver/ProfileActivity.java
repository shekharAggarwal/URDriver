package com.urdriver.urdriver;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.CheckDriverResponse;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.ImageDriver;
import com.urdriver.urdriver.retrofit.IURDriver;
import com.urdriver.urdriver.retrofit.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    TextView txt_name, txt_phone_number, txt_email, ratting;
    Toolbar toolbar;
    CircleImageView imgEdit, profileDriver;
    ViewPager slideViewPage;
    ConnectivityReceiver connectivityReceiver;
    Button btnDone;
    IURDriver mService = Common.getAPI();
    LinearLayout ViewName, ViewPhone, ViewEmail, edtName, edtPhone, edtEmail;
    EditText edtFullName, edtPhoneNumber, edtEmailId;

    Bitmap bitmap;
    private static final int IMAGE = 100;
    boolean isCheck = false;

    String uploaded_img_path = "";
    boolean isClick = false;

    int a = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Common.setBack(this);
        Common.StartFirebaseLogin(this);

        toolbar = findViewById(R.id.toolbar);
        txt_name = findViewById(R.id.txt_name);
        txt_phone_number = findViewById(R.id.txt_phone_number);
        txt_email = findViewById(R.id.txt_email);
        ViewName = findViewById(R.id.ViewName);
        ViewPhone = findViewById(R.id.ViewPhone);
        ViewEmail = findViewById(R.id.ViewEmail);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        ratting = findViewById(R.id.ratting);

        slideViewPage = findViewById(R.id.slideViewPage);

        profileDriver = findViewById(R.id.profileDriver);

        edtFullName = findViewById(R.id.edtFullName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtEmailId = findViewById(R.id.edtEmailId);

        imgEdit = findViewById(R.id.imgEdit);
        btnDone = findViewById(R.id.btnDone);
        btnDone.setEnabled(true);
        profileDriver.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (isClick) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, IMAGE);
                }
            }
        });


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Common.currentDriver != null) {
            txt_name.setText(Common.currentDriver.getName());
            txt_phone_number.setText(Common.currentDriver.getPhone());
            txt_email.setText(Common.currentDriver.getEmail());
            if (Common.currentDriver.getImage() != null || !Common.currentDriver.getImage().isEmpty()) {
                uploaded_img_path = Common.currentDriver.getImage();
                Picasso.get()
                        .load(Common.currentDriver.getImage())
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .error(getResources().getDrawable(R.drawable.map_round))
                        .into(profileDriver);

            }
            edtFullName.setText(Common.currentDriver.getName());
            edtPhoneNumber.setText(Common.currentDriver.getPhone());
            edtEmailId.setText(Common.currentDriver.getEmail());

            mService.getRatingByPhone(Common.currentDriver.getPhone()).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.body() != null) {

                        DecimalFormat df = new DecimalFormat("#.#");
                        df.setRoundingMode(RoundingMode.CEILING);
                        String s = Float.parseFloat(response.body()) > 0.0 ? "" + df.format(Float.parseFloat(response.body())) : "0.0";

                        if (Float.parseFloat(response.body()) > 0)
                            ratting.setText(s);
                        else
                            ratting.setText("0.0");
                    } else
                        ratting.setText("0.0");
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                }
            });

        }

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgEdit.setVisibility(View.GONE);
                ViewName.setVisibility(View.GONE);
                ViewPhone.setVisibility(View.GONE);
                ViewEmail.setVisibility(View.GONE);

                edtFullName.setVisibility(View.VISIBLE);
                edtPhoneNumber.setVisibility(View.VISIBLE);
                edtEmailId.setVisibility(View.VISIBLE);
                btnDone.setVisibility(View.VISIBLE);
                btnDone.setEnabled(true);
                isClick = true;
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDone.setEnabled(false);
                if (edtEmailId.getText().toString().isEmpty()) {
                    edtEmailId.setError("Enter Your Email");
                    edtEmailId.requestFocus();
                    return;
                }

                if (!edtEmailId.getText().toString().contains("@")) {
                    edtEmailId.setError("Enter Your Email");
                    edtEmailId.requestFocus();
                    return;
                }

                if (edtFullName.getText().toString().isEmpty()) {
                    edtFullName.setError("Enter Your Name");
                    edtFullName.requestFocus();
                    return;
                }

                if (edtPhoneNumber.getText().toString().isEmpty() || edtPhoneNumber.getText().toString().length() < 10) {
                    edtPhoneNumber.setError("Check Phone Number");
                    edtPhoneNumber.requestFocus();
                    return;
                }

                if (!txt_email.getText().toString().equalsIgnoreCase(edtEmailId.getText().toString())) {
                    mService.checkDriverEmail(edtEmailId.getText().toString()).enqueue(new Callback<CheckDriverResponse>() {
                        @Override
                        public void onResponse(Call<CheckDriverResponse> call, Response<CheckDriverResponse> response) {
                            if (response.body().equals("ok")) {
                                isCheck = true;
                                Toast.makeText(ProfileActivity.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<CheckDriverResponse> call, Throwable t) {
                            Log.d("Error", t.getMessage());
                            isCheck = true;
                        }
                    });
                    if (isCheck)
                        return;
                }

                if (!txt_phone_number.getText().toString().equalsIgnoreCase(edtPhoneNumber.getText().toString())) {
                    mService.checkDriver(edtPhoneNumber.getText().toString()).enqueue(new Callback<CheckDriverResponse>() {
                        @Override
                        public void onResponse(Call<CheckDriverResponse> call, Response<CheckDriverResponse> response) {
                            assert response.body() != null;
                            if (!response.body().getError_msg().equalsIgnoreCase("ok")) {
                                Common.fromActivity = "pro";
                                Common.AuthPhone = edtPhoneNumber.getText().toString();
                                Common.name = edtFullName.getText().toString();
                                Common.image = uploaded_img_path;
                                Common.phone = edtPhoneNumber.getText().toString();
                                Common.email = edtEmailId.getText().toString();
                                Common.sendOTP(ProfileActivity.this, edtPhoneNumber.getText().toString());

                            } else {
                                Toast.makeText(ProfileActivity.this, "Driver Exists", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<CheckDriverResponse> call, Throwable t) {
                            Log.d("Error", t.getMessage());
                        }
                    });
                    isClick = false;
                    return;
                }

                if (bitmap != null)
                    uploadImage();
                else
                    mService.updateDriver(Common.currentDriver.getId(),
                            edtFullName.getText().toString(),
                            Common.currentDriver.getPhone(),
                            uploaded_img_path,
                            edtPhoneNumber.getText().toString(),
                            edtEmailId.getText().toString())
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.body().equalsIgnoreCase("OK")) {
                                        mService.getDriverInfo(edtEmailId.getText().toString(), Common.currentDriver.getPassword()).enqueue(new Callback<Driver>() {
                                            @Override
                                            public void onResponse(Call<Driver> call, Response<Driver> response) {
                                                Common.currentDriver = response.body();
                                                Log.d("ERROR", new Gson().toJson(Common.currentDriver));
                                                assert Common.currentDriver != null;
                                                if (Common.currentDriver.getError_msg() == null) {
                                                    unregisterReceiver(connectivityReceiver);
                                                    onBackPressed();
                                                } else {
                                                    Toast.makeText(ProfileActivity.this, "" + Common.currentDriver.getError_msg(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Driver> call, Throwable t) {
                                                Log.d("Error", t.getMessage());
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("Error", t.getMessage());
                                }
                            });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        finish();
//        Intent intent = new Intent(ProfileActivity.this, YourAccount.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (a == 1) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            connectivityReceiver = new ConnectivityReceiver();
            registerReceiver(connectivityReceiver, intentFilter);
            MyApplication.getInstance().setConnectivityListener(this);
            a = 0;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                profileDriver.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String convertToString() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    private void uploadImage() {

        String image = convertToString();
        String imageName = Common.currentDriver.getPhone();
        IURDriver apiInterface = RetrofitClient.getClient(Common.BASE_URL).create(IURDriver.class);
        Call<ImageDriver> call = apiInterface.uploadImage(imageName, image);

        call.enqueue(new Callback<ImageDriver>() {
            @Override
            public void onResponse(Call<ImageDriver> call, Response<ImageDriver> response) {

                uploaded_img_path = "http://myinvented.com/urdriver/DriverImage/" + Common.currentDriver.getPhone() + ".jpeg";

                mService.updateDriver(Common.currentDriver.getId(),
                        edtFullName.getText().toString(),
                        Common.currentDriver.getPhone(),
                        uploaded_img_path,
                        edtPhoneNumber.getText().toString(),
                        edtEmailId.getText().toString())
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.body().equalsIgnoreCase("OK")) {
                                    mService.getDriverInfo(edtEmailId.getText().toString(), Common.currentDriver.getPassword()).enqueue(new Callback<Driver>() {
                                        @Override
                                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                                            Common.currentDriver = response.body();
                                            Log.d("ERROR", new Gson().toJson(Common.currentDriver));
                                            assert Common.currentDriver != null;
                                            if (Common.currentDriver.getError_msg() == null) {
                                                unregisterReceiver(connectivityReceiver);
                                                onBackPressed();
                                            } else {
                                                Toast.makeText(ProfileActivity.this, "" + Common.currentDriver.getError_msg(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Driver> call, Throwable t) {
                                            Log.d("Error", t.getMessage());
                                        }
                                    });
                                } else {
                                    Toast.makeText(ProfileActivity.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d("Error", t.getMessage());
                            }
                        });
            }

            @Override
            public void onFailure(Call<ImageDriver> call, Throwable t) {
                Log.d("Server Response", "" + t.toString());
            }
        });

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

