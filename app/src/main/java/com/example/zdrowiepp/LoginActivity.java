package com.example.zdrowiepp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button btnLogin, btnRegister, btnForgotPassword;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(view -> loginUser());
        btnRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnForgotPassword.setOnClickListener(view -> recoverPassword());
    }

    private void loginUser() {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (dbHelper.checkUser(userEmail, userPassword)) {
            Toast.makeText(LoginActivity.this, "Logowanie udane!", Toast.LENGTH_SHORT).show();
            // Przejdź do kolejnego ekranu
        } else {
            Toast.makeText(LoginActivity.this, "Nieprawidłowe dane!", Toast.LENGTH_SHORT).show();
        }
    }

    private void recoverPassword() {
        String userEmail = email.getText().toString().trim();
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Podaj email!", Toast.LENGTH_SHORT).show();
            return;
        }

        String recoveredPassword = dbHelper.getPassword(userEmail);
        if (recoveredPassword != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Odzyskiwanie hasła")
                    .setMessage("Twoje hasło: " + recoveredPassword)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            Toast.makeText(this, "Nie znaleziono użytkownika!", Toast.LENGTH_SHORT).show();
        }
    }
}
