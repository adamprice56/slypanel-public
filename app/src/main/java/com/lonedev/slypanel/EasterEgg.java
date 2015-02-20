package com.lonedev.slypanel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by adam on 11/02/15.
 */
public class EasterEgg extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                //View XML
                .setView(R.layout.easter_egg)
                        //Title
                .setTitle("#MoeinEasterEgg")
                        //Right button
                .setPositiveButton("Praise Moein", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Send details
                        EasterEgg.this.getDialog().cancel();
                    }
                });

        return  builder.create();
    }
}
