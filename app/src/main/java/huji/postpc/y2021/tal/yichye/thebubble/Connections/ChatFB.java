package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatFB implements Serializable {

    String ids;
    ArrayList<Message> chatMessages = new ArrayList<>();

    public ChatFB(){}

    public ChatFB(String chatIds){
        ids = chatIds;
    }

    public void addMessage(Message newMsg){
        chatMessages.add(newMsg);
    }

    public ArrayList<Message> getChatMessages() {
        return chatMessages;
    }

    public String getIds() {
        return ids;
    }
}
