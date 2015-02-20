package com.lonedev.slypanel;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Created by adam on 28/01/15.
 */
public class sshShell {


    EditText shellCommand;
    TextView terminal;
    Button sendButton;
    Session session;
    ByteArrayOutputStream outputStream;
    ByteArrayInputStream inputStream;
    Channel channel;
    boolean connected = false;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view  = inflater.inflate(R.layout.fragment_ssh, container, false);

        sendButton = (Button) view.findViewById(R.id.shellSendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connected) {
                    sendCommand(view);
                    Log.w("Button", "Sending command");
                }
                else {
                    connect(view);
                    Log.w("Button", "Trying to connect to: " + sshConnection.sshHost);
                    sendCommand(view);
                    Log.w("Button", "Sending command");
                }

            }
        });

        return (view);
    }


    public void connect(View view){

        inputStream = new ByteArrayInputStream(new byte[1000]);
        shellCommand = (EditText) view.findViewById(R.id.shellCommand);
        terminal  = (TextView) view.findViewById(R.id.terminal);

        String username = sshConnection.sshUsername;
        String password = sshConnection.sshPassword;
        String host     = sshConnection.sshHost;

        if(shellCommand.getText().toString() != ""){
            JSch jsch = new JSch();
            try {
                session = jsch.getSession(username, host, 22);
                session.setPassword(password);

                Properties properties = new Properties();
                properties.put("StrictHostKeyChecking", "no");
                session.setConfig(properties);
                session.connect();

                channel = session.openChannel("shell");
                channel.setInputStream(inputStream);

                try {
                    PrintStream shellStream = new PrintStream(channel.getOutputStream());
                    channel.setOutputStream(System.out);
                    shellStream.println("screen \r \n");
                    shellStream.println(shellCommand);
                    channel.connect();
                    connected = true;
                    Log.w("SSH", "Session should be connected");
                }
                catch (IOException Exception) {
                    Log.w("Exception", Exception);
                }

            } catch (JSchException Exception) {
                Log.w("Exception", Exception);
            }
        }

    }



    public void sendCommand(View view){

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        terminal.append(bufferedReader.toString());

        outputStream = new ByteArrayOutputStream();
        channel.setOutputStream(outputStream);
        terminal.append(outputStream.toString());
    }


}
