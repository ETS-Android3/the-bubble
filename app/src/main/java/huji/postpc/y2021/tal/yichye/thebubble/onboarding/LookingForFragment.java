package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import huji.postpc.y2021.tal.yichye.thebubble.R;

public class LookingForFragment extends Fragment
{

    private FragmentActivity activity;
    private ExtendedFloatingActionButton nextButton;
    private RangeSlider ageRangeSlider;
    private TextView ageRangeResult;

    private CheckBox[] genderCheckBoxs = new CheckBox[5];
    private Integer[] genderCheckBoxIds =
            {R.id.gender1, R.id.gender2, R.id.gender3, R.id.gender4, R.id.gender5};


public LookingForFragment()
    {
        super(R.layout.looking_for_screen);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NewUserViewModel newUserViewModel =
                new ViewModelProvider(requireActivity())
                        .get(NewUserViewModel.class);
        newUserViewModel.progressLiveData.setValue(OnBoardingActivity.LOOKING_FOR_FRAGMENT);

        ageRangeResult = view.findViewById(R.id.age_result_view);
        ageRangeSlider = (RangeSlider) view.findViewById(R.id.age_slider);
        nextButton = view.findViewById(R.id.extended_fab);
        activity = getActivity();


        newUserViewModel.minAgePreferenceLiveData.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer num) {
                handleRangeLiveDataListener(newUserViewModel);
            }
        });

        newUserViewModel.maxAgePreferenceLiveData.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer num) {
                handleRangeLiveDataListener(newUserViewModel);
            }
        });

        ageRangeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                Float minValue = slider.getValues().get(0);
                Float maxValue = slider.getValues().get(1);
                newUserViewModel.minAgePreferenceLiveData.setValue(minValue.intValue());
                newUserViewModel.maxAgePreferenceLiveData.setValue(maxValue.intValue());
            }
        });


        for (int i = 0; i < genderCheckBoxIds.length; i++) {
            final int num = i;
            genderCheckBoxs[i] = (CheckBox) view.findViewById(genderCheckBoxIds[i]);
            genderCheckBoxs[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                HashSet<Integer> currentGenderTendencyList = newUserViewModel.genderTendencyLiveData.getValue();
                if (isChecked) {
                    currentGenderTendencyList.add(num);
                } else {
                    currentGenderTendencyList.remove(Integer.valueOf(num));
                }
                newUserViewModel.genderTendencyLiveData.setValue(currentGenderTendencyList);
            });
        }

        newUserViewModel.genderTendencyLiveData.observe(getViewLifecycleOwner(), integers -> {
            for (int i = 0; i < genderCheckBoxs.length; i++)
            {
                boolean isInLiveData = integers.contains(i);
                genderCheckBoxs[i].setChecked(isInLiveData);
            }
            nextButton.setEnabled(!integers.isEmpty());
        });


        nextButton.setOnClickListener(v -> {
            moveToNextFragment();
        });

    }


    private void handleRangeLiveDataListener(NewUserViewModel newUserViewModel)
    {
        Integer minValue = newUserViewModel.minAgePreferenceLiveData.getValue();
        Integer maxValue = newUserViewModel.maxAgePreferenceLiveData.getValue();

        List<Float> list = new ArrayList<>();
        list.add((float) minValue);
        list.add((float) maxValue);

        ageRangeSlider.setValues(list);
        if (minValue.equals(maxValue)) {
            ageRangeResult.setText("" + minValue);
        }
        else {
            ageRangeResult.setText(minValue + "-" + maxValue);
        }
    }

    private void moveToNextFragment()
    {
        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.action_lookingForFragment_to_aboutMeFragment);
    }
}
