package com.bisikChat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

/**
 * LoginActivity — verifikasi PIN lokal.
 * Tidak ada request internet sama sekali.
 */
public class LoginActivity extends AppCompatActivity {

    private UserSession session;
    private TextView tvWelcome, tvAvatar;
    private EditText etPin;
    private StringBuilder pinInput = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new UserSession(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvAvatar  = findViewById(R.id.tvAvatar);
        etPin     = findViewById(R.id.etPin);

        tvWelcome.setText("Halo, " + session.getName() + "!");
        tvAvatar.setText(session.getAvatar());
        tvAvatar.getBackground().mutate();
        ((android.graphics.drawable.GradientDrawable) tvAvatar.getBackground())
            .setColor(session.getColor());

        // Numpad PIN buttons
        int[] numIds = {R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,
                        R.id.btn4,R.id.btn5,R.id.btn6,R.id.btn7,
                        R.id.btn8,R.id.btn9};
        String[] nums = {"0","1","2","3","4","5","6","7","8","9"};
        for (int i = 0; i < numIds.length; i++) {
            final String digit = nums[i];
            findViewById(numIds[i]).setOnClickListener(v -> appendPin(digit));
        }
        findViewById(R.id.btnDel).setOnClickListener(v -> deletePin());
        findViewById(R.id.btnLogin).setOnClickListener(v -> doLogin());
        // Skip PIN option (if no PIN set)
        if (!session.hasPin()) doLogin();
    }

    private void appendPin(String d) {
        if (pinInput.length() >= 6) return;
        pinInput.append(d);
        updateDots();
    }

    private void deletePin() {
        if (pinInput.length() > 0)
            pinInput.deleteCharAt(pinInput.length()-1);
        updateDots();
    }

    private void updateDots() {
        StringBuilder dots = new StringBuilder();
        for (int i = 0; i < pinInput.length(); i++) dots.append("● ");
        for (int i = pinInput.length(); i < 4; i++) dots.append("○ ");
        etPin.setText(dots.toString().trim());
        if (pinInput.length() >= 4) doLogin();
    }

    private void doLogin() {
        String entered = pinInput.toString();
        if (session.hasPin() && !session.verifyPin(entered)) {
            etPin.setError("PIN salah");
            etPin.setText("○ ○ ○ ○");
            pinInput.setLength(0);
            Toast.makeText(this, "PIN salah, coba lagi", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
