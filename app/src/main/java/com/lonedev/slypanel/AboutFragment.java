package com.lonedev.slypanel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by adam on 17/01/15.
 */
public class AboutFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                //View XML
                .setView(R.layout.about_app)
                        //Title
                .setTitle("About")
                        //Right button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Send details
                        AboutFragment.this.getDialog().cancel();
                    }
                });

        return  builder.create();
    }
}
