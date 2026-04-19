package com.bisikChat;

import android.bluetooth.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import java.util.*;

public class ScanActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private TextView tvScanStatus;
    private RecyclerView rvDevices;
    private Button btnScan;
    private DeviceAdapter deviceAdapter;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        session = new UserSession(this);

        tvScanStatus = findViewById(R.id.tvScanStatus);
        rvDevices    = findViewById(R.id.rvDevices);
        btnScan      = findViewById(R.id.btnScan);

        BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bm.getAdapter();

        deviceAdapter = new DeviceAdapter(deviceList, device -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("device_address", device.getAddress());
            i.putExtra("device_name", device.getName() != null ? device.getName() : "Perangkat");
            startActivity(i);
        });
        rvDevices.setLayoutManager(new LinearLayoutManager(this));
        rvDevices.setAdapter(deviceAdapter);

        btnScan.setOnClickListener(v -> loadPairedDevices());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        loadPairedDevices();
    }

    private void loadPairedDevices() {
        deviceList.clear();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            tvScanStatus.setText("Bluetooth tidak aktif");
            return;
        }
        Set<BluetoothDevice> paired = bluetoothAdapter.getBondedDevices();
        deviceList.addAll(paired);
        deviceAdapter.notifyDataSetChanged();
        tvScanStatus.setText(paired.isEmpty()
            ? "Tidak ada perangkat. Pair dulu via Settings."
            : paired.size() + " perangkat ditemukan");
    }

    // ── Inner Adapter ──────────────────────────────────────
    static class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.VH> {
        interface OnClick { void on(BluetoothDevice d); }
        private final List<BluetoothDevice> list;
        private final OnClick cb;
        DeviceAdapter(List<BluetoothDevice> l, OnClick c) { list=l; cb=c; }

        @Override public VH onCreateViewHolder(ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_device, p, false));
        }
        @Override public void onBindViewHolder(VH h, int pos) {
            BluetoothDevice d = list.get(pos);
            String name = d.getName() != null ? d.getName() : "Perangkat";
            h.tvName.setText(name);
            h.tvAddr.setText(d.getAddress());
            h.tvInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
            h.itemView.setOnClickListener(v -> cb.on(d));
        }
        @Override public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvAddr, tvInitial;
            VH(View v) { super(v);
                tvName    = v.findViewById(R.id.devName);
                tvAddr    = v.findViewById(R.id.devAddr);
                tvInitial = v.findViewById(R.id.devInitial);
            }
        }
    }
}
