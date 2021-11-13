package huji.postpc.y2021.tal.yichye.thebubble;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import huji.postpc.y2021.tal.yichye.thebubble.Connections.ChatsDB;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import androidx.appcompat.app.AppCompatDelegate;

public class TheBubbleApplication extends Application {

    private UsersDB usersDB;
    private ChatsDB chatsDB;
    private static TheBubbleApplication instance;
    private static SharedPreferences sp;

    public UsersDB getUsersDB() {
        usersDB = new UsersDB(this);
        return usersDB;
    }

    public ChatsDB getChatsDB(){
        chatsDB = new ChatsDB(this);
        return chatsDB;
    }

    public ImageStorageDB getImageStorageDB()
    {
        return ImageStorageDB.getInstance();
    }

    public static TheBubbleApplication getInstance()
    {
        return instance;
    }

    public SharedPreferences getSP() {
        return getSharedPreferences("local_db", MODE_PRIVATE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sp = getSharedPreferences("local_db", MODE_PRIVATE);
        usersDB = new UsersDB(this);
        instance = this;

        // TODO - ADD LOGOUT OPTION
        String userName = sp.getString("user_name", null);
        Intent intent;
        if (userName != null) {
            intent = new Intent(this, MainActivity.class).
                    setFlags(FLAG_ACTIVITY_NEW_TASK);
        }
        else {
            intent = new Intent(this, LoginActivity.class).
                    setFlags(FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);

    }

}
