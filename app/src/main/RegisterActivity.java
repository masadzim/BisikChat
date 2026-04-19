package com.bisikChat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

/**
 * RegisterActivity — buat profil lokal pertama kali.
 * Semua data disimpan di SharedPreferences, TIDAK ke internet.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etPin, etPinConfirm, etStatus, etDevice;
    private TextView tvAvatarPreview;
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        session = new UserSession(this);

        etName        = findViewById(R.id.etName);
        etPin         = findViewById(R.id.etPin);
        etPinConfirm  = findViewById(R.id.etPinConfirm);
        etStatus      = findViewById(R.id.etStatus);
        etDevice      = findViewById(R.id.etDevice);
        tvAvatarPreview = findViewById(R.id.tvAvatarPreview);

        // Live preview initial
        etName.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){
                String n = s.toString().trim();
                tvAvatarPreview.setText(n.isEmpty() ? "?" :
                        String.valueOf(n.charAt(0)).toUpperCase());
            }
            public void afterTextChanged(android.text.Editable e){}
        });

        findViewById(R.id.btnRegister).setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String name   = etName.getText().toString().trim();
        String pin    = etPin.getText().toString().trim();
        String pinc   = etPinConfirm.getText().toString().trim();
        String status = etStatus.getText().toString().trim();
        String device = etDevice.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Nama tidak boleh kosong"); return;
        }
        if (pin.length() < 4) {
            etPin.setError("PIN minimal 4 digit"); return;
        }
        if (!pin.equals(pinc)) {
            etPinConfirm.setError("PIN tidak cocok"); return;
        }

        session.saveProfile(name, pin, status, device);
        Toast.makeText(this, "Profil berhasil dibuat! 🎉", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
