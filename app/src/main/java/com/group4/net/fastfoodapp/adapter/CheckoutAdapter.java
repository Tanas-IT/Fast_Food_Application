package com.group4.net.fastfoodapp.adapter;

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
import com.group4.net.fastfoodapp.domain.Foods;
import com.group4.net.fastfoodapp.helper.ManagementCart;

import java.util.ArrayList;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.viewholder>{
    ArrayList<Foods> list;
    private ManagementCart managementCart;

    public CheckoutAdapter(ArrayList<Foods> list, ManagementCart managementCart) {
        this.list = list;
        this.managementCart = managementCart;
    }

    @NonNull
    @Override
    public CheckoutAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CheckoutAdapter.viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_checkout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutAdapter.viewholder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        double convertFeeEachItem = Math.round(list.get(position).getPrice() * 100.0) / 100.0;
        holder.feeEachItem.setText("$" + convertFeeEachItem);
        holder.num.setText(list.get(position).getNumberInCart() + "");

        Glide.with(holder.itemView.getContext())
                .load(list.get(position).getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView title, feeEachItem;
        ImageView pic;
        TextView num;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            num = itemView.findViewById(R.id.numberItemTxt);
        }
    }
}
