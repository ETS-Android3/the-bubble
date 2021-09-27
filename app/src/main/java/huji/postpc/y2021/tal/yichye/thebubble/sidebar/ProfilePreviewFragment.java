package huji.postpc.y2021.tal.yichye.thebubble.sidebar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
    private ImageView firstImagePlus;
    private ImageView secondImagePlus;
    private ImageView thirdImagePlus;
    private ImageView profileImagePlus;
    private int currentViewIndex;

    ViewPager viewPagerImages;

    private ImageView[] imageViews;
    private ImageView[] plusViews;
    private EditText aboutMeEditText;
    private EditText nameEditText;
    private TextView genderTextView;
    private UserViewModel userViewModel;
    private EditText ageTextView;
    private ImageStorageDB storageDB;
    private ActivityResultLauncher<Intent> mLauncher;
    private int currentOption;
    ViewPagerImagesAdapter imagesAdapter;


    public ProfilePreviewFragment()
    {
        super(R.layout.my_profile_side_bar);
        mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> uploadAndUpdateView(result));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel =  new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        storageDB = TheBubbleApplication.getInstance().getImageStorageDB();

        editButton = view.findViewById(R.id.imageViewEditPencil);
        checkedButton = view.findViewById(R.id.imageViewChecked);
        editButton.bringToFront();
        checkedButton.bringToFront();
        aboutMeEditText = view.findViewById(R.id.aboutMeEditProfile);
        nameEditText = view.findViewById(R.id.nameEditProfile);
        ageTextView = view.findViewById(R.id.ageEditProfile);
        genderTextView = view.findViewById(R.id.genderEditProfile);

        profileImage = view.findViewById(R.id.profileImageView);
        firstImage = view.findViewById(R.id.firstImageViewEditProfile);
        secondImage = view.findViewById(R.id.secondImageViewEditProfile);
        thirdImage = view.findViewById(R.id.thirdImageViewEditProfile);
        profileImagePlus = view.findViewById(R.id.plusProfile);
        firstImagePlus = view.findViewById(R.id.plus1);
        secondImagePlus = view.findViewById(R.id.plus2);
        thirdImagePlus = view.findViewById(R.id.plus3);
        imageViews = new ImageView[]{profileImage, firstImage, secondImage, thirdImage};
        plusViews = new ImageView[]{profileImagePlus, firstImagePlus, secondImagePlus, thirdImagePlus};

        citySpinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.city_array, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        setViewPagerImages(view);
        setAllViewsEditable(false);
        setAllImagesClickListeners();
        setImageView(profileImage, "profileImage");

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

        userViewModel.getGenderLiveData().observe(getViewLifecycleOwner(), new Observer<PersonData.Gender>() {
            @Override
            public void onChanged(PersonData.Gender gender) {
                String genderText = gender.toString().toLowerCase();
                genderTextView.setText(genderText);
            }
        });

        editButton.setOnClickListener(v -> {
            setAllViewsEditable(true);
        });

        checkedButton.setOnClickListener(v -> {
            if (allValidValues()) {
                setAllViewsEditable(false);
                userViewModel.setFullNameLiveData(nameEditText.getText().toString(), null);
                userViewModel.setAboutMeLiveData(aboutMeEditText.getText().toString(), null);
                userViewModel.setCityLiveData(citySpinner.getSelectedItem().toString(), null);
                setViewPagerImages(view);
            }
        });
    }

    private boolean allValidValues()
    {
        String fullName = nameEditText.getText().toString();
        String aboutMe = aboutMeEditText.getText().toString();
        if( !fullName.matches("[a-z A-Z]+") || fullName.length() == 0)
        {
            Toast.makeText(requireActivity(), "Invalid Name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (aboutMe.length() == 0){
            Toast.makeText(requireActivity(), "Must enter about me description",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void setAllImagesClickListeners(){

        for (int i = 0; i < plusViews.length ; i++) {
            final int index = i;
            plusViews[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentViewIndex = index;
                    selectImage(getActivity());
                }
            });
        }
    }

    private void setViewPagerImages(View view)
    {
        String userName = userViewModel.getUserNameLiveData().getValue();
        ArrayList<StorageReference> imagesRefs = new ArrayList<>();
        for (String imageName: userViewModel.getPhotosLiveData().getValue()) {
            imagesRefs.add(storageDB.createReference(userName, imageName));
        }

        viewPagerImages = view.findViewById(R.id.view_pager);
        imagesAdapter = new ViewPagerImagesAdapter(requireActivity(), imagesRefs);

        viewPagerImages.setAdapter(imagesAdapter);
    }

    private void setAllViewsEditable(boolean enabled)
    {
        editButton.setVisibility(enabled ? View.GONE: View.VISIBLE);
        checkedButton.setVisibility(enabled ? View.VISIBLE: View.GONE);

        aboutMeEditText.setEnabled(enabled);
        nameEditText.setEnabled(enabled);
        citySpinner.setEnabled(enabled);

        for (ImageView view: plusViews) {
            view.setVisibility(enabled ? View.VISIBLE: View.GONE);
        }

        for (int i = 0; i < imageViews.length; i++) {
            if (i == 0)
            {
                setImageView(imageViews[i], getImageNameByIndex(i));
                continue;
            }
            if (!enabled){
                setImageView(imageViews[i], getImageNameByIndex(i));
            }
            imageViews[i].setVisibility(enabled ? View.VISIBLE: View.GONE);

        }

        viewPagerImages.setVisibility(enabled ? View.GONE: View.VISIBLE);

    }


    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery", "Delete image", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    currentOption = 0;
                    mLauncher.launch(takePicture);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    currentOption = 1;
                    mLauncher.launch(pickPhoto);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                } else{
                    deleteImage();
                }
            }
        });
        builder.show();
    }

    private void deleteImage()
    {
        if (currentViewIndex != 0) {
            ArrayList<String> newArray = userViewModel.getPhotosLiveData().getValue();
            String imageName = getImageNameByIndex(currentViewIndex);
            newArray.remove(imageName);
            userViewModel.setPhotosLiveData(newArray);
            storageDB.deleteImage(userViewModel.getUserNameLiveData().getValue(), imageName);
            GlideApp.with(requireActivity()).clear(imageViews[currentViewIndex]);
            imageViews[currentViewIndex].setBackgroundResource(R.drawable.shape);
        }
        else{
            Toast.makeText(requireActivity(), "Must have profile picture", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAndUpdateView(ActivityResult result) {
        int resultCode = result.getData().getIntExtra("resultCode", -1);
        if (resultCode != Activity.RESULT_CANCELED) {
            Intent data = result.getData();
            switch (currentOption) {
                case 0:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        uploadImageToFB(bitmap);
                    }
                    break;
                case 1:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getActivity().getApplicationContext().
                                    getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                                cursor.close();
                                uploadImageToFB(bitmap);
                            }
                        }
                    }
                    break;
            }
        }
    }

    private String getImageNameByIndex(int index)
    {
        switch (index) {
            case 0:
                return "profileImage";
            case 1:
                return "firstImage";
            case 2:
                return "secondImage";
            case 3:
                return "thirdImage";
        }
        return "";
    }

    private void uploadImageToFB(Bitmap image)
    {
        String imageName = getImageNameByIndex(currentViewIndex);
        MutableLiveData<Boolean> uploadLiveData = storageDB.uploadImage(
                userViewModel.getUserNameLiveData().getValue(), imageName, image);

        uploadLiveData.observe(getViewLifecycleOwner(), isUpload -> {
            if (isUpload) {
                ArrayList<String> newArray = userViewModel.getPhotosLiveData().getValue();
                if (!newArray.contains(imageName) && !imageName.equals("profileImage")) {
                    newArray.add(imageName);
                    userViewModel.setPhotosLiveData(newArray);
                }
                setImageView(imageViews[currentViewIndex], imageName);
            }
            else{
                Toast.makeText(requireActivity(),
                        "Problem with uploading image to Firebase", Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void setImageView(ImageView imageView, String imageName)
    {
        ArrayList<String> imagesArray = userViewModel.getPhotosLiveData().getValue();
        if (imagesArray.contains(imageName) || imageName.equals("profileImage")) {
            StorageReference imageRef = storageDB.createReference(
                    userViewModel.getUserNameLiveData().getValue(), imageName);
            GlideApp.with(requireActivity() /* context */)
                    .load(imageRef)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);

        }
    }
}
