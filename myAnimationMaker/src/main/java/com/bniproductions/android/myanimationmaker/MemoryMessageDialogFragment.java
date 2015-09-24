package com.bniproductions.android.myanimationmaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Mark on 4/24/2015.
 */
public class MemoryMessageDialogFragment extends DialogFragment {

    private static final String DTAG = "MemoryMessageDialogFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.memory_message, null);

        builder.setMessage(R.string.memory_dialog_message)
                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FrameSliderActivity activity = (FrameSliderActivity)getActivity();
                        activity.launchAnimation();

                    }
                });

        return builder.create();
    }
}
