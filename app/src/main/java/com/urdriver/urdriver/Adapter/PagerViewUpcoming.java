package com.urdriver.urdriver.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.gson.Gson;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.model.DataMessage;
import com.urdriver.urdriver.model.MyResponse;
import com.urdriver.urdriver.model.RequestData;
import com.urdriver.urdriver.model.Token;
import com.urdriver.urdriver.retrofit.IFCMService;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.urdriver.urdriver.Common.Common.requestList;

public class PagerViewUpcoming extends PagerAdapter {

    Context context;
    List<RequestData> image;
    LayoutInflater inflater;
    TextView txtDateTime, txtFullName, txtDate, txtTime, txtLocation, txtPhoneNumber, txt_Way;
    Button btnStartTrip, btnCancelTrip;
    LinearLayout txt_decorated;
    IURDriver mService;

    public PagerViewUpcoming(Context context, List<RequestData> image) {
        this.context = context;
        this.image = image;
        inflater = LayoutInflater.from(context);
        mService = Common.getAPI();
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
        txt_Way = view.findViewById(R.id.txt_Way);
        txtLocation = view.findViewById(R.id.txtLocation);
        txtPhoneNumber = view.findViewById(R.id.txtPhoneNumber);
        btnStartTrip = view.findViewById(R.id.btnStartTrip);
        btnCancelTrip = view.findViewById(R.id.btnCancelTrip);
        txt_decorated = view.findViewById(R.id.txt_decorated);
        btnStartTrip.setVisibility(View.GONE);

        txtDateTime.setText(image.get(position).getPickupDate() + " / " + image.get(position).getPickupTime());
        txtFullName.setText(image.get(position).getFullName());
        txtDate.setText(image.get(position).getPickupDate());
        txtTime.setText(image.get(position).getPickupTime());
        txtLocation.setText(image.get(position).getSourceAddress() + "," + image.get(position).getSource() + " " + context.getResources().getString(R.string.arrow) + " " + image.get(position).getDestination());
        txt_Way.setText(image.get(position).getCabType().equals("0") ? "One Way" : "Round Way");
        txtPhoneNumber.setText(image.get(position).getPhoneNumber() + "/" + image.get(position).getBookAccount());

        btnCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCancelTrip.setEnabled(false);
                if (image.get(position).getCabType().equals("0")) {
                    mService.CancelByDriver(Common.currentDriver.getPhone(), "0", "" + image.get(position).getId())
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.body() != null) {
                                        if (response.body().equalsIgnoreCase("ok")) {
                                            sendNotificationToUser(image.get(position).getBookAccount());
                                            btnCancelTrip.setEnabled(true);
                                            sendNotification(Common.currentDriver.getPhone());
                                            Common.requestList.remove(position);
                                            PagerViewUpcoming pagerViewUpcoming = new PagerViewUpcoming(context, requestList);
                                            Common.CycleViewPager.setAdapter(pagerViewUpcoming);
                                        } else {
                                            Toast.makeText(context, "5", Toast.LENGTH_SHORT).show();
                                            btnCancelTrip.setEnabled(false);
                                            Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    btnCancelTrip.setEnabled(true);
                                    Toast.makeText(context, "6", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    mService.CancelByDriver(Common.currentDriver.getPhone(), "1", "" + image.get(position).getId())
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.body() != null) {
                                        if (response.body().equalsIgnoreCase("ok")) {
                                            btnCancelTrip.setEnabled(true);
                                            Common.requestList.remove(position);
                                            PagerViewUpcoming pagerViewUpcoming = new PagerViewUpcoming(context, requestList);
                                            Common.CycleViewPager.setAdapter(pagerViewUpcoming);
                                        } else {
                                            Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        container.addView(view);
        return view;
    }


    private void sendNotificationToUser(String phone) {
        mService.getToken(phone, "0")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        //when we have Token , just send notification to this token

                        Map<String, String> contentSend = new HashMap<>();
                        contentSend.put("title", "Trip Cancelled By Driver");
                        contentSend.put("Phone1", Common.currentDriver.getPhone());
                        contentSend.put("message", "Your trip is cancelled by driver");
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

    private void sendNotification(String phone) {
        mService.getToken(phone, "1")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        //when we have Token , just send notification to this token

                        Map<String, String> contentSend = new HashMap<>();
                        contentSend.put("title", "Trip Cancelled By Driver");
                        contentSend.put("message", "Your trip is cancelled by driver");
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
                                            } else {
                                                Log.d("ERROR", new Gson().toJson(response.body()));
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
//                            Log.d("ERROR",t.getMessage());
                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
