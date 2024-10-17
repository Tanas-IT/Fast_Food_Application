package com.group4.net.fastfoodapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.group4.net.fastfoodapp.R;
import com.group4.net.fastfoodapp.adapter.FoodListAdapter;
import com.group4.net.fastfoodapp.adapter.HistoryOrderAdapter;
import com.group4.net.fastfoodapp.data.Database;
import com.group4.net.fastfoodapp.databinding.ActivityHistoryOrderBinding;
import com.group4.net.fastfoodapp.domain.HistoryOrder;
import com.group4.net.fastfoodapp.helper.ManagementCart;

import java.util.ArrayList;

public class HistoryOrderActivity extends AppCompatActivity {

    ActivityHistoryOrderBinding binding;
    private ManagementCart managementCart;
    Intent intent;
    ArrayList<HistoryOrder> listHistoryOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listHistoryOrder = new ArrayList<>();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String currentEmail = sharedPreferences.getString("checkRole", "admin@gmail.com");
        Database database = new Database(HistoryOrderActivity.this);
        //database.QueryData("Drop table HistoryOrder ");
        database.QueryData("Create table if not exists HistoryOrder(id Integer Primary Key Autoincrement," +
                "email nvarchar(100)," +
                "title nvarchar(200)," +
                "description nvarchar(1000)," +
                "price nvarchar(1000)," +
                "time nvarchar(50)," +
                "status nvarchar(50))");

        // Lấy dữ liệu từ bảng
        String query = "SELECT * FROM HistoryOrder WHERE email = ?";
        Cursor cursor = database.getReadableDatabase().rawQuery(query, new String[]{currentEmail});
        if (cursor.moveToFirst()) {
            do {
                // Lấy giá trị của từng cột
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                listHistoryOrder.add(new HistoryOrder(email, title, description,price, status, time));

                binding.txtNoOrderHistory.setVisibility(View.GONE);
            } while (cursor.moveToNext());
        }
        else {
            binding.txtNoOrderHistory.setVisibility(View.VISIBLE);
        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.listHistoryOrder.setLayoutManager(new LinearLayoutManager(HistoryOrderActivity.this, LinearLayoutManager.VERTICAL,false));
        binding.listHistoryOrder.setAdapter(new HistoryOrderAdapter(listHistoryOrder));

    }
}