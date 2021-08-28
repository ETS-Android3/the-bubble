package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import huji.postpc.y2021.tal.yichye.thebubble.R;

public class AboutMeFragment extends Fragment
{

    private FragmentActivity activity;
    private ExtendedFloatingActionButton nextButton;
    private EditText aboutMeEditText;

    public AboutMeFragment()
    {
        super(R.layout.aboutme_screen);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NewUserViewModel newUserViewModel =
                new ViewModelProvider(requireActivity())
                        .get(NewUserViewModel.class);
        newUserViewModel.progressLiveData.setValue(OnBoardingActivity.ABOUT_ME_FRAGMENT);

        aboutMeEditText = (EditText) view.findViewById(R.id.aboutMeEditText);
        nextButton = view.findViewById(R.id.extended_fab);
        activity = getActivity();

        newUserViewModel.aboutMeLiveData.observe(getViewLifecycleOwner(), s -> {
            Editable currText = aboutMeEditText.getText();
            if (!s.equals(currText.toString())) {
                aboutMeEditText.setText(newUserViewModel.aboutMeLiveData.getValue());
            }
            nextButton.setEnabled(!s.isEmpty());
        });

        aboutMeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newUserViewModel.aboutMeLiveData.setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        nextButton.setOnClickListener(v -> {
            moveToNextFragment();
        });

    }

    private void moveToNextFragment()
    {
        FragmentActivity activity = getActivity();

        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.action_aboutMeFragment_to_communityRulesFragment);
    }
}
