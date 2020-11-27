package com.urdriver.urdriver.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.model.PaymentData;
import com.urdriver.urdriver.model.Trip;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    Context context;
    List<Trip> tripList;

    public TripAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mytrip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("ERROR", new Gson().toJson(tripList.get(position)));
        holder.txtFrom.setText(tripList.get(position).getSourceAddress() + ", " + tripList.get(position).getSource());
        holder.txtTo.setText(tripList.get(position).getDestinationAddress() + ", " + tripList.get(position).getDestination());
        float price = Float.parseFloat(tripList.get(position).getCabFare()) + Float.parseFloat(tripList.get(position).getTripToll());
        holder.txtPrice.setText(String.valueOf(price));
        PaymentData paymentData = new Gson().fromJson(tripList.get(position).getCabTnxId(), PaymentData.class);
        /*if (paymentData.getCheckBox())
            holder.checkbox.setChecked(paymentData.getCheckBox());
        else
            holder.checkbox.setVisibility(View.GONE);*/
        holder.txtDateTime.setText(getDate(tripList.get(position).getStartTrip()) + " " + context.getResources().getString(R.string.arrow) + " " + getDate(tripList.get(position).getDropTrip()));
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtFrom, txtTo, txtPrice, txtDateTime;
//        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFrom = itemView.findViewById(R.id.txtFrom);
            txtTo = itemView.findViewById(R.id.txtTo);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
//            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }

    private String getDate(String date) {
        String[] arr = date.split(" ", 2);
        return arr[0] + "/" + arr[1];
    }

}
