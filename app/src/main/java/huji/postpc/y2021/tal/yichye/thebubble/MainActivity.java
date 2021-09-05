package huji.postpc.y2021.tal.yichye.thebubble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.ConnectionsFragment;
import io.grpc.internal.JsonUtil;

import java.util.Set;

import static com.google.firebase.components.Dependency.setOf;


public class MainActivity extends AppCompatActivity {

    UserViewModel userViewModel ;
    SharedPreferences sp;
    ListenerRegistration listenerRegistration;


    private NavController sideNavController;
    private AppBarConfiguration appBarConfiguration;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private LinearLayout bottomBarLinearView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        userViewModel =  new ViewModelProvider(this).get(UserViewModel.class);
        sp = TheBubbleApplication.getInstance().getSP();
        setUserViewModel();
    }

    private void setUserViewModel() {
        if (sp != null){
            String userName = sp.getString("user_name", null);
            if (userName != null){
                LiveData<PersonData> personDataLiveData = TheBubbleApplication.getInstance().getUsersDB().getUserByID(userName);
                final Observer<PersonData> personDataObserver = personData -> {
                    if(personData != null){
                        System.out.println(personData);
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
        userViewModel.ageLiveData.setValue(personData.age);
        userViewModel.myGenderLiveData.setValue(personData.gender);
        userViewModel.cityLiveData.setValue(personData.city);
        userViewModel.profilePicture.setValue(personData.profilePicture);
        userViewModel.photosLiveData.setValue(personData.photos);
        userViewModel.minAgePreferenceLiveData.setValue(personData.minAgePreference);
        userViewModel.maxAgePreferenceLiveData.setValue(personData.maxAgePreference);
        userViewModel.genderTendency.setValue(personData.genderTendency);
        userViewModel.requestsLiveData.setValue(personData.requests);
        userViewModel.aboutMeLiveData.setValue(personData.aboutMe);
    }


    private void setMainView() {
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        NavigationView sideBarNavigationView = findViewById(R.id.sideBarNavigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomBarLinearView = findViewById(R.id.bottom_bar);

        // setup drawer navigation (side bar)
        setSupportActionBar(topAppBar);
        NavHostFragment navHostFragmentSideBar = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container_main);
        NavController navControllerSideBar = navHostFragmentSideBar.getNavController();

        // set of all top-level fragments. top level means the menu icon will appear instead of back arrow
        // and when pressing back on android it will exit from application
        Set<Integer> topLevelDest = Set.of(R.id.connections, R.id.agent, R.id.liveZone);
        appBarConfiguration = new AppBarConfiguration.
                Builder(topLevelDest).setOpenableLayout(drawerLayout).build();

        // handle correct icon when drawer is open\close (menu icon or back arrow)
        NavigationUI.setupActionBarWithNavController(this, navControllerSideBar, appBarConfiguration);

        // handle click listener and navigation between fragments
        NavigationUI.setupWithNavController(sideBarNavigationView, navControllerSideBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        sideBarNavigationView.setNavigationItemSelectedListener(sideBarNavListener);

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
                        enableBottomNavigationView();
                        navController.navigate(R.id.liveZone);
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
                        System.out.println("IN ON EVENT");
                        if (error != null) {
                            System.err.println(error.getMessage());

                        } else if (snapshot == null) {
                            System.err.println("snap shot is null");
                        } else {
                            System.out.println("in every");
                            PersonData changedPerson = snapshot.toObject(PersonData.class);
                            if (changedPerson != null) {
                                updateUserViewModel(changedPerson);
                            }
                        }
                        System.out.println(userViewModel.fullNameLiveData.getValue());
                        System.out.println(userViewModel.requestsLiveData.getValue());
                        System.out.println(userViewModel.userNameLiveData.getValue());
                        System.out.println(userViewModel.passwordLiveData.getValue());

                    }
                });
    }

    private void detachListener(){
        if(listenerRegistration != null)        listenerRegistration.remove();
    }


    @Override
    protected void onDestroy() {
        detachListener();
        super.onDestroy();

    }
}



