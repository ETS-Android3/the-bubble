package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
                Toast.makeText(requireContext(),
                        "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });



        adapter.XclickedCallable = new RequestsAdapter.OnXClicked() {
            @Override
            public void onClickedX(Request request) {
                ArrayList<Request> current_array = userViewModel.getRequestsLiveData().getValue();
                for (int i = 0; i < current_array.size(); i++) {
                    if (current_array.get(i).getReqUserId().equals(request.getReqUserId())){
                        current_array.remove(current_array.get(i)); // safe deletion
                    }
                }
                Collections.sort(current_array);
                userViewModel.getRequestsLiveData().setValue(current_array); // todo change to set firestore
                adapter.setUserRequests(current_array);// set adapter without deleted item

                //todo change whats shown on other user's requests
                TheBubbleApplication.getInstance().getUsersDB().getUserByID(request.reqUserId).observe(getViewLifecycleOwner(), personData->
                {
                    if (personData != null && checkIfOtherHasMyRequest(personData)) {
                        Toast.makeText(requireContext(),
                                "Request Dismissed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(),
                                "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };

        adapter.VclickedCallable = new RequestsAdapter.OnVClicked() {
            @Override
            public void onClickedV(Request request) {
                //todo add a new listing for new chat to the user

            }
        };



    }


    boolean checkIfOtherHasMyRequest(PersonData personData){
        //todo change to delete from firebase
        String selfId = sp.getString("user_name", null);
        for (int i = 0; i < personData.requests.size() ; i++) {
            if (personData.requests.get(i).getReqUserId().equals(selfId)){
                personData.requests.remove(i);
                return true;
            } }
        return false;
    }


}
