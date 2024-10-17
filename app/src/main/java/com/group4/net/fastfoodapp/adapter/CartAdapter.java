package com.group4.net.fastfoodapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.group4.net.fastfoodapp.R;
import com.group4.net.fastfoodapp.activity.CartActivity;
import com.group4.net.fastfoodapp.domain.Foods;
import com.group4.net.fastfoodapp.helper.ChangeNumberItemsListener;
import com.group4.net.fastfoodapp.helper.ManagementCart;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.viewholder> {
    ArrayList<Foods> list;
    private ManagementCart managementCart;
    ChangeNumberItemsListener changeNumberItemsListener;

    public CartAdapter(ArrayList<Foods> list, ManagementCart managementCart, ChangeNumberItemsListener changeNumberItemsListener) {
        this.list = list;
        this.managementCart = managementCart;
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public CartAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.viewholder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        double convertFeeEachItem = Math.round(list.get(position).getNumberInCart() * list.get(position).getPrice() * 100.0) / 100.0;
        holder.feeEachItem.setText("$" + convertFeeEachItem);
        holder.num.setText(list.get(position).getNumberInCart() + "");

        Glide.with(holder.itemView.getContext())
                .load(list.get(position).getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);

        holder.plusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managementCart.plusNumberItem(list, position, new ChangeNumberItemsListener() {
                    @Override
                    public void change() {
                        notifyDataSetChanged();
                        changeNumberItemsListener.change();
                    }

                    @Override
                    public void onCartUpdated() {

                    }
                });
            }
        });

        holder.minusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managementCart.minusNumberItem(list, position, new ChangeNumberItemsListener() {
                    @Override
                    public void change() {
                        notifyDataSetChanged();
                        changeNumberItemsListener.change();
                    }

                    @Override
                    public void onCartUpdated() {
                        notifyDataSetChanged();
                        changeNumberItemsListener.onCartUpdated();
                    }
                });
            }
        });

        holder.trashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managementCart.removeItem(list, position, new ChangeNumberItemsListener() {
                    @Override
                    public void change() {
                        notifyDataSetChanged();
                        changeNumberItemsListener.change();
                    }

                    @Override
                    public void onCartUpdated() {
                        notifyDataSetChanged();
                        changeNumberItemsListener.onCartUpdated();

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView title, feeEachItem,plusItem,minusItem;
        ImageView pic;
        TextView num;
        ConstraintLayout trashBtn;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            plusItem = itemView.findViewById(R.id.plusCartbtn);
            minusItem = itemView.findViewById(R.id.minusCartBtn);
            num = itemView.findViewById(R.id.numberItemTxt);
            trashBtn = itemView.findViewById(R.id.trashBtn);
        }
    }
}
