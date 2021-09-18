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
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.GlideApp;
import huji.postpc.y2021.tal.yichye.thebubble.ImageStorageDB;
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
    private ImageView profileImage;
    private EditText aboutMeEditText;
    private EditText nameEditText;
    private UserViewModel userViewModel;
    private EditText ageTextView;
    private ImageStorageDB storageDB;

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
        profileImage = view.findViewById(R.id.profileImageView);
        firstImage = view.findViewById(R.id.firstImageViewEditProfile);
        secondImage = view.findViewById(R.id.secondImageViewEditProfile);
        thirdImage = view.findViewById(R.id.thirdImageViewEditProfile);
        ageTextView = view.findViewById(R.id.ageEditProfile);

        userViewModel =  new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        storageDB = TheBubbleApplication.getInstance().getImageStorageDB();

        citySpinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.city_array, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        setViewPagerImages(view);
        setAllViewsAsEnable(false);

        userViewModel.getDateOfBirthLiveData().observe(getViewLifecycleOwner(),
                aLong -> ageTextView.setText(PersonData.calcAge(aLong)+ ""));

        userViewModel.getFullNameLiveData().observe(getViewLifecycleOwner(), s ->
                nameEditText.setText(s));

        userViewModel.getCityLiveData().observe(getViewLifecycleOwner(), city -> {
            int pos = adapter.getPosition(city);
            citySpinner.setSelection(pos);
        });

        userViewModel.getAboutMeLiveData().observe(getViewLifecycleOwner(), s ->
                aboutMeEditText.setText(s));

        setImageView(profileImage, "profileImage");

        editButton.setOnClickListener(v -> {
            editButton.setVisibility(View.GONE);
            checkedButton.setVisibility(View.VISIBLE);
            setAllViewsAsEnable(true);
        });

        checkedButton.setOnClickListener(v -> {
            editButton.setVisibility(View.VISIBLE);
            checkedButton.setVisibility(View.GONE);
            setAllViewsAsEnable(false);

            // TODO UPDATE VIEW MODEL WITH THE NEW VALUES
            userViewModel.setFullNameLiveData(nameEditText.getText().toString(), null);
            userViewModel.setAboutMeLiveData(aboutMeEditText.getText().toString(), null);
            userViewModel.setCityLiveData(citySpinner.getSelectedItem().toString(), null);
        });
    }

    private void setViewPagerImages(View view)
    {
        String userName = userViewModel.getUserNameIdLiveData().getValue();
        ArrayList<StorageReference> imagesRefs = new ArrayList<>();
        for (String imageName: userViewModel.getPhotosLiveData().getValue()) {
            imagesRefs.add(storageDB.createReference(userName, imageName));
        }

        ViewPager viewPagerImages = view.findViewById(R.id.view_pager);
        ViewPagerImagesAdapter ImagesAdapter = new ViewPagerImagesAdapter(requireActivity(), imagesRefs);

        viewPagerImages.setAdapter(ImagesAdapter);
    }

    private void setAllViewsAsEnable(boolean enabled)
    {
        aboutMeEditText.setEnabled(enabled);
        nameEditText.setEnabled(enabled);
        citySpinner.setEnabled(enabled);
    }


    private void setImageView(ImageView imageView, String imageName)
    {
        StorageReference imageRef = storageDB.createReference(userViewModel.getUserNameIdLiveData().getValue(), imageName);

        imageRef.getBytes(2000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                GlideApp.with(requireActivity() /* context */)
                        .load(imageRef).centerCrop()
                        .into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("onFailure");
            }
        });
    }
}
