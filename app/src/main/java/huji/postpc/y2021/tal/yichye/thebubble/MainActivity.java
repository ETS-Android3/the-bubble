package huji.postpc.y2021.tal.yichye.thebubble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
        }
    };
}
