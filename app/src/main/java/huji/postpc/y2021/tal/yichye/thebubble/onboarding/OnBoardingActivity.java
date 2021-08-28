package huji.postpc.y2021.tal.yichye.thebubble.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import huji.postpc.y2021.tal.yichye.thebubble.MainActivity;
import huji.postpc.y2021.tal.yichye.thebubble.R;

public class OnBoardingActivity extends AppCompatActivity
{
    public static int USER_INFO_FRAGMENT = 1;
    public static int PHONE_FRAGMENT = 2;
    public static int DATA_OF_BIRTH_FRAGMENT = 3;
    public static int GENDER_FRAGMENT = 4;
    public static int CITY_FRAGMENT = 5;
    public static int PHOTOS_FRAGMENT = 6;
    public static int LOOKING_FOR_FRAGMENT = 7;
    public static int ABOUT_ME_FRAGMENT = 8;
    public static int COMMUNITY_RULES_FRAGMENT = 9;
    public static int WELCOME_FRAGMENT = 10;
    public static int DONE_ON_BOARDING = 11;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_activity);

        NewUserViewModel newUserViewModel =
                new ViewModelProvider(this).get(NewUserViewModel.class);

        newUserViewModel.progressLiveData.observe(this, fragment_num -> {
            LinearProgressIndicator progressView = findViewById(R.id.progressView);
            int progress = fragment_num * 10;
            progressView.setProgressCompat(progress, true);

            if (fragment_num == WELCOME_FRAGMENT)
            {
                progressView.setVisibility(View.GONE);
            }
            else if (fragment_num == DONE_ON_BOARDING)
            {
                startActivity(new Intent(getApplication(), MainActivity.class));
                finishAffinity();
            }
        });
    }


}
