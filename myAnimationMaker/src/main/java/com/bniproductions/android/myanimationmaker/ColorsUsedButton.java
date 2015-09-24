package com.bniproductions.android.myanimationmaker;

public class ColorsUsedButton{
	//The object which represents a view (a row in the ListView) :
	int used_color;
	String hex_name;
	String DTAG = "ColorsUsedButton";
	
	public ColorsUsedButton(){
		super();
	}

	public ColorsUsedButton(int color) {
		super();
		used_color = color;
		hex_name = ColorPickerPreference.convertToARGB(used_color);
	}
	
	public int getColor(){
		return used_color;
	}
	
	public String getHexString(){
		return hex_name;
	}
}
