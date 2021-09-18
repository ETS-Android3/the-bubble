package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import huji.postpc.y2021.tal.yichye.thebubble.R;

public class SelfMessageHolder extends RecyclerView.ViewHolder {

    TextView date;
    TextView time;
    CardView msgCard;
    TextView msgText;

    public SelfMessageHolder(@NonNull View itemView) {
        super(itemView);
        date =itemView.findViewById(R.id.text_date_me);
        time = itemView.findViewById(R.id.text_timestamp_me);
        msgCard = itemView.findViewById(R.id.card_message_me);
        msgText = itemView.findViewById(R.id.text_message_me);
    }
    public CardView getMsgCard() {
        return msgCard;
    }

    public TextView getDate() {
        return date;
    }

    public TextView getTime() {
        return time;
    }

    public TextView getMsgText() {
        return msgText;
    }


    public void setSelfHolder(Message message){
        date.setText(message.getDateSent());
        time.setText(message.getTimeSent());
        msgText.setText(message.getContent());

    }



}
