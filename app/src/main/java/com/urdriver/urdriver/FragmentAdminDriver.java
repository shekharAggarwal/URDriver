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

import com.urdriver.urdriver.Adapter.DriverRequestAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.DriverRequestModel;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.List;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAdminDriver extends Fragment {

    RecyclerView recycler_driver;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IURDriver mService = Common.getAPI();
    LinearLayout ln2;
    View view;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_driver, null);

        swipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh);
        ln2 = view.findViewById(R.id.ln2);
        recycler_driver = view.findViewById(R.id.recycler_driver);
        recycler_driver.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_driver.setHasFixedSize(true);
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
                            getRequestDriverData();
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                        }
                    });
                } else {
                    getRequestDriverData();
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
                            getRequestDriverData();
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                        }
                    });
                } else {
                    getRequestDriverData();
                }

            }
        });
        return view;
    }

    private void getRequestDriverData() {
        recycler_driver.removeAllViews();
        mService = Common.getAPI();

        compositeDisposable.add(mService.getRequestDriver()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<DriverRequestModel>>() {
                    @Override
                    public void accept(List<DriverRequestModel> drivers) throws Exception {
                        if (drivers.size() != 0) {
                            ln2.setVisibility(View.GONE);
                            recycler_driver.setVisibility(View.VISIBLE);
                            DriverRequestAdapter driverRequestAdapter = new DriverRequestAdapter(getContext(), drivers);
                            recycler_driver.setAdapter(driverRequestAdapter);
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            ln2.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setRefreshing(false);
                            recycler_driver.setVisibility(View.GONE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ln2.setVisibility(View.VISIBLE);
                        recycler_driver.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Log.d("ERROR", throwable.getMessage());
                    }
                }));
    }

}
