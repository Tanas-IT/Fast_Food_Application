package com.group4.net.fastfoodapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.net.fastfoodapp.R;
import com.group4.net.fastfoodapp.databinding.ActivityDetailBinding;
import com.group4.net.fastfoodapp.databinding.ActivityDetailFoodForAdminBinding;
import com.group4.net.fastfoodapp.domain.Foods;
import com.group4.net.fastfoodapp.helper.ManagementCart;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DetailFoodForAdminActivity extends BaseActivity {
    ActivityDetailFoodForAdminBinding binding;
    private Foods object;
    private int num = 1;
    private ManagementCart managementCart;
    private String imagePath = "";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailFoodForAdminBinding.inflate((getLayoutInflater()));
        setContentView(binding.getRoot());
        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        managementCart = new ManagementCart(DetailFoodForAdminActivity.this);
        binding.backAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Glide.with(this)
                .load(object.getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(60))
                .into(binding.edtAdminPic);
        binding.edtAdminPrice.setText(object.getPrice() + "");
        binding.editAdminTime.setText(object.getTimeValue() + "");
        binding.edtAdminTitle.setText(object.getTitle());
        binding.editAdminDescription.setText(object.getDescription());


        binding.btnUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFood(object.getId());
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

        binding.btnRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemFromFirebase(object.getId());
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Hiển thị hình ảnh được chọn trong ImageView (edtAdminPic)
            binding.edtAdminPic.setImageURI(imageUri);
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
                            Toast.makeText(DetailFoodForAdminActivity.this, "Upload image successfully!", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DetailFoodForAdminActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateFood(int foodId) {
        // Tạo map với các giá trị mới
        Map<String, Object> foodUpdates = new HashMap<>();
        foodUpdates.put("BestFood", object.isBestFood());
        foodUpdates.put("CategoryId", object.getCategoryId());
        foodUpdates.put("Description", binding.editAdminDescription.getText().toString());
        foodUpdates.put("Id", foodId);
        if(imagePath != null && !imagePath.equals("")) {
            foodUpdates.put("ImagePath",  imagePath);
        }
        else {
            foodUpdates.put("ImagePath",  object.getImagePath());
        }
        foodUpdates.put("LocationId", 1);
        foodUpdates.put("Price", Double.parseDouble(binding.edtAdminPrice.getText().toString()));
        foodUpdates.put("PriceId", object.getPriceId());
        foodUpdates.put("Star", object.getStar());
        foodUpdates.put("TimeId", object.getTimeId());
        foodUpdates.put("TimeValue", Integer.parseInt(binding.editAdminTime.getText().toString()));
        foodUpdates.put("Title", binding.edtAdminTitle.getText().toString());

        // Cập nhật dữ liệu trong Firebase theo ID món ăn
        database.getReference().child("Foods").child(String.valueOf(foodId)).updateChildren(foodUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Thông báo khi cập nhật thành công
                        Toast.makeText(DetailFoodForAdminActivity.this, "Udpate Food Successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Thông báo khi cập nhật thất bại
                        Toast.makeText(DetailFoodForAdminActivity.this, "Update Food Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteItemFromFirebase(int foodId) {
        database.getReference().child("Foods").child(String.valueOf(foodId)).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Xóa thành công, thông báo cho người dùng
                        Toast.makeText(DetailFoodForAdminActivity.this, "Item deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Xóa thất bại, hiển thị thông báo lỗi
                        Toast.makeText(DetailFoodForAdminActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }

}