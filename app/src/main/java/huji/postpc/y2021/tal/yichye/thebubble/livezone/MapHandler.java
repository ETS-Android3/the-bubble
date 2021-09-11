package huji.postpc.y2021.tal.yichye.thebubble.livezone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.StorageReference;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.ArrayList;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.Request;
import huji.postpc.y2021.tal.yichye.thebubble.GlideApp;
import huji.postpc.y2021.tal.yichye.thebubble.ImageStorageDB;
import huji.postpc.y2021.tal.yichye.thebubble.PersonData;
import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;
import huji.postpc.y2021.tal.yichye.thebubble.UsersDB;
import huji.postpc.y2021.tal.yichye.thebubble.sidebar.ViewPagerImagesAdapter;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MapHandler {

    private double DEFAULT_ZOOM = 18;
    private MapView mapView;
    private GeoPoint centerLocation;
    private ImageStorageDB storageDB;
    private PersonData currentUser;
    private UsersDB usersDB;

    public MapHandler(MapView map)
    {
        this.mapView = map;
        this.usersDB = TheBubbleApplication.getInstance().getUsersDB();
        this.storageDB = TheBubbleApplication.getInstance().getImageStorageDB();
        this.currentUser = null;
        this.centerLocation = null;

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        IMapController mapController = mapView.getController();
        mapView.setMultiTouchControls(false);
        mapView.setMaxZoomLevel(DEFAULT_ZOOM);
        mapView.setMinZoomLevel(DEFAULT_ZOOM);
        mapController.setZoom(DEFAULT_ZOOM);
        mapController.stopAnimation(true);
    }

    public void setMyLocationOnMap(Bitmap profileImage)
    {
        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(mapView);
        myLocationOverlay.enableFollowLocation();
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setDirectionArrow(profileImage, profileImage);

        mapView.getOverlays().add(myLocationOverlay);
//        mapController.setCenter(myLocationoverlay.getMyLocation());
//        mapView.setScrollableAreaLimitDouble(mapView.getBoundingBox());
    }

    public void setCenter()
    {
        // TODO UPDATE CENTER ACCORDING TO THIS.CENTERLOCATION
        GeoPoint myLocation = new GeoPoint(31.7698, 35.2174);
        mapView.setExpectedCenter(myLocation);
        mapView.setScrollableAreaLimitDouble(mapView.getBoundingBox());
//        centerLocation = myLocation;
//        GeoPoint personLocation = getPersonLocation(personData);
//        GeoPoint myLocation = new GeoPoint(personLocation.getLatitude(), personLocation.getLongitude());

        // final code
//        mapView.setExpectedCenter(centerLocation);
//        mapView.setScrollableAreaLimitDouble(mapView.getBoundingBox());
//

    }

    public void showMarkerOnMap(Drawable profileImage, PersonData person, boolean isCenter) {
        GeoPoint personLocation = getPersonLocation(isCenter);
        GeoPoint location = new GeoPoint(personLocation.getLatitude(), personLocation.getLongitude());

        if (isCenter){
            this.currentUser = person;
            this.centerLocation = location;
        }

        setCenter();
        Marker myMarker = new Marker(mapView);
        myMarker.setPosition(location);
        myMarker.setTitle(person.getId());
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

        myMarker.setIcon(profileImage);
        myMarker.setId(person.getId());

        myMarker.setOnMarkerClickListener((marker, mapView) -> {
            if (!isCenter) {
                showPopupOnMap(person);
            }
            return true;
        });

        mapView.getOverlays().add(myMarker);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void showPopupOnMap(PersonData personData)
    {
        Context context = mapView.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.person_info_window, null);

        int width = mapView.getWidth() - 140;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });

        updatePopupViews(popupView, personData);
    }

    private void updatePopupViews(View popUpView, PersonData personData)
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


        sendRequestButton.setOnClickListener(v -> {
            Request requestIn = new Request(currentUser.getId(), currentUser.getName(), true);
            Request requestOut = new Request(personData.getId(), personData.getName(), false);
            usersDB.addRequest(currentUser.getId(), requestOut);
            usersDB.addRequest(personData.getId(), requestIn);
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

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });

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

    public void removeAllMarkers()
    {
        mapView.getOverlays().clear();
    }

    private GeoPoint getPersonLocation(boolean isCenter)
    {
        // TODO GET LOCATION FROM FIREBASE STORAGE
        if (isCenter){
            return new GeoPoint(31.7698, 35.2174);
        }
        return new GeoPoint(31.7708, 35.2172);
    }

}
