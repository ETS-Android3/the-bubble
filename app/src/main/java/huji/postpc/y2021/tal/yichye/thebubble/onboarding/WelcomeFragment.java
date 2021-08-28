package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.PersonData;
import huji.postpc.y2021.tal.yichye.thebubble.R;

public class WelcomeFragment extends Fragment
{
    private Button nextButton;

    public WelcomeFragment()
    {
        super(R.layout.welcome_screen);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NewUserViewModel newUserViewModel =
                new ViewModelProvider(requireActivity())
                        .get(NewUserViewModel.class);
        newUserViewModel.progressLiveData.setValue(OnBoardingActivity.WELCOME_FRAGMENT);

        nextButton = view.findViewById(R.id.extended_fab);

        createUser(newUserViewModel);

        nextButton.setOnClickListener(v -> {
            newUserViewModel.progressLiveData.setValue(OnBoardingActivity.DONE_ON_BOARDING);

        });

    }

    private void createUser(NewUserViewModel newUserViewModel)
    {
        PersonData newUser = new PersonData();
        newUser.phoneNumber = newUserViewModel.phoneNumberLiveData.getValue();
        newUser.dateOfBirth = newUserViewModel.dataOfBirthLiveData.getValue();
        newUser.city = newUserViewModel.cityLiveData.getValue();
        newUser.minAgePreference = newUserViewModel.minAgePreferenceLiveData.getValue();
        newUser.maxAgePreference = newUserViewModel.maxAgePreferenceLiveData.getValue();
        newUser.aboutMe = newUserViewModel.aboutMeLiveData.getValue();

        PersonData.Gender[] genderList = PersonData.Gender.values();
        newUser.gender = genderList[newUserViewModel.myGenderLiveData.getValue()];

        ArrayList<PersonData.Gender> tendencyList = new ArrayList<PersonData.Gender>();
        for (int genderIndex: newUserViewModel.genderTendencyLiveData.getValue()) {
            tendencyList.add(genderList[genderIndex]);
        }
        newUser.genderTendency = tendencyList;

        System.out.println(newUser);
    }
}