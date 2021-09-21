package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_OTHER = 1;
    private final static int TYPE_ME = 2;
    ArrayList<Message> messages;

    public void setMessages(ArrayList<Message> toSet){
        messages = toSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        if (viewType == TYPE_ME){
            view = LayoutInflater.from(context)
                    .inflate(R.layout.self_message_item, parent, false);
            return new SelfMessageHolder(view);
        }
        else {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.other_message_item, parent, false);
            return new OtherMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message currentMsg = messages.get(position);

        if (getItemViewType(position) == TYPE_ME){
            ((SelfMessageHolder)holder).setSelfHolder(currentMsg);
        }

        else {
            ((OtherMessageHolder)holder).setOtherHolder(currentMsg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String currentUser = TheBubbleApplication.getInstance().getSP().getString("user_name", null);
        if(currentUser == null){
            return -1;
        }
        else if (messages.get(position).getSenderId().equals(currentUser)) {
            return TYPE_ME;
        }
        else {
            return TYPE_OTHER;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
