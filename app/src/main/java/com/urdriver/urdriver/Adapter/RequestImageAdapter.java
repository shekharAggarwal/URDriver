package com.urdriver.urdriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.FullImageActivity;
import com.urdriver.urdriver.R;

import java.util.List;

public class RequestImageAdapter extends PagerAdapter {

    Context context;
    List<String> imageList;
    LayoutInflater layoutInflater;
    ImageView cab_image, img_delete;

    public RequestImageAdapter(Context context, List<String> imageList) {
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
        View itemView = layoutInflater.inflate(R.layout.cab_image_viewer, container, false);

        cab_image = itemView.findViewById(R.id.cab_image);
        img_delete = itemView.findViewById(R.id.img_delete);
        img_delete.setVisibility(View.GONE);
        Picasso.get().load(imageList.get(position)).into(cab_image);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageList != null) {
                    Common.imgCab = imageList;
                    context.startActivity(new Intent(context, FullImageActivity.class));
                }
            }
        });
        container.addView(itemView);
        return itemView;
    }
}
