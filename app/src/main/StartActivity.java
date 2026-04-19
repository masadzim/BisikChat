package com.bisikChat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

/**
 * StartActivity — pilih jenis percakapan setelah terhubung ke device.
 */
public class StartActivity extends AppCompatActivity {

    private String deviceAddress, deviceName;
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        session = new UserSession(this);

        deviceAddress = getIntent().getStringExtra("device_address");
        deviceName    = getIntent().getStringExtra("device_name");
        if (deviceName == null) deviceName = "Perangkat";

        TextView tvPeer = findViewById(R.id.tvPeerName);
        tvPeer.setText(deviceName);

        // Chat biasa
        findViewById(R.id.optChat).setOnClickListener(v -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("device_address", deviceAddress);
            i.putExtra("device_name", deviceName);
            startActivity(i);
        });

        // Panggilan suara
        findViewById(R.id.optCall).setOnClickListener(v -> {
            Intent i = new Intent(this, CallActivity.class);
            i.putExtra("peer_name", deviceName);
            i.putExtra("is_group", false);
            startActivity(i);
        });

        // Group chat
        findViewById(R.id.optGroup).setOnClickListener(v -> {
            startActivity(new Intent(this, GroupChatActivity.class));
        });

        // Group call / live call
        findViewById(R.id.optGroupCall).setOnClickListener(v -> {
            Intent i = new Intent(this, CallActivity.class);
            i.putExtra("peer_name", "Semua Anggota");
            i.putExtra("is_group", true);
            startActivity(i);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
