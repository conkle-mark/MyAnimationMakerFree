package com.bniproductions.android.myanimationmaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Mark on 10/16/2015.
 */
public class CreateProjectDialogFragment extends DialogFragment {

    TextView fileName;
    String file_name;
    TextView title;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.write_directory, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_Sphinx_Dialog_Alert);
        AlertDialog dialog = builder.show();

        fileName = (TextView) v.findViewById(R.id.write_directory);
        file_name = getArguments().getString("file_name");
        fileName.setText("Project Name: " + file_name);

        title = (TextView) v.findViewById(R.id.title_view);

        builder.setCustomTitle(title)

                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            ((FrameSliderActivity) getActivity()).write();
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
