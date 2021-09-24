package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;
import huji.postpc.y2021.tal.yichye.thebubble.UsersDB;

public class ChatsViewModel extends ViewModel {

    protected ChatsDB chatsDB = TheBubbleApplication.getInstance().getChatsDB();
    protected UsersDB usersDB = TheBubbleApplication.getInstance().getUsersDB();

    protected HashMap<String,MutableLiveData<ArrayList<Message>>> fireBaseChats = new HashMap<>() ;

    protected MutableLiveData<ChatInfo> lastChatPickedLiveData = new MutableLiveData<>();
    protected MutableLiveData<ChatFB> newChatAddedLiveData = new MutableLiveData<>();


    public ChatsDB getChatsDB() {
        return chatsDB;
    }

    public MutableLiveData<ChatInfo> getLastChatPickedLiveData() {
        return lastChatPickedLiveData;
    }

    public MutableLiveData<ChatFB> getNewChatAddedLiveData() {
        return newChatAddedLiveData;
    }

    public void setLastChatPickedLiveData(ChatInfo last){
        lastChatPickedLiveData.setValue(last);
    }

    /**
     * UPDATE VM
     * @param key
     * @param messagesArray
     */
    public void setFBchatLiveDataById(String key, ArrayList<Message> messagesArray) {
        if(this.fireBaseChats.containsKey(key)){
            this.fireBaseChats.get(key).setValue(messagesArray);
        }
        else
        {
            MutableLiveData<ArrayList<Message>> newLiveData = new MutableLiveData<>(messagesArray);
            this.fireBaseChats.put(key, newLiveData);
        }
    }



    public LiveData<ArrayList<Message>> getMessagesLiveDataById(String id){
        return fireBaseChats.get(id);
    }

    /**
     * UPDATE IN FIRESTORE
     * @param newValue
     * @param chatId
     */
    public void setMessagesForGivenChatFBId(Message added,ArrayList<Message> newValue, String chatId){
        chatsDB.updateChatMessages(newValue,chatId);
        String[] ids = chatId.split("-");
        usersDB.updateChatInfoByIdAndMsg(added, ids[0], ids[1]);
        usersDB.updateChatInfoByIdAndMsg(added, ids[1], ids[0]);
    }

    public void addChatToDB(ChatFB chatFB){
        chatsDB.addChatToDB(chatFB);
    }
}
