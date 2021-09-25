package huji.postpc.y2021.tal.yichye.thebubble.agent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.PersonData;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.SearchAlgorithm;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;
import huji.postpc.y2021.tal.yichye.thebubble.UserViewModel;
import pl.droidsonroids.gif.GifImageView;

public class AgentFragment extends Fragment {

    SearchAlgorithm algorithm;
    UserViewModel userViewModel;

    public AgentFragment(){
        super(R.layout.agent_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.agent_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel =  new ViewModelProvider(requireActivity()).get(UserViewModel.class);


        ViewPager2 viewPager = view.findViewById(R.id.agentViewPager);
        GifImageView loadingView = view.findViewById(R.id.loading);
        TextView loadingTextView = view.findViewById(R.id.loadingText);
        loadingTextView.setText("Looking for potential bubbles\nPlease wait");

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
                    loadingTextView.setText("We are sorry, but there are no potential bubbles right now");
                } else {
                    loadingTextView.setVisibility(View.GONE);
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
    }
}
