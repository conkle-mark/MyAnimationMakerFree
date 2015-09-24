package com.bniproductions.android.myanimationmaker;

import java.util.Locale;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ColorPickerDialogFragment extends DialogFragment implements ColorPickerView.OnColorChangedListener, View.OnClickListener{
	
    private ColorPickerView mColorPicker;

    //private ColorPickerPanelView mOldColor;
    private ColorPickerPanelView mNewColor;
    private EditText mHexVal;
    private boolean mHexValueEnabled = true;
    private ColorStateList mHexDefaultTextColor;
    private Button doneButton;

    private OnColorChangedListener mListener;
    
    String DTAG = "ColorPickerDialogFragment";
	
	public ColorPickerDialogFragment(){
		// Empty constructor required for DialogFragment
	}
	
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static ColorPickerDialogFragment newInstance(int num) {
    	ColorPickerDialogFragment f = new ColorPickerDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
	
    public interface OnColorChangedListener {
        public void onColorChanged(int color);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	getDialog().setTitle("Color Picker");
    	// R.layout.dialog_color_picker is the custom layout of my dialog
    	View layout = inflater.inflate(R.layout.dialog_color_picker, container);
    	   	
    	mColorPicker = (ColorPickerView) layout.findViewById(R.id.color_picker_view);
        //mOldColor = (ColorPickerPanelView) layout.findViewById(R.id.old_color_panel);
        mNewColor = (ColorPickerPanelView) layout.findViewById(R.id.new_color_panel);
        
        mColorPicker.setAlphaSliderVisible(true);
        
        mHexVal = (EditText) layout.findViewById(R.id.hex_val);
        mHexVal.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mHexDefaultTextColor = mHexVal.getTextColors();
        //mOldColor.setColor(getArguments().getInt("num"));
        doneButton = (Button) layout.findViewById(R.id.color_picker_done_button);

        mHexVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 5 || s.length() < 10) {
                    try {
                        int c = ColorPickerPreference.convertToColorInt(s.toString());
                        mColorPicker.setColor(c, true);
                        mNewColor.setColor(c);
                        mHexVal.setTextColor(mHexDefaultTextColor);
                    } catch (IllegalArgumentException e) {
                        mHexVal.setTextColor(Color.RED);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    /*
        ((LinearLayout) mOldColor.getParent()).setPadding(
                Math.round(mColorPicker.getDrawingOffset()),
                0,
                Math.round(mColorPicker.getDrawingOffset()),
                0
        );
    
        mOldColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mNewColor.setColor(mColorPicker.getColor());
                mHexVal.setText(ColorPickerPreference.convertToARGB(mColorPicker.getColor()));

            }
        });
        */

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tToast("new color");
                mNewColor.setColor(mColorPicker.getColor());
                mHexVal.setText(ColorPickerPreference.convertToARGB(mColorPicker.getColor()));
                DrawFrameActivity callingActivity = (DrawFrameActivity) getActivity();
                callingActivity.onUserSelectedColor(mColorPicker.getColor());
                getDialog().dismiss();
            }
        });
        mColorPicker.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
        	    // Does something cool
        		// tToast("new color");
        		mNewColor.setColor(mColorPicker.getColor());
        		mHexVal.setText(ColorPickerPreference.convertToARGB(mColorPicker.getColor()));
        		//mHexVal.setTextColor(mColorPicker.getColor());
				return false;
			}
        });
        
        mColorPicker.setOnColorChangedListener((com.bniproductions.android.myanimationmaker.ColorPickerView.OnColorChangedListener) mListener);
    	
    	getDialog().getWindow().setFormat(PixelFormat.RGBA_8888);
    	//Log.d(DTAG, "dialog_color_picker:height: "+layout.getHeight());
    	
    	WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
    	wmlp.gravity = Gravity.RIGHT;
    	// layout.set
    	return layout;
    }
    
    @Override
    public void onColorChanged(int color) {

    	mNewColor.setColor(color);
    	mHexVal.setText(Integer.toString(color));
            if (mHexValueEnabled)
                    updateHexValue(color);
    }
    
    private void updateHexValue(int color) {
        if (getAlphaSliderVisible()) {
                mHexVal.setText(ColorPickerPreference.convertToARGB(color).toUpperCase(Locale.getDefault()));
        } else {
                mHexVal.setText(ColorPickerPreference.convertToRGB(color).toUpperCase(Locale.getDefault()));
        }
        mHexVal.setTextColor(mHexDefaultTextColor);
    }
    
    public void setAlphaSliderVisible(boolean visible) {
        mColorPicker.setAlphaSliderVisible(visible);
        if (mHexValueEnabled) {
                updateHexLengthFilter();
                updateHexValue(getColor());
        }
    }

    public boolean getAlphaSliderVisible() {
        return mColorPicker.getAlphaSliderVisible();
    }

    public boolean getHexValueEnabled() {
    	return mHexValueEnabled;
    }

    private void updateHexLengthFilter() {
    	if (getAlphaSliderVisible())
            	mHexVal.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)});
    	else
            	mHexVal.setFilters(new InputFilter[] {new InputFilter.LengthFilter(7)});
    }

    public int getColor() {
    	return mColorPicker.getColor();
    }
    
    @Override
    public void onClick(View v) {
    	Log.d(DTAG, "onClick:unknown view onClick");
            if (v.getId() == R.id.color_picker_view) {
            	Log.d(DTAG, "onClick:color_picker_view");
                    if (mListener != null) {
                    	Log.d(DTAG, "onClick:mListener != null");
                            mListener.onColorChanged(mColorPicker.getColor());
                            mNewColor.setColor(mColorPicker.getColor());
                            mHexVal.setText(Integer.toString(mColorPicker.getColor()));
                            //mHexVal.setTextColor(mColorPicker.getColor());
                    }
            }
            dismiss();
    }
    
	protected void tToast(String s) {
        // Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getActivity(), s, duration);
        toast.show();
    }
  
	protected void tToast(String s, int time) {
        // Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getActivity(), s, duration);
        toast.show();
    }
}
