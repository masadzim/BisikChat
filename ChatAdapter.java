package com.bisikChat;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MINE=0, THEIRS=1, SYSTEM=2;
    private final List<ChatMessage> msgs;
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ChatAdapter(List<ChatMessage> msgs) { this.msgs = msgs; }

    @Override public int getItemViewType(int p) {
        ChatMessage m = msgs.get(p);
        if (m.isSystem) return SYSTEM;
        return m.isMine ? MINE : THEIRS;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (type == MINE)   return new MH(inf.inflate(R.layout.item_msg_mine,   parent, false));
        if (type == THEIRS) return new MH(inf.inflate(R.layout.item_msg_theirs, parent, false));
        return new SH(inf.inflate(R.layout.item_msg_system, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int p) {
        ChatMessage m = msgs.get(p);
        if (h instanceof MH) {
            ((MH)h).text.setText(m.text);
            ((MH)h).time.setText(sdf.format(new Date(m.timestamp)));
            TextView sender = h.itemView.findViewById(R.id.msgSender);
            if (sender != null) sender.setText(m.senderName != null ? m.senderName : "");
        } else if (h instanceof SH) {
            ((SH)h).text.setText(m.text);
        }
    }

    @Override public int getItemCount() { return msgs.size(); }

    static class MH extends RecyclerView.ViewHolder {
        TextView text, time;
        MH(View v) { super(v); text=v.findViewById(R.id.msgText); time=v.findViewById(R.id.msgTime); }
    }
    static class SH extends RecyclerView.ViewHolder {
        TextView text;
        SH(View v) { super(v); text=v.findViewById(R.id.systemText); }
    }
}
