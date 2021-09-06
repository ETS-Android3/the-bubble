package huji.postpc.y2021.tal.yichye.thebubble.sidebar;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import huji.postpc.y2021.tal.yichye.thebubble.R;

public class ProfilePreviewFragment extends Fragment {

    private Spinner citySpinner;
    private ImageView editButton;
    private ImageView checkedButton;
    private ImageView firstImage;
    private ImageView secondImage;
    private ImageView thirdImage;
    private EditText aboutMeEditText;
    private EditText nameEditText;
    private EditText ageEditText;

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
        ageEditText = view.findViewById(R.id.ageEditProfile);
        firstImage = view.findViewById(R.id.firstImageViewEditProfile);
        secondImage = view.findViewById(R.id.secondImageViewEditProfile);
        thirdImage = view.findViewById(R.id.thirdImageViewEditProfile);



        citySpinner = (Spinner) view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.city_array, R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        citySpinner.setAdapter(adapter);

        setAllViews(false);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.GONE);
                checkedButton.setVisibility(View.VISIBLE);
                setAllViews(true);
            }
        });


        checkedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.VISIBLE);
                checkedButton.setVisibility(View.GONE);
                setAllViews(false);
                // TODO UPDATE VIEW MODEL WITH THE NEW VALUES
            }
        });

    }

    private void setAllViews(boolean enabled)
    {
        aboutMeEditText.setEnabled(enabled);
        nameEditText.setEnabled(enabled);
        ageEditText.setEnabled(enabled);
        citySpinner.setEnabled(enabled);
//        Drawable regularDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.regular_gender);
//        Drawable selectDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.selected_gender);


    }
}
