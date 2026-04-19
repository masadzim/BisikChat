package com.bisikChat;

public class ChatMessage {
    public String text;
    public boolean isMine;
    public long timestamp;
    public String senderName;
    public boolean isSystem;

    public ChatMessage(String text, boolean isMine, long timestamp, String senderName) {
        this.text = text; this.isMine = isMine;
        this.timestamp = timestamp; this.senderName = senderName;
        this.isSystem = false;
    }

    public ChatMessage(String text, boolean isMine, long timestamp, String senderName, boolean isSystem) {
        this.text = text; this.isMine = isMine;
        this.timestamp = timestamp; this.senderName = senderName;
        this.isSystem = isSystem;
    }
}
