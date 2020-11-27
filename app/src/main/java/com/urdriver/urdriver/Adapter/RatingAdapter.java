package com.urdriver.urdriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.model.Rating;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    List<Rating> ratingList;
    Context context;
    boolean isCheck;

    public RatingAdapter(List<Rating> ratingList, Context context, boolean isCheck) {
        this.ratingList = ratingList;
        this.context = context;
        this.isCheck = isCheck;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!ratingList.get(position).getImage().equals("0") && !ratingList.get(position).getImage().isEmpty() && ratingList.get(position).getImage() != null)
            Picasso.get().load(ratingList.get(position).getImage()).error(context.getResources().getDrawable(R.drawable.map_round)).into(holder.UserImg);
        holder.UserReview.setText(ratingList.get(position).getReview());
        holder.UserName.setText(ratingList.get(position).getName().substring(0, 1).toUpperCase() + ratingList.get(position).getName().substring(1));

    }

    @Override
    public int getItemCount() {
        if (isCheck)
            return ratingList.size();
        else {
            if (ratingList.size() < 5)
                return ratingList.size();
            else
                return 4;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView UserReview, UserName;
        CircleImageView UserImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            UserImg = itemView.findViewById(R.id.UserImg);
            UserReview = itemView.findViewById(R.id.UserReview);
            UserName = itemView.findViewById(R.id.UserName);

        }
    }
}
