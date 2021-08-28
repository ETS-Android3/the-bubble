package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import huji.postpc.y2021.tal.yichye.thebubble.R;

public class CommunityRulesFragment extends Fragment
{

    private FragmentActivity activity;
    private ExtendedFloatingActionButton nextButton;

    public CommunityRulesFragment()
    {
        super(R.layout.community_guidelines);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NewUserViewModel newUserViewModel =
                new ViewModelProvider(requireActivity())
                        .get(NewUserViewModel.class);
        newUserViewModel.progressLiveData.setValue(OnBoardingActivity.COMMUNITY_RULES_FRAGMENT);

        nextButton = view.findViewById(R.id.extended_fab);
        activity = getActivity();

        nextButton.setOnClickListener(v -> {
            moveToNextFragment();
        });

    }
    private void moveToNextFragment()
    {
        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.action_communityRulesFragment_to_welcomeFragment);
    }
}
