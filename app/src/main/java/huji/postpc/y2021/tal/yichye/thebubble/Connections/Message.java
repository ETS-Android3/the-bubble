package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import java.io.Serializable;

public class Message implements Serializable {

    private boolean read;
    private String timeSent;
    private String dateSent;
    private String content;
    private String senderId;

    public Message(){}

    public Message(boolean didRead, String time, String msgContent, String sender, String date ) {
        read = didRead;
        timeSent = time;
        content = msgContent;
        senderId = sender;
        dateSent = date;
    }


    public String getSenderId() {
        return senderId;
    }

    public boolean isRead() {
        return read;
    }

    public String getContent() {
        return content;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public String getDateSent() {
        return dateSent;
    }
}
