package com.urdriver.urdriver.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.retrofit.IURDriver;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CabImageAdapter extends PagerAdapter {

    Context context;
    List<String> imageList;
    LayoutInflater layoutInflater;
    ImageView cab_image, img_delete;
    IURDriver mService;

    public CabImageAdapter(Context context, List<String> imageList) {
        this.context = context;
        this.imageList = imageList;
        mService = Common.getAPI();
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
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.cab_image_viewer, container, false);

        cab_image = itemView.findViewById(R.id.cab_image);
        img_delete = itemView.findViewById(R.id.img_delete);
        Picasso.get().load(imageList.get(position)).into(cab_image);

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(position);
            }
        });

        container.addView(itemView);
        return itemView;
    }

    public void removeItem(final int position) {
        String image = imageList.get(position);
        image = image.replace("http://myinvented.com/urdriver/", "");
        mService.DeleteImage(image).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                imageList.remove(position);
                Common.imageCab = imageList;
                Common.mDotLayout.setText(Common.imageCab.size() == 0 ? 0 + "/" + 0 : 1 + "/" + Common.imageCab.size());
                CabImageAdapter cabImageAdapter = new CabImageAdapter(context, imageList);
                Common.slideViewPage.setAdapter(cabImageAdapter);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                Toast.makeText(context, "try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
