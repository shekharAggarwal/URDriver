package com.urdriver.urdriver;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.urdriver.urdriver.Adapter.PagerViewToday;
import com.urdriver.urdriver.Adapter.pageAdapter;
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

public class FragmentToday extends Fragment {

    HorizontalInfiniteCycleViewPager today_viewer;
    IURDriver mService;
    pageAdapter requestDataOneWay;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Calendar calendar = Calendar.getInstance();
    View view;
    List<RequestData> requestDataList = new ArrayList<>();
    boolean isCheck = true;
    RelativeLayout r1;
    LinearLayout l1;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_today, null);

        today_viewer = view.findViewById(R.id.today_viewer);
        mService = Common.getAPI();
        requestDataOneWay = new pageAdapter();
        Paper.init(getContext());
        r1 = view.findViewById(R.id.r1);
        l1 = view.findViewById(R.id.ln1);

        swipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh);

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
                            loadList();
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {

                        }
                    });
                } else {
                    loadList();
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
                            loadList();
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {

                        }
                    });
                } else {
                    loadList();
                }

            }
        });
        return view;

    }

    private void loadList() {
        if (isCheck) {
            requestDataList.clear();
            requestDataList = new ArrayList<>();

            String date = null;
            int month = calendar.get(Calendar.MONTH) + 1;
            if (month < 10)
                date = calendar.get(Calendar.YEAR) + "-" + "0" + month + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            else
                date = calendar.get(Calendar.YEAR) + "-" + month + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            Log.d("date", date);
            compositeDisposable.add(mService.getRequestDataRoundWay(Common.currentDriver.getPhone(), date, 1, 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<RequestDataRoundWay>>() {
                        @Override
                        public void accept(List<RequestDataRoundWay> image) throws Exception {
                            for (int p = 0; p < image.size(); p++) {
                                RequestData requestData = new RequestData(image.get(p).getId(),
                                        image.get(p).getFullName(),
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
                                        "1",
                                        image.get(p).getRequestTime());
                                requestDataList.add(requestData);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                        }
                    }));
            compositeDisposable.add(mService.getRequestDataOneWay(Common.currentDriver.getPhone(), date, 1, 0)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<RequestDataOneWay>>() {
                        @Override
                        public void accept(List<RequestDataOneWay> image) throws Exception {
                            for (int p = 0; p < image.size(); p++) {
                                RequestData requestData = new RequestData(image.get(p).getId(),
                                        image.get(p).getFullName(),
                                        image.get(p).getPhoneNumber(),
                                        image.get(p).getEmail(),
                                        image.get(p).getSourceAddress(),
                                        image.get(p).getDestinationAddress(),
                                        image.get(p).getPickupDate(),
                                        "0000-00-00",
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
                                        "0",
                                        image.get(p).getRequestTime());
                                requestDataList.add(requestData);
                            }
                            if (Common.terms != null) {
                                Common.terms.setVisibility(View.VISIBLE);
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            today_viewer.removeAllViews();
                            PagerViewToday pagerViewToday = new PagerViewToday(getContext(), requestDataList);
                            today_viewer.setAdapter(pagerViewToday);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            swipeRefreshLayout.setRefreshing(false);
                            if (requestDataList.size() > 0) {
                                if (Common.terms != null) {
                                    Common.terms.setVisibility(View.VISIBLE);
                                }
                                r1.setVisibility(View.VISIBLE);
                                l1.setVisibility(View.GONE);
                                today_viewer.removeAllViews();
                                PagerViewToday pagerViewToday = new PagerViewToday(getContext(), requestDataList);
                                today_viewer.setAdapter(pagerViewToday);
                            } else {
                                r1.setVisibility(View.GONE);
                                l1.setVisibility(View.VISIBLE);
                                if (Common.terms != null) {
                                    Common.terms.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }));
            isCheck = false;
        } else {
            swipeRefreshLayout.setRefreshing(false);
            isCheck = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.currentDriver == null) {
            String email = Paper.book().read("email");
            String password = Paper.book().read("password");

            mService.getDriverInfo(email, password).enqueue(new Callback<Driver>() {
                @Override
                public void onResponse(Call<Driver> call, Response<Driver> response) {
                    Common.currentDriver = response.body();
                    loadList();
                }

                @Override
                public void onFailure(Call<Driver> call, Throwable t) {

                }
            });
        } else {
            loadList();
        }
    }

}
