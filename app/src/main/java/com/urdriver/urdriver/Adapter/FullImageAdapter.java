package com.urdriver.urdriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.urdriver.urdriver.R;

import java.util.List;

public class FullImageAdapter extends PagerAdapter {

    Context context;
    List<String> imageList;
    LayoutInflater layoutInflater;
    ImageView cab_image;

    public FullImageAdapter(Context context, List<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.full_image_viewer, container, false);

        cab_image = itemView.findViewById(R.id.cab_image);
        Picasso.get().load(imageList.get(position)).fit().into(cab_image);

        container.addView(itemView);
        return itemView;
    }

}
