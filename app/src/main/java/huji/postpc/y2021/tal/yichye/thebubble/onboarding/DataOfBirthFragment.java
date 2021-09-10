package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.time.LocalDate;

import huji.postpc.y2021.tal.yichye.thebubble.R;

public class DataOfBirthFragment extends Fragment
{

    private FragmentActivity activity;
    private ExtendedFloatingActionButton nextButton;
    private DatePicker datePicker;
    public DataOfBirthFragment()
    {
        super(R.layout.date_of_birth_screen);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NewUserViewModel newUserViewModel =
                new ViewModelProvider(requireActivity())
                        .get(NewUserViewModel.class);
        newUserViewModel.progressLiveData.setValue(OnBoardingActivity.DATA_OF_BIRTH_FRAGMENT);

        datePicker = view.findViewById(R.id.dateOfBirthPicker);
        nextButton = view.findViewById(R.id.extended_fab);
        activity = getActivity();

        nextButton.setOnClickListener(v -> {
            moveToNextFragment();
        });


        newUserViewModel.dataOfBirthLiveData.observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                LocalDate localDate = LocalDate.ofEpochDay(aLong);
                datePicker.updateDate(
                        localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
            }
        });

        datePicker.setOnDateChangedListener((view1, year, monthOfYear, dayOfMonth) -> {
            LocalDate newDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
            newUserViewModel.dataOfBirthLiveData.setValue(newDate.toEpochDay());

        });

    }

    private void moveToNextFragment()
    {
        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.action_dataOfBirthFragment_to_genderFragment);
    }

}
