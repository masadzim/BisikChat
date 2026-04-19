package com.bisikChat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Splash — cek apakah user sudah pernah buat profil lokal.
 * Kalau sudah → LoginActivity (minta PIN)
 * Kalau belum → RegisterActivity (buat profil baru)
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            UserSession session = new UserSession(this);
            Intent intent;
            if (session.isLoggedIn()) {
                intent = new Intent(this, LoginActivity.class);
            } else {
                intent = new Intent(this, RegisterActivity.class);
            }
            startActivity(intent);
            finish();
        }, 1800);
    }
}
