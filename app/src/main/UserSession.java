package com.bisikChat;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * UserSession — identitas pengguna HANYA di device ini.
 * Tidak ada cloud, tidak ada server.
 */
public class UserSession {
    private static final String PREF_NAME  = "bisik_user";
    private static final String KEY_LOGGED = "is_logged_in";
    private static final String KEY_NAME   = "display_name";
    private static final String KEY_AVATAR = "avatar_initial";
    private static final String KEY_COLOR  = "avatar_color";
    private static final String KEY_PIN    = "user_pin";
    private static final String KEY_STATUS = "user_status";
    private static final String KEY_DEVICE = "device_alias";

    private final SharedPreferences prefs;

    public UserSession(Context ctx) {
        prefs = ctx.getApplicationContext()
                   .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED, false);
    }

    public void saveProfile(String name, String pin, String status, String deviceAlias) {
        String initial = name.trim().isEmpty() ? "?" :
                String.valueOf(name.trim().charAt(0)).toUpperCase();
        int[] colors = {0xFF3B7BFF, 0xFF0ECFB0, 0xFFFF6B35, 0xFF8B5CF6,
                        0xFF22D97A, 0xFFFFB347, 0xFFFF4D6A, 0xFF06B6D4};
        int color = colors[Math.abs(name.hashCode()) % colors.length];
        prefs.edit()
             .putBoolean(KEY_LOGGED, true)
             .putString(KEY_NAME, name.trim())
             .putString(KEY_AVATAR, initial)
             .putInt(KEY_COLOR, color)
             .putString(KEY_PIN, pin)
             .putString(KEY_STATUS, status.isEmpty() ? "Tersedia" : status)
             .putString(KEY_DEVICE, deviceAlias.isEmpty() ? name + "'s Phone" : deviceAlias)
             .apply();
    }

    public boolean verifyPin(String inputPin) {
        return prefs.getString(KEY_PIN, "").equals(inputPin);
    }

    public boolean hasPin() {
        String p = prefs.getString(KEY_PIN, "");
        return p != null && !p.isEmpty();
    }

    public String getName()        { return prefs.getString(KEY_NAME, "Pengguna"); }
    public String getAvatar()      { return prefs.getString(KEY_AVATAR, "?"); }
    public int    getColor()       { return prefs.getInt(KEY_COLOR, 0xFF3B7BFF); }
    public String getStatus()      { return prefs.getString(KEY_STATUS, "Tersedia"); }
    public String getDeviceAlias() { return prefs.getString(KEY_DEVICE, "My Phone"); }

    public void updateStatus(String s) { prefs.edit().putString(KEY_STATUS, s).apply(); }
    public void updateName(String name) {
        String initial = name.trim().isEmpty() ? "?" :
                String.valueOf(name.trim().charAt(0)).toUpperCase();
        prefs.edit().putString(KEY_NAME, name.trim()).putString(KEY_AVATAR, initial).apply();
    }
    public void logout() { prefs.edit().clear().apply(); }
}
