package huji.postpc.y2021.tal.yichye.thebubble;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.ConnectionsFragment;
import io.grpc.internal.JsonUtil;


public class MainActivity extends AppCompatActivity {

    UserViewModel userViewModel ;
    SharedPreferences sp;
    ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("in main on create");
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
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(appNavListener);
    }




    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.connections_button:
                        selectedFragment = new ConnectionsFragment();
                        break;
                    case R.id.live_button:
                        selectedFragment = new LiveZoneFragment();
                        break;
                    case R.id.agent_button:
                        selectedFragment = new AgentFragment();
                        break;
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_main, selectedFragment)
                        .commit();
                return true;
            };



    View.OnClickListener appNavListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

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



