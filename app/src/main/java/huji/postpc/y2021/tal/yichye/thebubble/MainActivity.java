package huji.postpc.y2021.tal.yichye.thebubble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.MaterialToolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.StorageReference;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.google.firebase.components.Dependency.setOf;


public class MainActivity extends AppCompatActivity {


    private UserViewModel userViewModel ;
    private SharedPreferences sp;
    private ListenerRegistration listenerRegistration;

    private NavController sideNavController;
    private AppBarConfiguration appBarConfiguration;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private LinearLayout bottomBarLinearView;
    private BottomNavigationView bottomNavigationView;
    private NavigationView sideBarNavigationView;
    private ImageView profileImageView;
    private View headerView;
    private final int WORKER_FOREGROUND_PERMISSION_ID = 40;
    private final int WORKER_BACKGROUND_GROUND_PERMISSION_ID = 42;
    private final int LIVE_ZONE_FOREGROUND_PERMISSION_ID = 44;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        WorkManager.getInstance(this).cancelAllWork();
        userViewModel =  new ViewModelProvider(this).get(UserViewModel.class);
        sp = TheBubbleApplication.getInstance().getSP();
        setUserViewModel();

        if (checkLocationPermission(WORKER_FOREGROUND_PERMISSION_ID, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            if (checkLocationPermission(WORKER_BACKGROUND_GROUND_PERMISSION_ID, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                startBackgroundWorker();
            }
        }

    }

    private void setUserViewModel() {
        if (sp != null){
            String userName = sp.getString("user_name", null);
            if (userName != null){
                LiveData<PersonData> personDataLiveData = TheBubbleApplication.getInstance().getUsersDB().getUserByID(userName);
                final Observer<PersonData> personDataObserver = personData -> {
                    if(personData != null){
                        updateUserViewModel(personData);
                       }
                    else {
                        Toast.makeText(getBaseContext(),
                                "Person doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                    setMainView();
                    attach_listener(personData);

                };
                personDataLiveData.observe(this,personDataObserver);
            }
        }
    }

    private void updateUserViewModel(PersonData personData){
        userViewModel.fullNameLiveData.setValue(personData.fullName);
        userViewModel.userNameLiveData.setValue(personData.userName);
        userViewModel.passwordLiveData.setValue(personData.password);
        userViewModel.phoneNumberLiveData.setValue(personData.phoneNumber);
        userViewModel.dateOfBirthLiveData.setValue(personData.dateOfBirth);
        userViewModel.myGenderLiveData.setValue(personData.gender);
        userViewModel.cityLiveData.setValue(personData.city);
        userViewModel.profilePicture.setValue(personData.profilePicture);
        userViewModel.photosLiveData.setValue(personData.photos);
        userViewModel.minAgePreferenceLiveData.setValue(personData.minAgePreference);
        userViewModel.maxAgePreferenceLiveData.setValue(personData.maxAgePreference);
        userViewModel.genderTendency.setValue(personData.genderTendency);
        userViewModel.requestsLiveData.setValue(personData.requests);
        userViewModel.aboutMeLiveData.setValue(personData.aboutMe);
        userViewModel.chatsLiveData.setValue(personData.chatInfos);
        userViewModel.ignoreListLiveData.setValue(personData.ignoreList);
    }


    private void setMainView() {
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        sideBarNavigationView = findViewById(R.id.sideBarNavigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomBarLinearView = findViewById(R.id.bottom_bar);

        // setup drawer navigation (side bar)
        setSupportActionBar(topAppBar);
        NavHostFragment navHostFragmentSideBar = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container_main);
        NavController navControllerSideBar = navHostFragmentSideBar.getNavController();

        // set of all top-level fragments. top level means the menu icon will appear instead of back arrow
        // and when pressing back on android it will exit from application
        HashSet<Integer> topLevelDest = new HashSet<>();
        topLevelDest.add(R.id.connections);
        topLevelDest.add(R.id.agent);
        topLevelDest.add(R.id.liveZone);
        appBarConfiguration = new AppBarConfiguration.
                Builder(topLevelDest).setOpenableLayout(drawerLayout).build();

        // handle correct icon when drawer is open\close (menu icon or back arrow)
        NavigationUI.setupActionBarWithNavController(this, navControllerSideBar, appBarConfiguration);

        // handle click listener and navigation between fragments
        NavigationUI.setupWithNavController(sideBarNavigationView, navControllerSideBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        sideBarNavigationView.setNavigationItemSelectedListener(sideBarNavListener);

        createLiveDataListeners();
    }

    private void createLiveDataListeners()
    {
        headerView = sideBarNavigationView.getHeaderView(0);
        TextView fullNameTextView = (TextView) headerView.findViewById(R.id.full_name_header);
        TextView userNameTextView = (TextView) headerView.findViewById(R.id.user_name_header);
        profileImageView = (ImageView) headerView.findViewById(R.id.pic_header);
        TextView logoutTextView = (TextView) headerView.findViewById(R.id.logout_header);

        userViewModel.getFullNameLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                fullNameTextView.setText(s);
            }
        });

        userViewModel.getUserNameLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                userNameTextView.setText(s);
            }
        });

        setProfileImageView(profileImageView);

        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TheBubbleApplication.getInstance().getSP().edit().remove("user_name").apply();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class).
                        setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAfterTransition();
            }
        });

    }

    private void setProfileImageView(ImageView profileImageView)
    {
        ImageStorageDB storageDB = TheBubbleApplication.getInstance().getImageStorageDB();
        StorageReference imageRef = storageDB.createReference(userViewModel.getUserNameLiveData().getValue(), "profileImage");
        GlideApp.with(MainActivity.this /* context */)
                .load(imageRef)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(profileImageView);
    }

    /**
     * opening drawer layout when menu icon is clicked and handling the back button
     * press when we are not in non top-level destinations
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragmentSideBar = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container_main);
        NavController navControllerSideBar = navHostFragmentSideBar.getNavController();
        bottomBarLinearView.setVisibility(View.VISIBLE);
        bottomNavigationView.getMenu().setGroupEnabled(R.id.group_bottom_menu,true);
        bottomNavigationView.getMenu().setGroupCheckable(R.id.group_bottom_menu,true, true);
        setProfileImageView(profileImageView);
        return NavigationUI.navigateUp(navControllerSideBar, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container_main);
        NavController navController = navHostFragment.getNavController();
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    private NavigationView.OnNavigationItemSelectedListener sideBarNavListener =
            item -> {
                NavHostFragment navHostFragment = (NavHostFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_container_main);
                NavController navController = navHostFragment.getNavController();
                drawerLayout.close();

                switch (item.getItemId()) {
                    case R.id.myProfile:
                        disableBottomNavigationView();
                        navController.navigate(R.id.myProfile);
                        break;
                    case R.id.myTendency:
                        disableBottomNavigationView();
                        navController.navigate(R.id.myTendency);
                        break;
                    case R.id.communityRules:
                        disableBottomNavigationView();
                        navController.navigate(R.id.communityRules);
                        break;
                    case R.id.appNavigation:
                        disableBottomNavigationView();
                        navController.navigate(R.id.appNavigation);
                        break;
                }
                return true;
            };



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                NavHostFragment navHostFragment = (NavHostFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_container_main);
                NavController navController = navHostFragment.getNavController();
                switch (item.getItemId()) {
                    case R.id.liveZone:
                        //TODO CHECK IF NEED TO CHANGE LAYOUT OF LIVE ZONE BUTTON
                        if (checkLocationPermission(LIVE_ZONE_FOREGROUND_PERMISSION_ID, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            enableBottomNavigationView();
                            navController.navigate(R.id.liveZone);
                        }
                        else{
                            Toast.makeText(MainActivity.this,
                                    "Must allow location permissions before running live zone",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.agent:
                        enableBottomNavigationView();
                        navController.navigate(R.id.agent);
                        break;
                    case R.id.connections:
                        enableBottomNavigationView();
                        navController.navigate(R.id.connections);
                        break;
                }
                return true;
            };



    private boolean checkLocationPermission(int permissionId, String permission)
    {
        if ((ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else if (shouldShowRequestPermissionRationale(permission)) {
            // TODO - CHANGE TEXT MESSAGE
            // TODO - CHECK WHICH PERMISSION IS NEEDED
            Toast.makeText(MainActivity.this, "Must allow location permission", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{permission}, permissionId);
            return false;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, permissionId);
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        bottomNavigationView.setClickable(false);
        if (bottomNavigationView.isEnabled()) {
            finishAffinity();
        }
        else {
            super.onBackPressed();
            enableBottomNavigationView();
        }

    }

    private void enableBottomNavigationView() {
        bottomNavigationView.getMenu().setGroupEnabled(R.id.group_bottom_menu, true);
        bottomNavigationView.getMenu().setGroupCheckable(R.id.group_bottom_menu, true, true);
        bottomNavigationView.setEnabled(true);
    }

    private void disableBottomNavigationView()
    {
        bottomNavigationView.getMenu().setGroupEnabled(R.id.group_bottom_menu,false);
        bottomNavigationView.getMenu().setGroupCheckable(R.id.group_bottom_menu,false, false);
        bottomNavigationView.setEnabled(false);
    }

    private void attach_listener(PersonData personData){
        listenerRegistration = TheBubbleApplication.getInstance()
                .getUsersDB()
                .getDb()
                .collection("users")
                .document(personData.getId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            System.err.println(error.getMessage());
                        } else if (snapshot != null){
                            PersonData changedPerson = snapshot.toObject(PersonData.class);
                            if (changedPerson != null) {
                                updateUserViewModel(changedPerson);
                            }
                        }
                    }
                });
    }

    private void detachListener(){
        if(listenerRegistration != null){
            listenerRegistration.remove();
        }
    }


    private void startBackgroundWorker(){
        WorkManager workManager = WorkManager.getInstance(this);
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(BackgroundLocationWorker.class,
                15, TimeUnit.MINUTES)
                .addTag("background")
                .build();
        workManager.enqueueUniquePeriodicWork(
                "background",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest); // run worker every 15 min
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // TODO MAYBE CHECK ALL RESULT IN GRANT RESULT ARRAY
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == WORKER_FOREGROUND_PERMISSION_ID) {
                checkLocationPermission(WORKER_BACKGROUND_GROUND_PERMISSION_ID, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
            else if (requestCode == WORKER_BACKGROUND_GROUND_PERMISSION_ID) {
                startBackgroundWorker();
            }
            else if (requestCode == LIVE_ZONE_FOREGROUND_PERMISSION_ID) {
                NavHostFragment navHostFragment = (NavHostFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_container_main);
                NavController navController = navHostFragment.getNavController();
                enableBottomNavigationView();
                navController.navigate(R.id.liveZone);
            }
        } else {
            // TODO change message
            Toast.makeText(MainActivity.this,
                    "Must allow location permission" + permissions[0],
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        detachListener();
        super.onDestroy();
    }
}



