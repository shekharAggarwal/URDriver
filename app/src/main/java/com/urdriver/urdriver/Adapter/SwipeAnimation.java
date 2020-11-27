package com.urdriver.urdriver.Adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

public class SwipeAnimation implements ViewPager.OnPageChangeListener, ViewPager.PageTransformer {

    private ViewPager mViewPager;
    private CardAdapter mAdapter;
    private float mLastOffSet;
    private boolean mScalingEnabled;

    public SwipeAnimation(ViewPager mViewPager, CardAdapter mAdapter) {
        this.mViewPager = mViewPager;
        mViewPager.addOnPageChangeListener(this);
        this.mAdapter = mAdapter;
    }

    public void enableScaling(boolean enable) {
        if (mScalingEnabled && !enable) {
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1);
                currentCard.animate().scaleX(1);
            }
        } else if (!mScalingEnabled && enable) {
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1.1f);
                currentCard.animate().scaleX(1.1f);
            }
        }

        mScalingEnabled = enable;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realCurrentPosition;
        int nextPosition;
        float baseElevation = mAdapter.getBaseElevation();
        float realOffSet;
        boolean goingLeft = mLastOffSet > positionOffset;
        if (goingLeft) {
            realCurrentPosition = position + 1;
            nextPosition = position;
            realOffSet = 1 - positionOffset;
        } else {
            nextPosition = position + 1;
            realCurrentPosition = position;
            realOffSet = positionOffset;
        }
        if (nextPosition > mAdapter.getCount() - 1 || realCurrentPosition > mAdapter.getCount() - 1) {
            return;
        }
        CardView currentCard = mAdapter.getCardViewAt(realCurrentPosition);
        if (currentCard != null) {
            if (mScalingEnabled) {
                currentCard.setScaleX((float) (1 + 0.1 * (1 - realOffSet)));
                currentCard.setScaleY((float) (1 + 0.1 * (1 - realOffSet)));
            }
            currentCard.setCardElevation(baseElevation + baseElevation * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (1 - realOffSet));
        }
        CardView nextCard = mAdapter.getCardViewAt(nextPosition);
        if (nextCard != null) {
            if (mScalingEnabled) {
                nextCard.setScaleX((float) (1 + 0.1 * (1 - realOffSet)));
                nextCard.setScaleY((float) (1 + 0.1 * (1 - realOffSet)));
            }
            nextCard.setCardElevation(baseElevation + baseElevation * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (1 - realOffSet));
        }
        mLastOffSet = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void transformPage(@NonNull View page, float position) {

    }
}
