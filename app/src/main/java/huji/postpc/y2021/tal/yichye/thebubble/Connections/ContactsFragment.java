package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.UserViewModel;

public class ContactsFragment extends Fragment {

    UserViewModel userViewModel;
    ChatsViewModel chatsViewModel;
    RecyclerView chatsRecycler;
    TextView chatsMessageTextView;


    public ContactsFragment(){
        super(R.layout.contacts_layout);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel =  new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        chatsViewModel = new ViewModelProvider(requireActivity()).get(ChatsViewModel.class);

        ContactsAdapter adapter = new ContactsAdapter();
        adapter.setChatInfos(userViewModel.getChatsLiveData().getValue());
        chatsMessageTextView = view.findViewById(R.id.noChatsTextView);
        chatsRecycler = view.findViewById(R.id.contactsRecycler);
        chatsRecycler.setAdapter(adapter);
        chatsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL,false));

        //update if new contacts added or deleted
        userViewModel.getChatsLiveData().observe(getViewLifecycleOwner(), personDataArr->
        {
            if (personDataArr != null) {
                if (personDataArr.size() == 0) {
                    chatsMessageTextView.setVisibility(View.VISIBLE);
                    chatsRecycler.setVisibility(View.GONE);
                }
                else{
                    chatsMessageTextView.setVisibility(View.GONE);
                    chatsRecycler.setVisibility(View.VISIBLE);
                }
                adapter.setChatInfos(personDataArr);
            } else {
                Toast.makeText(requireContext(),
                        "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });


        adapter.XclickedCallable = new ContactsAdapter.OnXClicked() {
            @Override
            public void onClickedX(ChatInfo chatInfo) {
                int res = findChatToRemove(chatInfo);
                if (res != -1){
                    userViewModel.getChatsLiveData().getValue().remove(res);
                    adapter.setChatInfos(userViewModel.getChatsLiveData().getValue());
                    userViewModel.setChatsLiveData(userViewModel.getChatsLiveData().getValue(), null);
                }
                else {
                    Log.d("chat", "no such chat was found");
                }
            }
        };

        adapter.listingClickedCallable = new ContactsAdapter.OnListingClicked() {
            @Override
            public void onListingClicked(ChatInfo chatInfo) {
                chatsViewModel.setLastChatPickedLiveData(chatInfo);
            }
        };
    }

    private int findChatToRemove(ChatInfo chatInfo){
        ArrayList<ChatInfo> currentChats = userViewModel.getChatsLiveData().getValue();
        for (int i = 0; i < currentChats.size() ; i++) {
            if(currentChats.get(i).getChatWith().equals(chatInfo.getChatWith())){
                return i;
            }
        }
        return -1;
    }



}
