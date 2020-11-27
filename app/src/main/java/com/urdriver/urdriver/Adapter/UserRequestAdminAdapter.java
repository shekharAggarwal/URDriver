package com.urdriver.urdriver.Adapter;

import android.app.Activity;
import android.content.Intent;
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
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.DriversRequest;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.model.Cab;
import com.urdriver.urdriver.model.DataMessage;
import com.urdriver.urdriver.model.MyResponse;
import com.urdriver.urdriver.model.RequestData;
import com.urdriver.urdriver.model.Token;
import com.urdriver.urdriver.retrofit.IFCMService;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRequestAdminAdapter extends RecyclerView.Adapter<UserRequestAdminAdapter.ViewHolder> {

    Activity context;
    List<RequestData> requestData;
    IURDriver mService;
    CompositeDisposable compositeDisposable;

    public UserRequestAdminAdapter(Activity context, List<RequestData> requestData) {
        this.context = context;
        this.requestData = requestData;
        mService = Common.getAPI();
        compositeDisposable = new CompositeDisposable();
    }

    @NonNull
    @Override
    public UserRequestAdminAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserRequestAdminAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cab_request_admin_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txtDate.setText(requestData.get(position).getPickupDate());
        holder.txtTime.setText(requestData.get(position).getPickupTime());
        Cab cab = new Gson().fromJson(requestData.get(position).getCabs(), Cab.class);
        holder.txt_type_cab.setText(requestData.get(position).getCabModel() + "," + cab.cabBrand + " (" + cab.cabType + ")");
        holder.txtSource.setText(requestData.get(position).getSource());
        holder.txtDestination.setText(requestData.get(position).getDestination());
        holder.txtAddress.setText(requestData.get(position).getSourceAddress());
        Picasso.get().load(Common.BASE_URL + "UserImage/" + requestData.get(position).getBookAccount() + ".jpeg").error(context.getResources().getDrawable(R.drawable.ic_profile)).into(holder.user_image);
        holder.txtFullName.setText(requestData.get(position).getFullName());
        holder.txtPhone.setText(requestData.get(position).getPhoneNumber() + "/" + requestData.get(position).getBookAccount());
        if (requestData.get(position).getCabType().equals("0"))
            holder.txt_Way.setText("One Way");
        else if (requestData.get(position).getCabType().equals("1"))
            holder.txt_Way.setText("Round Way");
        compositeDisposable.add(mService.getCabAdmin(cab.cabType, requestData.get(position).getSource().toUpperCase(), cab.cabModel).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> cabs) throws Exception {
                        List<String> cab = new ArrayList<>();
                        cab.add("Select Driver");
                        for (int i = 0; i < cabs.size(); i++)
                            cab.add(cabs.get(i));
                        holder.spinner_cab_driver.setItems(cab);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ERROR", throwable.getMessage());
                    }
                }));

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.spinner_cab_driver.getText().toString().equalsIgnoreCase("Select Driver")) {
                    if (requestData.get(position).getCabType().equals("0")) {
                        mService.updateRequest(requestData.get(position).getBookAccount(),
                                requestData.get(position).getCabModel(),
                                Integer.parseInt(requestData.get(position).getId()),
                                1,
                                0,
                                holder.spinner_cab_driver.getText().toString())
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.body() != null)
                                            if (response.body().equals("ok")) {
                                                context.recreate();
                                                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                                                if (requestData.get(position).getCabType().equals("0")) {
                                                    Common.way = "0";
                                                    context.startActivity(new Intent(context, DriversRequest.class));
                                                    sendNotification(holder.spinner_cab_driver.getText().toString());
                                                    sendNotificationToUser(requestData.get(position).getBookAccount(), Common.currentDriver.getName());
                                                } else if (requestData.get(position).getCabType().equals("1")) {
                                                    Common.way = "1";
                                                    context.startActivity(new Intent(context, DriversRequest.class));
                                                    sendNotification(holder.spinner_cab_driver.getText().toString());
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
                    } else if (requestData.get(position).getCabType().equals("1")) {
                        mService.updateRequest(requestData.get(position).getBookAccount(),
                                requestData.get(position).getCabModel(),
                                Integer.parseInt(requestData.get(position).getId()),
                                1, 1, holder.spinner_cab_driver.getText().toString())
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.body() != null)
                                            if (response.body().equals("ok")) {
                                                context.recreate();
                                                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                                                if (requestData.get(position).getCabType().equals("0")) {
                                                    Common.way = "0";
                                                    context.startActivity(new Intent(context, DriversRequest.class));
                                                    sendNotificationToUser(requestData.get(position).getBookAccount(), Common.currentDriver.getName());
                                                } else if (requestData.get(position).getCabType().equals("1")) {
                                                    Common.way = "1";
                                                    context.startActivity(new Intent(context, DriversRequest.class));
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
                } else {
                    Toast.makeText(context, "Select Driver", Toast.LENGTH_SHORT).show();
                }
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
                            Integer.parseInt(requestData.get(position).getId()),
                            3, 1, "00")
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

    private void sendNotification(String phone) {
        mService.getToken(phone, "1")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        //when we have Token , just send notification to this token

                        Map<String, String> contentSend = new HashMap<>();
                        contentSend.put("title", "Cab Booking");
                        contentSend.put("message", "Your receive a new booking");
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
                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_image;
        TextView txt_type_cab, txtFullName, txtPhone, txtDate, txtTime, txtSource, txtDestination, txtAddress, txt_Way, txt_decorated;
        Button btnAccept, btnDecline;
        MaterialSpinner spinner_cab_driver;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_type_cab = itemView.findViewById(R.id.txt_type_cab);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtSource = itemView.findViewById(R.id.txtSource);
            txtDestination = itemView.findViewById(R.id.txtDestination);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txt_Way = itemView.findViewById(R.id.txt_Way);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            spinner_cab_driver = itemView.findViewById(R.id.spinner_cab_driver);
            txt_decorated = itemView.findViewById(R.id.txt_decorated);
            user_image = itemView.findViewById(R.id.user_image);
        }
    }
}
