package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.GlideApp;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;
import huji.postpc.y2021.tal.yichye.thebubble.UserViewModel;

public class ConversationFragment extends Fragment {

    ChatInfo currentOpenedChatInfo = null;
    UserViewModel userViewModel;
    ChatsViewModel chatsViewModel;
    RecyclerView conversationRecycler;
    Button sendButton;
    ImageView chatWithImage;
    TextView chatWithName;
    EditText userInput;
    ImageView backArrow;

    public interface OnBackPressedListener { public void onPressed();}

    protected OnBackPressedListener listenerForBackArrow;




    public ConversationFragment ( ){
        super(R.layout.conversation_layout);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // todo set adapter and recycler
        String selfId= TheBubbleApplication.getInstance().getSP().getString("user_name", null);

        chatsViewModel = new ViewModelProvider(requireActivity()).get(ChatsViewModel.class);
        setAllForAdapter(view);
        setViews(view);
        //todo set contact info header -- IMAGE!!!!

        chatWithName.setText(currentOpenedChatInfo.getChatWith());
        StorageReference imageRef = TheBubbleApplication
                .getInstance()
                .getImageStorageDB()
                .createReference(currentOpenedChatInfo.getChatWith(), "profileImage");
        GlideApp.with(this)
                .load(imageRef)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(chatWithImage);


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatsViewModel.setLastChatPickedLiveData(null);
                listenerForBackArrow.onPressed();
            }
        });

        //todo send button listener
        sendButton.setOnClickListener(v -> {
            String msgContent = userInput.getText().toString();
            userInput.setText("");
            String[] dateAndTime = getTimeForNow();
            Message msg = new Message(false,dateAndTime[1], msgContent,  selfId, dateAndTime[0]); // todo check
            LiveData<ArrayList<Message>> arrOfMessages = chatsViewModel.getMessagesLiveDataById(MessagingFragment.getIdsForChatFB(currentOpenedChatInfo.getChatWith()));

            if ( arrOfMessages.getValue().add(msg)){
                //todo update FB with new msg
                chatsViewModel.setMessagesForGivenChatFBId(msg, arrOfMessages.getValue(), MessagingFragment.getIdsForChatFB(currentOpenedChatInfo.getChatWith()));
            }
            else{
                System.err.println("did not succeed in adding new msg");
            }

    });






        //todo back button listener
    }

    private void setViews(View view) {
        chatWithImage = view.findViewById(R.id.otherImage);
        chatWithName = view.findViewById(R.id.otherName);
        sendButton = view.findViewById(R.id.button_send);
        backArrow = view.findViewById(R.id.backArrow);
        userInput = view.findViewById(R.id.edit_message);
    }


    private void setAllForAdapter(View view) {
        ConversationAdapter adapter = new ConversationAdapter();

        // todo change chatVM to support this with current chat --- get from dictionary
        if (currentOpenedChatInfo != null){
            LiveData<ArrayList<Message>> arrOfMessages =
            chatsViewModel.getMessagesLiveDataById(MessagingFragment.getIdsForChatFB(currentOpenedChatInfo.getChatWith()));
            adapter.setMessages(arrOfMessages.getValue());

            //todo listen to changes in msges from VM
            Observer<ArrayList<Message>> observer = messages -> {
                if (messages != null){
                    adapter.setMessages(messages);
                }
                else {
                    System.err.println("error in observer conversation frag");
                }
            };
            arrOfMessages.observe(getViewLifecycleOwner(), observer);
        }
        else {
            System.err.println("current opened chat not defined");
        }


        conversationRecycler = view.findViewById(R.id.recyclerConversation);
        conversationRecycler.setAdapter(adapter);
        conversationRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL,false)); // check if  require activity is ok
    }




    public void setCurrentOpenedChatInfo(ChatInfo currentOpenedChatInfo) {
        this.currentOpenedChatInfo = currentOpenedChatInfo;
    }


    private String[] getTimeForNow(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String info = dtf.format(now);
        return info.split(" ");
    }
}
