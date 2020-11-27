package com.urdriver.urdriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.urdriver.urdriver.Interface.IItemClickListener;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.model.PaymentData;
import com.urdriver.urdriver.model.Trip;

import java.util.List;

public class RefundAdapter extends RecyclerView.Adapter<RefundAdapter.ViewHolder> {

    Context context;
    List<Trip> tripList;
    FragmentManager fragment;

    public RefundAdapter(Context context, List<Trip> tripList, FragmentManager fragment) {
        this.context = context;
        this.tripList = tripList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_refund, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        PaymentData paymentData = new Gson().fromJson(tripList.get(position).getCabTnxId(), PaymentData.class);

        holder.txtFrom.setText(tripList.get(position).getSourceAddress() + ", " + tripList.get(position).getSource());
        holder.txtTo.setText(tripList.get(position).getDestinationAddress() + ", " + tripList.get(position).getDestination());
        holder.txtPrice.setText("₹" + paymentData.getTnxAmount());
        holder.txtRefundId.setText(paymentData.getRefundId());
        if (paymentData.getTxnStatus().equalsIgnoreCase("TXN_SUCCESS")) {
            holder.status.setText("Your refund is successful");
            holder.status.setBackground(context.getResources().getDrawable(R.color.green));
        } else if (paymentData.getTxnStatus().equalsIgnoreCase("PENDING")) {
            holder.status.setText("Your refund is pending");
            holder.status.setBackground(context.getResources().getDrawable(R.color.yellow_back));
        } else if (paymentData.getTxnStatus().equalsIgnoreCase("TXN_FAILURE")) {
            holder.status.setText("Your refund is failed");
            holder.status.setBackground(context.getResources().getDrawable(R.color.red));
        }
        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {
                /*if (tripList.get(position).getCabDriver() != null) {
                    AfterRide afterRide = new AfterRide(tripList.get(position).getCabDriver(), tripList.get(position));
                    afterRide.showNow(fragment, "SS");
                }*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtFrom, txtTo, txtPrice, txtRefundId, status;

        IItemClickListener iItemClickListener;

        public void setiItemClickListener(IItemClickListener iItemClickListener) {
            this.iItemClickListener = iItemClickListener;
        }


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFrom = itemView.findViewById(R.id.txtFrom);
            txtTo = itemView.findViewById(R.id.txtTo);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtRefundId = itemView.findViewById(R.id.txtRefundId);
            status = itemView.findViewById(R.id.status);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            iItemClickListener.onClick(v);
        }
    }
}
