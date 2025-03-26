package com.example.zdrowiepp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText email, password;
    Button btnRegister, btnBackToLogin;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        btnRegister = findViewById(R.id.btn_register_confirm);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);

        dbHelper = new DatabaseHelper(this);

        btnRegister.setOnClickListener(view -> registerUser());
        btnBackToLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Wypełnij wszystkie pola!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = dbHelper.insertUser(userEmail, userPassword);
        if (isInserted) {
            Toast.makeText(RegisterActivity.this, "Rejestracja udana!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "Błąd rejestracji! Email może już istnieć.", Toast.LENGTH_SHORT).show();
        }
    }
}
