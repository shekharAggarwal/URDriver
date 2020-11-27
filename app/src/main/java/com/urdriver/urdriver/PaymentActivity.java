package com.urdriver.urdriver;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.urdriver.urdriver.Adapter.PaymentAdminAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.model.PaymentData;
import com.urdriver.urdriver.model.Trip;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    RecyclerView recycler_payment;
    CheckBox checkbox;
    Button btnPay;
    Toolbar toolbar;
    List<Trip> trips = new ArrayList<>(), list = new ArrayList<>(), tripsList;
    PaymentData paymentData;
    PaymentAdminAdapter paymentAdminAdapter;
    IURDriver mService = Common.getAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

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

        Common.TotalAmount = findViewById(R.id.txtCashPayment);
        Common.TotalAmount.setText("0.0");
        checkbox = findViewById(R.id.checkbox);
        btnPay = findViewById(R.id.btnPay);
        recycler_payment = findViewById(R.id.recycler_payment);
        recycler_payment.setLayoutManager(new LinearLayoutManager(this));
        recycler_payment.setHasFixedSize(true);
        for (int i = 0; i < Common.tripList.size(); i++) {
            paymentData = new Gson().fromJson(Common.tripList.get(i).getCabTnxId(), PaymentData.class);
            if (!paymentData.getCheckBox())
                trips.add(Common.tripList.get(i));
        }
        paymentAdminAdapter = new PaymentAdminAdapter(this, trips);
        recycler_payment.setAdapter(paymentAdminAdapter);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.Price = 0;
                    list = new ArrayList<>();
                    for (int i = 0; i < trips.size(); i++) {
                        paymentData = new Gson().fromJson(trips.get(i).getCabTnxId(), PaymentData.class);
//                        if (!paymentData.getCheckBox()) {
                        paymentData.setCheckBox(true);
                        trips.get(i).setCabTnxId(new Gson().toJson(paymentData));
//                        }
                        list.add(trips.get(i));
                        Common.Price = Common.Price + getAmount(trips.get(i).getCabFare(), paymentData.getTnxAmount());
                    }
                    Common.TotalAmount.setText("" + Common.Price);
                    recycler_payment.removeAllViewsInLayout();
                    paymentAdminAdapter = new PaymentAdminAdapter(PaymentActivity.this, list);
                    recycler_payment.setAdapter(paymentAdminAdapter);
                } else {
                    trips = new ArrayList<>();
                    Common.Price = 0;
                    for (int i = 0; i < list.size(); i++) {
                        paymentData = new Gson().fromJson(list.get(i).getCabTnxId(), PaymentData.class);
//                        if (!paymentData.getCheckBox()) {
                        paymentData.setCheckBox(false);
                        list.get(i).setCabTnxId(new Gson().toJson(paymentData));
//                        }
                        trips.add(list.get(i));
                    }
                    Common.TotalAmount.setText("" + Common.Price);
                    recycler_payment.removeAllViewsInLayout();
                    paymentAdminAdapter = new PaymentAdminAdapter(PaymentActivity.this, trips);
                    recycler_payment.setAdapter(paymentAdminAdapter);
                }
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.Price != 0) {
                    tripsList = paymentAdminAdapter.getList();
                    for (int i = 0; i < tripsList.size(); i++) {
                        mService.updateTxnIdTrip(tripsList.get(i).getId(), tripsList.get(i).getCabTnxId()).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (!response.body().equals("ok")) {
                                    Toast.makeText(PaymentActivity.this, "Error in updating data on server", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d("ERROR", t.getMessage());
                            }
                        });
                    }
                    onBackPressed();
                } else {
                    Toast.makeText(PaymentActivity.this, "Select the trip", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private double getAmount(String cabFare, String tnxAmount) {
        Double a = Double.parseDouble(tnxAmount) - (Double.parseDouble(cabFare) / 10);
        return a;
    }
}
