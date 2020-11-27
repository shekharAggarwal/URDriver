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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.Cab;
import com.urdriver.urdriver.model.DataMessage;
import com.urdriver.urdriver.model.MyResponse;
import com.urdriver.urdriver.model.PaymentData;
import com.urdriver.urdriver.model.Token;
import com.urdriver.urdriver.model.Trip;
import com.urdriver.urdriver.retrofit.IFCMService;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    IURDriver mService = Common.getAPI();

    ConnectivityReceiver connectivityReceiver;
    TextView amountDis, date_time, distance, tax, totalAmount, paidAmount, txtNightCharges, txtDiscount;
    Button btn_payment;
    float cabTotal;
    String pric;
    Trip trip;
    SpinKitView spin_kit;
    int nc = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Common.setTop(this);

        amountDis = findViewById(R.id.amount);
        date_time = findViewById(R.id.date_time);
        distance = findViewById(R.id.distance);
        tax = findViewById(R.id.tax);
        totalAmount = findViewById(R.id.totalAmount);
        txtNightCharges = findViewById(R.id.txtNightCharges);
        txtDiscount = findViewById(R.id.txtDiscount);
        paidAmount = findViewById(R.id.paidAmount);
        btn_payment = findViewById(R.id.btn_payment);
        spin_kit = findViewById(R.id.spin_kit);

        mService.getTrip(Common.id).enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                trip = response.body();
                assert trip != null;
                date_time.setText(getDate(trip.getStartTrip()) + " " + getResources().getString(R.string.arrow) + " " + getDate(trip.getDropTrip()));
                distance.setText((Integer.parseInt(trip.getDropMeter()) - Integer.parseInt(trip.getPickUpMeter())) + " Km");
                tax.setText(" +Rs " + trip.getTripToll());
                FirebaseDatabase.getInstance().getReference("NightStay")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                nc = dataSnapshot.getValue(Integer.class);
                                if (trip.getDropDate().contains("0000-00-00")) {
                                    pric = String.valueOf(Integer.parseInt(calculateOneWayPrice(String.valueOf((Integer.parseInt(trip.getDropMeter()) - Integer.parseInt(trip.getPickUpMeter()))),
                                            trip.getCabs())) + Integer.parseInt(trip.getTripToll()) + (Integer.parseInt(trip.getNightStay()) * nc));
                                    Double discount = Double.parseDouble(pric) - (Double.parseDouble(pric) * 95) / 100;
                                    pric = String.valueOf(Double.parseDouble(pric) - (discount));
                                    totalAmount.setText("Rs " + pric);
                                    txtNightCharges.setText(" +Rs" + Integer.parseInt(trip.getNightStay()) * nc);
                                    txtDiscount.setText(" -Rs" + (discount));
                                } else {
                                    pric = String.valueOf(Integer.parseInt(calculateRoundWayPrice(String.valueOf((Integer.parseInt(trip.getDropMeter()) - Integer.parseInt(trip.getPickUpMeter()))),
                                            String.valueOf(Common.getCountOfDays(trip.getPickupDate(), trip.getDropDate())),
                                            trip.getCabs())) + Integer.parseInt(trip.getTripToll()) + (Integer.parseInt(trip.getNightStay()) * nc));
                                    Double discount = Double.parseDouble(pric) - (Double.parseDouble(pric) * 95) / 100;
                                    pric = String.valueOf(Double.parseDouble(pric) - (discount));
                                    totalAmount.setText("Rs " + pric);
                                    txtNightCharges.setText(" +Rs" + Integer.parseInt(trip.getNightStay()) * nc);
                                    txtDiscount.setText(" -Rs" + (discount));
                                }
                                paidAmount.setText(" -Rs " + trip.getCabFare());
                                try {
                                    String amount1 = Float.toString(Float.parseFloat(pric) - Float.parseFloat(trip.getCabFare()));
                                    amountDis.setText(" Rs " + amount1);
                                } catch (NumberFormatException ex) { // handle your exception
                                    Log.d("ERROR", ex.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("ERROR", databaseError.getMessage());
                            }
                        });
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
            }
        });

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_payment.setEnabled(false);
                Sprite fadingCircle = new FadingCircle();
                spin_kit.setIndeterminateDrawable(fadingCircle);
                spin_kit.setVisibility(View.VISIBLE);
                btn_payment.setEnabled(false);
                PaymentData paymentData = new Gson().fromJson(trip.getCabTnxId(), PaymentData.class);
                paymentData.setCash("" + amountDis.getText().toString());
                paymentData.setCheckBox(false);
                mService.updateFare("5", Common.id, pric, new Gson().toJson(paymentData)).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (!response.body().equals("OK")) {
                            Toast.makeText(InvoiceActivity.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                            Sprite fadingCircle = new FadingCircle();
                            spin_kit.setIndeterminateDrawable(fadingCircle);
                            spin_kit.setVisibility(View.GONE);
                            btn_payment.setEnabled(true);
                        } else {
                            Sprite fadingCircle = new FadingCircle();
                            spin_kit.setIndeterminateDrawable(fadingCircle);
                            spin_kit.setVisibility(View.GONE);
                            btn_payment.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        Sprite fadingCircle = new FadingCircle();
                        spin_kit.setIndeterminateDrawable(fadingCircle);
                        spin_kit.setVisibility(View.GONE);
                        btn_payment.setEnabled(true);
                    }
                });

                spin_kit.setVisibility(View.VISIBLE);
                btn_payment.setEnabled(false);

                mService.updateTripStatusBooking("2", Common.id, "1").enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("OK")) {
                            sendNotificationToUser(trip.getBookAccount());
                            Paper.book().delete("TripId");
                            Paper.book().delete("TripPhone");
                            Sprite fadingCircle = new FadingCircle();
                            spin_kit.setIndeterminateDrawable(fadingCircle);
                            spin_kit.setVisibility(View.GONE);
                            btn_payment.setEnabled(true);
                        } else {
                            Toast.makeText(InvoiceActivity.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                            Sprite fadingCircle = new FadingCircle();
                            spin_kit.setIndeterminateDrawable(fadingCircle);
                            spin_kit.setVisibility(View.GONE);
                            btn_payment.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("ERROR", t.getMessage());
                        Sprite fadingCircle = new FadingCircle();
                        spin_kit.setIndeterminateDrawable(fadingCircle);
                        spin_kit.setVisibility(View.GONE);
                        btn_payment.setEnabled(true);
                    }
                });

            }
        });
    }


    private String calculateOneWayPrice(String Distance, String Cabs) {
        float totalDistance;
        Cab cab = new Gson().fromJson(Cabs, Cab.class);

        totalDistance = Float.parseFloat(Distance);

        cabTotal = (Float.parseFloat(String.valueOf(cab.cabPrice)) * totalDistance);
        return String.valueOf(Math.round(cabTotal));
    }

    private String calculateRoundWayPrice(String Distance, String Day, String Cabs) {

        float totalDistance;
        //total distance
        totalDistance = (250 * Integer.parseInt(Day));

        Cab cab = new Gson().fromJson(Cabs, Cab.class);

        if (totalDistance < (Integer.parseInt(Distance)))
            totalDistance = Float.parseFloat(Distance);

        cabTotal = (Float.parseFloat(String.valueOf(cab.cabPrice)) * totalDistance);
        return String.valueOf(Math.round(cabTotal));

    }

    @Override
    public void onBackPressed() {

    }

    private String getDate(String date) {
        String[] arr = date.split(" ", 2);
        return arr[0] + "/" + arr[1];
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

    private void sendNotificationToUser(String phone) {
        mService.getToken(phone, "0")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        //when we have Token , just send notification to this token

                        Map<String, String> contentSend = new HashMap<>();
                        contentSend.put("title", "Ride Status ok");
                        contentSend.put("Phone1", Common.currentDriver.getPhone());
                        DataMessage dataMessage = new DataMessage();
                        if (response.body().getToken() != null)
                            dataMessage.setTo(response.body().getToken());
                        dataMessage.setData(contentSend);

                        IFCMService ifcmService = Common.getGetFCMService();
                        ifcmService.sendNotification(dataMessage)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success == 1) {
                                                Log.d("ERROR", new Gson().toJson(response.body()));
                                                finish();
                                                startActivity(new Intent(InvoiceActivity.this, ScheduleActivity.class));
                                                Toast.makeText(InvoiceActivity.this, "Ride Done", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("ERROR", new Gson().toJson(response.body()));
                                                Toast.makeText(InvoiceActivity.this, "Failed to update ride", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Toast.makeText(InvoiceActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
//                            Log.d("ERROR",t.getMessage());
                        Toast.makeText(InvoiceActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
