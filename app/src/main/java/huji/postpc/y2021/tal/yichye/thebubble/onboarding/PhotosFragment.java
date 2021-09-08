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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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
    private ImageView rect1;
    private ImageView rect2;
    private ImageView rect3;
    private ImageView current_picked = null;
    private String currentImageNamePicked;
    int currentOption;
    private NewUserViewModel newUserViewModel;
    private ActivityResultLauncher<Intent> mLauncher;
    private ImageStorageDB storageDB;


    public PhotosFragment() {
        super(R.layout.photos_screen);
        storageDB = TheBubbleApplication.getInstance().getImageStorageDB();
        mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        selectAndUpload(result);
                    }
        });
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

    private void moveToNextFragment()
    {
        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.action_photosFragment_to_lookingForFragment);
    }

    private void setAllClickListeners(@NonNull View view){
        profile_pic_plus =  view.findViewById(R.id.plusProfile);
        rect1 = view.findViewById(R.id.rect1);
        rect2 = view.findViewById(R.id.rect2);
        rect3 = view.findViewById(R.id.rect3);
        reg_plus1 = view.findViewById(R.id.plus1);
        reg_plus2 = view.findViewById(R.id.plus2);
        reg_plus3 = view.findViewById(R.id.plus3);
        profile_image_spot = view.findViewById(R.id.userIcon);

        profile_pic_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_picked = profile_image_spot;
                currentImageNamePicked = "profileImage";
                selectImage(profile_pic_plus, getActivity());
            }
        });

        reg_plus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_picked = rect1;
                reg_plus1.setVisibility(View.GONE);
                currentImageNamePicked = "firstImage";
                selectImage(reg_plus1, getActivity());
            }
        });

        reg_plus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_picked = rect2;
                reg_plus2.setVisibility(View.GONE);
                currentImageNamePicked = "secondImage";
                selectImage(reg_plus2, getActivity());
            }
        });

        reg_plus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_picked = rect3;
                reg_plus3.setVisibility(View.GONE);
                currentImageNamePicked = "thirdImage";
                selectImage(reg_plus3, getActivity());
            }
        });
    }


    private void selectImage(ImageView reg_plus, Context context) {
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
                    reg_plus.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    public void selectAndUpload(ActivityResult result) {
        System.out.println("in on Activity result");
        int resultCode = result.getData().getIntExtra("resultCode", -1);
        if (resultCode != Activity.RESULT_CANCELED) {
            Intent data = result.getData();
            switch (currentOption) {
                case 0:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        System.out.println("in case 0");
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        // TODO: INSTEAD OF UPLOAD TO FIREBASE, NEED TO SAVE IN LIVEDATA
                        uploadImageToFB(bitmap, currentImageNamePicked);
                    }

                    break;
                case 1:
                    System.out.println("in case 1");

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

                                // TODO: INSTEAD OF UPLOAD TO FIREBASE, NEED TO SAVE IN LIVEDATA
                                uploadImageToFB(bitmap, currentImageNamePicked);
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void setAllImageViews()
    {
        setImageView(profile_image_spot, "profileImage");
        setImageView(rect1, "firstImage");
        setImageView(rect2, "secondImage");
        setImageView(rect3, "thirdImage");
    }


    private void setImageView(ImageView imageView, String imageName)
    {
        try {
            StorageReference imageRef = storageDB.createReference(newUserViewModel.userNameLiveData.getValue(), imageName);

            imageRef.getBytes(2000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    GlideApp.with(requireActivity() /* context */)
                            .load(imageRef)
                            .into(imageView);
                    if (imageName.equals("profileImage")) {
                        nextButton.setEnabled(true);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("photos fragment", "problem with downloading the image");
                }
            });
        } catch (Exception e) {
            Log.d("photos fragment", "problem with getting reference");
            e.printStackTrace();
        }
    }

    private void uploadImageToFB(Bitmap image, String imageName)
    {
        Log.d("login", "upload image");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = storageDB.createReference(newUserViewModel.userNameLiveData.getValue(), imageName);
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(requireActivity(),
                        "Problem with uploading image to Firebase", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (imageName.equals("profileImage")){
                    nextButton.setEnabled(true);
                }
                Toast.makeText(requireActivity(),
                        "Finish uploading image to Firebase", Toast.LENGTH_SHORT).show();

                GlideApp.with(requireActivity() /* context */)
                        .load(ref)
                        .into(current_picked);

                ArrayList<String> newArray = newUserViewModel.photosLiveData.getValue();
                if (!newArray.contains(imageName) && !imageName.equals("profileImage"))
                {
                    newArray.add(imageName);
                    newUserViewModel.photosLiveData.setValue(newArray);
                }
            }
        });
    }

}
