package com.urdriver.urdriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.urdriver.urdriver.InfoUserForAdmin;
import com.urdriver.urdriver.Interface.IItemClickListener;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.model.UserDetails;

import java.util.List;

public class UserAdminAdapter extends RecyclerView.Adapter<UserAdminAdapter.ViewHolder> {

    Context context;
    List<UserDetails> userDetailsList;

    public UserAdminAdapter(Context context, List<UserDetails> userDetailsList) {
        this.context = context;
        this.userDetailsList = userDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.UserName.setText(userDetailsList.get(position).getName());
        holder.UserEmail.setText(userDetailsList.get(position).getEmail());
        holder.UserPhone.setText(userDetailsList.get(position).getPhone());
        Picasso.get().load(userDetailsList.get(position).getUserImage()).error(context.getResources().getDrawable(R.drawable.ic_profile)).fit().into(holder.UserImage);
        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InfoUserForAdmin.class);
                intent.putExtra("PHONE", new Gson().toJson(userDetailsList.get(position)));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userDetailsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView UserImage;
        TextView UserName, UserEmail, UserPhone;

        IItemClickListener iItemClickListener;

        public void setiItemClickListener(IItemClickListener iItemClickListener) {
            this.iItemClickListener = iItemClickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            UserImage = itemView.findViewById(R.id.UserImage);
            UserName = itemView.findViewById(R.id.UserName);
            UserEmail = itemView.findViewById(R.id.UserEmail);
            UserPhone = itemView.findViewById(R.id.UserPhone);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iItemClickListener.onClick(v);
        }
    }
}
