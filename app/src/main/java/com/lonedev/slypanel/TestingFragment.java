package com.lonedev.slypanel;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by adam on 22/11/14.
 */
public class TestingFragment extends Fragment {

    public TestingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View TestingView = inflater.inflate(R.layout.testing_ssh, container, false);

        MainActivity.commandBox = (EditText) TestingView.findViewById(R.id.commandBox);
        MainActivity.usernameBox = (EditText) TestingView.findViewById(R.id.usernameBox);
        MainActivity.passwordBox = (EditText) TestingView.findViewById(R.id.passwordBox);
        MainActivity.ipAddressBox = (EditText) TestingView.findViewById(R.id.ipAddressBox);
        MainActivity.statusBarText = (TextView) TestingView.findViewById(R.id.statusBarText);


        return (TestingView);
    }

}