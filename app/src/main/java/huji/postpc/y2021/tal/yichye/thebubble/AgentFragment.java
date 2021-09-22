package huji.postpc.y2021.tal.yichye.thebubble;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.HashMap;

public class AgentFragment extends Fragment {

    SearchAlgorithm algorithm;
    UserViewModel userViewModel;

    public AgentFragment(){
        super(R.layout.agent_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.agent_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel =  new ViewModelProvider(requireActivity()).get(UserViewModel.class);


        ViewPager2 viewPager = view.findViewById(R.id.agentViewPager);
        CircularProgressIndicator loadingView = view.findViewById(R.id.loading);
        TextView loadingTextView = view.findViewById(R.id.loadingText);
        loadingTextView.setText("We are looking for your destiny\nPlease wait");

        algorithm = new SearchAlgorithm(requireActivity());
        algorithm.SearchForPossibleMatches();
        algorithm.getPossibleMatchesLiveData().observe(requireActivity(),
                userNames -> algorithm.activateAgentSearch());

        String userId = userViewModel.getUserNameLiveData().getValue();



        algorithm.getAgentSearchFinished().observe(getViewLifecycleOwner(), isFinish -> {
            if (isFinish) {
                loadingView.setVisibility(View.GONE);
                ArrayList<PersonData> possibleMatchesAgent = algorithm.getPossibleMatchesAgentLiveData().getValue();
                if (possibleMatchesAgent == null || possibleMatchesAgent.size() == 0) {
                    loadingTextView.setText("We a sorry, but there are no possible matches right now :(");
                } else {
                    loadingTextView.setVisibility(View.GONE);
                    System.out.println("is finish");
                    TheBubbleApplication.getInstance().getUsersDB().getUserByID(userId).
                            observe(getViewLifecycleOwner(), personData -> {
                                AgentAdapter agentAdapter = new AgentAdapter(possibleMatchesAgent, userViewModel);
                                viewPager.setAdapter(agentAdapter);
                                viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
                                algorithm.getAgentSearchFinished().setValue(false);
                            });
                }
            }
        });




        ArrayList<PersonData> arrayList = new ArrayList<>();
        PersonData personData = new PersonData();

        personData.fullName = "Alice";
        arrayList.add(personData);
        personData = new PersonData();
        personData.fullName = "Jane";
        arrayList.add(personData);


    }
}
