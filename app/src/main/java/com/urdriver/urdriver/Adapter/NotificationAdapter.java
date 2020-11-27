package com.urdriver.urdriver.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Activity context;
    List<RequestData> requestData;
    IURDriver mService;

    public NotificationAdapter(Activity context, List<RequestData> requestData) {
        this.context = context;
        this.requestData = requestData;
        mService = Common.getAPI();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txtDate.setText(requestData.get(position).getPickupDate());
        holder.txtTime.setText(requestData.get(position).getPickupTime());
        holder.txtSource.setText(requestData.get(position).getSource());
        holder.txtDestination.setText(requestData.get(position).getDestination());
        holder.txtAddress.setText(requestData.get(position).getSourceAddress());
        if (requestData.get(position).getCabType().equals("0"))
            holder.txt_Way.setText("One Way");
        else if (requestData.get(position).getCabType().equals("1"))
            holder.txt_Way.setText("Round Way");

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestData.get(position).getCabType().equals("0"))
                    mService.updateRequest(requestData.get(position).getBookAccount(),
                            requestData.get(position).getCabModel(),
                            Integer.parseInt(requestData.get(position).getId()),
                            1,
                            0,
                            "00")
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.body() != null)
                                        if (response.body().equals("ok")) {
                                            context.recreate();
                                            Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                                            if (requestData.get(position).getCabType().equals("0")) {
                                                Common.way = "0";
                                                sendNotificationToUser(requestData.get(position).getBookAccount(), Common.currentDriver.getName());
                                            } else if (requestData.get(position).getCabType().equals("1")) {
                                                Common.way = "1";
                                                sendNotificationToUser(requestData.get(position).getBookAccount(), Common.currentDriver.getName());
                                            }
                                        } else {
                                            Toast.makeText(context, "" + response.body(), Toast.LENGTH_SHORT).show();
                                        }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                else if (requestData.get(position).getCabType().equals("1"))
                    mService.updateRequest(requestData.get(position).getBookAccount(), requestData.get(position).getCabModel(), Integer.parseInt(requestData.get(position).getId()), 1, 1, "00")
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.body() != null)
                                        if (response.body().equals("ok")) {
                                            context.recreate();
                                            Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                                            if (requestData.get(position).getCabType().equals("0")) {
                                                Common.way = "0";
                                                sendNotificationToUser(requestData.get(position).getBookAccount(), Common.currentDriver.getName());
                                            } else if (requestData.get(position).getCabType().equals("1")) {
                                                Common.way = "1";
                                                sendNotificationToUser(requestData.get(position).getBookAccount(), Common.currentDriver.getName());
                                            }
                                        } else {
                                            Toast.makeText(context, "" + response.body(), Toast.LENGTH_SHORT).show();
                                        }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
            }
        });

        holder.btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestData.get(position).getCabType().equals("0"))
                    mService.updateRequest(requestData.get(position).getBookAccount(),
                            requestData.get(position).getCabModel(),
                            Integer.parseInt(requestData.get(position).getId()), 3, 0, "00")
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Log.d("ERROR", new Gson().toJson(response.body()));
                                    if (response.body() != null)
                                        if (response.body().equals("ok")) {
                                            context.recreate();
                                            Toast.makeText(context, "Request Denied", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "" + response.body(), Toast.LENGTH_SHORT).show();
                                        }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("ERROR", new Gson().toJson(t.getCause()));
                                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                else if (requestData.get(position).getCabType().equals("1"))
                    mService.updateRequest(requestData.get(position).getBookAccount(),
                            requestData.get(position).getCabModel(),
                            Integer.parseInt(requestData.get(position).getId()), 3, 1, "00")
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.body() != null)
                                        if (response.body().equals("ok")) {
                                            context.recreate();
                                            Toast.makeText(context, "Request Denied", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "" + response.body(), Toast.LENGTH_SHORT).show();
                                        }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

            }
        });
    }

    @Override
    public int getItemCount() {
        return requestData.size();
    }

    private void sendNotificationToUser(String phone, final String name) {
        mService.getToken(phone, "0")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        //when we have Token , just send notification to this token

                        Map<String, String> contentSend = new HashMap<>();
                        contentSend.put("title", "Cab Confirmation");
                        contentSend.put("message", "Your cab request confirm by " + name);
                        contentSend.put("Phone", Common.currentDriver.getPhone());
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
//                            Log.d("ERROR",t.getMessage());
                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtDate, txtTime, txtSource, txtDestination, txtAddress, txt_Way, txt_decorated;
        Button btnAccept, btnDecline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtSource = itemView.findViewById(R.id.txtSource);
            txtDestination = itemView.findViewById(R.id.txtDestination);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txt_Way = itemView.findViewById(R.id.txt_Way);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            txt_decorated = itemView.findViewById(R.id.txt_decorated);
        }
    }
}
