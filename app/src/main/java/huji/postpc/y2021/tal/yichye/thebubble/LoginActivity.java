package huji.postpc.y2021.tal.yichye.thebubble;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;


import huji.postpc.y2021.tal.yichye.thebubble.onboarding.OnBoardingActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText userNameEditText;
    private TextInputEditText passwordEditText;
    private Button signInButton;
    private TextView signUpTextView;

    private boolean clickedOnSignIn;

    private ActivityResultLauncher<String> requestPermissionToStorageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionToStorageLauncher = registerForActivityResult(new
                ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                if (clickedOnSignIn) {
                    checkUserCredentials();
                }
                else {
                    startActivity(new Intent(getApplicationContext(), OnBoardingActivity.class));
                }
            } else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Files and Media permission");
//                builder.setMessage("Please give Files and Media permission in order to use the app");
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.show();
            }
        });
        startLoginProcess();
    }

    private void startLoginProcess()
    {
        LoginActivity loginActivity = this;
        if (TheBubbleApplication.getInstance().getSP().getString("user_name", null) != null)
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        setContentView(R.layout.login_screen);
        userNameEditText = findViewById(R.id.userNameTextInputEdit);
        passwordEditText = findViewById(R.id.passwordTextInputEdit);
        signInButton = findViewById(R.id.signInButton);
        signUpTextView = findViewById(R.id.signUpTextView);

        defineGradientColorToText(signInButton);
        signInButton.setOnClickListener(v -> {
            clickedOnSignIn = true;
            int hasPermission = ContextCompat.checkSelfPermission(loginActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                checkUserCredentials();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Files and Media permission");
                builder.setMessage("Please give Files and Media permission in order to use the app");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissionToStorageLauncher.launch(
                                Manifest.permission.READ_EXTERNAL_STORAGE);
                        dialogInterface.dismiss();
                    }
                });
                builder.show();

            }
        });

        signUpTextView.setOnClickListener(v -> {
            clickedOnSignIn = false;
            int hasPermission = ContextCompat.checkSelfPermission(loginActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(getApplicationContext(), OnBoardingActivity.class));
            }
            else {
                requestPermissionToStorageLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE);
            }
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

    protected void defineGradientColorToText(Button view)
    {
        int startColor = ContextCompat.getColor(getApplicationContext(),
                R.color.gradient_peach);
        int endColor = ContextCompat.getColor(getApplicationContext(),
                R.color.gradient_purple);

        Shader shader = new LinearGradient(0,0,0,view.getLineHeight(),
                startColor, endColor, Shader.TileMode.REPEAT);
        view.getPaint().setShader(shader);
    }
}
