package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.PersonData;

public class ChatsDB {

    private FirebaseFirestore db = null;

    public ChatsDB(Context context){
        db = FirebaseFirestore.getInstance();
    }


    public LiveData<ChatFB> getChatByID(String chatFBid)
    {
        MutableLiveData<ChatFB> liveData = new MutableLiveData<>();
        db.collection("chats").document(chatFBid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ChatFB chatFB = documentSnapshot.toObject(ChatFB.class);
                        liveData.setValue(chatFB);
                    }
                    else {
                        liveData.setValue(null);
                    }
                });
        return liveData;
    }

    public void addChatToDB(ChatFB chatInfoToSave){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chats").document(chatInfoToSave.getIds()).set(chatInfoToSave);
    }

    public FirebaseFirestore getDb() {
        return db;
    }


    public void updateChatMessages(ArrayList<Message> newValue, String chatId){
        db.collection("chats").document(chatId).update("chatMessages", newValue);
    }
}
