package com.group4.net.fastfoodapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.net.fastfoodapp.databinding.ActivityAddFoodForAdminBinding;
import com.group4.net.fastfoodapp.domain.Foods;
import com.group4.net.fastfoodapp.helper.ManagementCart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddFoodForAdminActivity extends BaseActivity {
    ActivityAddFoodForAdminBinding binding;
    private Foods object;
    private int num = 1;
    private ManagementCart managementCart;
    private String imagePath = "";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    ArrayList<String> valueList = new ArrayList<>();
    ArrayList<Integer> idList = new ArrayList<>();
    private int sizeOfListFood = 0;
    int selectedCategoryrId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFoodForAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariable();
    }

    private void setVariable() {
        managementCart = new ManagementCart(AddFoodForAdminActivity.this);
        binding.backAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnChangeImageFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        binding.adminAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood();
            }
        });



        DatabaseReference  myRef = database.getReference("Category");
        // Lấy dữ liệu từ Firebase
       myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                valueList.clear(); // Xóa danh sách trước khi tải dữ liệu mới
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy id và value từ Firebase
                    Integer id = snapshot.child("Id").getValue(Integer.class);
                    String value = snapshot.child("Name").getValue(String.class);
                    // Thêm vào danh sách
                    idList.add(id);
                    valueList.add(value);
                }

                // Tạo và gán Adapter cho Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddFoodForAdminActivity.this,
                        android.R.layout.simple_spinner_item, valueList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.adminspinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
            }
       });

        DatabaseReference  myRefFood = database.getReference("Foods");
        myRefFood.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    sizeOfListFood++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        });

        // Xử lý khi người dùng chọn một mục từ Spinner
        binding.adminspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryrId = idList.get(position);
                String selectedValue = valueList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addFood() {
        // Tạo map với các giá trị mới
        Map<String, Object> foodAdd = new HashMap<>();

        int foodId = sizeOfListFood;
        String title = binding.edtAdminAddTitle.getText().toString();
        double price = Double.parseDouble(binding.edtAdminAddPrice.getText().toString());
        int timeCooking = Integer.parseInt(binding.editAdminAddTime.getText().toString());
        String description = binding.editAdminAddDescription.getText().toString();

        foodAdd.put("BestFood", Boolean.valueOf("true"));
        foodAdd.put("CategoryId", selectedCategoryrId);
        foodAdd.put("Description", description);
        foodAdd.put("Id", foodId);
        if(imagePath != null && !imagePath.equals("")) {
            foodAdd.put("ImagePath",  imagePath);
        }
        else {
            foodAdd.put("ImagePath",  "https://firebasestorage.googleapis.com/v0/b/prm392-project-bd047.appspot.com/o/default_food_image_nobg.png?alt=media&token=0607f351-ec94-48c9-a694-c8e53c2bf6a4");
        }
        foodAdd.put("LocationId", 1);
        foodAdd.put("Price", price);
        foodAdd.put("PriceId", 1);
        foodAdd.put("Star", 5);
        foodAdd.put("TimeId", 1);
        foodAdd.put("TimeValue", timeCooking);
        foodAdd.put("Title",title);

        // Cập nhật dữ liệu trong Firebase theo ID món ăn
        database.getReference().child("Foods").child(String.valueOf(foodId)).setValue(foodAdd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Thông báo khi cập nhật thành công
                        Toast.makeText(AddFoodForAdminActivity.this, "Add Food Successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Thông báo khi cập nhật thất bại
                        Toast.makeText(AddFoodForAdminActivity.this, "Add Food Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Hiển thị hình ảnh được chọn trong ImageView (edtAdminPic)
            binding.edtAdminAddPic.setImageURI(imageUri);
            // Upload hình ảnh lên Firebase Storage
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("food_images/" + System.currentTimeMillis() + ".jpg");

            // Upload tệp
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Lấy URL của tệp sau khi upload thành công
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            imagePath = uri.toString(); // Đây là link của ảnh
                            // Lưu URL vào biến imagePath để sử dụng trong logic khác (ví dụ: update dữ liệu)
                            Toast.makeText(AddFoodForAdminActivity.this, "Upload image successfully!", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddFoodForAdminActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}