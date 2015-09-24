package com.bniproductions.android.myanimationmaker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class ColorsUsedButtonAdapter extends ArrayAdapter<ColorsUsedButton>{

	private int layoutResourceId;
	private Context context;
	private ArrayList<ColorsUsedButton> buttons;
	private String DTAG = "ColorsUsedButtonAdapter";

	public ColorsUsedButtonAdapter(Context context, int buttonViewResourceId,
			ArrayList<ColorsUsedButton> objects) {
		super(context, buttonViewResourceId, objects);
		
        layoutResourceId = buttonViewResourceId;
        this.context = context;
        buttons = objects;
	}
	
	 @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        ButtonHolder button = null;
	        if(row == null)
	        {
	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            row = inflater.inflate(layoutResourceId, parent, false);
	            
	            button = new ButtonHolder();
	            button.my_button = (Button)row.findViewById(R.id.used_color_button);
	            
	            row.setTag(button);
	        }
	        else
	        {
	            button = (ButtonHolder)row.getTag();
	        }

	        ColorsUsedButton the_button = buttons.get(position); // ArrayList of Buttons
	        button.my_button.setBackgroundColor(the_button.getColor()) ;
	        button.my_button.setText(the_button.getHexString());
	        button.my_button.setFocusable(false);
	        button.my_button.setClickable(false);
	        button.my_button.setFocusableInTouchMode(false);
	        return row;
	    }
	 
	 // The holder pattern allows access to the held objects attributes
	 static class ButtonHolder
	    {
	        Button my_button;
	    }
}
