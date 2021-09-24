package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;


import huji.postpc.y2021.tal.yichye.thebubble.R;

public class CityFragment extends Fragment
{

    private FragmentActivity activity;
    private ExtendedFloatingActionButton nextButton;
    private Spinner citySpinner;

    public CityFragment()
    {
        super(R.layout.city_screen);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NewUserViewModel newUserViewModel =
                new ViewModelProvider(requireActivity())
                        .get(NewUserViewModel.class);
        newUserViewModel.progressLiveData.setValue(OnBoardingActivity.CITY_FRAGMENT);

        nextButton = view.findViewById(R.id.extended_fab);
        activity = getActivity();

        citySpinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.city_array, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        newUserViewModel.cityLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String city) {
                int pos = adapter.getPosition(city);
                citySpinner.setSelection(pos);
            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String city = getResources().getStringArray(R.array.city_array)[position];
                newUserViewModel.cityLiveData.setValue(city);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
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
        navController.navigate(R.id.action_cityFragment_to_photosFragment);
    }
}
