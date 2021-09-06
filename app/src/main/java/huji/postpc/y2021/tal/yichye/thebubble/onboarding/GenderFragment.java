package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import huji.postpc.y2021.tal.yichye.thebubble.R;

public class GenderFragment extends Fragment
{

    private FragmentActivity activity;
    private ExtendedFloatingActionButton nextButton;
    private RadioGroup genderRadioGroup;

    public GenderFragment()
    {
        super(R.layout.gender_screen);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NewUserViewModel newUserViewModel =
                new ViewModelProvider(requireActivity())
                        .get(NewUserViewModel.class);
        newUserViewModel.progressLiveData.setValue(OnBoardingActivity.GENDER_FRAGMENT);

        genderRadioGroup = (RadioGroup) view.findViewById(R.id.genderRadioGroup);
        nextButton = view.findViewById(R.id.extended_fab);
        activity = getActivity();

        newUserViewModel.myGenderLiveData.observe(getViewLifecycleOwner(), integer ->
                genderRadioGroup.getChildAt(integer).setEnabled(true));

        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            View radioButton = group.findViewById(checkedId);
            int newSelectedIndex = group.indexOfChild(radioButton);
            newUserViewModel.myGenderLiveData.setValue(newSelectedIndex);
        });


        nextButton.setOnClickListener(v -> {
            moveToNextFragment();
        });

    }

    private void moveToNextFragment()
    {
        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.action_genderFragment_to_cityFragment);
    }
}
