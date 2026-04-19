package com.bisikChat;

import android.os.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import android.content.Intent;
import java.util.*;

/**
 * GroupChatActivity — chat grup multi-peer via Bluetooth mesh sederhana.
 * Setiap pesan broadcast ke semua koneksi aktif.
 */
public class GroupChatActivity extends AppCompatActivity {

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText etMessage;
    private TextView tvMemberCount;
    private UserSession session;

    // Simulasi member group (dalam implementasi nyata dari BT connected peers)
    private final String[] GROUP_MEMBERS = {"Istri", "Andi", "Budi", "Sari"};
    private final String[] MEMBER_REPLIES = {
        "Oke siap! 😊", "Otw nih!", "Sudah di sini 🙌",
        "Hahaha bener banget!", "Setuju!", "Tunggu ya 5 menit lagi",
        "Yuk yuk!", "Di mana? Nyusul ah"
    };

    private Handler autoReply = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        session = new UserSession(this);

        recyclerView  = findViewById(R.id.recyclerView);
        etMessage     = findViewById(R.id.etMessage);
        tvMemberCount = findViewById(R.id.tvMemberCount);

        tvMemberCount.setText(GROUP_MEMBERS.length + " anggota aktif");

        chatAdapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        addSystem("Grup dibuat · " + GROUP_MEMBERS.length + " anggota via Bluetooth");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());
        etMessage.setOnEditorActionListener((v, id, e) -> { sendMessage(); return true; });

        // Group call button
        findViewById(R.id.btnGroupCall).setOnClickListener(v -> {
            Intent i = new Intent(this, CallActivity.class);
            i.putExtra("peer_name", "Grup · " + GROUP_MEMBERS.length + " anggota");
            i.putExtra("is_group", true);
            startActivity(i);
        });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;
        addMsg(text, true, session.getName());
        etMessage.setText("");

        // Simulate one random member reply
        int delay = 1200 + new Random().nextInt(800);
        autoReply.postDelayed(() -> {
            String member = GROUP_MEMBERS[new Random().nextInt(GROUP_MEMBERS.length)];
            String reply  = MEMBER_REPLIES[new Random().nextInt(MEMBER_REPLIES.length)];
            addMsg(reply, false, member);
        }, delay);
    }

    private void addMsg(String text, boolean mine, String sender) {
        messages.add(new ChatMessage(text, mine, System.currentTimeMillis(), sender));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void addSystem(String text) {
        messages.add(new ChatMessage(text, false, System.currentTimeMillis(), null, true));
        chatAdapter.notifyItemInserted(messages.size() - 1);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        autoReply.removeCallbacksAndMessages(null);
    }
}
