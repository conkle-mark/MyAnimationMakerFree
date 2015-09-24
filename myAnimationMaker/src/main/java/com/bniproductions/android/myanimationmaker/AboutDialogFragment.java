package com.bniproductions.android.myanimationmaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class AboutDialogFragment extends DialogFragment {

    private static final String TAG = "AboutDialogFragment";

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        View v = inflater.inflate(R.layout.about_my_animation_maker, null);
        
        //final TextView about= (EditText)v.findViewById(R.id.about_my_animation_maker);
        builder.setOnKeyListener( new DialogInterface.OnKeyListener()
        {

            @Override
            public boolean onKey ( DialogInterface dialog , int keyCode , KeyEvent event ) {
                // disable search button action
                Log.d(TAG, "event: "+event.toString());
                return false;
            }
        });


        builder.setMessage(R.string.enter_settings)
        	   .setView(v)
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {

                   }                  
               });
        
        return builder.create();		
	}

}
