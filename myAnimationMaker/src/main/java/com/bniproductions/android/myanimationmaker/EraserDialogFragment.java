package com.bniproductions.android.myanimationmaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Mark on 5/29/2015.
 */
public class EraserDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // builder.setView(inflater.inflate(R.layout.save_as, null));
        View v= inflater.inflate(R.layout.eraser_dialog_fragment, null);

        // saveAsEditText = (EditText) findViewById(R.id.editTextDialogUserSaveAs);
        final EditText ed= (EditText)v.findViewById(R.id.edittext_eraser_width);

        builder.setMessage(R.string.enter_eraser_width)
                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((DrawFrameActivity)getActivity()).eraserClick(ed.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
