package com.urdriver.urdriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.urdriver.urdriver.Common.Common;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.model.PaymentData;
import com.urdriver.urdriver.model.Trip;

import java.util.List;

public class PaymentAdminAdapter extends RecyclerView.Adapter<PaymentAdminAdapter.ViewHolder> {

    Context context;
    List<Trip> tripList;

    public PaymentAdminAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_layout, parent, false);
        return new PaymentAdminAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txtDateTime.setText(tripList.get(position).getStartTrip());
        holder.txtDropDate.setText(tripList.get(position).getDropTrip());
        holder.txtFrom.setText(tripList.get(position).getSourceAddress());
        holder.txtTo.setText(tripList.get(position).getDestinationAddress());
        holder.txtPrice.setText(tripList.get(position).getCabFare());
        final PaymentData paymentData = new Gson().fromJson(tripList.get(position).getCabTnxId(), PaymentData.class);
        holder.txtCashPayment.setText(paymentData.getCash());
        holder.txtOnlinePayment.setText(paymentData.getTnxAmount());

        if (paymentData.getCheckBox())
            holder.checkbox.setChecked(true);
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    holder.checkbox.setChecked(true);
                    paymentData.setCheckBox(true);
                    tripList.get(position).setCabTnxId(new Gson().toJson(paymentData));
                    Common.Price = Common.Price + getAmount(tripList.get(position).getCabFare(), paymentData.getTnxAmount());
                    Common.TotalAmount.setText("" + Common.Price);
                } else {
                    holder.checkbox.setChecked(false);
                    paymentData.setCheckBox(false);
                    tripList.get(position).setCabTnxId(new Gson().toJson(paymentData));
                    Common.Price = Common.Price - getAmount(tripList.get(position).getCabFare(), paymentData.getTnxAmount());
                    Common.TotalAmount.setText("" + Common.Price);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public List<Trip> getList() {
        return tripList;
    }

    private double getAmount(String cabFare, String tnxAmount) {
        Double a = Double.parseDouble(tnxAmount) - (Double.parseDouble(cabFare) / 10);
        return a;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtDateTime, txtDropDate, txtFrom, txtTo, txtPrice, txtCashPayment, txtOnlinePayment;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            txtDropDate = itemView.findViewById(R.id.txtDropDate);
            txtFrom = itemView.findViewById(R.id.txtFrom);
            txtTo = itemView.findViewById(R.id.txtTo);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtCashPayment = itemView.findViewById(R.id.txtCashPayment);
            txtOnlinePayment = itemView.findViewById(R.id.txtOnlinePayment);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}
