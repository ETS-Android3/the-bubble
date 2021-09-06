package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import huji.postpc.y2021.tal.yichye.thebubble.R;

public class PhoneNumberFragment extends Fragment
{
    private FragmentActivity activity;
    private ExtendedFloatingActionButton nextButton;
    private TextInputEditText phoneNumberEditText;

    public PhoneNumberFragment()
    {
        super(R.layout.phone_number_screen);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NewUserViewModel newUserViewModel =
                new ViewModelProvider(requireActivity())
                .get(NewUserViewModel.class);

        newUserViewModel.progressLiveData.setValue(OnBoardingActivity.PHONE_FRAGMENT);
        activity = getActivity();
        nextButton = view.findViewById(R.id.extended_fab);
        phoneNumberEditText = (TextInputEditText)view.findViewById(R.id.phoneNumberTextInputEdit);

        nextButton.setEnabled(isValidPhoneNumber(newUserViewModel.phoneNumberLiveData.getValue()));

        newUserViewModel.phoneNumberLiveData.observe(getViewLifecycleOwner(), s -> {
            Editable currText = phoneNumberEditText.getText();
            if (!s.equals(currText.toString())) {
                phoneNumberEditText.setText(newUserViewModel.phoneNumberLiveData.getValue());
                phoneNumberEditText.setSelection(s.length());
            }
            nextButton.setEnabled(isValidPhoneNumber(newUserViewModel.phoneNumberLiveData.getValue()));

        });

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newUserViewModel.phoneNumberLiveData.setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        nextButton.setOnClickListener(v -> {
            moveToNextFragment();
        });

    }

    private boolean isValidPhoneNumber(String number)
    {
        return number.length() == 10 && number.startsWith("05");
    }

    private void moveToNextFragment()
    {
        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.action_phoneNumberFragment_to_dataOfBirthFragment);
    }
}
