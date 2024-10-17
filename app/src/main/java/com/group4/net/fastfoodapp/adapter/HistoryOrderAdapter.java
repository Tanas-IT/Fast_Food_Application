package com.group4.net.fastfoodapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.group4.net.fastfoodapp.R;
import com.group4.net.fastfoodapp.activity.HistoryOrderActivity;
import com.group4.net.fastfoodapp.data.Database;
import com.group4.net.fastfoodapp.domain.Foods;
import com.group4.net.fastfoodapp.domain.HistoryOrder;
import com.group4.net.fastfoodapp.helper.ManagementCart;

import java.util.ArrayList;

public class HistoryOrderAdapter extends RecyclerView.Adapter<HistoryOrderAdapter.viewholder>{

    ArrayList<HistoryOrder> list;

    public HistoryOrderAdapter(ArrayList<HistoryOrder> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryOrderAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryOrderAdapter.viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_history_order,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

       holder.txtTitle.setText(list.get(position).getTitle());
       holder.txtDescription.setText(list.get(position).getDescription());
       holder.txtTime.setText(list.get(position).getTime());
       holder.txtPrice.setText(String.valueOf(list.get(position).getPrice()));
       holder.txtStatus.setText(list.get(position).getStatus());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription, txtTime, txtPrice, txtStatus;
        ImageView imageHistoryOrder;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitleHistoryOrder);
            imageHistoryOrder = itemView.findViewById(R.id.img_history);
            txtDescription = itemView.findViewById(R.id.txtHistoryOrderDescription);
            txtTime = itemView.findViewById(R.id.txtHistoryOrderTime);
            txtPrice = itemView.findViewById(R.id.txtHistoryOrderPrice);
            txtStatus = itemView.findViewById(R.id.txtHistoryOrderStatus);
        }
    }
}
