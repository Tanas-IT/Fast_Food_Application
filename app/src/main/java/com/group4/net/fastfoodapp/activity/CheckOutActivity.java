package com.group4.net.fastfoodapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.group4.net.fastfoodapp.R;
import com.group4.net.fastfoodapp.adapter.CheckoutAdapter;
import com.group4.net.fastfoodapp.api.PaymentAPI;
import com.group4.net.fastfoodapp.api.PaymentRequest;
import com.group4.net.fastfoodapp.api.PaymentResponse;
import com.group4.net.fastfoodapp.api.RetrofitClient;
import com.group4.net.fastfoodapp.data.Database;
import com.group4.net.fastfoodapp.databinding.ActivityCheckOutBinding;
import com.group4.net.fastfoodapp.domain.Foods;
import com.group4.net.fastfoodapp.helper.ManagementCart;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutActivity extends FragmentActivity implements OnMapReadyCallback {

    ActivityCheckOutBinding binding;
    private ManagementCart managementCart;
    Intent intent;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText editTextLocation;
    private final int LOCATION_REQUEST_CODE = 101;
    private Marker selectedMarker;

    private PaymentAPI paymentApi;
    double amount;

    Database database;
    String email= "";
    String description = "";
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
    String formattedDateTime = sdf.format(currentTime);

    Locale localeVN = new Locale("vi", "VN");
    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("checkRole", "");
        // Tao database History
        database = new Database(CheckOutActivity.this, "HistoryOrder.sqllite",null, 1);

        // Tao table HistoryOrder
        database.QueryData("Create table if not exists HistoryOrder(id Integer Primary Key Autoincrement," +
                "email nvarchar(100)," +
                "title nvarchar(200)," +
                "description nvarchar(1000)," +
                "price nvarchar(1000)," +
                "time nvarchar(50)," +
                "status nvarchar(50))");

        intent = getIntent();
        editTextLocation = findViewById(R.id.edtAddress);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        managementCart = new ManagementCart(this);
        if(managementCart.getListCart().isEmpty()) {
            ArrayList<Foods> foodsList = (ArrayList<Foods>) getIntent().getSerializableExtra("foodsList");
            for (Foods food: foodsList) {
                managementCart.insertFood(food, "Checkout Food");
                description += "- " + food.getTitle() + "\n";
            }
        }
        else {
            for (Foods food : managementCart.getListCart()) {
                description += "- " + food.getTitle() + "\n";
            }
        }


        // Khởi tạo Retrofit với base URL của server
        paymentApi = RetrofitClient.getClient("https://prm392babyshopapi20240919153910.azurewebsites.net/").create(PaymentAPI.class);

        setVariable();
        calculateCart();
        initCartList();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Kiểm tra quyền truy cập vị trí
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        getLocationAndSetMarker();

    }

    private void checkCartEmpty() {
        if (managementCart.getListCart().isEmpty()) {
            binding.checkoutSuccessTxt.setVisibility(View.VISIBLE);
            binding.btnEmptyCheckout.setVisibility(View.VISIBLE);
            binding.scrollViewCheckout.setVisibility(View.GONE);
        } else {
            binding.checkoutSuccessTxt.setVisibility(View.GONE);
            binding.btnEmptyCheckout.setVisibility(View.GONE);
            binding.scrollViewCheckout.setVisibility(View.VISIBLE);
        }
    }

    private void initCartList() {
        if (managementCart.getListCart().isEmpty()) {
            binding.checkoutSuccessTxt.setVisibility(View.VISIBLE);
            binding.btnEmptyCheckout.setVisibility(View.VISIBLE);
            binding.scrollViewCheckout.setVisibility(View.GONE);
        } else {
            binding.checkoutSuccessTxt.setVisibility(View.GONE);
            binding.btnEmptyCheckout.setVisibility(View.GONE);
            binding.scrollViewCheckout.setVisibility(View.VISIBLE);
        }
        binding.checkoutView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.checkoutView.setAdapter( new CheckoutAdapter(managementCart.getListCart(), managementCart));
    }

    private void calculateCart() {
        double delivery = 10;
        double total =  Math.round((managementCart.getTotalFee() + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round(managementCart.getTotalFee()* 100.0) / 100.0;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.deliveryTxt.setText("$" + delivery);

        amount = Math.round(total*24580);


        // Định dạng số
        String formattedAmount = currencyVN.format(amount);
        binding.totalTxt.setText("$" + total + " = " + formattedAmount);
    }

    private void setVariable() {
        binding.backCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(2, intent);
                finish();
            }
        });
        binding.btnEmptyCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        binding.btnAccurateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationAndSetMarker();
            }
        });
        binding.paymentBtn.setEnabled(true);
        binding.paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID uuid = UUID.randomUUID();
                int uniqueID =  uuid.hashCode();
                PaymentRequest paymentRequest = new PaymentRequest(uniqueID, "Thanh toán đơn hàng #" + uniqueID, amount);
                String formattedAmount = currencyVN.format(amount);
                String sql = "Insert into HistoryOrder values(null, ? , ? , ?, ?, ?, ?)";
                SQLiteStatement statement = database.getWritableDatabase().compileStatement(sql);
                statement.bindString(1, email);
                statement.bindString(2, "Order #" + uniqueID);
                statement.bindString(3, description);
                statement.bindString(4, formattedAmount);
                statement.bindString(5, formattedDateTime);
                statement.bindString(6, "Success");
                statement.execute();

                // Gửi yêu cầu thanh toán
                paymentApi.makePayment(paymentRequest).enqueue(new Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Nhận URL thanh toán từ server
                            String paymentUrl = response.body().getPaymentUrl();

                            // Mở trình duyệt với URL thanh toán
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
                            startActivity(browserIntent);

                        } else {
                            Toast.makeText(CheckOutActivity.this, "Lỗi khi nhận phản hồi từ server", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
                        Toast.makeText(CheckOutActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                binding.paymentBtn.setEnabled(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            handleDeepLink(intent);
        }
    }
    private void handleDeepLink(Intent intent) {
        Uri data = intent.getData();
        if (data != null && "fastfoodapp".equals(data.getScheme())) {
            // Lấy tham số từ URL callback
            String vnpResponseCode = data.getQueryParameter("vnp_ResponseCode");

            // Kiểm tra kết quả thanh toán
            if ("00".equals(vnpResponseCode)) {
                Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                managementCart.clearCart();

                binding.checkoutSuccessTxt.setVisibility(View.VISIBLE);
                binding.btnEmptyCheckout.setVisibility(View.VISIBLE);
                binding.scrollViewCheckout.setVisibility(View.GONE);
            } else {
                // Thanh toán thất bại, hiện thông báo lỗi
                Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getLocationAndSetMarker();
        // Lắng nghe sự kiện khi người dùng chọn vị trí trên bản đồ
        mMap.setOnMapClickListener(latLng -> {
            // Xóa marker trước đó nếu có
            if (selectedMarker != null) {
                selectedMarker.remove();
            }

            // Thêm marker tại vị trí mới
            selectedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));

            // Chuyển đổi từ tọa độ sang địa chỉ và hiển thị trong EditText
            Geocoder geocoder = new Geocoder(CheckOutActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String addressLine = address.getAddressLine(0);
                    editTextLocation.setText(addressLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void getLocationAndSetMarker() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Lấy được vị trí hiện tại
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                            // Đặt marker tại vị trí hiện tại
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                            // Chuyển đổi từ tọa độ sang địa chỉ và hiển thị trong EditText
                            Geocoder geocoder = new Geocoder(CheckOutActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    String addressLine = address.getAddressLine(0);
                                    editTextLocation.setText(addressLine);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    // Yêu cầu quyền truy cập vị trí
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndSetMarker();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}