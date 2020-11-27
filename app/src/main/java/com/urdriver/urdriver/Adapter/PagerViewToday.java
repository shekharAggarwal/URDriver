package com.urdriver.urdriver.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.gson.Gson;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.StartTrip;
import com.urdriver.urdriver.model.DataMessage;
import com.urdriver.urdriver.model.MyResponse;
import com.urdriver.urdriver.model.RequestData;
import com.urdriver.urdriver.model.Token;
import com.urdriver.urdriver.model.Trip;
import com.urdriver.urdriver.retrofit.IFCMService;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagerViewToday extends PagerAdapter {

    Context context;
    List<RequestData> image;
    LayoutInflater inflater;
    TextView txtDateTime, txtFullName, txtDate, txtTime, txtLocation, txtPhoneNumber, txt_Way;
    Button btnStartTrip, btnCancelTrip;
    IURDriver mService;
    LinearLayout txt_decorated;
    int p;

    public PagerViewToday(Context context, List<RequestData> image) {
        this.context = context;
        this.image = image;
        inflater = LayoutInflater.from(context);
        mService = Common.getAPI();
        Paper.init(context);
    }

    @Override
    public int getCount() {
        if (image != null)
            return image.size();
        else
            return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        View view = inflater.inflate(R.layout.item_schedule, container, false);
        txtDateTime = view.findViewById(R.id.txtDateTime);
        txtFullName = view.findViewById(R.id.txtFullName);
        txtDate = view.findViewById(R.id.txtDate);
        txtTime = view.findViewById(R.id.txtTime);
        txtLocation = view.findViewById(R.id.txtLocation);
        txtPhoneNumber = view.findViewById(R.id.txtPhoneNumber);
        btnStartTrip = view.findViewById(R.id.btnStartTrip);
        btnCancelTrip = view.findViewById(R.id.btnCancelTrip);
        txt_Way = view.findViewById(R.id.txt_Way);
        txt_decorated = view.findViewById(R.id.txt_decorated);

        btnCancelTrip.setVisibility(View.GONE);
        txtDateTime.setText("Today");
        txtFullName.setText(image.get(position).getFullName());
        txtDate.setText(image.get(position).getPickupDate());
        txtTime.setText(image.get(position).getPickupTime());
        txtLocation.setText(image.get(position).getSourceAddress() + "," + image.get(position).getSource() + " " + context.getResources().getString(R.string.arrow) + " " + image.get(position).getDestination());
        txtPhoneNumber.setText(image.get(position).getPhoneNumber() + "/" + image.get(position).getBookAccount());

        if (image.get(position).getCabType().equals("0"))
            txt_Way.setText("One Way");
        else
            txt_Way.setText("Round Way");

        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p = position;
                showCabsDialog();
            }
        });

        container.addView(view);
        return view;
    }

    private void showCabsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Start Trip");

        View view = LayoutInflater.from(context).inflate(R.layout.item_start_trip, null);

        final EditText edtReading = view.findViewById(R.id.edtReading);

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                if (edtReading.getText().toString().replaceAll(" ", "").length() == 0) {
                    edtReading.setError("Enter Cab Meter Reading");
                    edtReading.requestFocus();
                    return;
                }

                Date dt = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String currentTime = sdf.format(dt);
                mService.insertTrip(image.get(p).getFullName(),
                        image.get(p).getPhoneNumber(),
                        image.get(p).getEmail(),
                        image.get(p).getSourceAddress(),
                        image.get(p).getDestinationAddress(),
                        image.get(p).getPickupDate(),
                        image.get(p).getDropDate(),
                        image.get(p).getPickupTime(),
                        image.get(p).getSource(),
                        image.get(p).getDestination(),
                        image.get(p).getCabs(),
                        image.get(p).getBookAccount(),
                        image.get(p).getCabFare(),
                        image.get(p).getCabDriver(),
                        image.get(p).getCabStatus(),
                        image.get(p).getCabModel(),
                        image.get(p).getCabTnxId(),
                        currentTime,
                        "0000-00-00 00:00:00",
                        "3",
                        edtReading.getText().toString(),
                        "0",
                        "0",
                        image.get(p).getCabType())
                        .enqueue(new Callback<Trip>() {
                            @Override
                            public void onResponse(Call<Trip> call, Response<Trip> response) {

                                Common.trip = response.body();
                                Paper.book().write("TripId", Common.trip.getId());
                                Paper.book().write("TripPhone", Common.currentDriver.getPhone());
                                dialogInterface.dismiss();
                                if (image.get(p).getCabType().equals("0"))
                                    mService.updateRequest(image.get(p).getBookAccount(), image.get(p).getCabModel(), Integer.parseInt(
                                            image.get(p).getId()), 4, 0, "00")
                                            .enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    if (response.body() != null)
                                                        if (response.body().equalsIgnoreCase("ok")) {
                                                            sendNotificationToUser(image.get(p).getBookAccount(), edtReading.getText().toString());
                                                            sendNotification(image.get(p).getCabDriver(), edtReading.getText().toString());
                                                            context.startActivity(new Intent(context, StartTrip.class));
                                                        } else {
                                                            Toast.makeText(context, "" + response.body(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    else
                                                        Toast.makeText(context, "Try Again!", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onFailure(Call<String> call, Throwable t) {
                                                    Log.d("ERROR", t.getMessage());
                                                }
                                            });
                                else if (image.get(p).getCabType().equals("1"))
                                    mService.updateRequest(image.get(p).getBookAccount(), image.get(p).getCabModel(), Integer.parseInt(
                                            image.get(p).getId()), 4, 1, "00")
                                            .enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    if (response.body().equalsIgnoreCase("ok")) {
                                                        sendNotificationToUser(image.get(p).getBookAccount(), edtReading.getText().toString());
                                                        sendNotification(image.get(p).getCabDriver(), edtReading.getText().toString());
                                                        context.startActivity(new Intent(context, StartTrip.class));
                                                    } else {
                                                        Toast.makeText(context, "" + response.body(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<String> call, Throwable t) {
                                                    Log.d("ERROR", t.getMessage());
                                                }
                                            });
                            }

                            @Override
                            public void onFailure(Call<Trip> call, Throwable t) {
                                Log.d("error", t.getMessage());
                            }
                        });
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.black));
            }
        });
        dialog.show();
    }

    private void sendNotificationToUser(String phone, final String km) {
        mService.getToken(phone, "0")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {

                        Map<String, String> contentSend = new HashMap<>();
                        contentSend.put("title", "Start Trip");
                        contentSend.put("MapStatus", "Start");
                        contentSend.put("Phone2", Common.currentDriver.getPhone());
                        contentSend.put("message", "Your Trip is started at " + km);
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
                                                Toast.makeText(context, "Notification send", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("ERROR", new Gson().toJson(response.body()));
                                                Toast.makeText(context, "Notification send failed ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendNotification(String phone, final String km) {
        mService.getToken(phone, "1")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        //when we have Token , just send notification to this token

                        Map<String, String> contentSend = new HashMap<>();
                        contentSend.put("title", "Start Trip");
                        contentSend.put("message", "Your trip started at " + km);
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
                                                Toast.makeText(context, "Notification send", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("ERROR", new Gson().toJson(response.body()));
                                                Toast.makeText(context, "Notification send failed ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
