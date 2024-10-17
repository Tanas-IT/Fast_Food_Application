package com.group4.net.fastfoodapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.group4.net.fastfoodapp.R;
import com.group4.net.fastfoodapp.databinding.ActivityProfileBinding;
import com.group4.net.fastfoodapp.helper.ManagementCart;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent myIntent = getIntent();
        String photoUrl = myIntent.getStringExtra("imageURL");
        managementCart = new ManagementCart(this);

        // Dùng Glide để tải ảnh
        Glide.with(this)
                .load(photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.btn_4)
                .error(R.drawable.btn_4)
                .into(binding.imgProfile);

        binding.txtProfileEmail.setText(myIntent.getStringExtra("email"));
        if(myIntent.getStringExtra("fullName") != null) {
            binding.txtProfileFullName.setText(myIntent.getStringExtra("fullName"));
        }
        else {
            binding.txtProfileFullName.setVisibility(View.GONE);
            binding.txtTitleFullNameProfile.setVisibility(View.GONE);
        }

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void logout() {
        // Đăng xuất khỏi Firebase
        FirebaseAuth.getInstance().signOut();

        // Đăng xuất khỏi Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // Sau khi hoàn tất đăng xuất, chuyển người dùng đến màn hình đăng nhập
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(ProfileActivity.this, "Log Out Success", Toast.LENGTH_SHORT).show();
            managementCart.clearCart(); // Xóa giỏ hàng khi đăng xuất
            finish(); // Đóng Activity hiện tại
        });
    }
}