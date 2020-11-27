package com.urdriver.urdriver;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.urdriver.urdriver.Adapter.PagerViewUpcoming;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.RequestData;
import com.urdriver.urdriver.model.RequestDataOneWay;
import com.urdriver.urdriver.model.RequestDataRoundWay;
import com.urdriver.urdriver.retrofit.IURDriver;
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer;

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

import static com.urdriver.urdriver.Common.Common.requestList;

public class FragmentUpcoming extends Fragment {

    HorizontalInfiniteCycleViewPager upcoming_viewer;
    IURDriver mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Calendar calendar = Calendar.getInstance();
    boolean isCheck = true;

    RelativeLayout r1;
    LinearLayout l1;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming, null);

        Common.CycleViewPager = view.findViewById(R.id.upcoming_viewer);
        mService = Common.getAPI();
        r1 = view.findViewById(R.id.r1);
        l1 = view.findViewById(R.id.ln1);
        requestList = new ArrayList<>();
        swipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh);

        Paper.init(getContext());

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
                            Log.d("ERROR", t.getMessage());
                            Toast.makeText(getContext(), "Try Again", Toast.LENGTH_SHORT).show();
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

                            Log.d("ERROR", t.getMessage());
                            Toast.makeText(getContext(), "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loadList();
                }

            }
        });

        //animation
        BookFlipPageTransformer bookFlipPageTransformer = new BookFlipPageTransformer();
        bookFlipPageTransformer.setScaleAmountPercent(10f);
        Common.CycleViewPager.setPageTransformer(true, bookFlipPageTransformer);
        return view;

    }

    private void loadList() {

        if (isCheck) {
            String date = null;
            requestList = new ArrayList<>();

            int month = calendar.get(Calendar.MONTH) + 1;
            if (month < 10)
                date = calendar.get(Calendar.YEAR) + "-" + "0" + month + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            else
                date = calendar.get(Calendar.YEAR) + "-" + month + "-" + calendar.get(Calendar.DAY_OF_MONTH);

            compositeDisposable.add(mService.getRequestDataRoundWay(Common.currentDriver.getPhone(), date, 1, 3)
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
                                requestList.add(requestData);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                        }
                    }));
            compositeDisposable.add(mService.getRequestDataOneWay(Common.currentDriver.getPhone(), date, 1, 2)
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
                                requestList.add(requestData);
                            }
                            if (Common.terms != null) {
                                Common.terms.setVisibility(View.VISIBLE);
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            PagerViewUpcoming pagerViewUpcoming = new PagerViewUpcoming(getContext(), requestList);
                            Common.CycleViewPager.setAdapter(pagerViewUpcoming);


//                        for (int i = 0; i < requestDataOneWays.size(); i++) {
//                            requestDataOneWay.addCardItems(requestDataOneWays.get(i));
//                        }
//                        SwipeAnimation swipeAnimation = new SwipeAnimation(today_viewer, requestDataOneWay);
////        PagerViewToday pagerViewToday = new PagerViewToday(getContext(), requestDataOneWays);
//                        today_viewer.setAdapter(requestDataOneWay);
//                        today_viewer.setPageTransformer(false, );
//                        today_viewer.setOffscreenPageLimit(3);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            swipeRefreshLayout.setRefreshing(false);
                            if (requestList.size() > 0) {
                                if (Common.terms != null) {
                                    Common.terms.setVisibility(View.VISIBLE);
                                }
                                r1.setVisibility(View.VISIBLE);
                                l1.setVisibility(View.GONE);
                                PagerViewUpcoming pagerViewUpcoming = new PagerViewUpcoming(getContext(), requestList);
                                Common.CycleViewPager.setAdapter(pagerViewUpcoming);
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
            isCheck = true;
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Paper.init(getContext());

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
