package com.group4.net.fastfoodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.group4.net.fastfoodapp.R;
import com.group4.net.fastfoodapp.adapter.CartAdapter;
import com.group4.net.fastfoodapp.databinding.ActivityCartBinding;
import com.group4.net.fastfoodapp.domain.Foods;
import com.group4.net.fastfoodapp.helper.ChangeNumberItemsListener;
import com.group4.net.fastfoodapp.helper.ManagementCart;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity {

    ActivityCartBinding binding;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managementCart = new ManagementCart(this);
        
        setVariable();
        calculateCart();
        initCartList();

    }

    private void checkCartEmpty() {
        if (managementCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.btnEmptyCart.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.btnEmptyCart.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }
    }

    private void initCartList() {
        if (managementCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.btnEmptyCart.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.btnEmptyCart.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.cartView.setAdapter( new CartAdapter(managementCart.getListCart(), managementCart, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                calculateCart();
            }

            @Override
            public void onCartUpdated() {
                checkCartEmpty();
            }
        }));
    }

    private void calculateCart() {
        double delivery = 10;
        double total =  Math.round((managementCart.getTotalFee() + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round(managementCart.getTotalFee()* 100.0) / 100.0;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnEmptyCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CartActivity.this, CheckOutActivity.class);
                myIntent.putExtra("foodsList", managementCart.getListCart());
                startActivityForResult(myIntent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.btnEmptyCart.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        }
    }
}