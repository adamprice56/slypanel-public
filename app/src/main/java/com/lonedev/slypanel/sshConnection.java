package com.lonedev.slypanel;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.InputStream;
import java.net.ConnectException;

/**
 * Created by adam on 26/11/14.
 */
public class sshConnection extends Activity {

    //SSH Command for cpu percentage top -b -n2 | grep "Cpu(s)" | awk '{print $2 + $4}'
    //Get the second line of this for true cpu percentage
    //Takes about 3-4 seconds to get it so refresh at ~5 seconds
    static String sshUsername = "nullUsername";
    static String sshPassword = "nullPassword";
    static String sshHost = "not.an.ip.address";
    static int port = 22;
    static String sshSendCommand = "fakecommand";
    public static boolean testing = false;

    static boolean updatingGraphs;

    static Context context;


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.fragment_main);
    }

    static Session sshSession ;
    String statusMessage;
    static String replyLine = null;
    static String cpuReplyLine = null;
    static String ramReplyLine = null;
    static String tempReplyLine = "Blank";

    FileManager fileMan;
    MainActivity mainActivity;
    ServerStatusFragment serverStatus;


    Thread sshManager = new Thread(new Runnable() {

        @Override
        public void run() {
            try {
                try {
                    String sshKey = null;
                    boolean alive = true;
                    JSch jschManager = new JSch();

                    Looper.prepare();
                    port = 22;

                    if (fileMan == null) {
                        fileMan = new FileManager();
                    }

                    if (mainActivity == null) {
                        mainActivity = new MainActivity();
                    }

                    if (serverStatus == null) {
                        serverStatus = new ServerStatusFragment();
                    }

                    if (sshHost == "not.an.ip.address") {
                        sshUsername = fileMan.loadDetails("username");
                        sshHost = fileMan.loadDetails("host");

                        if (sshHost == "not.an.ip.address") {
                            Log.w("Error", "Failed to retrieve settings");
                        }
                    }

                        if (sshPassword == "nullPassword") {
                            Log.w("Error", "Failed to get password");
                        }


                    sshSession = jschManager.getSession(sshUsername, sshHost, port);
                    Log.w("SSH", "Username: " + sshUsername + " Host: " + sshHost + " Port: " + port);
                    sshSession.setPassword(sshPassword);
                    sshSession.setConfig("StrictHostKeyChecking", "no");
//                    try {
//                        //Store SSH Key either in database or in a local key file on the device
//                        sshKey = fileMan.loadDetails("sshKey");
//                    }
//                    catch (NullPointerException Exception) {
//                        Log.w("Exception", Exception);
//                    }
//
//                    if (sshKey != null) {
//                        jschManager.addIdentity(sshKey);
//                        Log.w("Status", "SSH Public Key loaded");
//                    }
//                    else {
//                        Log.w("Status", "SSH Key Failed to load");
//                    }


                    if (!sshSession.isConnected()) {
                        sshSession.connect();
                    }


                    String cpuCommand = "top -b -n2 | grep \"Cpu(s)\" | awk '{print $2 + $4}' | tail -1";
                    String ramCommand = "top -bn1 | awk '/Mem/ { mem = \"\" $5 / $3 * 100 \"\" } END { print mem * 1000 }'";
                    String tempCommand = "echo 40.0";


                    Channel cpuChannel = sshSession.openChannel("exec");
                    ((ChannelExec) cpuChannel).setCommand(cpuCommand);
                    cpuChannel.setInputStream(null);
                    ((ChannelExec) cpuChannel).setErrStream(System.err);
                    InputStream cpuInStream = cpuChannel.getInputStream();


                    Channel ramChannel = sshSession.openChannel("exec");
                    ((ChannelExec) ramChannel).setCommand(ramCommand);
                    ramChannel.setInputStream(null);
                    ((ChannelExec) ramChannel).setErrStream(System.err);
                    InputStream ramInStream = ramChannel.getInputStream();



                    Channel tempChannel = sshSession.openChannel("exec");
                    ((ChannelExec) tempChannel).setCommand(tempCommand);
                    tempChannel.setInputStream(null);
                    ((ChannelExec) tempChannel).setErrStream(System.err);
                    InputStream tempInStream = tempChannel.getInputStream();

                    while (tempReplyLine.equals("Blank")) {
                        cpuChannel.connect();
                        if (cpuChannel.isConnected()) {
                            readOutput(cpuInStream, "cpu");
                            Log.w("ssh", "CPU command read");
                            cpuChannel.disconnect();
                        }

                        ramChannel.connect();
                        if (ramChannel.isConnected()) {
                            readOutput(ramInStream, "ram");
                            ramChannel.disconnect();
                        }

                        tempChannel.connect();
                        if (tempChannel.isConnected()) {
                            readOutput(tempInStream, "temp");
                            tempChannel.disconnect();
                        }
                    }

                    MainActivity.connected = true;

                    context = getApplicationContext();

                } catch (JSchException jschX) {
                    Log.w("Error", "Couldn't create the channel");
                    Log.w("Status", System.err.toString());
                    Log.w("JschException", jschX);


                    if (jschX.toString().contains("timeout")) {
                        Log.w("Error", "IP Address is incorrect");
                        Toast.makeText(getApplicationContext(), "Cannot connect to host, Check IP and connection.", Toast.LENGTH_LONG).show();

                    }

                    if (jschX.toString().contains("Auth fail")) {
                        Log.w("Error", "Username/Password Incorrect");
                        Toast.makeText(getApplicationContext(), "Username/Password Incorrect", Toast.LENGTH_LONG).show();
                    }
                    if (jschX.toString().contains("socket")) {
                        sshSession.disconnect();
                    }
                    if (jschX.toString().contains("ECONNRESET")) {
                        Toast.makeText(getApplicationContext(), "Connection failure", Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Please check your details", Toast.LENGTH_LONG).show();
                        ConnectionFragment connFrag = new ConnectionFragment();
                        connFrag.show(getFragmentManager(), "ConnectionFragment");
                    }
                }
            }
            catch (ConnectException Exception) {
                Log.w("Exception", Exception);
                Toast.makeText(getApplicationContext(), "Connection failure", Toast.LENGTH_LONG).show();
                ConnectionFragment connFrag = new ConnectionFragment();
                connFrag.show(getFragmentManager(), "ConnectionFragment");
            }
            catch (Exception Exception) {

                Log.w("Exception", Exception);
            }
            Looper.loop();
        }
    });

    public void resetCredentials() {
        sshUsername = "nullUsername";
        sshPassword = "nullPassword";
        sshHost = "not.an.ip.address";
    }

    public void sendCommand(String commandToSend) {
        if (MainActivity.commandBox != null) {
            sshSendCommand = MainActivity.commandBox.getText().toString();
        }
        else {
            sshSendCommand = commandToSend;
        }
//        MainActivity.statusBarText.setText("Command ready to be sent to: " + sshHost);

        try {
            sshManager.start();
        }
        catch (java.lang.IllegalThreadStateException Exception) {
            Log.w("Exception", "Output: " + Exception);
            sshManager.interrupt();
            sshManager = new sshConnection().sshManager;
            sshManager.start();
        }

        if (MainActivity.statusBarText != null) {
            MainActivity.statusBarText.setText("Command: " + sshSendCommand + "was sent to " + sshHost);
        }
    }

    public void setCommand(String commandToSend) {
        if (MainActivity.commandBox != null) {
            sshSendCommand = MainActivity.commandBox.getText().toString();
        } else {
            sshSendCommand = commandToSend;
        }


    }

    public void startSsh() {
        try {
            sshManager.start();
        }
        catch (java.lang.IllegalThreadStateException Exception) {
            Log.w("Thread", "Restarting sshManager");

            sshManager.interrupt();
            sshManager = new sshConnection().sshManager;
            sshManager.start();

        }

    }

    void readOutput(InputStream in, String channelName) {
        byte[] tmp = new byte[1024];
        try {
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    switch (channelName) {

                        default:
                            replyLine = (new String(tmp, 0, i));
                            Log.w("Reply", replyLine);
                            break;
                        case "cpu":
                            cpuReplyLine = (new String(tmp, 0, i));
                            Log.w("Reply", cpuReplyLine);
                            break;
                        case "ram":
                            ramReplyLine = (new String(tmp, 0, i));
                            Log.w("Reply", ramReplyLine);
                            break;
                        case "temp":
                            tempReplyLine = (new String(tmp, 0, i));
                            Log.w("Reply", tempReplyLine);
                            break;
                    }

                }
            }
        } catch (Exception exception) {
            Log.w("Exception", exception);
        }
    }
}
