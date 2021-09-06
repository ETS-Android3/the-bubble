package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import huji.postpc.y2021.tal.yichye.thebubble.R;

public class PhotosFragment extends Fragment
{

    private FragmentActivity activity;
    private ExtendedFloatingActionButton nextButton;
    ImageView profile_image_spot;
    ImageView profile_pic_plus;
    ImageView reg_plus1;
    ImageView reg_plus2;
    ImageView reg_plus3;
    ImageView rect1;
    ImageView rect2;
    ImageView rect3;
    ImageView current_picked = null;

    public PhotosFragment()
    {
        super(R.layout.photos_screen);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        NewUserViewModel newUserViewModel =
                new ViewModelProvider(requireActivity())
                        .get(NewUserViewModel.class);
        newUserViewModel.progressLiveData.setValue(OnBoardingActivity.PHOTOS_FRAGMENT);

        nextButton = view.findViewById(R.id.extended_fab);
        activity = getActivity();

        nextButton.setOnClickListener(v -> {
            moveToNextFragment();
        });
        setAllClickListeners(view);


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
                selectImage(getActivity());
            }
        });

        reg_plus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_picked = rect1;
                reg_plus1.setVisibility(View.GONE);
                selectImage(getActivity());
            }
        });

        reg_plus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_picked = rect2;
                reg_plus2.setVisibility(View.GONE);
                selectImage(getActivity());
            }
        });


        reg_plus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_picked = rect3;
                reg_plus3.setVisibility(View.GONE);
                selectImage(getActivity());
            }
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
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    reg_plus1.setVisibility(View.VISIBLE);
                    reg_plus2.setVisibility(View.VISIBLE);
                    reg_plus3.setVisibility(View.VISIBLE);
                    dialog.dismiss();

                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("in on Activity result");
        super.onActivityResult(requestCode, resultCode, data);

//        getActivity();
        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        System.out.println("in case 0");
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                        BitmapDrawable selectedImage = new BitmapDrawable(getResources(), bitmap);
                        current_picked.setBackground(selectedImage);
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
                                BitmapDrawable image = new BitmapDrawable(getResources(), bitmap);
                                current_picked.setBackground(image);
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

}
