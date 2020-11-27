package com.urdriver.urdriver;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.urdriver.urdriver.Adapter.RatingAdapter;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Network.ConnectivityReceiver;
import com.urdriver.urdriver.Network.MyApplication;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.Rating;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRating extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    ConnectivityReceiver connectivityReceiver;

    TextView name, model, ratting, allReview;
    RatingBar rattingBar;
    RecyclerView recycler_rating;
    IURDriver mService = Common.getAPI();
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Toolbar toolbar;
    List<Rating> ratingList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    CircleImageView userImg;
    boolean isCheck = true;

    RelativeLayout listView;
    LinearLayout ln1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rating);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Common.setTop(this);

        name = findViewById(R.id.name);
        ratting = findViewById(R.id.ratting);
        rattingBar = findViewById(R.id.rattingBar);
        model = findViewById(R.id.model);
        allReview = findViewById(R.id.allReview);
        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        userImg = findViewById(R.id.userImg);

        listView = findViewById(R.id.listView);
        ln1 = findViewById(R.id.ln1);

        recycler_rating = findViewById(R.id.recycler_rating);
        recycler_rating.setLayoutManager(new LinearLayoutManager(this));
        recycler_rating.setHasFixedSize(true);

        if (Common.currentDriver.getImage() != null || !Common.currentDriver.getImage().isEmpty()) {
            Picasso.get()
                    .load(Common.currentDriver.getImage())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .error(getResources().getDrawable(R.drawable.map_round))
                    .into(userImg);
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Paper.init(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                if (Common.currentDriver != null) {
                    name.setText(Common.currentDriver.getName().substring(0, 1).toUpperCase() + Common.currentDriver.getName().substring(1));
                    mService.getCabModel(Common.currentDriver.getPhone()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            model.setText(response.body());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("Error", t.getMessage());
                        }
                    });
                } else {
                    String email = Paper.book().read("email");
                    String password = Paper.book().read("password");
                    mService.getDriverInfo(email, password).enqueue(new Callback<Driver>() {
                        @Override
                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                            Common.currentDriver = response.body();
                            name.setText(Common.currentDriver.getName().substring(0, 1).toUpperCase() + Common.currentDriver.getName().substring(1));
                            mService.getCabModel(Common.currentDriver.getPhone()).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    model.setText(response.body());
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("Error", t.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("Error", t.getMessage());
                        }
                    });
                }
                compositeDisposable.add(mService.getRating(Common.currentDriver.getPhone())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<Rating>>() {
                            @Override
                            public void accept(List<Rating> ratings) throws Exception {
                                ratingList = ratings;
                                if (ratings.size() < 5)
                                    allReview.setVisibility(View.GONE);
                                else {
                                    allReview.setVisibility(View.VISIBLE);
                                    allReview.setText("All " + ratings.size() + " review");
                                }
                                setRatings(ratings);
                                RatingAdapter ratingAdapter = new RatingAdapter(ratings, ViewRating.this, false);
                                recycler_rating.setAdapter(ratingAdapter);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                Log.d("Error", throwable.getMessage());
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if (Common.currentDriver != null) {
                    name.setText(Common.currentDriver.getName().substring(0, 1).toUpperCase() + Common.currentDriver.getName().substring(1));
                    mService.getCabModel(Common.currentDriver.getPhone()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            model.setText(response.body());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("Error", t.getMessage());
                        }
                    });
                } else {
                    String email = Paper.book().read("email");
                    String password = Paper.book().read("password");
                    mService.getDriverInfo(email, password).enqueue(new Callback<Driver>() {
                        @Override
                        public void onResponse(Call<Driver> call, Response<Driver> response) {
                            Common.currentDriver = response.body();
                            name.setText(Common.currentDriver.getName().substring(0, 1).toUpperCase() + Common.currentDriver.getName().substring(1));
                            mService.getCabModel(Common.currentDriver.getPhone()).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    model.setText(response.body());
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("Error", t.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Driver> call, Throwable t) {
                            Log.d("Error", t.getMessage());
                        }
                    });
                }
                compositeDisposable.add(mService.getRating(Common.currentDriver.getPhone())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<Rating>>() {
                            @Override
                            public void accept(List<Rating> ratings) throws Exception {
                                ratingList = ratings;
                                if (ratingList.size() == 0) {
                                    listView.setVisibility(View.GONE);
                                    ln1.setVisibility(View.VISIBLE);
                                }
                                if (ratings.size() < 5)
                                    allReview.setVisibility(View.GONE);
                                else {
                                    allReview.setVisibility(View.VISIBLE);
                                    allReview.setText("All " + ratings.size() + " review");
                                }
                                setRatings(ratings);
                                RatingAdapter ratingAdapter = new RatingAdapter(ratings, ViewRating.this, false);
                                recycler_rating.setAdapter(ratingAdapter);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                Log.d("Error", throwable.getMessage());
                                if (ratingList.size() == 0) {
                                    listView.setVisibility(View.GONE);
                                    ln1.setVisibility(View.VISIBLE);
                                }
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }));

            }
        });


        allReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheck) {
                    recycler_rating.removeAllViews();
                    RatingAdapter ratingAdapter = new RatingAdapter(ratingList, ViewRating.this, true);
                    recycler_rating.setAdapter(ratingAdapter);
                    isCheck = false;
                    allReview.setText("Show Less");
                } else {
                    recycler_rating.removeAllViews();
                    RatingAdapter ratingAdapter = new RatingAdapter(ratingList, ViewRating.this, false);
                    recycler_rating.setAdapter(ratingAdapter);
                    isCheck = true;
                    allReview.setVisibility(View.VISIBLE);
                    allReview.setText("All " + ratingList.size() + " review");

                }
            }
        });
    }

    private void setRatings(List<Rating> ratings) {
        float sum = 0;
        for (int i = 0; i < ratings.size(); i++) {
            if (i == 0)
                sum = Float.parseFloat(ratings.get(i).getRating());
            else
                sum = sum + Float.parseFloat(ratings.get(i).getRating());
        }
        float rate = sum / ratings.size();

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        String s = rate > 0.0 ? "" + df.format(rate) : "0.0";
        ratting.setText(s);

        rattingBar.setRating(Float.parseFloat(df.format(rate)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
//        // register connection status listener
//        MyApplication.getInstance().setConnectivityListener(this);
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
}
