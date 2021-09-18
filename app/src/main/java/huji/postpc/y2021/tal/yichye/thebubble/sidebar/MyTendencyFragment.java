package huji.postpc.y2021.tal.yichye.thebubble.sidebar;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

import huji.postpc.y2021.tal.yichye.thebubble.PersonData;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.UserViewModel;

public class MyTendencyFragment extends Fragment {

    private UserViewModel userViewModel;
    private RangeSlider ageSlider;
    private TextView ageResult;
    private CheckBox[] genderCheckBoxs = new CheckBox[5];
    private Integer[] genderCheckBoxIds =
            {R.id.gender1_side_bar, R.id.gender2_side_bar, R.id.gender3_side_bar,
                    R.id.gender4_side_bar, R.id.gender5_side_bar};


    public MyTendencyFragment()
    {
        super(R.layout.my_tendency_side_bar);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel =  new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        ageSlider = view.findViewById(R.id.age_slider);
        ageResult = view.findViewById(R.id.age_result_view);

        userViewModel.getMinAgePreferenceLiveData().observe(getViewLifecycleOwner(),
                num -> handleRangeLiveDataListener());

        userViewModel.getMaxAgePreferenceLiveData().observe(getViewLifecycleOwner(),
                num -> handleRangeLiveDataListener());

        ageSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                Float minValue = slider.getValues().get(0);
                Float maxValue = slider.getValues().get(1);
                userViewModel.setMinAgePreferenceLiveData(minValue.intValue(), null);
                userViewModel.setMaxAgePreferenceLiveData(maxValue.intValue(), null);
            }
        });

        for (int i = 0; i < genderCheckBoxIds.length; i++) {
            final int num = i;
            genderCheckBoxs[i] = (CheckBox) view.findViewById(genderCheckBoxIds[i]);
            genderCheckBoxs[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                ArrayList<PersonData.Gender> currentGenderTendencyList =
                        userViewModel.getGenderTendencyLiveData().getValue();
                if (isChecked) {
                    if (!currentGenderTendencyList.contains(PersonData.Gender.values()[num]))
                    {
                        currentGenderTendencyList.add(PersonData.Gender.values()[num]);
                    }
                } else {
                    currentGenderTendencyList.remove(PersonData.Gender.values()[num]);
                }
                userViewModel.setGenderTendencyLiveData(currentGenderTendencyList, null);

            });

        }


        userViewModel.getGenderTendencyLiveData().observe(getViewLifecycleOwner(), genders -> {
            for (int i = 0; i < genderCheckBoxs.length; i++)
            {
                boolean isInLiveData = genders.contains(PersonData.Gender.values()[i]);
                genderCheckBoxs[i].setChecked(isInLiveData);
            }
        });

    }

    private void handleRangeLiveDataListener()
    {
        Integer minValue = userViewModel.getMinAgePreferenceLiveData().getValue();
        Integer maxValue = userViewModel.getMaxAgePreferenceLiveData().getValue();

        List<Float> list = new ArrayList<>();
        list.add((float) minValue);
        list.add((float) maxValue);

        ageSlider.setValues(list);
        if (minValue.equals(maxValue)) {
            ageResult.setText("" + minValue);
        }
        else {
            ageResult.setText(minValue + "-" + maxValue);
        }
    }
}
