package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import huji.postpc.y2021.tal.yichye.thebubble.MainActivity;
import huji.postpc.y2021.tal.yichye.thebubble.PersonData;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;
import huji.postpc.y2021.tal.yichye.thebubble.UserViewModel;


public class RequestsFragment extends Fragment {

    UserViewModel userViewModel;
    ChatsViewModel chatsViewModel;
    RecyclerView requestsRecycler;
    SharedPreferences sp = TheBubbleApplication.getInstance().getSP();


    public RequestsFragment() {
        super(R.layout.fragment_requests);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("before calling user view model");


        userViewModel =  new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        chatsViewModel = new ViewModelProvider(requireActivity()).get(ChatsViewModel.class);
        RequestsAdapter adapter = new RequestsAdapter();
        adapter.setUserRequests(userViewModel.getRequestsLiveData().getValue());
        requestsRecycler = view.findViewById(R.id.reqRecycler);
        requestsRecycler.setAdapter(adapter);
        requestsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL,false)); // check if  require activity is ok
        userViewModel.getRequestsLiveData().observe(getViewLifecycleOwner(), personDataArr->
        {
            if (personDataArr != null) {
                adapter.setUserRequests(personDataArr);
            } else {
                System.err.println("error in observer req fragment");
            }
        });



        adapter.XclickedCallable = new RequestsAdapter.OnXClicked() {
            @Override
            public void onClickedX(Request request, String forToast) {
                ArrayList<Request> current_array = userViewModel.getRequestsLiveData().getValue();
                for (int i = 0; i < current_array.size(); i++) {
                    if (current_array.get(i).getReqUserId().equals(request.getReqUserId())){
                        current_array.remove(current_array.get(i)); // safe deletion
                    }
                }
//                Collections.sort(current_array);
                userViewModel.setRequestsLiveData(current_array, null); // todo change to set firestore
                adapter.setUserRequests(current_array);// set adapter without deleted item

                //todo change calling to DB ???

                TheBubbleApplication.getInstance().getUsersDB().getUserByID(request.reqUserId).observe(getViewLifecycleOwner(), personData->
                {
                    int index = checkIfOtherHasMyRequest(personData);
                    if (personData != null && index!= -1) {
                        personData.requests.remove(index);
                        userViewModel.setRequestsLiveData(personData.getRequests(), personData.getId());
                        Toast.makeText(requireContext(),
                                forToast, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(),
                                "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };

        adapter.VclickedCallable = new RequestsAdapter.OnVClicked() {
            @Override
            public void onClickedV(Request request, String forToast) {
                //todo add a new listing for new chat to the user
                String selfId = sp.getString("user_name", null);
                if (selfId == null) {
                    System.err.println("no user in system");
                    return;
                }
                ChatFB newChat = new ChatFB(MessagingFragment.getIdsForChatFB(request.reqUserId));
                //my listing
                ChatInfo chatInfo1 = new ChatInfo(request.reqUserId);
                //other listing
                ChatInfo chatInfo2 = new ChatInfo(selfId);
                chatsViewModel.addChatToDB(newChat);
                chatsViewModel.newChatAddedLiveData.setValue(newChat);
                ArrayList<ChatInfo> currSelfChatInfos = userViewModel.getChatsLiveData().getValue(); //todo need to update adapter?
                currSelfChatInfos.add(chatInfo1);
                userViewModel.setChatsLiveData(currSelfChatInfos, null);
                System.out.println(request.getReqUserId()+ "-----------------------");
                LiveData<PersonData> personDataLiveData = TheBubbleApplication.getInstance().getUsersDB().getUserByID(request.getReqUserId());
                Observer<PersonData> observer = personData -> {
                    System.out.println(personDataLiveData.getValue() + "--------------------------");
                    boolean success = personData.chatInfos.add(chatInfo2);
                    if(success) userViewModel.setChatsLiveData(personData.chatInfos, personData.getId());
                };
                personDataLiveData.observe(getViewLifecycleOwner(), observer);
                adapter.XclickedCallable.onClickedX(request, forToast);
            }

        };

    }

    private int checkIfOtherHasMyRequest(PersonData personData){
        //todo change to delete from firebase
        String selfId = sp.getString("user_name", null);
        for (int i = 0; i < personData.requests.size() ; i++) {
            if (personData.requests.get(i).getReqUserId().equals(selfId)){
                return i;
            } }
        return -1;
    }


}
