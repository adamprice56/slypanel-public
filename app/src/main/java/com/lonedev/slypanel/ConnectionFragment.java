package com.lonedev.slypanel;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Created by adam on 10/01/15.
 */
public class ConnectionFragment extends DialogFragment {

    public static RadioButton passwordOnly;
    public static RadioButton sshKeyOnly;
    public static RadioButton sshKeyAndPassword;
    public static EditText host;
    public static EditText username;
    public static EditText password;
    public static EditText sshKey;
    public static Button cancelButton;
    public static Button okButton;

    public static String storedHost;
    public static String storedUsername;
    public static String storedSshKey;
    public static String sshPassword;

    FileManager fManager = new FileManager();
    sshConnection sshConnect;
    sshService sshServe;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view  = inflater.inflate(R.layout.connection_editor, container, false);

        return (view);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Do things

        if (sshServe == null) {
            sshServe = new sshService();
        }

        try {
            host = (EditText) view.findViewById(R.id.hostDialog);
            username = (EditText) view.findViewById(R.id.usernameDialog);
            password = (EditText) view.findViewById(R.id.passwordDialog);
            sshKey = (EditText) view.findViewById(R.id.sshKeyDialog);

            passwordOnly = (RadioButton) view.findViewById(R.id.passwordOnlyRadio);
            sshKeyOnly = (RadioButton) view.findViewById(R.id.sshKeyOnlyRadio);
            sshKeyAndPassword = (RadioButton) view.findViewById(R.id.sshKeyPasswordRadio);

            cancelButton = (Button) view.findViewById(R.id.cancelButtonDialog);
            okButton = (Button) view.findViewById(R.id.okButtonDialog);

            okButton.setOnClickListener(okButtonPressed);
            cancelButton.setOnClickListener(cancelButtonPressed);

            MainActivity.allowRefresh = false;

            loadDetails();
            if (!storedHost.equals("127.0.0.1")) {
                Toast.makeText(getActivity(), "Settings loaded", Toast.LENGTH_LONG).show();
            }

        }
        catch (NullPointerException Exception) {
            Log.w("Exception", "findviewbyid came back null :(");
            Log.w("Exception", Exception);
        }


    }

    View.OnClickListener okButtonPressed = new View.OnClickListener() {
        public void onClick(View view) {
            if (sshConnect == null) {
                sshConnect = new sshConnection();
            }

            sshConnect.resetCredentials();
            sendDetails();
        }
    };
    View.OnClickListener cancelButtonPressed = new View.OnClickListener() {
        public void onClick(View view) {
            ConnectionFragment.this.getDialog().cancel();
        }
    };



    void sendDetails() {
        Log.w("Status", "Trying to save details");

        if (checkDetails().equals("true")) {
            storedHost = host.getText().toString();
            storedUsername = username.getText().toString();
            storedSshKey = sshKey.getText().toString();
            sshPassword = password.getText().toString();
            ServerStatusFragment.sshPassword = password.getText().toString();
            Log.w("Values", "Host: " + storedHost + " Username: " + storedUsername);
            fManager.saveDetails(storedHost, storedUsername, storedSshKey);
            MainActivity.allowRefresh = true;
            sshConnection.testing = false;
            ConnectionFragment.this.getDialog().cancel();
            MainActivity.allowRefresh = true;
            sshServe.setCredentials(storedHost, storedUsername, password.getText().toString());
        }
        if (checkDetails().equals("false")){
            Toast.makeText(getActivity(), "Please check the details", Toast.LENGTH_SHORT).show();
            ConnectionFragment.this.getDialog().show();
        }
        if (checkDetails().equals("neither")){
            ConnectionFragment.this.getDialog().cancel();
            EasterEgg easterEgg = new EasterEgg();
            easterEgg.show(getFragmentManager(), "EasterEgg");
            Toast.makeText(getActivity(), "9 + 10 = 21... Right?", Toast.LENGTH_SHORT).show();
        }
    }

    String checkDetails() {

        Pattern ipAddress = Pattern.compile("([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))");

        storedHost = host.getText().toString();
        storedUsername = username.getText().toString();

        Log.w("Host: ", storedHost);

        boolean hostOK = ipAddress.matcher(storedHost).matches();
        boolean usernameOK = storedUsername.length() > 0;

        if (hostOK && usernameOK) {
            return "true";
        }
        else {
            if (storedHost.equals("1.3.3.7.21")) {
                return "neither";
            }
            else {
                return "false";
            }
        }
    }

    void loadDetails() {
        FileManager fileMan = new FileManager();

        storedHost = fileMan.loadDetails("host");
        storedUsername = fileMan.loadDetails("username");

        if (!storedHost.equals("127.0.0.1") && !storedUsername.equals("defaultUsername")) {
            host.setText(storedHost);
            username.setText(storedUsername);
        }
    }
}
