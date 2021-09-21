package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.ChatInfo;
import huji.postpc.y2021.tal.yichye.thebubble.Connections.Request;
import huji.postpc.y2021.tal.yichye.thebubble.PersonData;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;

public class AppNavigationFragment extends Fragment
{
    private Button nextButton;

    public AppNavigationFragment()
    {
        super(R.layout.app_navigation_screen);
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
        newUser.fullName = newUserViewModel.fullNameLiveData.getValue();
        newUser.userName = newUserViewModel.userNameLiveData.getValue();
        newUser.password = newUserViewModel.passwordLiveData.getValue();
        newUser.phoneNumber = newUserViewModel.phoneNumberLiveData.getValue();
        newUser.dateOfBirth = newUserViewModel.dataOfBirthLiveData.getValue();
        newUser.city = newUserViewModel.cityLiveData.getValue();
        newUser.minAgePreference = newUserViewModel.minAgePreferenceLiveData.getValue();
        newUser.maxAgePreference = newUserViewModel.maxAgePreferenceLiveData.getValue();
        newUser.aboutMe = newUserViewModel.aboutMeLiveData.getValue();
        newUser.ignoreList = newUserViewModel.ignoreListLiveData.getValue();

        PersonData.Gender[] genderList = PersonData.Gender.values();
        newUser.gender = genderList[newUserViewModel.myGenderLiveData.getValue()];

        ArrayList<PersonData.Gender> tendencyList = new ArrayList<PersonData.Gender>();
        for (int genderIndex: newUserViewModel.genderTendencyLiveData.getValue()) {
            tendencyList.add(genderList[genderIndex]);
        }
        newUser.genderTendency = tendencyList;
        newUser.requests = new ArrayList<>();
        newUser.chatInfos = new ArrayList<>();
        newUser.photos = newUserViewModel.photosLiveData.getValue();

        TheBubbleApplication application = TheBubbleApplication.getInstance();
        SharedPreferences sp = application.getSP();
        sp.edit().putString("user_name", newUser.userName).apply();
        application.getUsersDB().addUserToDB(newUser);

    }
}
