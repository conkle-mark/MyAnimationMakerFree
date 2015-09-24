package com.bniproductions.android.myanimationmaker;

import java.io.File;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class FrameCell extends View{

	private File imageFile;
	private TextView mTextView;
	
	public FrameCell(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setFocusable(true);
		setClickable(true);
	}
	
	public FrameCell(Context context, AttributeSet attrs, File imFile, TextView tView){
		super(context, attrs);
		imageFile = imFile;
		mTextView = tView;
		this.setFocusable(true);
		this.setClickable(true);
	}
	
	public File getImageFile(){
		return imageFile;
	}
	
	public TextView getTextView(){
		return mTextView;
	}
	
	public void setTextViewText(String text){
		mTextView.setText(text);
	}

	public void setTextViewTextSize(int size){
		mTextView.setHeight(size);
	}
}
