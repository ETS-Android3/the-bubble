package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

import huji.postpc.y2021.tal.yichye.thebubble.GlideApp;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;

public class ContactsAdapter extends RecyclerView.Adapter<ContactHolder> {

    ArrayList<ChatInfo> chatInfos;
    interface OnXClicked { void onClickedX(ChatInfo chatInfo); }
    public ContactsAdapter.OnXClicked XclickedCallable = null;
    interface OnListingClicked { void onListingClicked(ChatInfo chatInfo); }
    public  ContactsAdapter.OnListingClicked listingClickedCallable = null;
    Context adapterContext = null;


    public ContactsAdapter(){
        super();
    }


    public void setChatInfos(ArrayList<ChatInfo> toSet){
        chatInfos = toSet;
        Collections.sort(toSet);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        adapterContext = context;
        View view = LayoutInflater.from(context)
                .inflate(R.layout.contact_item, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        ChatInfo chatInfo = chatInfos.get(position);
        String withId = chatInfo.getChatWith();

        StorageReference imageRef = TheBubbleApplication
                .getInstance()
                .getImageStorageDB()
                .createReference(chatInfo.getChatWith(), "profileImage");
        GlideApp.with(adapterContext)
                .load(imageRef)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.getUserIcon());

        if (chatInfo.getLastSentMsg() == null){
            holder.getLastMsg().setText("NEW CONTACT");
            holder.getLastMsg().setTextColor(Color.parseColor("#CA7CC8"));
            holder.getLastMsgTimeOrDate().setText("");
        }
        else {
            holder.getLastMsg().setText(makeMessageSuitableLength(chatInfo.getLastSentMsg()));
            String res = getDateOrTimeForItem(chatInfo.getDateLastSentMsg(),chatInfo.getTimeLastSentMsg());
            holder.getLastMsgTimeOrDate().setText(res);
        }
        holder.getUserName().setText(withId);
        //todo set text for i image - at the moment


        holder.getDeleteIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XclickedCallable.onClickedX(chatInfo);
            }
        });

        holder.getNameAndMsgLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo when click listing update live data by defining function in fragment of contacts

                listingClickedCallable.onListingClicked(chatInfo);

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatInfos.size();
    }

    private String makeMessageSuitableLength(String msg){
        if (msg.length() < 20) return msg;
        else {
            return msg.substring(0,17) + "...";

        }
    }

    private String getDateOrTimeForItem(String date, String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(date+ " "+ time,formatter);
        if(dateTime.toLocalDate().equals(LocalDate.now())){
            return time;
        }
        else {
            return date;
        }
    }
}
