package com.urdriver.urdriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Interface.IItemClickListener;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.ShowDriverRequest;
import com.urdriver.urdriver.model.DriverRequestModel;

import java.util.List;

public class DriverRequestAdapter extends RecyclerView.Adapter<DriverRequestAdapter.ViewHolder> {


    Context context;
    List<DriverRequestModel> drivers;

    public DriverRequestAdapter(Context context, List<DriverRequestModel> drivers) {
        this.context = context;
        this.drivers = drivers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_drivers_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.txt_driver_name.setText(drivers.get(position).getName());
        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {
                Common.driverRequestModel = drivers.get(position);
                context.startActivity(new Intent(context, ShowDriverRequest.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_driver_name;


        IItemClickListener iItemClickListener;

        public void setiItemClickListener(IItemClickListener iItemClickListener) {
            this.iItemClickListener = iItemClickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_driver_name = itemView.findViewById(R.id.txt_driver_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iItemClickListener.onClick(v);
        }
    }

}
