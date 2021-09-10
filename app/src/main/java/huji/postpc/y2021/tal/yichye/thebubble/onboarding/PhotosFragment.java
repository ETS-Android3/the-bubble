package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.GlideApp;
import huji.postpc.y2021.tal.yichye.thebubble.ImageStorageDB;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;

public class PhotosFragment extends Fragment
{
    private FragmentActivity activity;
    private ExtendedFloatingActionButton nextButton;
    private ImageView profile_image_spot;
    private ImageView profile_pic_plus;
    private ImageView reg_plus1;
    private ImageView reg_plus2;
    private ImageView reg_plus3;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView currentImageViewClicked = null;
    private ImageView currentPlusImageViewClicked = null;
    private String currentImageNamePicked;
    int currentOption;
    private NewUserViewModel newUserViewModel;
    private ActivityResultLauncher<Intent> mLauncher;
    private ImageStorageDB storageDB;


    public PhotosFragment() {
        super(R.layout.photos_screen);
        storageDB = TheBubbleApplication.getInstance().getImageStorageDB();
        mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> uploadAndUpdateView(result));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newUserViewModel = new ViewModelProvider(requireActivity())
                        .get(NewUserViewModel.class);
        newUserViewModel.progressLiveData.setValue(OnBoardingActivity.PHOTOS_FRAGMENT);

        nextButton = view.findViewById(R.id.extended_fab);
        activity = getActivity();

        nextButton.setOnClickListener(v -> {
            moveToNextFragment();
        });

        nextButton.setEnabled(false);
        setAllClickListeners(view);
        setAllImageViews();
    }

    private void setAllClickListeners(@NonNull View view){
        profile_pic_plus =  view.findViewById(R.id.plusProfile);
        imageView1 = view.findViewById(R.id.imageView1);
        imageView2 = view.findViewById(R.id.imageView2);
        imageView3 = view.findViewById(R.id.imageView3);
        reg_plus1 = view.findViewById(R.id.plus1);
        reg_plus2 = view.findViewById(R.id.plus2);
        reg_plus3 = view.findViewById(R.id.plus3);
        profile_image_spot = view.findViewById(R.id.userIcon);

        profile_pic_plus.setOnClickListener(v -> {
            currentImageViewClicked = profile_image_spot;
            currentImageNamePicked = "profileImage";
            currentPlusImageViewClicked = profile_pic_plus;
            selectImage(getActivity());
        });

        reg_plus1.setOnClickListener(v -> {
            currentImageViewClicked = imageView1;
            currentImageNamePicked = "firstImage";
            currentPlusImageViewClicked = reg_plus1;
            selectImage(getActivity());
        });

        reg_plus2.setOnClickListener(v -> {
            currentImageViewClicked = imageView2;
            currentImageNamePicked = "secondImage";
            currentPlusImageViewClicked = reg_plus2;
            selectImage(getActivity());
        });

        reg_plus3.setOnClickListener(v -> {
            currentImageViewClicked = imageView3;
            currentImageNamePicked = "thirdImage";
            currentPlusImageViewClicked = reg_plus3;
            selectImage(getActivity());
        });

    }


    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

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
                }
            }
        });
        builder.show();
    }


    private void uploadAndUpdateView(ActivityResult result) {
        int resultCode = result.getData().getIntExtra("resultCode", -1);
        if (resultCode != Activity.RESULT_CANCELED) {
            Intent data = result.getData();
            switch (currentOption) {
                case 0:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        uploadImageToFB(bitmap, currentImageNamePicked);
                    }
                    break;
                case 1:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                                cursor.close();
                                uploadImageToFB(bitmap, currentImageNamePicked);
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void uploadImageToFB(Bitmap image, String imageName)
    {
        currentPlusImageViewClicked.setEnabled(false);
        MutableLiveData<Boolean> isUploadLiveData = storageDB.uploadImage(
                newUserViewModel.userNameLiveData.getValue(), imageName, image);

        isUploadLiveData.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isUpload) {
                if (isUpload) {
                    Toast.makeText(requireActivity(),
                            "Finished uploading image to Firebase", Toast.LENGTH_SHORT).show();

                    ArrayList<String> newArray = newUserViewModel.photosLiveData.getValue();
                    if (imageName.equals("profileImage")){
                        newUserViewModel.profilePhotoLiveData.setValue("profileImage");
                    }
                    else if (!newArray.contains(imageName)) {
                        newArray.add(imageName);
                        newUserViewModel.photosLiveData.setValue(newArray);
                    }
                    setImageView(currentImageViewClicked, imageName);
                    currentPlusImageViewClicked.setEnabled(true);

                }
                else{
                    Toast.makeText(requireActivity(),
                            "Problem with uploading image to Firebase", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setAllImageViews()
    {
        setImageView(profile_image_spot, "profileImage");
        setImageView(imageView1, "firstImage");
        setImageView(imageView2, "secondImage");
        setImageView(imageView3, "thirdImage");
    }


    private void setImageView(ImageView imageView, String imageName)
    {
        ArrayList<String> imagesArray = newUserViewModel.photosLiveData.getValue();
        String profileImage = newUserViewModel.profilePhotoLiveData.getValue();
        if (imagesArray.contains(imageName) || imageName.equals(profileImage)) {
            StorageReference imageRef = storageDB.createReference(newUserViewModel.userNameLiveData.getValue(), imageName);
            GlideApp.with(requireActivity() /* context */)
                    .load(imageRef)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);

            if (imageName.equals("profileImage")) {
                nextButton.setEnabled(true);
            }
        }
    }


    private void moveToNextFragment()
    {
        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.action_photosFragment_to_lookingForFragment);
    }

}
