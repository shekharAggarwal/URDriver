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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.urdriver.urdriver.Adapter.AllItemAdapter;
import com.urdriver.urdriver.Adapter.UserAdminAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.DriverRequestModel;
import com.urdriver.urdriver.model.Trip;
import com.urdriver.urdriver.model.UserDetails;
import com.urdriver.urdriver.model.UserForAdminModel;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    String[] DataDriver = {"Detail View", "Status", "Update", "Delete", "ADD", "Payment"}, DataUser = {"Detail View", "Status"};

    Toolbar toolbar;
    EditText phone;
    MaterialSpinner spinner;
    Button btnGo;
    RecyclerView recycler_view;
    TextView txtUSER;
    int spinnerPostion = 0;

    int statusCode = 0;
    IURDriver mService;

    ConnectivityReceiver connectivityReceiver;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

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

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        phone = findViewById(R.id.phone);
        spinner = findViewById(R.id.spinner);
        btnGo = findViewById(R.id.btnGo);

        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setHasFixedSize(true);
        txtUSER = findViewById(R.id.txtUSER);
        mService = Common.getAPI();
    }

    private void UserFunction() {
        switch (spinnerPostion) {
            case 0:
                statusCode = 1;
                mService.getUsersInfoForAdmin(phone.getText().toString(), "0").enqueue(new Callback<UserDetails>() {
                    @Override
                    public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                        if (response.body() != null) {
                            List<UserDetails> list = new ArrayList<>();
                            list.add(response.body());
                            recycler_view.removeAllViews();
                            UserAdminAdapter userAdminAdapter = new UserAdminAdapter(InfoActivity.this, list);
                            recycler_view.setAdapter(userAdminAdapter);
                            btnGo.setEnabled(true);
                            phone.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDetails> call, Throwable t) {
                        Log.d("ERROR", t.getMessage());
                        Toast.makeText(InfoActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        btnGo.setEnabled(true);
                        phone.setText("");
                        onBackPressed();
                    }
                });
                break;
            case 1:
                mService.getUsersCheckStatus(phone.getText().toString(), "1").enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body() != null) {
                            if (Integer.parseInt(response.body()) > 0) {
                                mService.getUsersInfoForAdminStatus(phone.getText().toString(), "3").enqueue(new Callback<UserForAdminModel>() {
                                    @Override
                                    public void onResponse(Call<UserForAdminModel> call, Response<UserForAdminModel> response) {
                                        if (response.body() != null) {
                                            btnGo.setEnabled(true);
                                            Intent intent = new Intent(InfoActivity.this, UserForAdmin.class);
                                            Common.userForAdminModel = response.body();
                                            intent.putExtra("TEXTPHONE", phone.getText().toString());
                                            startActivity(intent);
                                            phone.setText("");
                                        } else {
                                            Toast.makeText(InfoActivity.this, "ERROR while getting data try again!!", Toast.LENGTH_SHORT).show();
                                            phone.setText("");
                                            btnGo.setEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserForAdminModel> call, Throwable t) {
                                        Toast.makeText(InfoActivity.this, "ERROR while getting data try again!!", Toast.LENGTH_SHORT).show();
                                        phone.setText("");
                                        btnGo.setEnabled(true);
                                    }
                                });

                            } else {
                                Toast.makeText(InfoActivity.this, "Trip not started yet!!", Toast.LENGTH_SHORT).show();
                                phone.setText("");
                                btnGo.setEnabled(true);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("ERROR", t.getMessage());
                        Toast.makeText(InfoActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        btnGo.setEnabled(true);
                        phone.setText("");
                    }
                });
                break;
        }
    }

    private void driverFunction() {
        switch (spinnerPostion) {
            case 0:
                mService.getCabByPhone(phone.getText().toString()).enqueue(new Callback<DriverRequestModel>() {
                    @Override
                    public void onResponse(Call<DriverRequestModel> call, Response<DriverRequestModel> response) {
                        Common.driverRequestModel = response.body();
                        if (response.body() != null) {
                            if (response.body().getDriverStatus() == 0) {
                                Toast.makeText(InfoActivity.this, "Driver request is not accepted ", Toast.LENGTH_SHORT).show();
                                phone.setText("");
                            } else if (response.body().getDriverStatus() == 2) {
                                Toast.makeText(InfoActivity.this, "Driver request denied", Toast.LENGTH_SHORT).show();
                                phone.setText("");
                            } else {
                                Intent intent = new Intent(InfoActivity.this, ShowDriverRequest.class);
                                intent.putExtra("TEXT", "INFO");
                                startActivity(intent);
                                phone.setText("");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DriverRequestModel> call, Throwable t) {
                        Log.d("ERROR", t.getMessage());
                        Toast.makeText(InfoActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        phone.setText("");
                    }
                });
                break;
            case 1:
                mService.getDriverInfoCheck(phone.getText().toString(), "1").enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body() != null) {
                            if (Integer.parseInt(response.body()) > 0) {
                                Intent intent = new Intent(InfoActivity.this, DriverStatus.class);
                                intent.putExtra("TEXT", phone.getText().toString());
                                startActivity(intent);
                                phone.setText("");
                            } else {
                                Toast.makeText(InfoActivity.this, "Trip not started yet!!", Toast.LENGTH_SHORT).show();
                                phone.setText("");
                            }
                        } else {
                            Toast.makeText(InfoActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                            phone.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("ERROR", t.getMessage());
                        Toast.makeText(InfoActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        phone.setText("");
                    }
                });
                break;
            case 2:
                mService.getCabByPhone(phone.getText().toString()).enqueue(new Callback<DriverRequestModel>() {
                    @Override
                    public void onResponse(Call<DriverRequestModel> call, Response<DriverRequestModel> response) {
                        Common.driverRequestModel = response.body();
                        if (response.body() != null) {
                            if (response.body().getDriverStatus() == 0) {
                                Toast.makeText(InfoActivity.this, "Driver request is not accepted ", Toast.LENGTH_SHORT).show();
                                phone.setText("");
                            } else if (response.body().getDriverStatus() == 2) {
                                Toast.makeText(InfoActivity.this, "Driver request denied", Toast.LENGTH_SHORT).show();
                                phone.setText("");
                            } else {
                                Common.baseActivity = "Info";
                                startActivity(new Intent(InfoActivity.this, UpdateCab.class));
                                phone.setText("");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DriverRequestModel> call, Throwable t) {
                        Log.d("ERROR", t.getMessage());
                        Toast.makeText(InfoActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        phone.setText("");
                    }
                });
                break;
            case 3:
                mService.getCabByPhone(phone.getText().toString()).enqueue(new Callback<DriverRequestModel>() {
                    @Override
                    public void onResponse(Call<DriverRequestModel> call, Response<DriverRequestModel> response) {
                        Common.driverRequestModel = response.body();
                        if (response.body() != null) {
                            if (response.body().getDriverStatus() == 0) {
                                Toast.makeText(InfoActivity.this, "Driver request is not accepted ", Toast.LENGTH_SHORT).show();
                                phone.setText("");
                            } else if (response.body().getDriverStatus() == 2) {
                                Toast.makeText(InfoActivity.this, "Driver request denied", Toast.LENGTH_SHORT).show();
                                phone.setText("");
                            } else {
                                Intent intent = new Intent(InfoActivity.this, ShowDriverRequest.class);
                                intent.putExtra("TEXT", "DELETE");
                                startActivity(intent);
                                phone.setText("");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DriverRequestModel> call, Throwable t) {
                        Log.d("ERROR", t.getMessage());
                        Toast.makeText(InfoActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        phone.setText("");
                    }
                });
                break;
            case 4:
                Common.baseActivity = "Info";
                phone.setText("");
                btnGo.setEnabled(true);
                startActivity(new Intent(InfoActivity.this, RegisterActivity.class));
                break;
            case 5:
                btnGo.setEnabled(true);
                compositeDisposable.add(mService.getTripData("3", phone.getText().toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<Trip>>() {
                            @Override
                            public void accept(List<Trip> trips) throws Exception {
                                Common.tripList = trips;
                                phone.setText("");
                                startActivity(new Intent(InfoActivity.this, PaymentActivity.class));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.d("Error", throwable.getMessage());
                            }
                        }));
                break;
        }
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
        ForResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        if (!connectivityReceiver.isOrderedBroadcast()) {
            registerReceiver(connectivityReceiver, intentFilter);
            MyApplication.getInstance().setConnectivityListener(this);
        }

    }

    @Override
    public void onBackPressed() {

        if (Common.check.equalsIgnoreCase("user")) {
            if (statusCode == 1) {
                statusCode = 0;
                compositeDisposable.add(mService.getUsersForAdmin("2")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<UserDetails>>() {
                            @Override
                            public void accept(List<UserDetails> userDetails) throws Exception {
                                if (userDetails.size() > 0) {
                                    recycler_view.removeAllViews();
                                    UserAdminAdapter userAdminAdapter = new UserAdminAdapter(InfoActivity.this, userDetails);
                                    recycler_view.setAdapter(userAdminAdapter);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.d("ERROR", throwable.getMessage());

                            }
                        }));
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
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

    void ForResume() {

        if (getIntent().getStringExtra("TEXT") != null) {
            Common.check = getIntent().getStringExtra("TEXT");
        }
        if (Common.check.equalsIgnoreCase("driver")) {
            //driver data
            spinner.setItems(DataDriver);

            compositeDisposable.add(mService.getAllDriver()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<DriverRequestModel>>() {
                        @Override
                        public void accept(List<DriverRequestModel> driverRequestModels) throws Exception {
                            AllItemAdapter allItemAdapter = new AllItemAdapter(InfoActivity.this, driverRequestModels);
                            recycler_view.setAdapter(allItemAdapter);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                        }
                    }));


            spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                    spinnerPostion = position;
                    if (position == 4)
                        phone.setVisibility(View.GONE);
                    else
                        phone.setVisibility(View.VISIBLE);

                }
            });

            btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnGo.setEnabled(false);
                    if (spinnerPostion != 4) {
                        if (phone.getText().toString().isEmpty() || phone.getText().toString().length() < 10) {
                            phone.setError("Enter Correct Phone Number");
                            phone.requestFocus();
                            btnGo.setEnabled(true);
                            return;
                        }

                        if (Common.check != null && Common.check.equalsIgnoreCase("driver"))
                            mService.checkPhoneExist(phone.getText().toString(), "0")
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if (response.body().equalsIgnoreCase("ok")) {
                                                driverFunction();
                                                btnGo.setEnabled(true);
                                            } else {
                                                Toast.makeText(InfoActivity.this, "Not Exists", Toast.LENGTH_SHORT).show();
                                                btnGo.setEnabled(true);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            Log.d("ERROR", t.getMessage());
                                            Toast.makeText(InfoActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                            btnGo.setEnabled(true);
                                        }
                                    });
                        else {
                            Toast.makeText(InfoActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            btnGo.setEnabled(true);
                        }
                    } else {
                        driverFunction();
                        btnGo.setEnabled(true);
                    }
                }
            });
        } else if (Common.check.equalsIgnoreCase("user")) {
            txtUSER.setText("All Users");
            //info user
            spinner.setItems(DataUser);

            compositeDisposable.add(mService.getUsersForAdmin("2")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<UserDetails>>() {
                        @Override
                        public void accept(List<UserDetails> userDetails) throws Exception {
                            if (userDetails.size() > 0) {
                                UserAdminAdapter userAdminAdapter = new UserAdminAdapter(InfoActivity.this, userDetails);
                                recycler_view.setAdapter(userAdminAdapter);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());

                        }
                    }));

            spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                    spinnerPostion = position;
                }
            });

            btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnGo.setEnabled(false);

                    if (phone.getText().toString().isEmpty() || phone.getText().toString().length() < 10) {
                        phone.setError("Enter Correct Phone Number");
                        phone.requestFocus();
                        btnGo.setEnabled(true);
                        return;
                    }

                    mService.checkPhoneExist(phone.getText().toString(), "1").enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.body() != null) {
                                if (response.body().equalsIgnoreCase("ok")) {
                                    UserFunction();
                                } else {
                                    Toast.makeText(InfoActivity.this, "Phone Not Exists", Toast.LENGTH_SHORT).show();
                                    btnGo.setEnabled(true);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            Toast.makeText(InfoActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                            btnGo.setEnabled(true);
                        }
                    });
                }
            });
        }
    }
}
