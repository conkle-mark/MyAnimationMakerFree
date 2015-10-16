package com.bniproductions.android.myanimationmaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Mark on 10/9/2015.
 */
public class OnBackPressedDialogFragment extends DialogFragment {

    TextView title;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.on_back_pressed, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_Sphinx_Dialog_Alert);
        AlertDialog dialog = builder.show();

        title = (TextView) v.findViewById(R.id.title_view);

        builder.setCustomTitle(title)
                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            (getActivity()).finish();
                            dismiss();
                        }catch (NumberFormatException ex){
                            dismiss();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
