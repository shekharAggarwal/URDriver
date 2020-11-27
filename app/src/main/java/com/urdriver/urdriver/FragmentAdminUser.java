package com.urdriver.urdriver;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.urdriver.urdriver.Adapter.UserRequestAdminAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.RequestData;
import com.urdriver.urdriver.model.RequestDataOneWay;
import com.urdriver.urdriver.model.RequestDataRoundWay;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAdminUser extends Fragment {
    View view;
    RecyclerView recycler_user;
    List<RequestData> requestData = new ArrayList<>();
    IURDriver mService = Common.getAPI();
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    LinearLayout ln2;
    boolean isCheck = true;
    String date;
    int c1 = 0, c2 = 0, c3 = 0, c4 = 0;
    int Noti = 0;
    Calendar calendar = Calendar.getInstance();
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_user, null);

        swipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh);
        recycler_user = view.findViewById(R.id.recycler_user);
        recycler_user.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_user.setHasFixedSize(true);
        ln2 = view.findViewById(R.id.ln2);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                if (Common.currentDriver == null) {
                    String email = Paper.book().read("email");
                    String password = Paper.book().read("password");
                    mService.getDriverInfo(email, password).enqueue(new Callback<Driver>() {
                        @Override
                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                            Common.currentDriver = response.body();
                            getNotification();
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                        }
                    });
                } else {
                    getNotification();
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if (Common.currentDriver == null) {
                    String email = Paper.book().read("email");
                    String password = Paper.book().read("password");

                    mService.getDriverInfo(email, password).enqueue(new Callback<Driver>() {
                        @Override
                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                            Common.currentDriver = response.body();
                            getNotification();

                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                        }
                    });
                } else {
                    getNotification();
                }

            }
        });
        return view;
    }

    private void getOneWayNotSeen() {
        if (c1 == 0) {
            c1 = 1;
            recycler_user.removeAllViews();
            requestData.clear();
            requestData = new ArrayList<>();

            compositeDisposable.add(mService.getRequestDataOneWay("00", date, 0, 0)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<RequestDataOneWay>>() {
                        @Override
                        public void accept(List<RequestDataOneWay> requestDataOneWays) throws Exception {
                            for (int i = 0; i < requestDataOneWays.size(); i++) {
                                RequestData request = new RequestData(requestDataOneWays.get(i).getId(),
                                        requestDataOneWays.get(i).getFullName(),
                                        requestDataOneWays.get(i).getPhoneNumber(),
                                        requestDataOneWays.get(i).getEmail(),
                                        requestDataOneWays.get(i).getSourceAddress(),
                                        requestDataOneWays.get(i).getDestinationAddress(),
                                        requestDataOneWays.get(i).getPickupDate(),
                                        "0000-00-00",
                                        requestDataOneWays.get(i).getPickupTime(),
                                        requestDataOneWays.get(i).getSource(),
                                        requestDataOneWays.get(i).getDestination(),
                                        requestDataOneWays.get(i).getCabs(),
                                        requestDataOneWays.get(i).getBookAccount(),
                                        requestDataOneWays.get(i).getCabFare(),
                                        requestDataOneWays.get(i).getCabDriver(),
                                        requestDataOneWays.get(i).getCabStatus(),
                                        requestDataOneWays.get(i).getCabModel(),
                                        requestDataOneWays.get(i).getCabTnxId(),
                                        "0",
                                        requestDataOneWays.get(i).getRequestTime());
                                requestData.add(request);
                            }
                            getOneWayNotAccepted();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            getOneWayNotAccepted();
                        }
                    }));
        }
    }

    private void getOneWayNotAccepted() {
        if (c2 == 0) {
            c2 = 1;
            compositeDisposable.add(mService.getRequestDataOneWay("00", date, 0, 2)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<RequestDataOneWay>>() {
                        @Override
                        public void accept(List<RequestDataOneWay> requestDataOneWays) throws Exception {
                            for (int i = 0; i < requestDataOneWays.size(); i++) {
                                RequestData request = new RequestData(requestDataOneWays.get(i).getId(),
                                        requestDataOneWays.get(i).getFullName(),
                                        requestDataOneWays.get(i).getPhoneNumber(),
                                        requestDataOneWays.get(i).getEmail(),
                                        requestDataOneWays.get(i).getSourceAddress(),
                                        requestDataOneWays.get(i).getDestinationAddress(),
                                        requestDataOneWays.get(i).getPickupDate(),
                                        "0000-00-00",
                                        requestDataOneWays.get(i).getPickupTime(),
                                        requestDataOneWays.get(i).getSource(),
                                        requestDataOneWays.get(i).getDestination(),
                                        requestDataOneWays.get(i).getCabs(),
                                        requestDataOneWays.get(i).getBookAccount(),
                                        requestDataOneWays.get(i).getCabFare(),
                                        requestDataOneWays.get(i).getCabDriver(),
                                        requestDataOneWays.get(i).getCabStatus(),
                                        requestDataOneWays.get(i).getCabModel(),
                                        requestDataOneWays.get(i).getCabTnxId(),
                                        "0",
                                        requestDataOneWays.get(i).getRequestTime());
                                requestData.add(request);
                            }
                            getRoundWayNotAccepted();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            getRoundWayNotAccepted();
                        }
                    }));
        }
    }

    private void getRoundWayNotAccepted() {
        if (c3 == 0) {
            c3 = 1;
            compositeDisposable.add(mService.getRequestDataRoundWay("00", date, 0, 3)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<RequestDataRoundWay>>() {
                        @Override
                        public void accept(List<RequestDataRoundWay> requestDataRoundWays) throws Exception {
                            for (int i = 0; i < requestDataRoundWays.size(); i++) {
                                RequestData request = new RequestData(requestDataRoundWays.get(i).getId(),
                                        requestDataRoundWays.get(i).getFullName(),
                                        requestDataRoundWays.get(i).getPhoneNumber(),
                                        requestDataRoundWays.get(i).getEmail(),
                                        requestDataRoundWays.get(i).getSourceAddress(),
                                        requestDataRoundWays.get(i).getDestinationAddress(),
                                        requestDataRoundWays.get(i).getPickupDate(),
                                        requestDataRoundWays.get(i).getDropDate(),
                                        requestDataRoundWays.get(i).getPickupTime(),
                                        requestDataRoundWays.get(i).getSource(),
                                        requestDataRoundWays.get(i).getDestination(),
                                        requestDataRoundWays.get(i).getCabs(),
                                        requestDataRoundWays.get(i).getBookAccount(),
                                        requestDataRoundWays.get(i).getCabFare(),
                                        requestDataRoundWays.get(i).getCabDriver(),
                                        requestDataRoundWays.get(i).getCabStatus(),
                                        requestDataRoundWays.get(i).getCabModel(),
                                        requestDataRoundWays.get(i).getCabTnxId(),
                                        "1",
                                        requestDataRoundWays.get(i).getRequestTime());
                                requestData.add(request);
                            }
                            getRoundWayNotSeen();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            getRoundWayNotSeen();
                        }
                    }));
        }
    }

    private void getRoundWayNotSeen() {
        if (c4 == 0) {
            c4 = 1;
            compositeDisposable.add(mService.getRequestDataRoundWay("00", date, 0, 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<RequestDataRoundWay>>() {
                                   @Override
                                   public void accept(List<RequestDataRoundWay> requestDataRoundWays) throws Exception {
                                       for (int i = 0; i < requestDataRoundWays.size(); i++) {
                                           RequestData request = new RequestData(requestDataRoundWays.get(i).getId(),
                                                   requestDataRoundWays.get(i).getFullName(),
                                                   requestDataRoundWays.get(i).getPhoneNumber(),
                                                   requestDataRoundWays.get(i).getEmail(),
                                                   requestDataRoundWays.get(i).getSourceAddress(),
                                                   requestDataRoundWays.get(i).getDestinationAddress(),
                                                   requestDataRoundWays.get(i).getPickupDate(),
                                                   requestDataRoundWays.get(i).getDropDate(),
                                                   requestDataRoundWays.get(i).getPickupTime(),
                                                   requestDataRoundWays.get(i).getSource(),
                                                   requestDataRoundWays.get(i).getDestination(),
                                                   requestDataRoundWays.get(i).getCabs(),
                                                   requestDataRoundWays.get(i).getBookAccount(),
                                                   requestDataRoundWays.get(i).getCabFare(),
                                                   requestDataRoundWays.get(i).getCabDriver(),
                                                   requestDataRoundWays.get(i).getCabStatus(),
                                                   requestDataRoundWays.get(i).getCabModel(),
                                                   requestDataRoundWays.get(i).getCabTnxId(),
                                                   "1",
                                                   requestDataRoundWays.get(i).getRequestTime());
                                           requestData.add(request);
                                       }
                                       swipeRefreshLayout.setRefreshing(false);
                                       UserRequestAdminAdapter notificationAdapter = new UserRequestAdminAdapter(getActivity(), requestData);
                                       recycler_user.setAdapter(notificationAdapter);
                                   }
                               },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Log.d("ERROR", throwable.getMessage());
                                    swipeRefreshLayout.setRefreshing(false);
                                    UserRequestAdminAdapter notificationAdapter = new UserRequestAdminAdapter(getActivity(), requestData);
                                    recycler_user.setAdapter(notificationAdapter);
                                }
                            }));
        }
    }

    private void getNotification() {
        c1 = 0;
        c2 = 0;
        c3 = 0;
        c4 = 0;
        Noti = 0;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;

        int month1 = calendar.get(Calendar.MONTH) + 1;
        int day1 = calendar.get(Calendar.DAY_OF_MONTH);
        String date1;
        if (month < 10) {
            date = calendar.get(Calendar.YEAR) + "-" + "0" + month + "-" + day;
            date1 = calendar.get(Calendar.YEAR) + "-" + "0" + month1 + "-" + day1;
        } else {
            date = calendar.get(Calendar.YEAR) + "-" + month + "-" + day;
            date1 = calendar.get(Calendar.YEAR) + "-" + month1 + "-" + day1;
        }

        mService.getCount("00", "0", date).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Noti = Noti + Integer.parseInt(response.body());
                getRoundWayNotification();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Error", t.getMessage());
                getRoundWayNotification();
            }
        });
    }

    private void getRoundWayNotification() {
        mService.getCount("00", "1", date).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Noti = Noti + Integer.parseInt(response.body());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Noti == 0) {
                            ln2.setVisibility(View.VISIBLE);
                            recycler_user.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            ln2.setVisibility(View.GONE);
                            recycler_user.setVisibility(View.VISIBLE);
                            if (isCheck)
                                getOneWayNotSeen();
                            else {
                                isCheck = false;
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
