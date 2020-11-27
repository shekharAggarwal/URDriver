package com.urdriver.urdriver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.urdriver.urdriver.Adapter.CabImageAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.Services.NotificationHelper;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.ImageDriver;
import com.urdriver.urdriver.retrofit.IURDriver;
import com.urdriver.urdriver.retrofit.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewCab extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText actVehicle, actModel, txtSeating, txt_car_number;
    MaterialSpinner txt_Cab_type, txt_cab_city;
    Button btnAddCab;
    LinearLayout addImgCab;
    IURDriver mService;
    ConnectivityReceiver connectivityReceiver;
    Toolbar toolbar;
    RelativeLayout rootLayout;
    Bitmap bitmap;
    List<String> suggestList = new ArrayList<>(), CabType = new ArrayList<>();
    SpinKitView spin_kit;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_cab);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.
                    TRANSPARENT);
        }
        CabType.add("MINI");
        CabType.add("SEDAN");
        CabType.add("SUV");
        Common.setTop(this);
        Common.imageCab = new ArrayList<>();
        mService = Common.getAPI();
        actVehicle = findViewById(R.id.actVehicle);
        rootLayout = findViewById(R.id.root_layout);
        actModel = findViewById(R.id.actModel);
        txtSeating = findViewById(R.id.txtSeating);
        txt_car_number = findViewById(R.id.txt_car_number);
        txt_Cab_type = findViewById(R.id.spinner_Cab_type);
        addImgCab = findViewById(R.id.addImgCab);
        Common.mDotLayout = findViewById(R.id.linearLayout);
        txt_cab_city = findViewById(R.id.spinner_cab_city);
        Common.slideViewPage = findViewById(R.id.slideViewPage);
        btnAddCab = findViewById(R.id.btnAddCab);
        spin_kit = findViewById(R.id.spin_kit);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        FirebaseDatabase.getInstance().getReference("CityList")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        suggestList = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String list = postSnapshot.getValue(String.class);
                            suggestList.add(list);
                        }
                        txt_cab_city.setItems(suggestList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERRORDATA", databaseError.getMessage());
                    }
                });
        txt_Cab_type.setItems(CabType);
        addImgCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Common.PICK_IMAGE_REQUEST);
            }
        });

        btnAddCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sprite fadingCircle = new FadingCircle();
                spin_kit.setIndeterminateDrawable(fadingCircle);
                spin_kit.setVisibility(View.VISIBLE);
                btnAddCab.setEnabled(false);
                checkData();
            }
        });
    }

    private void checkData() {
        if (actVehicle.getText().toString().isEmpty()) {
            actVehicle.setError("Enter Brand Of Your Cab");
            actVehicle.requestFocus();
            spin_kit.setVisibility(View.GONE);
            btnAddCab.setEnabled(true);
            return;
        }
        if (actModel.getText().toString().isEmpty()) {
            actModel.setError("Enter Model Of Your Cab");
            actModel.requestFocus();
            spin_kit.setVisibility(View.GONE);
            btnAddCab.setEnabled(true);
            return;
        }
        if (txtSeating.getText().toString().isEmpty()) {
            txtSeating.setError("Enter Seating Of Your Cab");
            txtSeating.requestFocus();
            spin_kit.setVisibility(View.GONE);
            btnAddCab.setEnabled(true);
            return;
        }
        if (txt_car_number.getText().toString().isEmpty()) {
            txt_car_number.setError("Enter Number Of Your Cab");
            txt_car_number.requestFocus();
            spin_kit.setVisibility(View.GONE);
            btnAddCab.setEnabled(true);
            return;
        }
        if (txt_Cab_type.getText().toString().isEmpty()) {
            txt_Cab_type.setError("Enter Cab Type");
            txt_Cab_type.requestFocus();
            spin_kit.setVisibility(View.GONE);
            btnAddCab.setEnabled(true);
            return;
        }

        if (txt_cab_city.getText().toString().isEmpty()) {
            txt_cab_city.setError("Enter Cab City");
            txt_cab_city.requestFocus();
            spin_kit.setVisibility(View.GONE);
            btnAddCab.setEnabled(true);
            return;
        }

        if (Common.imageCab.size() < 3) {
            Toast.makeText(this, "Upload at least 3 image of cab", Toast.LENGTH_SHORT).show();
            spin_kit.setVisibility(View.GONE);
            btnAddCab.setEnabled(true);
            return;
        }

        /*for (int i = 0; i < list.size(); i++) {
            boolean isCheck = false;
            for (int j = 0; j < Common.imageCab.size(); j++) {
                if (Common.imageCab.get(j).equals(list.get(i))) {
                    isCheck = false;
                    break;
                } else
                    isCheck = true;
            }
            if (isCheck) {
                FirebaseStorage.getInstance().getReferenceFromUrl(list.get(i)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR", e.getMessage());
                    }
                });
            }
        }*/

        final String cabImage = new Gson().toJson(Common.imageCab);

        if (Common.baseActivity != null && Common.baseActivity.equalsIgnoreCase("info")) {

            mService.registerNewDriver(Common.register.getName(),
                    Common.register.getEmail(),
                    Common.register.getPhone(),
                    Common.register.getPassword(),
                    "http",
                    Common.register.getAadharNumber(),
                    Common.register.getAadharImage(),
                    Common.register.getLicenseImage(),
                    1)
                    .enqueue(new Callback<Driver>() {
                        @Override
                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                            Common.currentDriver = response.body();
                            assert Common.currentDriver != null;
                            if (Common.currentDriver.getError_msg() == null) {

                                mService.addNewCab(actVehicle.getText().toString(),
                                        actModel.getText().toString(),
                                        cabImage,
                                        txtSeating.getText().toString(),
                                        txt_Cab_type.getText().toString().toUpperCase(),
                                        txt_cab_city.getText().toString().toUpperCase(),
                                        txt_car_number.getText().toString(),
                                        Common.currentDriver.getPhone())
                                        .enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if (response.body() != null)
                                                    if (response.body().equals("ok")) {

                                                        Intent intent = new Intent(AddNewCab.this, DriversRequest.class);
                                                        spin_kit.setVisibility(View.GONE);
                                                        btnAddCab.setEnabled(true);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        finish();
                                                        startActivity(intent);
                                                        Common.baseActivity = null;

                                                    } else {
                                                        spin_kit.setVisibility(View.GONE);
                                                        btnAddCab.setEnabled(true);
                                                        Snackbar.make(rootLayout, response.body(), Snackbar.LENGTH_LONG).show();
                                                    }
                                                else {
                                                    spin_kit.setVisibility(View.GONE);
                                                    btnAddCab.setEnabled(true);
                                                    Log.d("ERROR", new Gson().toJson(response.body()));
                                                    Toast.makeText(AddNewCab.this, "ERROR", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                Log.d("ERROR", t.getMessage());
                                                Toast.makeText(AddNewCab.this, "Try Again", Toast.LENGTH_SHORT).show();
                                                spin_kit.setVisibility(View.GONE);
                                                btnAddCab.setEnabled(true);

                                            }
                                        });
                                Common.register = null;
                            } else {

                                spin_kit.setVisibility(View.GONE);
                                btnAddCab.setEnabled(true);
                                Toast.makeText(AddNewCab.this, "" + Common.currentDriver.getError_msg(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());

                            spin_kit.setVisibility(View.GONE);
                            btnAddCab.setEnabled(true);
                            Toast.makeText(AddNewCab.this, "Try Later", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            mService.registerNewDriver(Common.register.getName(),
                    Common.register.getEmail(),
                    Common.register.getPhone(),
                    Common.register.getPassword(),
                    "http",
                    Common.register.getAadharNumber(),
                    Common.register.getAadharImage(),
                    Common.register.getLicenseImage(),
                    0)
                    .enqueue(new Callback<Driver>() {
                        @Override
                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                            Common.currentDriver = response.body();
                            assert Common.currentDriver != null;
                            if (Common.currentDriver.getError_msg() == null) {

                                mService.addNewCab(actVehicle.getText().toString(),
                                        actModel.getText().toString(),
                                        cabImage,
                                        txtSeating.getText().toString(),
                                        txt_Cab_type.getText().toString().toUpperCase(),
                                        txt_cab_city.getText().toString().toUpperCase(),
                                        txt_car_number.getText().toString(),
                                        Common.currentDriver.getPhone())
                                        .enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if (response.body() != null)
                                                    if (response.body().equals("ok")) {
                                                        buildNotification();
                                                        Intent intent = new Intent(AddNewCab.this, LoginActivity.class);
                                                        spin_kit.setVisibility(View.GONE);
                                                        btnAddCab.setEnabled(true);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        finish();
                                                        startActivity(intent);
                                                    } else {
                                                        spin_kit.setVisibility(View.GONE);
                                                        btnAddCab.setEnabled(true);
                                                        Snackbar.make(rootLayout, response.body(), Snackbar.LENGTH_LONG).show();
                                                    }
                                                else {
                                                    spin_kit.setVisibility(View.GONE);
                                                    btnAddCab.setEnabled(true);
                                                    Log.d("ERROR", new Gson().toJson(response.body()));
                                                    Toast.makeText(AddNewCab.this, "ERROR", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                Log.d("ERROR", t.getMessage());
                                                Toast.makeText(AddNewCab.this, "Try Again", Toast.LENGTH_SHORT).show();
                                                spin_kit.setVisibility(View.GONE);
                                                btnAddCab.setEnabled(true);

                                            }
                                        });
                                Common.register = null;
                            } else {

                                spin_kit.setVisibility(View.GONE);
                                btnAddCab.setEnabled(true);
                                Toast.makeText(AddNewCab.this, "" + Common.currentDriver.getError_msg(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());

                            spin_kit.setVisibility(View.GONE);
                            btnAddCab.setEnabled(true);
                            Toast.makeText(AddNewCab.this, "Try Later", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        Call<ImageDriver> call = apiInterface.UploadCabImage(imageName, image, Common.register.getPhone());
        final int[] a = {1};
        call.enqueue(new Callback<ImageDriver>() {
            @Override
            public void onResponse(Call<ImageDriver> call, Response<ImageDriver> response) {
                if (a[0] == 1) {
                    mDialog.dismiss();
                    Toast.makeText(AddNewCab.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                    Common.imageCab.add("http://myinvented.com/urdriver/cab_image/" + Common.register.getPhone() + "/" + imageName + ".jpeg");
                    Common.mDotLayout.setText("");
                    Common.slideViewPage.removeAllViews();
                    CabImageAdapter cabImageAdapter = new CabImageAdapter(AddNewCab.this, Common.imageCab);
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
                Toast.makeText(AddNewCab.this, "ERROR try Again", Toast.LENGTH_SHORT).show();
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
    protected void onStop() {
        super.onStop();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }

    private void buildNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //From API 26 ,we need implement Notification cancel
            NotificationHelper helper;
            Notification.Builder builder;
            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 1, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), defaultSoundUri);
            r.play();
            helper = new NotificationHelper(AddNewCab.this);
            builder = helper.getURDriveNotification("Registration", "Admin will response with in 24hrs", defaultSoundUri, fullScreenPendingIntent);

            helper.getManager().notify(new Random().nextInt(), builder.build());
        } else {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), defaultSoundUri);
            r.play();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(AddNewCab.this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Registration")
                    .setContentText("Admin will response with in 24hrs")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);
            NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            noti.notify(new Random().nextInt(), builder.build());

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
    }
}
