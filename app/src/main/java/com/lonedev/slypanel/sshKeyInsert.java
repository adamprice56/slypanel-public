package com.lonedev.slypanel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by adam on 13/01/15.
 */
public class sshKeyInsert extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                //View XML
                .setView(R.layout.ssh_key)
                        //Title
                .setTitle("Enter SSH Key")
                        //Right button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Send details

                    }
                })
                        //Left button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Negative action
                        sshKeyInsert.this.getDialog().cancel();
                    }
                });

        return  builder.create();
    }
}