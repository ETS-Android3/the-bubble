package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;
import huji.postpc.y2021.tal.yichye.thebubble.UserViewModel;
import android.widget.FrameLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;


public class MessagingFragment extends Fragment {

    ContactsFragment contactsFragment = null;
    ConversationFragment conversationFragment= null;
    ChatsViewModel chatsViewModel;
    UserViewModel userViewModel;
    FrameLayout frameLayout;
    ArrayList<ListenerRegistration> listenerRegistrations = new ArrayList<>();


    public MessagingFragment() {
        super(R.layout.fragment_messaging);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatsViewModel = new ViewModelProvider(requireActivity()).get(ChatsViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        setChatViewModel();

        contactsFragment = new ContactsFragment();
        conversationFragment = new ConversationFragment();

        frameLayout = view.findViewById(R.id.fragment_container_view);
        getChildFragmentManager().beginTransaction().replace(frameLayout.getId(), contactsFragment).commit();

        conversationFragment.listenerForBackArrow = () -> getChildFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(), contactsFragment)
                .commit();

        setLastPickedObserver();
        setNewChatAddedListener();
    }


    private void setChatViewModel() {
        for(ChatInfo chatInfo : userViewModel.getChatsLiveData().getValue()){
            String chatIdentifier = getIdsForChatFB(chatInfo.getChatWith());
            LiveData<ChatFB> chatFBLiveData = TheBubbleApplication
                    .getInstance()
                    .getChatsDB()
                    .getChatByID(chatIdentifier);
            Observer<ChatFB> observer = chatFB -> {
                if (chatFB != null){
                    updateChatViewModel(chatFB);
                    attach_listener(chatFB);
                }
            };
            chatFBLiveData.observe(getViewLifecycleOwner(), observer);
        }
    }

    public void attach_listener(ChatFB chatFB){
        ListenerRegistration listenerRegistration = TheBubbleApplication.getInstance()
                .getChatsDB()
                .getDb()
                .collection("chats")
                .document(chatFB.getIds())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            System.err.println(error.getMessage());

                        } else if (snapshot == null) {
                            Log.d("message", "snapshot is null");
                        } else {
                            ChatFB changedChatFB = snapshot.toObject(ChatFB.class);
                            if (changedChatFB != null) {
                                updateChatViewModel(changedChatFB);
                            }
                        }
                    }
                });
        listenerRegistrations.add(listenerRegistration);
    }

    private void updateChatViewModel(ChatFB chatFB) {
        chatsViewModel.setFBchatLiveDataById(chatFB.getIds(), chatFB.getChatMessages());
    }


    public static String getIdsForChatFB(String otherId){
        String selfId = TheBubbleApplication.getInstance().getSP().getString("user_name", null);
        if(selfId == null) {
            Log.d("message", "No user in sp");
            return null;
        }
        else if (otherId.compareTo(selfId) < 0){
            return  otherId + '-' + selfId;
        }
        else {
            return selfId + '-' + otherId;
        }
    }


    private void setLastPickedObserver() {
        MutableLiveData<ChatInfo> currentChat = chatsViewModel.getLastChatPickedLiveData();
        final Observer<ChatInfo> chatObserver = new Observer<ChatInfo>() {
            @Override
            public void onChanged(ChatInfo chat) {
                if (conversationFragment != null && chat != null){
                    conversationFragment.setCurrentOpenedChatInfo(chat);
                    getChildFragmentManager().beginTransaction().replace(frameLayout.getId(),
                            conversationFragment).commit();
                }
            }
        };
        currentChat.observe(getViewLifecycleOwner(), chatObserver);
    }

    private void setNewChatAddedListener(){
        MutableLiveData<ChatFB> chetAdded = chatsViewModel.getNewChatAddedLiveData();
        Observer<ChatFB> observer = chatFB -> attach_listener(chatFB);
        chetAdded.observe(getViewLifecycleOwner(),observer);
    }

    private void detachListeners(){
        for (ListenerRegistration l : listenerRegistrations){
            l.remove();
        }
    }

    @Override
    public void onDestroy() {
        detachListeners();
        super.onDestroy();
    }
}