package com.lonedev.slypanel;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by adam on 11/02/15.
 */

public class sshService extends AsyncTask<String, Void, String> {

    public Session session;
    public Channel channel;
    public String host = null;
    public int port = 22;
    public String username;
    public String password;

    String sentCommand;
    public String replyLine;

    FileManager fileManager = new FileManager();
    JSch jSch = new JSch();
    ServerStatusFragment serverStatusFragment = new ServerStatusFragment();

    InputStream in = null;

    final String cpuCommand = "top -b -n2 | grep \"Cpu(s)\" | awk '{print $2 + $4}' | tail -1 && Done";
    final String ramCommand = "free | awk 'FNR == 3 {print $3/($3+$4)*100}'";
    final String tempCommand = "echo 40.0";

    void setCredentials(String newHost, String newUsername, String newPassword) {
        host = newHost;
        username = newUsername;
        password = newPassword;
    }

    void checkPassword() {
        password = ConnectionFragment.sshPassword;
    }

    @Override
    protected String doInBackground(String... command) {

        try {
            sentCommand = command[0];
            return executeCommands(command[0]);
        } catch (IOException e) {
            return "Failed to send command.";
        }
    }

    void doGraphs() {
        switch (sentCommand) {
            case cpuCommand:
                ServerStatusFragment.cpuReplyLine = replyLine;
                serverStatusFragment.updateCpuGraph(replyLine);
                break;
            case ramCommand:
                ServerStatusFragment.ramReplyLine = replyLine;
                serverStatusFragment.updateRamGraph(replyLine);
                break;
            case tempCommand:
                ServerStatusFragment.tempReplyLine = replyLine;
                serverStatusFragment.updateTempGraph(replyLine);
                break;
            default:
                Log.w("Reply", replyLine);
                break;
        }
        if (ServerStatusFragment.isNumeric(replyLine)) {
            Log.w("Response", replyLine);
        }
        else {
            Log.w("Response", "replyLine was not numeric");
            ServerStatusFragment.cpuReplyLine = "Fail";
        }

    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        //Send the reply
        Log.w("Async", "Finished");
    }


    private String executeCommands(String command) throws IOException {
        boolean found = false;
        try {

            if (host == null) {
                host = fileManager.loadDetails("host");
                username = fileManager.loadDetails("username");
            }
            session = jSch.getSession(username, host, port);
            checkPassword();
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            //TODO - REMOVE THIS
            Log.w("DEBUG", "Host: " + host + " Username: " + username);
            session.connect();
            channel = session.openChannel("exec");


            DataOutputStream dataOut = new DataOutputStream(channel.getOutputStream());

            in = channel.getInputStream();



            //Send command
            Log.w("Command", "Command: " + command);
            String commandWithCheck = command + "\r\n";

            ((ChannelExec) channel).setCommand(commandWithCheck);

            channel.connect(1000);


            byte[] tmp = new byte[1024];

            replyLine = "";

            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    replyLine = (new String(tmp, 0, i));

                    Log.w("ReplyLine", replyLine);
                    found = true;
                    doGraphs();
                    channel.disconnect();
                    session.disconnect();
                    return replyLine;
                }
            }
        }
        catch (JSchException Exception) {
            Log.w("Exception", Exception);
            return "Failed to connect";
        }
        finally {
            if (in != null && found) {
                in.close();
                channel.disconnect();
                session.disconnect();
                doGraphs();
                return replyLine;
            }
        }
    }
}