package com.group4.net.fastfoodapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.group4.net.fastfoodapp.R;
import com.group4.net.fastfoodapp.adapter.CategoryAdapter;
import com.group4.net.fastfoodapp.adapter.SliderAdapter;
import com.group4.net.fastfoodapp.databinding.ActivityMainBinding;
import com.group4.net.fastfoodapp.domain.Category;
import com.group4.net.fastfoodapp.domain.SliderItems;
import com.group4.net.fastfoodapp.helper.TinyDB;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;
    Intent myIntent;
    TinyDB tinyDB;
    private static final String KEY_SELECTED_ITEM = "selected_item";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         myIntent = getIntent();
         tinyDB = new TinyDB(MainActivity.this);
        initCategory();
        initBanner();
        setVariable();

    }

    private void initBanner() {
        DatabaseReference myRef = database.getReference("Banners");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItems> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot issue: snapshot.getChildren()) {
                        items.add(issue.getValue(SliderItems.class));
                    }
                    banners(items);
                    binding.progressBarBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void banners(ArrayList<SliderItems> items) {
        binding.viewpager2.setAdapter(new SliderAdapter(items, binding.viewpager2));
        binding.viewpager2.setClipChildren(false);
        binding.viewpager2.setClipToPadding(false);
        binding.viewpager2.setOffscreenPageLimit(3);
        binding.viewpager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.viewpager2.setPageTransformer(compositePageTransformer);

    }
    private void setVariable() {

        binding.bottomMenu.setItemSelected(R.id.home, true);
        tinyDB.putInt(KEY_SELECTED_ITEM, R.id.home);
        String emailAdmin =  myIntent.getStringExtra("email");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("checkRole", emailAdmin);  // Hoặc "user"
        editor.apply();

        if(emailAdmin.equals("admin@gmail.com")) {
            binding.bottomMenu.setVisibility(View.GONE);
            binding.btnAddFoodForAdmin.setVisibility(View.VISIBLE);
            binding.btnLogoutAdmin.setVisibility(View.VISIBLE);
            binding.btnAddFoodForAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AddFoodForAdminActivity.class);
                    startActivity(intent);
                }
            });
            binding.btnLogoutAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }
        else {
            binding.btnAddFoodForAdmin.setVisibility(View.GONE);
            binding.btnLogoutAdmin.setVisibility(View.GONE);
            binding.bottomMenu.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                @Override
                public void onItemSelected(int i) {
                    if (i == R.id.cart) {
                        startActivity(new Intent(MainActivity.this, CartActivity.class));
                    }
                    if (i == R.id.profile) {
                        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("imageURL", myIntent.getStringExtra("imageURL"));
                        profileIntent.putExtra("fullName", myIntent.getStringExtra("fullName"));
                        profileIntent.putExtra("email", myIntent.getStringExtra("email"));
                        startActivity(profileIntent);
                    }
                    if( i == R.id.chat) {
                        startActivity(new Intent(MainActivity.this, HistoryOrderActivity.class));
                    }
                }
            });
        }
        String photoUrl = myIntent.getStringExtra("imageURL");
        // Dùng Glide để tải ảnh
        Glide.with(this)
                .load(photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.btn_4)
                .error(R.drawable.btn_4)
                .into(binding.avatarProfile);

        if(myIntent.getStringExtra("fullName") == null) {
            binding.txtNameOfUser.setText(myIntent.getStringExtra("email"));
        }
        else {
            binding.txtNameOfUser.setText(myIntent.getStringExtra("fullName"));
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        int selectedItemId = tinyDB.getInt(KEY_SELECTED_ITEM); // Default item ID
        binding.bottomMenu.setItemSelected(selectedItemId, true);
    }

    private void initCategory() {
        DatabaseReference  myRef = database.getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot issue:snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));
                    }
                    if(list.size() > 0) {
                        binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                        binding.categoryView.setAdapter(new CategoryAdapter(list));
                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}