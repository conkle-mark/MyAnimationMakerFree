package com.bniproductions.android.myanimationmaker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityAnimationSettings extends AppCompatActivity {

    private static final String TAG = "ActivityAnimationSetting";
    private Toolbar toolbar;
    private CheckBox twentyFramesCheckBox;
    private CheckBox allowRepeatCheckBox;
    private EditText fileNameEditText;
    private EditText frameRateEditText;
    private EditText lengthEditText;
    private EditText heightEditText;
    private EditText widthEditText;
    private Button cancelButton;
    private Button createButton;
    private Intent intent;
    private int screen_height;
    private int screen_width;
    private int rowHeight;
    private int thumbRowHeight;

    private boolean allowOverTwentyFrames = false;
    private boolean allowRepeat = false;
    private String fileName;
    private int frameRate = -1;
    private int animationLength = -1;
    private int imageHeight = -1;
    private int imageWidth = -1;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_settings);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.myanimationicon);

        setScreenDims();

        twentyFramesCheckBox = (CheckBox) findViewById(R.id.grid_limited_checkbox);
        allowRepeatCheckBox = (CheckBox) findViewById(R.id.repeat);
        fileNameEditText = (EditText) findViewById(R.id.file_name);
        frameRateEditText = (EditText) findViewById(R.id.frame_rate);
        lengthEditText = (EditText) findViewById(R.id.animation_length_secs);
        heightEditText = (EditText) findViewById(R.id.image_height);
        widthEditText = (EditText) findViewById(R.id.image_width);
        cancelButton = (Button) findViewById(R.id.cancel);
        createButton = (Button) findViewById(R.id.create);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(twentyFramesCheckBox.isChecked()){
                    allowOverTwentyFrames = true;
                }
                if(allowRepeatCheckBox.isChecked()){
                    allowRepeat = true;
                }

                fileName = fileNameEditText.getText().toString();
                if(!frameRateEditText.getText().toString().matches("")){
                    frameRate = Integer.valueOf(frameRateEditText.getText().toString());
                }
                if(!lengthEditText.getText().toString().matches("")){
                    animationLength = Integer.valueOf(lengthEditText.getText().toString());
                }
                if(!heightEditText.getText().toString().matches("")){
                    imageHeight = Integer.valueOf(heightEditText.getText().toString());
                }
                if(!lengthEditText.getText().toString().matches("")){
                    imageWidth = Integer.valueOf(widthEditText.getText().toString());
                }
                if(getResources().getConfiguration().orientation == 1){// portrait
                    int tempHeight = imageHeight;
                    imageHeight = imageWidth;
                    imageWidth = tempHeight;
                }


                if(frameRate != -1 && animationLength != -1 && imageHeight != -1 && imageWidth != -1 && fileName != null){
                    hideSoftKeyBoard();
                    intent = new Intent(v.getContext(), FrameSliderActivity.class);
                    intent.putExtra("allow_20", allowOverTwentyFrames);
                    intent.putExtra("repeat", allowRepeat);
                    intent.putExtra("file_name", fileName);
                    intent.putExtra("frame_rate", frameRate);
                    intent.putExtra("animation_length", animationLength);
                    intent.putExtra("image_height", imageHeight);
                    intent.putExtra("image_width", imageWidth);
                    intent.putExtra("position", position);
                    startActivity(intent);
                    finish();
                }else if(frameRate == -1){
                    tToast("Frame Rate was never set");
                }if(animationLength == -1){
                    tToast("Animation length was never set");
                }if(imageHeight == -1){
                    tToast("Animation Height was empty");
                }if(imageWidth == -1){
                    tToast("Animation Width was empty");
                }if(fileName == null){
                    tToast("Animation name was empty");
                }
            }
        });

        if(screen_width > 0 && screen_height > 0){
            setDimensionHints();
        }
    }

    private void hideSoftKeyBoard(){
        //Log.d(TAG, "getCurrentFocus: "+getCurrentFocus());
        if(getCurrentFocus() != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }



    private void setScreenDims() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_height = size.y;
        screen_width = size.x;
        rowHeight = screen_height;
        thumbRowHeight = rowHeight/3;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
    }

    private void setDimensionHints(){
        heightEditText.setText("" + screen_height);
        widthEditText.setText("" + screen_width);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed");
    }

    public void tToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }
}
