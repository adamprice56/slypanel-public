package com.lonedev.slypanel;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * Created by adam on 20/01/15.
 */
public class FileManager extends Activity {


    public void saveDetails(String host, String username, String sshKey) {
        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();

        editor.putString("host", host);
        editor.putString("username", username);
        editor.putString("sshkey", sshKey);

        editor.apply();

        Log.w("Save", "Saved " + host + " " + username  + " " + sshKey);

    }

    public String loadDetails(String detail) {
        switch (detail) {
            case "host":
                Log.w("Load", "Host loaded");
                return MainActivity.sharedPreferences.getString("host", "127.0.0.1");
            case "username":
                Log.w("Load", "Username loaded");
                return MainActivity.sharedPreferences.getString("username", "defaultUsername");
            case "sshKey":
                Log.w("Load", "sshkey loaded");
                return MainActivity.sharedPreferences.getString("sshkey", "No ssh key");
            default:
                return null;
        }
    }
}
