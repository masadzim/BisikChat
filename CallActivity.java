package com.bisikChat;

import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

/**
 * CallActivity — layar panggilan suara / group call via Bluetooth.
 * (Audio routing nyata membutuhkan AudioRecord/AudioTrack — di sini skeleton siap dikembangkan)
 */
public class CallActivity extends AppCompatActivity {

    private TextView tvPeerName, tvStatus, tvTimer, tvAvatar, tvScreenTitle;
    private LinearLayout llGroupParticipants;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private int seconds = 0;
    private boolean muted = false, speakerOn = true, isGroup = false;
    private String peerName;

    private final String[] GROUP_NAMES = {"Istri", "Andi", "Sari", "Budi"};
    private final int[] GROUP_COLORS   = {0xFF0ECFB0, 0xFF8B5CF6, 0xFF22D97A, 0xFFFFB347};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        peerName    = getIntent().getStringExtra("peer_name");
        isGroup     = getIntent().getBooleanExtra("is_group", false);
        if (peerName == null) peerName = "Teman";

        tvPeerName          = findViewById(R.id.tvPeerName);
        tvStatus            = findViewById(R.id.tvStatus);
        tvTimer             = findViewById(R.id.tvTimer);
        tvAvatar            = findViewById(R.id.tvAvatar);
        tvScreenTitle       = findViewById(R.id.tvScreenTitle);
        llGroupParticipants = findViewById(R.id.llGroupParticipants);

        tvPeerName.setText(peerName);
        tvScreenTitle.setText(isGroup ? "Group Call" : "Panggilan Suara");
        tvAvatar.setText(String.valueOf(peerName.charAt(0)).toUpperCase());

        if (isGroup) {
            tvAvatar.setVisibility(View.GONE);
            llGroupParticipants.setVisibility(View.VISIBLE);
            buildGroupParticipants();
        }

        // Connecting → connected after 1.5s
        tvStatus.setText("Menyambungkan...");
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            tvStatus.setText(isGroup ? "● Live · " + GROUP_NAMES.length + " peserta" : "● Terhubung");
            startTimer();
        }, 1500);

        // Buttons
        findViewById(R.id.btnMute).setOnClickListener(v -> toggleMute());
        findViewById(R.id.btnSpeaker).setOnClickListener(v -> toggleSpeaker());
        findViewById(R.id.btnEnd).setOnClickListener(v -> endCall());
        findViewById(R.id.btnBack).setOnClickListener(v -> endCall());
    }

    private void buildGroupParticipants() {
        llGroupParticipants.removeAllViews();
        for (int i = 0; i < GROUP_NAMES.length; i++) {
            View item = getLayoutInflater().inflate(R.layout.item_call_participant, llGroupParticipants, false);
            TextView av   = item.findViewById(R.id.partAvatar);
            TextView name = item.findViewById(R.id.partName);
            av.setText(String.valueOf(GROUP_NAMES[i].charAt(0)));
            name.setText(GROUP_NAMES[i]);
            ((GradientDrawable) av.getBackground().mutate()).setColor(GROUP_COLORS[i]);
            // random "speaking" highlight
            if (i == 0) av.setAlpha(1f); else av.setAlpha(0.75f);
            llGroupParticipants.addView(item);
        }
    }

    private void startTimer() {
        timerHandler.post(new Runnable() {
            @Override public void run() {
                seconds++;
                int m = seconds / 60, s = seconds % 60;
                tvTimer.setText(String.format("%02d:%02d", m, s));
                timerHandler.postDelayed(this, 1000);
            }
        });
    }

    private void toggleMute() {
        muted = !muted;
        Button btn = findViewById(R.id.btnMute);
        btn.setText(muted ? "🔇 Bisukan ✓" : "🎙️ Bisukan");
        btn.setAlpha(muted ? 0.6f : 1f);
    }

    private void toggleSpeaker() {
        speakerOn = !speakerOn;
        Button btn = findViewById(R.id.btnSpeaker);
        btn.setText(speakerOn ? "🔊 Speaker" : "🔈 Speaker");
        btn.setAlpha(speakerOn ? 1f : 0.6f);
    }

    private void endCall() {
        timerHandler.removeCallbacksAndMessages(null);
        finish();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacksAndMessages(null);
    }
}
