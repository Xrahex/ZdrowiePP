package com.example.zdrowiepp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private InterstitialAd mInterstitialAd;
    private AdView adView;
    EditText email, password;
    Button btnLogin, btnRegister, btnForgotPassword;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeAdMob();
        initializeViews();
        loadBannerAd();

        dbHelper = new DatabaseHelper(this);
        setupClickListeners();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(view -> loginUser());
        btnRegister.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        btnForgotPassword.setOnClickListener(view -> recoverPassword());
    }

    private void initializeAdMob() {
        MobileAds.initialize(this, initializationStatus -> {
            Log.d(TAG, "AdMob zainicjalizowany");
            loadInterstitialAd();
        });
    }

    private void loginUser() {
        try {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString();

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper != null && dbHelper.checkUser(userEmail, userPassword)) {
                Toast.makeText(LoginActivity.this, "Logowanie udane!", Toast.LENGTH_SHORT).show();
                showInterstitialAd();
            } else {
                Toast.makeText(LoginActivity.this, "Nieprawidłowe dane!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Błąd podczas logowania: " + e.getMessage());
            Toast.makeText(this, "Wystąpił błąd podczas logowania", Toast.LENGTH_SHORT).show();
        }
    }

    private void recoverPassword() {
        try {
            String userEmail = email.getText().toString().trim();
            if (userEmail.isEmpty()) {
                Toast.makeText(this, "Podaj email!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper != null) {
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
            } else {
                Toast.makeText(this, "Błąd bazy danych", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Błąd przy odzyskiwaniu hasła: " + e.getMessage());
            Toast.makeText(this, "Wystąpił błąd przy odzyskiwaniu hasła", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
        adView = findViewById(R.id.adView);
    }

    private void loadBannerAd() {
        if (adView != null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    Log.e(TAG, "Błąd ładowania reklamy: " + adError.getMessage());
                }
            });
        }
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                mInterstitialAd = null;
                                loadInterstitialAd();
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, "Błąd ładowania reklamy pełnoekranowej: " + loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            Log.d(TAG, "Reklama pełnoekranowa nie jest gotowa");
            loadInterstitialAd();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mInterstitialAd == null) {
            loadInterstitialAd();
        }
    }
}
