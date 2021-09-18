package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

//todo currently assuming user icon and name is in object
public class ChatInfo implements Serializable, Comparable<ChatInfo> {

    private String chatWith;
    private String lastSentMsg;
    private String timeLastSentMsg;
    private String dateLastSentMsg;
    boolean newChat;



    public ChatInfo(){}

    public ChatInfo(String id){
        chatWith = id;
        lastSentMsg = null;
    }




    public String getChatWith() {
        return chatWith;
    }

    public String getLastSentMsg() {
        return lastSentMsg;
    }

    public String getTimeLastSentMsg() {
        return timeLastSentMsg;
    }

    public String getDateLastSentMsg() {
        return dateLastSentMsg;
    }

    public boolean isNewChat() {
        return newChat;
    }

    public void setLastSentMsg(String lastSentMsg) {
        this.lastSentMsg = lastSentMsg;
    }



    public void setTimeLastSentMsg(String timeLastSentMsg) {
        this.timeLastSentMsg = timeLastSentMsg;
    }

    public void setDateLastSentMsg(String dateLastSentMsg) {
        this.dateLastSentMsg = dateLastSentMsg;
    }

    @Override
    public int compareTo(ChatInfo o) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if(lastSentMsg == null ){
            System.out.println("IN COMPARE TO LASTMSG NULL");
            if(o.lastSentMsg == null)
            {
                return o.getChatWith().compareTo(chatWith);
            }
            else  return -1;
        }
        else if(o.lastSentMsg == null){
            return 1;
        }
        else{

            try{
                if(sdf.parse(o.getDateLastSentMsg()).before(sdf.parse(dateLastSentMsg))){
                    return -1;
                }
                return 1;
            }
            catch(ParseException e){
                e.printStackTrace();
                return -2;
            }
        }


    }
}
