package huji.postpc.y2021.tal.yichye.thebubble.sidebar;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;

import huji.postpc.y2021.tal.yichye.thebubble.PersonData;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;
import huji.postpc.y2021.tal.yichye.thebubble.UserViewModel;

public class ProfilePreviewFragment extends Fragment {

    private Spinner citySpinner;
    private ImageView editButton;
    private ImageView checkedButton;
    private ImageView firstImage;
    private ImageView secondImage;
    private ImageView thirdImage;
    private EditText aboutMeEditText;
    private EditText nameEditText;
    private UserViewModel userViewModel;
    private EditText ageTextView;

    public ProfilePreviewFragment()
    {
        super(R.layout.my_profile_side_bar);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editButton = view.findViewById(R.id.imageViewEditPencil);
        checkedButton = view.findViewById(R.id.imageViewChecked);
        editButton.bringToFront();
        checkedButton.bringToFront();
        aboutMeEditText = view.findViewById(R.id.aboutMeEditProfile);
        nameEditText = view.findViewById(R.id.nameEditProfile);
        firstImage = view.findViewById(R.id.firstImageViewEditProfile);
        secondImage = view.findViewById(R.id.secondImageViewEditProfile);
        thirdImage = view.findViewById(R.id.thirdImageViewEditProfile);
        ageTextView = view.findViewById(R.id.ageEditProfile);

        userViewModel =  new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        citySpinner = (Spinner) view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.city_array, R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        citySpinner.setAdapter(adapter);

        setAllViews(false);


        userViewModel.getDateOfBirthLiveData().observe(getViewLifecycleOwner(),
                aLong -> ageTextView.setText(PersonData.calcAge(aLong)+ ""));

        userViewModel.getFullNameLiveData().observe(getViewLifecycleOwner(), s ->
                nameEditText.setText(s));

        userViewModel.getCityLiveData().observe(getViewLifecycleOwner(), city -> {
            int pos = adapter.getPosition(city);
            citySpinner.setSelection(pos);
        });


        editButton.setOnClickListener(v -> {
            editButton.setVisibility(View.GONE);
            checkedButton.setVisibility(View.VISIBLE);
            setAllViews(true);
        });

        checkedButton.setOnClickListener(v -> {
            editButton.setVisibility(View.VISIBLE);
            checkedButton.setVisibility(View.GONE);
            setAllViews(false);

            // TODO UPDATE VIEW MODEL WITH THE NEW VALUES
            userViewModel.setFullNameLiveData(nameEditText.getText().toString());
            userViewModel.setAboutMeLiveData(aboutMeEditText.getText().toString());
            userViewModel.setCityLiveData(citySpinner.getSelectedItem().toString());
        });
    }

    private void setAllViews(boolean enabled)
    {
        aboutMeEditText.setEnabled(enabled);
        nameEditText.setEnabled(enabled);
        citySpinner.setEnabled(enabled);

    }
}
