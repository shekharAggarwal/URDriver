package com.urdriver.urdriver.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.urdriver.urdriver.R;
import com.urdriver.urdriver.model.RequestDataOneWay;

import java.util.ArrayList;
import java.util.List;

public class pageAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<RequestDataOneWay> mData;
    private float mBaseElevation;

    public pageAdapter() {
        mViews = new ArrayList<>();
        mData = new ArrayList<>();
    }

    public void addCardItems(RequestDataOneWay item) {
        mViews.add(null);
        mData.add(item);
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_schedule, container, false);


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
