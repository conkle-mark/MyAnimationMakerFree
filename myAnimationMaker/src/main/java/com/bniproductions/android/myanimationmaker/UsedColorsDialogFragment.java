package com.bniproductions.android.myanimationmaker;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;


public class UsedColorsDialogFragment extends DialogFragment{
	
	Button solidLine;
	Button airbrush;
	//private int dialogWidth;
	ArrayList<Integer> colorsUsed;
	String DTAG = "UsedColorsDialogFragment";
	
	
	public UsedColorsDialogFragment(){
		// Empty constructor required for DialogFragment
	}
	
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static UsedColorsDialogFragment newInstance(int num) {
    	UsedColorsDialogFragment f = new UsedColorsDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	getDialog().setTitle("Colors Used");

        View color_used_layout = inflater.inflate(R.layout.colors_used_dialog_fragment, null);
        ArrayList<ColorsUsedButton> colorsUsedButtons = new ArrayList<ColorsUsedButton>();
        Context mContext = getActivity();
        
        final ListView colors_used = (ListView)color_used_layout.findViewById(R.id.colors_used);
        
        final DrawFrameActivity drawBoard = (DrawFrameActivity) getActivity();
        
        colorsUsed = new ArrayList<Integer>(drawBoard.usedColors);
        Log.d(DTAG, "colorsUsed.size: "+colorsUsed.size());
        for(int i = 0; i < colorsUsed.size(); i++){
        	colorsUsedButtons.add(new ColorsUsedButton(colorsUsed.get(i)));
        	Log.d(DTAG, "onCreateView: colorsUsed.get(i) "+colorsUsed.get(i));
        }
        
        ColorsUsedButtonAdapter adapter = new ColorsUsedButtonAdapter(mContext, R.layout.list_button, colorsUsedButtons);
              
        colors_used.setAdapter(adapter);
        
        colors_used.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View row, int position,
					long arg3) {
				
				ColorsUsedButton item = (ColorsUsedButton) arg0.getItemAtPosition(position);
				drawBoard.onUserSelectedColor(item.getColor());
				dismiss();
			}
       	
        });      
        return color_used_layout;
    }
}
