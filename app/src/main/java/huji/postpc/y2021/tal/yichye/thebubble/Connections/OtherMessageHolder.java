package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import huji.postpc.y2021.tal.yichye.thebubble.R;

public class OtherMessageHolder extends RecyclerView.ViewHolder {

    TextView date;
    TextView time;
    CardView msgCard;
    TextView msgText;


    public OtherMessageHolder(@NonNull View itemView) {
        super(itemView);
        date =itemView.findViewById(R.id.text_gchat_date_other);
        time = itemView.findViewById(R.id.text_timestamp_other);
        msgCard = itemView.findViewById(R.id.card_message_other);
        msgText = itemView.findViewById(R.id.text_message_other);
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

    public void setOtherHolder(Message message){
        date.setText(message.getDateSent());
        time.setText(message.getTimeSent());
        msgText.setText(message.getContent());
    }
}
