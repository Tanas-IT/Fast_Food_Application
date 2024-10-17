package com.group4.net.fastfoodapp.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group4.net.fastfoodapp.R;

public class RegisterActivity extends AppCompatActivity {

    EditText edtRegisterEmail, edtRegisterPassword,
            edtConfirmPassword;
    Button btnRegister, btnAlreadyHaveAccount;

    private FirebaseAuth mAuth;

    private final String REQUIRED = "Required";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();



        edtRegisterEmail = findViewById(R.id.edt_register_email);
        edtRegisterPassword = findViewById(R.id.edt_register_password);
        edtConfirmPassword = findViewById(R.id.edt_register_confirm_password);
        btnAlreadyHaveAccount = findViewById(R.id.btn_already_have_account);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(edtRegisterEmail.getText().toString(), edtRegisterPassword.getText().toString());
            }
        });

        btnAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // Method for email/password sign-up
    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(RegisterActivity.this, "Regiser Success", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                        finish();
                    }}
                );
    }

    private boolean validateForm() {
        String registerEmail = edtRegisterEmail.getText().toString();
        if(TextUtils.isEmpty(registerEmail)) {
            edtRegisterEmail.setError(REQUIRED);
            return false;
        }

        String registerPassword = edtRegisterPassword.getText().toString();
        if(registerPassword.length() < 6) {
            edtRegisterPassword.setError("Password must be at least 6 characters");
            return false;
        }
        String registerConfirmPassword = edtConfirmPassword.getText().toString();
        if(TextUtils.isEmpty(registerPassword) || !registerPassword.equals(registerConfirmPassword)) {
            edtConfirmPassword.setError("Confirm password must not be empty and must be same password");
            return false;
        }
        return  true;
    }

}