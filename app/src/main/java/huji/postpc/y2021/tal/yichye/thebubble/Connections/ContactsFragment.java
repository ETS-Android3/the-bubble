package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.os.Bundle;
import android.view.View;
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
//    Button b;

    public ContactsFragment(){
        super(R.layout.contacts_layout);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel =  new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        chatsViewModel = new ViewModelProvider(requireActivity()).get(ChatsViewModel.class);

        ContactsAdapter adapter = new ContactsAdapter();
//        System.out.println("TRYING T CREATE CONTACTS AGAIN ()()()()()");
//        System.out.println(userViewModel.getChatsLiveData().getValue().get(0).getDateLastSentMsg());
        adapter.setChatInfos(userViewModel.getChatsLiveData().getValue());
        chatsRecycler = view.findViewById(R.id.contactsRecycler);
        chatsRecycler.setAdapter(adapter);
        chatsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL,false)); // check if  require activity is ok

        //update if new contacts added or deleted
        userViewModel.getChatsLiveData().observe(getViewLifecycleOwner(), personDataArr->
        {
            if (personDataArr != null) {
//                System.out.println("@@@@@@@@@@@IN OBSERVE for changes in chatsLiveData " + personDataArr.get(0).getChatWith() );
                adapter.setChatInfos(personDataArr);
            } else {
                Toast.makeText(requireContext(),
                        "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });


        adapter.XclickedCallable = new ContactsAdapter.OnXClicked() {
            @Override
            public void onClickedX(ChatInfo chatInfo) {
                //TODO DELETE CHAT ONLY AT USER AT THE MOMENT
                int res = findChatToRemove(chatInfo);
                if (res != -1){
                    userViewModel.getChatsLiveData().getValue().remove(res);
                    adapter.setChatInfos(userViewModel.getChatsLiveData().getValue());
                    //todo also delete from fb
                    userViewModel.setChatsLiveData(userViewModel.getChatsLiveData().getValue(), null);
                }
                else {
                    System.err.println("no such chat was found");
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
