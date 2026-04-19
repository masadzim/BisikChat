package com.bisikChat;

import android.bluetooth.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.*;
import android.Manifest;

/**
 * MainActivity — Home screen.
 * Menampilkan profil, status BT, kontak terakhir, dan navigasi ke semua fitur.
 */
public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private UserSession session;
    private TextView tvSelfAvatar, tvSelfName, tvSelfStatus, tvBtStatus;

    private static final int REQUEST_PERMISSIONS = 1;
    private static final int REQUEST_ENABLE_BT   = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new UserSession(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish(); return;
        }

        setContentView(R.layout.activity_main);
        initViews();
        initBluetooth();
        requestPermissions();
    }

    private void initViews() {
        tvSelfAvatar = findViewById(R.id.tvSelfAvatar);
        tvSelfName   = findViewById(R.id.tvSelfName);
        tvSelfStatus = findViewById(R.id.tvSelfStatus);
        tvBtStatus   = findViewById(R.id.tvBtStatus);

        tvSelfName.setText(session.getName());
        tvSelfStatus.setText("● " + session.getStatus());
        tvSelfAvatar.setText(session.getAvatar());
        ((GradientDrawable) tvSelfAvatar.getBackground().mutate()).setColor(session.getColor());

        // Avatar long press → profil menu
        tvSelfAvatar.setOnLongClickListener(v -> { showProfileMenu(); return true; });

        // Quick action cards
        findViewById(R.id.cardScan).setOnClickListener(v ->
            startActivity(new Intent(this, ScanActivity.class)));

        findViewById(R.id.cardStart).setOnClickListener(v ->
            startActivity(new Intent(this, StartActivity.class)));

        findViewById(R.id.cardGroup).setOnClickListener(v ->
            startActivity(new Intent(this, GroupChatActivity.class)));

        findViewById(R.id.cardCall).setOnClickListener(v -> {
            Intent i = new Intent(this, CallActivity.class);
            i.putExtra("peer_name", "Group Call");
            i.putExtra("is_group", true);
            startActivity(i);
        });

        // Bottom nav
        findViewById(R.id.navScan).setOnClickListener(v ->
            startActivity(new Intent(this, ScanActivity.class)));
        findViewById(R.id.navGroup).setOnClickListener(v ->
            startActivity(new Intent(this, GroupChatActivity.class)));
        findViewById(R.id.navNew).setOnClickListener(v ->
            startActivity(new Intent(this, StartActivity.class)));
    }

    private void initBluetooth() {
        BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bm.getAdapter();
        if (bluetoothAdapter == null) {
            tvBtStatus.setText("🔴 Bluetooth tidak tersedia");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        } else {
            int paired = bluetoothAdapter.getBondedDevices().size();
            tvBtStatus.setText("🔵 Bluetooth aktif · " + paired + " perangkat dipasangkan");
        }
    }

    private void requestPermissions() {
        String[] perms = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION
        };
        List<String> needed = new ArrayList<>();
        for (String p : perms)
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                needed.add(p);
        if (!needed.isEmpty())
            ActivityCompat.requestPermissions(this, needed.toArray(new String[0]), REQUEST_PERMISSIONS);
    }

    private void showProfileMenu() {
        new AlertDialog.Builder(this)
            .setTitle(session.getName())
            .setItems(new String[]{"👤 Lihat Profil", "✏️ Edit Status", "🚪 Logout"}, (d, w) -> {
                if (w == 0) showProfile();
                else if (w == 1) editStatus();
                else confirmLogout();
            }).show();
    }

    private void showProfile() {
        new AlertDialog.Builder(this)
            .setTitle("Profil Saya")
            .setMessage("Nama     : " + session.getName()
                + "\nStatus   : " + session.getStatus()
                + "\nDevice   : " + session.getDeviceAlias()
                + "\n\n🔒 Data tersimpan lokal · Tidak ada cloud")
            .setPositiveButton("OK", null).show();
    }

    private void editStatus() {
        EditText et = new EditText(this);
        et.setText(session.getStatus());
        new AlertDialog.Builder(this)
            .setTitle("Edit Status")
            .setView(et)
            .setPositiveButton("Simpan", (d, w) -> {
                session.updateStatus(et.getText().toString().trim());
                tvSelfStatus.setText("● " + session.getStatus());
            })
            .setNegativeButton("Batal", null).show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
            .setTitle("Logout?")
            .setMessage("Profil lokal akan dihapus dari perangkat ini.")
            .setPositiveButton("Logout", (d, w) -> {
                session.logout();
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
            })
            .setNegativeButton("Batal", null).show();
    }
}
