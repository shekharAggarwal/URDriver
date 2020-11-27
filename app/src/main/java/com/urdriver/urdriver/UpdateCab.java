package com.urdriver.urdriver;

import android.app.ProgressDialog;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.urdriver.urdriver.Adapter.CabImageAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.ImageDriver;
import com.urdriver.urdriver.model.cabDetails;
import com.urdriver.urdriver.retrofit.IURDriver;
import com.urdriver.urdriver.retrofit.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateCab extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText actVehicle, actModel, txtSeating, txt_car_number;
    EditText txt_cab_city, txt_cab_type;
    Button btnUpdateCab;
    LinearLayout addImgCab;
    IURDriver mService;
    ConnectivityReceiver connectivityReceiver;
    RelativeLayout rootLayout;
    cabDetails cabDetail;
    Toolbar toolbar;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_cab);

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

        Common.imageCab = new ArrayList<>();

        mService = Common.getAPI();

        actVehicle = findViewById(R.id.actVehicle);
        rootLayout = findViewById(R.id.root_layout);
        actModel = findViewById(R.id.actModel);
        txtSeating = findViewById(R.id.txtSeating);
        txt_car_number = findViewById(R.id.txt_car_number);
        toolbar = findViewById(R.id.toolbar);
        txt_cab_type = findViewById(R.id.txt_cab_type);
        Common.mDotLayout = findViewById(R.id.linearLayout);
        addImgCab = findViewById(R.id.addImgCab);
        txt_cab_city = findViewById(R.id.txt_cab_city);
        Common.slideViewPage = findViewById(R.id.slideViewPage);
        btnUpdateCab = findViewById(R.id.btnUpdateCab);

        Common.act = "UPCAB";

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Common.baseActivity == null || !Common.baseActivity.equalsIgnoreCase("info"))
            if (Common.currentDriver != null) {
                mService.getcabbydriver(Common.currentDriver.getPhone()).enqueue(new Callback<cabDetails>() {
                    @Override
                    public void onResponse(Call<cabDetails> call, Response<cabDetails> response) {
                        cabDetail = response.body();
                        assert cabDetail != null;

                        Common.imageCab = new Gson().fromJson(cabDetail.getCabImage(),
                                new TypeToken<List<String>>() {
                                }.getType());
                        Common.slideViewPage.removeAllViews();
                        CabImageAdapter cabImageAdapter = new CabImageAdapter(UpdateCab.this, Common.imageCab);
                        Common.slideViewPage.setAdapter(cabImageAdapter);
                        addDotsIndicator(0);
                        Common.slideViewPage.addOnPageChangeListener(viewListener);

                        actVehicle.setText(cabDetail.getCabBrand());
                        actModel.setText(cabDetail.getCabModel());
                        txtSeating.setText(cabDetail.getCabSitting());
                        txt_cab_type.setText(cabDetail.getCabType());
                        txt_car_number.setText(cabDetail.getCabNumber());
                        txt_cab_city.setText(cabDetail.getCabLocation());
                    }

                    @Override
                    public void onFailure(Call<cabDetails> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                    }
                });
            } else {
                Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
            }
        else {
            if (Common.driverRequestModel != null) {
                actVehicle.setEnabled(true);
                txt_car_number.setEnabled(true);
                txtSeating.setEnabled(true);
                actModel.setEnabled(true);
                txt_cab_city.setEnabled(true);
                txt_cab_type.setEnabled(true);
                mService.getcabbydriver(Common.driverRequestModel.getPhone()).enqueue(new Callback<cabDetails>() {
                    @Override
                    public void onResponse(Call<cabDetails> call, Response<cabDetails> response) {
                        cabDetail = response.body();
                        assert cabDetail != null;

                        Common.imageCab = new Gson().fromJson(cabDetail.getCabImage(),
                                new TypeToken<List<String>>() {
                                }.getType());
                        Common.slideViewPage.removeAllViews();
                        CabImageAdapter cabImageAdapter = new CabImageAdapter(UpdateCab.this, Common.imageCab);
                        Common.slideViewPage.setAdapter(cabImageAdapter);
                        addDotsIndicator(0);
                        Common.slideViewPage.addOnPageChangeListener(viewListener);

                        actVehicle.setText(cabDetail.getCabBrand());
                        actModel.setText(cabDetail.getCabModel());
                        txtSeating.setText(cabDetail.getCabSitting());
                        txt_cab_type.setText(cabDetail.getCabType());
                        txt_car_number.setText(cabDetail.getCabNumber());
                        txt_cab_city.setText(cabDetail.getCabLocation());
                    }

                    @Override
                    public void onFailure(Call<cabDetails> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                    }
                });
            } else {
                Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
            }
        }
        btnUpdateCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnUpdateCab.setEnabled(false);
                checkData();
            }
        });

        addImgCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
            }
        });

    }

    private void checkData() {
        if (txt_cab_type.getText().toString().isEmpty()) {
            txt_cab_type.setError("Enter Cab Type");
            txt_cab_type.requestFocus();
            btnUpdateCab.setEnabled(true);
            return;
        }

        if (txt_cab_city.getText().toString().isEmpty()) {
            txt_cab_city.setError("Enter Cab City");
            txt_cab_city.requestFocus();
            btnUpdateCab.setEnabled(true);
            return;
        }
        if (Common.imageCab.size() < 3) {
            Toast.makeText(this, "Upload at least 3 image of cab", Toast.LENGTH_SHORT).show();
            btnUpdateCab.setEnabled(true);
            return;
        }

        String cabImage = new Gson().toJson(Common.imageCab);

        if (Common.baseActivity == null || !Common.baseActivity.equalsIgnoreCase("info")) {
            mService.updateCabDetail(txt_cab_type.getText().toString(),
                    cabImage,
                    txt_cab_city.getText().toString(),
                    Common.currentDriver.getPhone(),
                    actVehicle.getText().toString(),
                    actModel.getText().toString(),
                    txtSeating.getText().toString(),
                    txt_car_number.getText().toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            assert response.body() != null;
                            if (response.body().equals("OK")) {
                                Toast.makeText(UpdateCab.this, "Cab Updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdateCab.this, ScheduleActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                btnUpdateCab.setEnabled(true);
                                finish();
                                startActivity(intent);
                            } else {
                                Snackbar.make(rootLayout, response.body(), Snackbar.LENGTH_LONG).show();
                                btnUpdateCab.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            Snackbar.make(rootLayout, t.getMessage(), Snackbar.LENGTH_LONG).show();
                            btnUpdateCab.setEnabled(true);
                        }
                    });
        } else {
            mService.updateCabDetail(txt_cab_type.getText().toString(),
                    cabImage,
                    txt_cab_city.getText().toString(),
                    Common.driverRequestModel.getPhone(),
                    actVehicle.getText().toString(),
                    actModel.getText().toString(),
                    txtSeating.getText().toString(),
                    txt_car_number.getText().toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            assert response.body() != null;
                            if (response.body().equals("OK")) {
                                Toast.makeText(UpdateCab.this, "Cab Updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdateCab.this, DriversRequest.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                btnUpdateCab.setEnabled(true);
                                startActivity(intent);
                                Common.baseActivity = null;

                            } else {
                                Snackbar.make(rootLayout, response.body(), Snackbar.LENGTH_LONG).show();
                                btnUpdateCab.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            Snackbar.make(rootLayout, t.getMessage(), Snackbar.LENGTH_LONG).show();
                            btnUpdateCab.setEnabled(true);
                        }
                    });
        }
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                uploadToServer();
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

    private void uploadToServer() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Uploading....");
        mDialog.show();
        String image = convertToString();
        final String imageName = UUID.randomUUID().toString();
        IURDriver apiInterface = RetrofitClient.getClient(Common.BASE_URL).create(IURDriver.class);
        Call<ImageDriver> call = apiInterface.UploadCabImage(imageName, image, Common.currentDriver.getPhone());
        final int[] a = {1};
        call.enqueue(new Callback<ImageDriver>() {
            @Override
            public void onResponse(Call<ImageDriver> call, Response<ImageDriver> response) {
                if (a[0] == 1) {
                    mDialog.dismiss();
                    Toast.makeText(UpdateCab.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                    Common.imageCab.add("http://myinvented.com/urdriver/cab_image/" +
                            Common.currentDriver.getPhone() + "/" + imageName + ".jpeg");
                    if (Common.imgCab != null)
                        Common.imgCab.remove(Common.imgCab.size() - 1);
                    Common.mDotLayout.setText("");
                    Common.slideViewPage.removeAllViews();
                    CabImageAdapter cabImageAdapter = new CabImageAdapter(UpdateCab.this, Common.imageCab);
                    Common.slideViewPage.setAdapter(cabImageAdapter);
                    addDotsIndicator(0);
                    Common.slideViewPage.addOnPageChangeListener(viewListener);
                    a[0] = 0;
                }
            }

            @Override
            public void onFailure(Call<ImageDriver> call, Throwable t) {
                Log.d("Server Response", "" + t.toString());
                mDialog.dismiss();
                Toast.makeText(UpdateCab.this, "ERROR try Again", Toast.LENGTH_SHORT).show();
            }
        });
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
