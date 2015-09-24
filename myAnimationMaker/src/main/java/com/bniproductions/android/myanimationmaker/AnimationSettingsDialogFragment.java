package com.bniproductions.android.myanimationmaker;

import java.nio.charset.IllegalCharsetNameException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class AnimationSettingsDialogFragment extends DialogFragment {

    private static final String TAG = "SettingsDialogFragment";

    int screen_height;
    int screen_width;
    int rate;
    int length;
    int repeat;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.initialize_animation_params, null);

        final EditText file_name = (EditText) v.findViewById(R.id.file_name);
        final EditText frame_rate = (EditText) v.findViewById(R.id.frame_rate);
        final EditText len_secs = (EditText) v.findViewById(R.id.animation_length_secs);
        final EditText image_height = (EditText) v.findViewById(R.id.image_height);
        final EditText image_width = (EditText) v.findViewById(R.id.image_width);
        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.grid_limited_checkbox);
        final CheckBox repeatBox = (CheckBox) v.findViewById(R.id.repeat_checkbox);

        screen_height = ((FrameSliderActivity) getActivity()).getScreenHeight();
        screen_width = ((FrameSliderActivity) getActivity()).getScreenWidth();

        image_height.setText(Integer.toString(screen_height));
        image_width.setText(Integer.toString(screen_width));

        builder.setMessage(R.string.enter_settings)
                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        if (frame_rate.getText().toString().matches("")
                                || len_secs.getText().toString().matches("")
                                || file_name.getText().toString().matches("")
                                || image_height.getText().toString().matches("")
                                || image_width.getText().toString().matches("")) {
                            ((FrameSliderActivity) getActivity()).tToast("You must enter all fields", 1);
                            return;
                        } else {


                            try {
                                ((FrameSliderActivity) getActivity()).setFileName(file_name.getText().toString());
                            } catch (IllegalCharsetNameException ex) {
                                ((FrameSliderActivity) getActivity()).tToast("You must enter a file name", 1);
                                ((FrameSliderActivity) getActivity()).createProject();
                            }


                            try {
                                ((FrameSliderActivity) getActivity()).setFrameRate(Integer.parseInt(frame_rate.getText().toString()));
                            } catch (NumberFormatException ex) {
                                ((FrameSliderActivity) getActivity()).tToast("You must enter a frame rate", 1);
                                ((FrameSliderActivity) getActivity()).createProject();
                            }


                            try {
                                ((FrameSliderActivity) getActivity()).setAnimationLength(Integer.parseInt(len_secs.getText().toString()));
                            } catch (NumberFormatException ex) {
                                ((FrameSliderActivity) getActivity()).tToast("You must enter an animation length", 1);
                                ((FrameSliderActivity) getActivity()).createProject();
                            }


                            try {
                                ((FrameSliderActivity) getActivity()).setImageSize(Integer.parseInt(image_height.getText().toString()), Integer.parseInt(image_width.getText().toString()));
                            } catch (NumberFormatException ex) {
                                ((FrameSliderActivity) getActivity()).tToast("You must enter valid size parameters", 1);
                                ((FrameSliderActivity) getActivity()).createProject();
                            }

                            if (!checkBox.isChecked()) {

                                rate = Integer.parseInt(frame_rate.getText().toString());
                                length = Integer.parseInt(len_secs.getText().toString());
                                if (rate * length > 20) {
                                    ((FrameSliderActivity) getActivity()).tToast("Too many frames, frame rate times animation lenth must be less that 20!", 1);
                                    ((FrameSliderActivity) getActivity()).createProject();
                                } else {
                                    ((FrameSliderActivity) getActivity()).checkDirectory(file_name.getText().toString());
                                }
                            } else {
                                ((FrameSliderActivity) getActivity()).checkDirectory(file_name.getText().toString());
                            }

                            if(repeatBox.isChecked()){
                                ((FrameSliderActivity) getActivity()).setRepeat(true);
                            }else{
                                ((FrameSliderActivity) getActivity()).setRepeat(false);
                            }

                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.initialize_animation_params, null);

        final EditText file_name = (EditText) v.findViewById(R.id.file_name);
        final EditText frame_rate = (EditText) v.findViewById(R.id.frame_rate);
        final EditText len_secs = (EditText) v.findViewById(R.id.animation_length_secs);

        if (file_name.equals("")) {
            ((FrameSliderActivity) getActivity()).tToast("You must enter a file name", 1);
            file_name.setError("You must enter a file name");
            show();
        } else if (frame_rate.equals("")) {
            ((FrameSliderActivity) getActivity()).tToast("You must enter a frame rate", 1);
            frame_rate.setError("You must enter a frame rate");
            show();
        } else if (len_secs.equals("")) {
            ((FrameSliderActivity) getActivity()).tToast("You must enter an animation length", 1);
            len_secs.setError("You must enter an animation length");
            show();
        }

        super.dismiss();
    }


    public void show() {
        this.onCreate(getArguments());
    }
}
