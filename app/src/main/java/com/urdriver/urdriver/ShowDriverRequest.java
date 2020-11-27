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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.urdriver.urdriver.Adapter.RequestImageAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.cabDetails;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowDriverRequest extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    TextView txtName, txtEmail, txtPhoneNumber, txtAadhar, txtCabBrand, txtCabModel, txtCabNumber,
            txtCabSeating, toolbar_title, txtDriverCity, txtCabType;

    ImageView img_aadhar, img_driving;

    Button btnAccept, btnDecline;

    IURDriver mService;
    Toolbar toolbar;

    ConnectivityReceiver connectivityReceiver;
    SpinKitView spin_kit, spin_kit1;
    RelativeLayout deleteDriver;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_driver_request);

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

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtAadhar = findViewById(R.id.txtAadhar);
        txtCabBrand = findViewById(R.id.txtCabBrand);
        txtCabModel = findViewById(R.id.txtCabModel);
        txtCabNumber = findViewById(R.id.txtCabNumber);
        txtCabSeating = findViewById(R.id.txtCabSeating);
        txtCabType = findViewById(R.id.txtCabType);
        txtDriverCity = findViewById(R.id.txtDriverCity);
        Common.mDotLayout = findViewById(R.id.linearLayout);
        img_aadhar = findViewById(R.id.img_aadhar);
        img_driving = findViewById(R.id.img_driving);
        Common.slideViewPage = findViewById(R.id.slideViewPage);
        btnAccept = findViewById(R.id.btnAccept);
        btnDecline = findViewById(R.id.btnDecline);
        toolbar = findViewById(R.id.toolbar);
        spin_kit = findViewById(R.id.spin_kit);
        spin_kit1 = findViewById(R.id.spin_kit1);
        deleteDriver = findViewById(R.id.deleteDriver);
        btnDelete = findViewById(R.id.btnDelete);
        toolbar_title = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mService = Common.getAPI();

        if (getIntent().getStringExtra("TEXT") != null && getIntent().getStringExtra("TEXT").equals("INFO")) {
            btnDecline.setVisibility(View.GONE);
            btnAccept.setVisibility(View.GONE);
        }

        if (getIntent().getStringExtra("TEXT") != null && getIntent().getStringExtra("TEXT").equals("DELETE")) {
            btnDecline.setVisibility(View.GONE);
            btnAccept.setVisibility(View.GONE);
            toolbar_title.setText("DELETE DRIVER");
            deleteDriver.setVisibility(View.VISIBLE);
        }

        if (Common.driverRequestModel != null) {
            txtName.setText(Common.driverRequestModel.getName());
            txtEmail.setText(Common.driverRequestModel.getEmail());
            txtPhoneNumber.setText(Common.driverRequestModel.getPhone());
            txtAadhar.setText(Common.driverRequestModel.getAadharNumber());
            if (Common.driverRequestModel.getAadharImage() != null)
                Picasso.get().load(Common.driverRequestModel.getAadharImage()).fit()
                        .error(getResources().getDrawable(R.drawable.aadhaar)).into(img_aadhar);
            if (Common.driverRequestModel.getLicenseImage() != null)
                Picasso.get().load(Common.driverRequestModel.getLicenseImage()).fit()
                        .error(getResources().getDrawable(R.drawable.driver_license)).into(img_driving);

            mService.getcabbydriver(Common.driverRequestModel.getPhone()).enqueue(new Callback<cabDetails>() {
                @Override
                public void onResponse(Call<cabDetails> call, Response<cabDetails> response) {
                    if (response.body() != null) {
                        txtCabBrand.setText(response.body().getCabBrand());
                        txtCabModel.setText(response.body().getCabModel());
                        txtCabNumber.setText(response.body().getCabNumber());
                        txtCabSeating.setText(response.body().getCabSitting());
                        txtCabType.setText(response.body().getCabType());
                        txtDriverCity.setText(response.body().getCabLocation());
                        Common.imageCab = new Gson().fromJson(response.body().getCabImage(),
                                new TypeToken<List<String>>() {
                                }.getType());
                        RequestImageAdapter cabImageAdapter = new RequestImageAdapter(ShowDriverRequest.this, Common.imageCab);
                        Common.slideViewPage.setAdapter(cabImageAdapter);
                        addDotsIndicator(0);
                        Common.slideViewPage.addOnPageChangeListener(viewListener);
                    }
                }

                @Override
                public void onFailure(Call<cabDetails> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                }
            });
        }

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Sprite fadingCircle = new FadingCircle();
                spin_kit.setIndeterminateDrawable(fadingCircle);
                spin_kit.setVisibility(View.VISIBLE);
                btnAccept.setEnabled(false);
                btnDecline.setEnabled(false);
                img_aadhar.setEnabled(false);
                img_driving.setEnabled(false);
                toolbar.setEnabled(false);

                mService.updateDriverStatus(Common.driverRequestModel.getPhone(), "1", "")
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.body() != null) {
                                    if (response.body().equalsIgnoreCase("ok")) {
                                        onBackPressed();
                                        spin_kit.setVisibility(View.GONE);
                                        btnAccept.setEnabled(true);
                                        Toast.makeText(ShowDriverRequest.this, "Request Accepted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        spin_kit.setVisibility(View.GONE);
                                        btnAccept.setEnabled(true);
                                        Toast.makeText(ShowDriverRequest.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d("ERROR", t.getMessage());
                                Toast.makeText(ShowDriverRequest.this, "Try Again", Toast.LENGTH_SHORT).show();
                                spin_kit.setVisibility(View.GONE);
                                btnAccept.setEnabled(true);
                            }
                        });
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAccept.setEnabled(false);
                btnDecline.setEnabled(false);
                img_aadhar.setEnabled(false);
                img_driving.setEnabled(false);
                toolbar.setEnabled(false);
                startActivity(new Intent(ShowDriverRequest.this, RequestDenied.class));

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sprite fadingCircle = new FadingCircle();
                spin_kit1.setIndeterminateDrawable(fadingCircle);
                spin_kit1.setVisibility(View.VISIBLE);
                btnDelete.setEnabled(false);
                mService.deleteDriver(Common.driverRequestModel.getPhone()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body() != null) {
                            if (response.body().equalsIgnoreCase("ok")) {
                                Toast.makeText(ShowDriverRequest.this, "Deleted", Toast.LENGTH_SHORT).show();
                                spin_kit1.setVisibility(View.GONE);
                                btnDelete.setEnabled(true);
                                onBackPressed();
                            } else {
                                Toast.makeText(ShowDriverRequest.this, "Try Again", Toast.LENGTH_SHORT).show();
                                spin_kit1.setVisibility(View.GONE);
                                btnDelete.setEnabled(true);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("ERROR", t.getMessage());
                        Toast.makeText(ShowDriverRequest.this, "Try Again", Toast.LENGTH_SHORT).show();
                        spin_kit1.setVisibility(View.GONE);
                        btnDelete.setEnabled(true);

                    }
                });

            }
        });

        img_driving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.imgCab = new ArrayList<>();
                Common.imgCab.add(Common.driverRequestModel.getLicenseImage());
                startActivity(new Intent(ShowDriverRequest.this, FullImageActivity.class));
            }
        });

        img_aadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.imgCab = new ArrayList<>();
                Common.imgCab.add(Common.driverRequestModel.getAadharImage());
                startActivity(new Intent(ShowDriverRequest.this, FullImageActivity.class));
            }
        });
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void addDotsIndicator(int position) {
        Common.mDotLayout.setText(position + 1 + "/" + Common.imageCab.size());
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
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        if (!connectivityReceiver.isOrderedBroadcast()) {
            registerReceiver(connectivityReceiver, intentFilter);
            MyApplication.getInstance().setConnectivityListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }
}
