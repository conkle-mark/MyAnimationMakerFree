package com.bniproductions.android.myanimationmaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class OverWriteDirectoryDialogFragment extends DialogFragment{

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        View v= inflater.inflate(R.layout.over_write_directory, null);
        
        final int overwrite = 1;
		
		builder.setMessage(R.string.enter_settings)
  	   		.setView(v)
  	   		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
          	   try{
          		   ((FrameSliderActivity)getActivity()).deleteDirectory(overwrite);
          	   }catch(NumberFormatException ex){
          		   ((FrameSliderActivity)getActivity()).tToast("Something has gone horribly Wrong!");
          		   ((FrameSliderActivity)getActivity()).createProject();
          	   }
             }
             
         })
         .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
                 // User cancelled the dialog
          	   // ((FrameSliderActivity)getActivity()).init();
             }
         });
		 
		return builder.create();
	}	
}
