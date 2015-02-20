package com.lonedev.slypanel;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by root on 02/10/14.
 */
public class SSHFragment extends Fragment {

    public SSHFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View SSHView = inflater.inflate(R.layout.fragment_ssh, container, false);
        return (SSHView);
    }

}
