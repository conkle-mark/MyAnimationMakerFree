package com.bniproductions.android.myanimationmaker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class LineTypeDialogFragment extends DialogFragment {
	
	ImageButton solidLine;
	ImageButton airbrush;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	getDialog().setTitle("Select Line type");
        // Get the layout inflater
        // LayoutInflater mInflater = getActivity().getLayoutInflater();

        View layout = inflater.inflate(R.layout.line_type_dialog_fragment, null);
        
        solidLine = (ImageButton) layout.findViewById(R.id.solid);
        airbrush = (ImageButton) layout.findViewById(R.id.airbrush);
        
        solidLine.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DrawFrameActivity callingActivity = (DrawFrameActivity) getActivity();
				callingActivity.doLineTypeClick(1);
				getDialog().dismiss();
			}
        	
        });
        
        airbrush.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				DrawFrameActivity callingActivity = (DrawFrameActivity) getActivity();
				callingActivity.doLineTypeClick(2);
				getDialog().dismiss();
			}
        	
        });

        return layout;
    }
}
