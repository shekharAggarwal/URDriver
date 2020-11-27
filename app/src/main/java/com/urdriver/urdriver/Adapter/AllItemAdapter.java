package com.urdriver.urdriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.Interface.IItemClickListener;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.ShowDriverRequest;
import com.urdriver.urdriver.model.DriverRequestModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllItemAdapter extends RecyclerView.Adapter<AllItemAdapter.ViewHolder> {

    Context context;
    List<DriverRequestModel> drivers;

    public AllItemAdapter(Context context, List<DriverRequestModel> drivers) {
        this.context = context;
        this.drivers = drivers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_drivers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.name.setText(drivers.get(position).getName());
        Picasso.get().load(drivers.get(position).getDriverImage()).error(context.getResources().getDrawable(R.drawable.ic_profile)).fit().into(holder.image);

        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {
                Common.driverRequestModel = drivers.get(position);
                Intent intent = new Intent(context, ShowDriverRequest.class);
                intent.putExtra("TEXT", "INFO");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView image;
        TextView name;

        IItemClickListener iItemClickListener;

        public void setiItemClickListener(IItemClickListener iItemClickListener) {
            this.iItemClickListener = iItemClickListener;
        }


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iItemClickListener.onClick(v);
        }
    }
}
