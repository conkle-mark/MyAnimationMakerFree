package com.bniproductions.android.myanimationmaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class OpenAnimationDialogFragment extends DialogFragment {

	private static final String DTAG = "OpenAnimationDF";
	ArrayList<String> projectArrayList = new ArrayList<String>();

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        View v = inflater.inflate(R.layout.open_animation_dialog_fragment, null);

		Bundle args = getArguments();

		projectArrayList = args.getStringArrayList("projectNames");

		final Spinner fileNames = (Spinner)v.findViewById(R.id.project_list);

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getActivity(),
				R.layout.spinner_item,
				projectArrayList );

		fileNames.setAdapter(arrayAdapter);

		projectArrayList = getArguments().getStringArrayList("projectNames");

		builder.setMessage(R.string.open_file_message)
 	   		   	.setView(v)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (!projectArrayList.isEmpty()) {
							((FrameSliderActivity) getActivity()).setTitle(fileNames.getSelectedItem().toString());
							((FrameSliderActivity) getActivity()).openAnimation(fileNames.getSelectedItem().toString());
						}
					}
				})
		        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {

		                   }
		        });
				
		return builder.create();
			
	}

}
