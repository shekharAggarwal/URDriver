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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.ImageDriver;
import com.urdriver.urdriver.retrofit.IURDriver;
import com.urdriver.urdriver.retrofit.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPapers extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    ConnectivityReceiver connectivityReceiver;
    ImageView img_aadhar, img_driving;
    private static final int PICK_FILE_REQUEST = 1222;
    int img = 0, aadhar = 0, lic = 0;
    Button btnSubmit;
    IURDriver mService;

    Bitmap bitmapLic, bitmapAdh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_papers);

        mService = Common.getAPI();
        img_aadhar = findViewById(R.id.img_aadhar);
        img_driving = findViewById(R.id.img_driving);
        btnSubmit = findViewById(R.id.btnSubmit);

        img_aadhar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                Toast.makeText(UploadPapers.this, "clicked", Toast.LENGTH_SHORT).show();
                if (Common.register != null) {
                    chooseImage();
                    img = 1;
                }
            }
        });

        img_driving.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                Toast.makeText(UploadPapers.this, "clicked", Toast.LENGTH_SHORT).show();
                if (Common.register != null) {
                    chooseImage();
                    img = 2;
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (aadhar == 2 && lic == 2) {
                    if (uploadAadharFileToDataBase()) {
                        if (uploadLicenseFileToDataBase()) {
                            if (aadhar == 0 || lic == 0) {
                                Toast.makeText(UploadPapers.this, "Upload required papers", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            startActivityAddCab();
                        } else
                            Toast.makeText(UploadPapers.this, "can't upload license try again!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(UploadPapers.this, "can't upload aadhar try again!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(UploadPapers.this, "Select All Papers", Toast.LENGTH_SHORT).show();*/
                if (aadhar == 0 || lic == 0) {
                    Toast.makeText(UploadPapers.this, "Upload required papers", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityAddCab();
            }
        });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();

            try {
                if (img == 1) {
                    bitmapAdh = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    img_aadhar.setImageBitmap(bitmapAdh);
//                    aadhar = 2;
                    uploadAadharFileToDataBase();
                } else if (img == 2) {
                    bitmapLic = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    img_driving.setImageBitmap(bitmapLic);
//                    lic = 2;
                    uploadLicenseFileToDataBase();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String convertToStringLic() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapLic.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    private String convertToStringAdh() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapAdh.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    private void uploadLicenseFileToDataBase() {
        final ProgressDialog mDialog = new ProgressDialog(UploadPapers.this);
        mDialog.setMessage("Uploading....");
        mDialog.setCancelable(false);
        mDialog.show();
        String image = convertToStringLic();
        String imageName = Common.register.getPhone();
        IURDriver apiInterface = RetrofitClient.getClient(Common.BASE_URL).create(IURDriver.class);
        Call<ImageDriver> call = apiInterface.uploadLicense(imageName, image);
        call.enqueue(new Callback<ImageDriver>() {
            @Override
            public void onResponse(Call<ImageDriver> call, Response<ImageDriver> response) {

//                ImageDriver img_pojo = response.body();
                Common.register.setLicenseImage("http://myinvented.com/urdriver/driver_license/" + Common.register.getPhone() + ".jpeg");
                lic = 1;
                mDialog.dismiss();
//                Log.d("Server Response", "" + img_pojo.getResponse());
            }

            @Override
            public void onFailure(Call<ImageDriver> call, Throwable t) {
                Log.d("Server Response", "" + t.toString());
                lic = 0;
                Toast.makeText(UploadPapers.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
        });
        /*if (lic == 1)
            return true;
        else
            return false;*/
    }

    private void uploadAadharFileToDataBase() {
        final ProgressDialog mDialog = new ProgressDialog(UploadPapers.this);
        mDialog.setMessage("Uploading....");
        mDialog.setCancelable(false);
        mDialog.show();
        String image = convertToStringAdh();
        String imageName = Common.register.getPhone();
        IURDriver apiInterface = RetrofitClient.getClient(Common.BASE_URL).create(IURDriver.class);
        Call<ImageDriver> call = apiInterface.uploadAadhar(imageName, image);
        call.enqueue(new Callback<ImageDriver>() {
            @Override
            public void onResponse(Call<ImageDriver> call, Response<ImageDriver> response) {

//                ImageDriver img_pojo = response.body();
                Common.register.setAadharImage("http://myinvented.com/urdriver/driver_aadhar/"
                        + Common.register.getPhone()
                        + ".jpeg");
                aadhar = 1;
//                Log.d("Server Response", "" + img_pojo.getResponse());
                mDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ImageDriver> call, Throwable t) {
                Log.d("Server Response", "" + t.toString());
                aadhar = 0;
                Toast.makeText(UploadPapers.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
        });
        /*if (aadhar == 1)
            return true;
        else
            return false;*/

    }

    @Override
    public void onBackPressed() {

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

    private void startActivityAddCab() {
        Intent intent = new Intent(UploadPapers.this, AddNewCab.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }
}
