package huji.postpc.y2021.tal.yichye.thebubble;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import huji.postpc.y2021.tal.yichye.thebubble.onboarding.OnBoardingActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText userNameEditText;
    private TextInputEditText passwordEditText;
    private Button signInButton;
    private TextView signUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        userNameEditText = findViewById(R.id.userNameTextInputEdit);
        passwordEditText = findViewById(R.id.passwordTextInputEdit);
        signInButton = findViewById(R.id.signInButton);
        signUpTextView = findViewById(R.id.signUpTextView);

        signInButton.setOnClickListener(v -> {
            checkUserCredentials();
        });

        signUpTextView.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), OnBoardingActivity.class));
            finishAffinity();
        });
    }

    private void checkUserCredentials()
    {
        UsersDB usersDB = TheBubbleApplication.getInstance().getUsersDB();
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!userName.equals("") && !password.equals("")) {
            usersDB.getUserByID(userName).observe(this, personData -> {
                if (personData != null && password.equals(personData.password)) {
                    String userName1 = userNameEditText.getText().toString();
                    TheBubbleApplication.getInstance().getSP().edit().
                            putString("user_name", userName1).apply();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finishAffinity();
                } else {
                    Toast.makeText(getBaseContext(),
                            "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(getBaseContext(),
                    "Must enter user name and password", Toast.LENGTH_SHORT).show();
        }
    }
}
