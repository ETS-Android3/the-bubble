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
import com.google.android.material.textfield.TextInputLayout;


import huji.postpc.y2021.tal.yichye.thebubble.R;

public class LoginDetailsFragment extends Fragment {

    private FragmentActivity activity;
    private ExtendedFloatingActionButton nextButton;

    private TextInputEditText fullNameEditText;
    private TextInputEditText userNameEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout fullNameTextView;
    private TextInputLayout userNameTextView;
    private TextInputLayout passwordTextView;

    private boolean isFullNameValid = false;
    private boolean isUserNameValue = false;
    private boolean isPasswordValid = false;

    public LoginDetailsFragment()
    {
        super(R.layout.create_login_details_screen);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NewUserViewModel newUserViewModel =
                new ViewModelProvider(requireActivity())
                        .get(NewUserViewModel.class);
        newUserViewModel.progressLiveData.setValue(OnBoardingActivity.USER_INFO_FRAGMENT);

        fullNameEditText = (TextInputEditText) view.findViewById(R.id.nameEditText);
        userNameEditText = (TextInputEditText) view.findViewById(R.id.userNameEditText);
        passwordEditText = (TextInputEditText) view.findViewById(R.id.passwordEditText);
        fullNameTextView = (TextInputLayout) view.findViewById(R.id.nameTextView);
        userNameTextView = (TextInputLayout) view.findViewById(R.id.userNameTextView);
        passwordTextView = (TextInputLayout) view.findViewById(R.id.passwordTextView);

        nextButton = view.findViewById(R.id.extended_fab);
        activity = getActivity();

        nextButton.setEnabled(isFullNameValid && isUserNameValue && isPasswordValid);
        handleFullNameField(newUserViewModel);
        handleUserNameField(newUserViewModel);
        handlePasswordField(newUserViewModel);

        nextButton.setOnClickListener(v -> {
            moveToNextFragment();
        });

    }


    private void handleFullNameField(NewUserViewModel newUserViewModel)
    {
        newUserViewModel.fullNameLiveData.observe(getViewLifecycleOwner(), s -> {
            Editable currText = fullNameEditText.getText();
            if (!s.equals(currText.toString())) {
                fullNameEditText.setText(newUserViewModel.fullNameLiveData.getValue());
                fullNameEditText.setSelection(s.length());
            }
        });

        fullNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fullName = s.toString();
                newUserViewModel.fullNameLiveData.setValue(fullName);
                if( !fullName.matches("[a-z A-Z]+") || fullName.length() == 0)
                {
                    isFullNameValid = false;
                    String error = getString(R.string.error_message_names);
                    fullNameTextView.setErrorEnabled(true);
                    fullNameTextView.setError(error);
                    fullNameTextView.setErrorIconDrawable(R.drawable.ic_baseline_clear_24);
                }
                else
                {
                    isFullNameValid = true;
                    fullNameTextView.setErrorEnabled(false);
                    fullNameTextView.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                    fullNameTextView.setEndIconDrawable(R.drawable.ic_baseline_check_24);
                    fullNameTextView.setHelperText("");
                }
                nextButton.setEnabled(isFullNameValid && isUserNameValue && isPasswordValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void handleUserNameField(NewUserViewModel newUserViewModel)
    {
        newUserViewModel.userNameLiveData.observe(getViewLifecycleOwner(), s -> {
            Editable currText = userNameEditText.getText();
            if (!s.equals(currText.toString())) {
                userNameEditText.setText(newUserViewModel.userNameLiveData.getValue());
                userNameEditText.setSelection(s.length());
            }
        });

        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userName = s.toString();
                newUserViewModel.userNameLiveData.setValue(userName);
                if( !userName.matches("[a-zA-Z0-9]+") || userName.length() < 4)
                {
                    isUserNameValue = false;
                    String error = "Must enter valid user name with at least 4 characters";
                    userNameTextView.setErrorEnabled(true);
                    userNameTextView.setError(error);
                    userNameTextView.setErrorIconDrawable(R.drawable.ic_baseline_clear_24);
                }
                else
                {
                    isUserNameValue = true;
                    userNameTextView.setErrorEnabled(false);
                    userNameTextView.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                    userNameTextView.setEndIconDrawable(R.drawable.ic_baseline_check_24);
                    userNameTextView.setHelperText("");
                }
                nextButton.setEnabled(isFullNameValid && isUserNameValue && isPasswordValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void handlePasswordField(NewUserViewModel newUserViewModel)
    {
        newUserViewModel.passwordLiveData.observe(getViewLifecycleOwner(), s -> {
            Editable currText = passwordEditText.getText();
            if (!s.equals(currText.toString())) {
                passwordEditText.setText(newUserViewModel.passwordLiveData.getValue());
                passwordEditText.setSelection(s.length());
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                newUserViewModel.passwordLiveData.setValue(password);
                if( !password.matches("[a-zA-Z0-9]+") || password.length() < 5)
                {
                    isPasswordValid = false;
                    String error = "Must enter valid password with at least 5 characters";
                    passwordTextView.setErrorEnabled(true);
                    passwordTextView.setError(error);
                    passwordTextView.setErrorIconDrawable(R.drawable.ic_baseline_clear_24);
                }
                else
                {
                    isPasswordValid = true;
                    passwordTextView.setErrorEnabled(false);
                    passwordTextView.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                    passwordTextView.setEndIconDrawable(R.drawable.ic_baseline_check_24);
                    passwordTextView.setHelperText("");
                }
                nextButton.setEnabled(isFullNameValid && isUserNameValue && isPasswordValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void moveToNextFragment()
    {
        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.action_loginDetailsFragment_to_phoneNumberFragment);
    }
}
