package huji.postpc.y2021.tal.yichye.thebubble.livezone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.StorageReference;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.util.ArrayList;
import java.util.HashMap;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.Request;
import huji.postpc.y2021.tal.yichye.thebubble.GlideApp;
import huji.postpc.y2021.tal.yichye.thebubble.ImageStorageDB;
import huji.postpc.y2021.tal.yichye.thebubble.PersonData;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;
import huji.postpc.y2021.tal.yichye.thebubble.UserViewModel;
import huji.postpc.y2021.tal.yichye.thebubble.UsersDB;
import huji.postpc.y2021.tal.yichye.thebubble.sidebar.ViewPagerImagesAdapter;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MapHandler {

    private double DEFAULT_ZOOM = 19;
    private MapView mapView;
    private GeoPoint centerLocation;
    private ImageStorageDB storageDB;
    private PersonData currentUser;
    private UsersDB usersDB;
    private PopupWindow[] openedPopupWindow;
    private UserViewModel userViewModel;

    public MapHandler(MapView map, UserViewModel userViewModel)
    {
        this.mapView = map;
        this.usersDB = TheBubbleApplication.getInstance().getUsersDB();
        this.storageDB = TheBubbleApplication.getInstance().getImageStorageDB();
        this.currentUser = null;
        this.centerLocation = null;
        this.openedPopupWindow =  new PopupWindow[]{null, null};
        this.userViewModel = userViewModel;

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        IMapController mapController = mapView.getController();
        mapView.setMultiTouchControls(false);
        mapView.setMaxZoomLevel(DEFAULT_ZOOM);
        mapView.setMinZoomLevel(DEFAULT_ZOOM);
        mapController.setZoom(DEFAULT_ZOOM);
        mapController.stopAnimation(true);

    }

    public void setCenter()
    {
        mapView.setScrollableAreaLimitDouble(null);
        mapView.getController().setCenter(centerLocation);
        mapView.setScrollableAreaLimitDouble(mapView.getBoundingBox());
    }

    public void showMarkerOnMap(Drawable profileImage, Pair<PersonData, HashMap<String, Double>> match, boolean isCenter) {
        if (!isCenter && checkRequestBetweenUsers(match.first)){
            return;
        }

        double latitude = match.second.get("latitude");
        double longitude = match.second.get("longitude");
        GeoPoint location = new GeoPoint(latitude, longitude);

        if (isCenter){
            this.currentUser = match.first;
            this.centerLocation = location;
            setCenter();
        }

        Marker myMarker = new Marker(mapView);
        myMarker.setPosition(location);
        myMarker.setTitle(match.first.getId());
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

        myMarker.setIcon(profileImage);
        myMarker.setId(match.first.getId());

        myMarker.setOnMarkerClickListener((marker, mapView) -> {
            if (!isCenter) {
                showPopupOnMap(myMarker, match.first);
            }
            return true;
        });

        mapView.getOverlays().add(myMarker);
        mapView.invalidate();
    }


    private boolean checkRequestBetweenUsers(PersonData otherPerson)
    {
        for (Request request: userViewModel.getRequestsLiveData().getValue()) {
            if (request.getReqUserId().equals(otherPerson.getId())){
                return true;
            }
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showPopupOnMap(Marker currentMarker, PersonData personData)
    {
        Context context = mapView.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.person_info_window, null);

        int width = mapView.getWidth() - 140;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        openedPopupWindow[0] = popupWindow;
        popupView.setOnTouchListener((v, event) -> {
            openedPopupWindow[0] = null;
            popupWindow.dismiss();
            return true;
        });

        updatePopupViews(popupView, personData, currentMarker);
    }

    private void updatePopupViews(View popUpView, PersonData personData, Marker currentMarker)
    {
        TextView fullNameView = popUpView.findViewById(R.id.fullNamePopupView);
        TextView ageView = popUpView.findViewById(R.id.agePopupView);
        TextView aboutMeView = popUpView.findViewById(R.id.aboutMePopupView);
        ImageView profileImageView = popUpView.findViewById(R.id.profileImagePopupView);
        MaterialButton sendRequestButton = popUpView.findViewById(R.id.sendRequestButton);
        MaterialButton viewProfileButton = popUpView.findViewById(R.id.viewProfileButton);
        TextView genderView = popUpView.findViewById(R.id.genderPopupView);

        fullNameView.setText(personData.getName() +",");
        ageView.setText(personData.getAge() +"");
        genderView.setText(personData.gender.toString().toLowerCase() + "");
        String aboutMe = personData.getAboutMe();
        String cutAboutMe = aboutMe.substring(0, Math.min(20, aboutMe.length()));
        if (cutAboutMe.length() != personData.getAboutMe().length()){
            cutAboutMe += "...";
        }

        aboutMeView.setText(cutAboutMe);

        StorageReference profileImageRef = storageDB.createReference(personData.getId(), "profileImage");
        GlideApp.with(mapView.getContext())
                .load(profileImageRef)
                .centerCrop()
                .into(profileImageView);

        if (checkRequestBetweenUsers(personData)){
            sendRequestButton.setEnabled(false);
            sendRequestButton.setText("Request sent !");
            sendRequestButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mapView.getContext(), R.color.disable_gray)));
//            Drawable icon = currentMarker.getIcon();
//            icon.setAlpha(255);
//
//            mapView.getOverlays().remove(currentMarker);
//            currentMarker.setIcon(icon);
//            mapView.getOverlays().add(currentMarker);
            mapView.invalidate();
        }

        sendRequestButton.setOnClickListener(v -> {
            Request requestIn = new Request(userViewModel.getUserNameLiveData().getValue(),
                    userViewModel.getFullNameLiveData().getValue(), true);
            Request requestOut = new Request(personData.getId(), personData.getName(), false);
            usersDB.addRequest(currentUser.getId(), requestOut);
            usersDB.addRequest(personData.getId(), requestIn);

            sendRequestButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mapView.getContext(), R.color.disable_gray)));
            sendRequestButton.setEnabled(false);
            Toast.makeText(this.mapView.getContext(), "Request was sent", Toast.LENGTH_LONG).show();
        });

        viewProfileButton.setOnClickListener(v -> showFullPopupOnMap(personData, profileImageRef));
    }


    @SuppressLint("ClickableViewAccessibility")
    private void showFullPopupOnMap(PersonData personData, StorageReference profileImageRef)
    {
        Context context = mapView.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.full_person_info_window, null);

        int width = mapView.getWidth() - 140;
        int height = mapView.getHeight() - 140;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        openedPopupWindow[1] = popupWindow;
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        updateFullPopupViews(popupView, personData, profileImageRef);
    }

    private void updateFullPopupViews(View popUpView, PersonData personData, StorageReference profileImageRef)
    {
        ImageStorageDB storageDB = TheBubbleApplication.getInstance().getImageStorageDB();
        TextView aboutMeView = popUpView.findViewById(R.id.aboutMeEditProfile);
        TextView fullNameView = popUpView.findViewById(R.id.nameEditProfile);
        TextView ageView = popUpView.findViewById(R.id.ageEditProfile);
        TextView cityView = popUpView.findViewById(R.id.city);
        TextView genderView = popUpView.findViewById(R.id.genderEditProfile);
        ImageView profileImage = popUpView.findViewById(R.id.profileImageView);

        aboutMeView.setMovementMethod(new ScrollingMovementMethod());

        String userName = personData.userName;
        ArrayList<StorageReference> imagesRefs = new ArrayList<>();
        for (String imageName: personData.photos) {
            imagesRefs.add(storageDB.createReference(userName, imageName));
        }

        ViewPager viewPagerImages = popUpView.findViewById(R.id.view_pager);
        ViewPagerImagesAdapter imagesAdapter = new ViewPagerImagesAdapter(popUpView.getContext(), imagesRefs);
        viewPagerImages.setAdapter(imagesAdapter);

        fullNameView.setText(personData.getName());
        ageView.setText(personData.getAge() +"");
        aboutMeView.setText(personData.aboutMe);
        cityView.setText(personData.city);
        genderView.setText(personData.gender.toString().toLowerCase() +"");

        GlideApp.with(popUpView.getContext()).load(profileImageRef).centerCrop().into(profileImage);
    }

    public void cleanupMap()
    {
        mapView.getOverlays().clear();
        for (PopupWindow popup: openedPopupWindow)
        if (popup != null){
            popup.dismiss();
        }
    }

}
