package com.bisikChat;

import android.bluetooth.*;
import android.os.*;
import java.io.*;
import java.util.UUID;

public class BluetoothService {
    private static final UUID APP_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final String APP_NAME = "BisikChat";

    public static final int MESSAGE_READ        = 1;
    public static final int MESSAGE_CONNECTED   = 2;
    public static final int MESSAGE_DISCONNECTED= 3;
    public static final int MESSAGE_ERROR       = 4;

    private final Handler handler;
    private ServerThread serverThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    public BluetoothService(Handler handler) { this.handler = handler; }

    public synchronized void startServer() {
        stopAll();
        serverThread = new ServerThread();
        serverThread.start();
    }

    public synchronized void connect(BluetoothDevice device) {
        stopAll();
        connectThread = new ConnectThread(device);
        connectThread.start();
    }

    public void write(byte[] out) {
        ConnectedThread t;
        synchronized (this) { t = connectedThread; }
        if (t != null) t.write(out);
    }

    public synchronized void disconnect() {
        stopAll();
        handler.obtainMessage(MESSAGE_DISCONNECTED).sendToTarget();
    }

    public synchronized void stop() { stopAll(); }

    private void stopAll() {
        if (connectThread  != null) { connectThread.cancel();  connectThread  = null; }
        if (connectedThread!= null) { connectedThread.cancel();connectedThread= null; }
        if (serverThread   != null) { serverThread.cancel();   serverThread   = null; }
    }

    private synchronized void onConnected(BluetoothSocket socket, String name) {
        stopAll();
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        handler.obtainMessage(MESSAGE_CONNECTED, name).sendToTarget();
    }

    private class ServerThread extends Thread {
        private final BluetoothServerSocket ss;
        ServerThread() {
            BluetoothServerSocket tmp = null;
            try { tmp = BluetoothAdapter.getDefaultAdapter()
                           .listenUsingRfcommWithServiceRecord(APP_NAME, APP_UUID); }
            catch (IOException ignored) {}
            ss = tmp;
        }
        public void run() {
            BluetoothSocket socket;
            while (true) {
                try { socket = ss.accept(); }
                catch (IOException e) { break; }
                if (socket != null) {
                    String n = socket.getRemoteDevice().getName();
                    onConnected(socket, n != null ? n : "Perangkat");
                    try { ss.close(); } catch (IOException ignored) {}
                    break;
                }
            }
        }
        void cancel() { try { ss.close(); } catch (IOException ignored) {} }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        ConnectThread(BluetoothDevice d) {
            device = d;
            BluetoothSocket tmp = null;
            try { tmp = d.createRfcommSocketToServiceRecord(APP_UUID); }
            catch (IOException ignored) {}
            socket = tmp;
        }
        public void run() {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            try {
                socket.connect();
                onConnected(socket, device.getName() != null ? device.getName() : "Perangkat");
            } catch (IOException e) {
                try { socket.close(); } catch (IOException ignored) {}
                handler.obtainMessage(MESSAGE_ERROR, "Gagal terhubung").sendToTarget();
                handler.obtainMessage(MESSAGE_DISCONNECTED).sendToTarget();
            }
        }
        void cancel() { try { socket.close(); } catch (IOException ignored) {} }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream in;
        private final OutputStream out;
        ConnectedThread(BluetoothSocket s) {
            socket = s;
            InputStream ti = null; OutputStream to = null;
            try { ti = s.getInputStream(); to = s.getOutputStream(); }
            catch (IOException ignored) {}
            in = ti; out = to;
        }
        public void run() {
            byte[] buf = new byte[1024];
            int bytes;
            while (true) {
                try { bytes = in.read(buf);
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buf.clone()).sendToTarget(); }
                catch (IOException e) {
                    handler.obtainMessage(MESSAGE_DISCONNECTED).sendToTarget(); break; }
            }
        }
        void write(byte[] b) {
            try { out.write(b); }
            catch (IOException e) { handler.obtainMessage(MESSAGE_ERROR, "Gagal kirim").sendToTarget(); }
        }
        void cancel() { try { socket.close(); } catch (IOException ignored) {} }
    }
}
