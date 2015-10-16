package com.bniproductions.android.myanimationmaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class OverWriteDirectoryDialogFragment extends DialogFragment {


    private static final String TAG = "OverWriteDialog";
    private String file_name;
    private TextView fileName;
    private TextView title;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.over_write_directory, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_Sphinx_Dialog_Alert);
        AlertDialog dialog = builder.show();



        // Get the layout inflater
        fileName = (TextView) v.findViewById(R.id.directory);
        file_name = getArguments().getString("file_name");
        fileName.setText("Project Name: " + file_name);

        title = (TextView) v.findViewById(R.id.title_view);

        final int overwrite = 1;

        builder.setCustomTitle(title)
        //builder.setMessage("OverWrite?")
                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            ((FrameSliderActivity) getActivity()).deleteDirectory();
                            dismiss();

                        } catch (NumberFormatException ex) {
                            ((FrameSliderActivity) getActivity()).tToast("Something has gone horribly Wrong!");
                            //((FrameSliderActivity) getActivity()).createProject();
                        }
                    }

                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        getActivity().finish();

                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        Button pButton = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
        Button nButton = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_NEGATIVE);

        pButton.setBackgroundColor(getResources().getColor(R.color.primaryCyanDarker));
        nButton.setBackgroundColor(getResources().getColor(R.color.primaryCyanDarker));
        pButton.setTextColor(getResources().getColor(R.color.primaryCyan));
        nButton.setTextColor(getResources().getColor(R.color.primaryCyan));
    }

}
