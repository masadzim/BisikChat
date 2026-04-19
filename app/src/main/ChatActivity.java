package com.bisikChat;

import android.bluetooth.*;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import java.util.*;

/**
 * ChatActivity — chat 1-on-1 via Bluetooth.
 * Nama pengirim dikirim bersama pesan: "NAMA|ISI_PESAN"
 */
public class ChatActivity extends AppCompatActivity {

    private BluetoothService bluetoothService;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();

    private RecyclerView recyclerView;
    private EditText etMessage;
    private TextView tvPeerName, tvStatus, tvCallBtn;
    private UserSession session;

    private String deviceAddress, deviceName;
    private boolean connected = false;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_READ:
                    byte[] buf = (byte[]) msg.obj;
                    String raw = new String(buf, 0, msg.arg1);
                    String sender = "Tamu", text = raw;
                    if (raw.contains("|")) {
                        sender = raw.substring(0, raw.indexOf("|"));
                        text   = raw.substring(raw.indexOf("|") + 1);
                    }
                    addMsg(text, false, sender);
                    break;
                case BluetoothService.MESSAGE_CONNECTED:
                    connected = true;
                    tvStatus.setText("● Online via Bluetooth");
                    addSystem("Terhubung dengan " + msg.obj);
                    break;
                case BluetoothService.MESSAGE_DISCONNECTED:
                    connected = false;
                    tvStatus.setText("● Terputus");
                    addSystem("Koneksi terputus");
                    break;
                case BluetoothService.MESSAGE_ERROR:
                    Toast.makeText(ChatActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        session = new UserSession(this);

        deviceAddress = getIntent().getStringExtra("device_address");
        deviceName    = getIntent().getStringExtra("device_name");
        if (deviceName == null) deviceName = "Tamu";

        tvPeerName = findViewById(R.id.tvPeerName);
        tvStatus   = findViewById(R.id.tvStatus);
        etMessage  = findViewById(R.id.etMessage);
        recyclerView = findViewById(R.id.recyclerView);
        tvCallBtn  = findViewById(R.id.tvCallBtn);

        tvPeerName.setText(deviceName);

        // Set peer avatar initial
        TextView tvPeerAv = findViewById(R.id.tvPeerAvatar);
        tvPeerAv.setText(String.valueOf(deviceName.charAt(0)).toUpperCase());

        chatAdapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // BT
        bluetoothService = new BluetoothService(handler);
        bluetoothService.startServer();
        if (deviceAddress != null) connectTo();

        // Listeners
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());
        etMessage.setOnEditorActionListener((v, id, e) -> { sendMessage(); return true; });

        tvCallBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, CallActivity.class);
            i.putExtra("peer_name", deviceName);
            i.putExtra("is_group", false);
            startActivity(i);
        });

        addSystem("Chat dengan " + deviceName + " · Tanpa Internet");
    }

    private void connectTo() {
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = ba.getRemoteDevice(deviceAddress);
        bluetoothService.connect(device);
        tvStatus.setText("⏳ Menyambungkan...");
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;
        if (!connected) {
            Toast.makeText(this, "Belum terhubung", Toast.LENGTH_SHORT).show();
            return;
        }
        String payload = session.getName() + "|" + text;
        bluetoothService.write(payload.getBytes());
        addMsg(text, true, session.getName());
        etMessage.setText("");
    }

    private void addMsg(String text, boolean mine, String sender) {
        messages.add(new ChatMessage(text, mine, System.currentTimeMillis(), sender));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void addSystem(String text) {
        messages.add(new ChatMessage(text, false, System.currentTimeMillis(), null, true));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (bluetoothService != null) bluetoothService.stop();
    }
}
