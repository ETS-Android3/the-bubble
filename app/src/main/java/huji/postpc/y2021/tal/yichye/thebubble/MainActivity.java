package huji.postpc.y2021.tal.yichye.thebubble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Set;

import static com.google.firebase.components.Dependency.setOf;


public class MainActivity extends AppCompatActivity {


    private  NavController sideNavController;
    private AppBarConfiguration appBarConfiguration;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private LinearLayout bottomBarLinearView;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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



    //    public DrawerLayout drawerLayout;
//    public ActionBarDrawerToggle actionBarDrawerToggle;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // drawer layout instance to toggle the menu icon to open
//        // drawer and back button to close drawer
//        drawerLayout = findViewById(R.id.my_drawer_layout);
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
//
//        // pass the Open and Close toggle for the drawer layout listener
//        // to toggle the button
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//
//        // to make the Navigation drawer icon always appear on the action bar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }
//
    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


}



//    drawerLayout = findViewById(R.id.drawer_layout);
//    bottomNavigationView = findViewById(R.id.bottom_navigation);
//    bottomBarLinearView = findViewById(R.id.bottom_bar);
//
//    findViewById(R.id.menuImageView).setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            drawerLayout.openDrawer(GravityCompat.START);
//            bottomBarLinearView.setVisibility(View.GONE);
////                bottomNavigationView.setVisibility(View.GONE);
//        }
//    });
//
//    NavigationView sideBarNavigationView = findViewById(R.id.sideBarNavigationView);
//    NavHostFragment navHostFragmentSideBar = (NavHostFragment)
//            getSupportFragmentManager().findFragmentById(R.id.fragment_container_main_side_bar);
//    NavController navControllerSideBar = navHostFragmentSideBar.getNavController();
//        NavigationUI.setupWithNavController(sideBarNavigationView, navControllerSideBar);
//
//
//}
//
//    @Override
//    public void onBackPressed() {
//        return;
//    }

//
//    // side bar navigation - WORKING!!!
//    setSupportActionBar(topAppBar);
//    NavHostFragment navHostFragmentSideBar = (NavHostFragment)
//            getSupportFragmentManager().findFragmentById(R.id.fragment_container_main_side_bar);
//    NavController navControllerSideBar = navHostFragmentSideBar.getNavController();
//        appBarConfiguration = new AppBarConfiguration.
//                Builder(navControllerSideBar.getGraph()).setOpenableLayout(drawerLayout).build();
//                NavigationUI.setupActionBarWithNavController(this, navControllerSideBar, appBarConfiguration);
//                NavigationUI.setupWithNavController(sideBarNavigationView, navControllerSideBar);

// Setup bottom navigation bar - WORKING!!!
//        NavHostFragment navHostFragment = (NavHostFragment)
//                getSupportFragmentManager().findFragmentById(R.id.fragment_container_main);
//        NavController navController = navHostFragment.getNavController();
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);
//        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

