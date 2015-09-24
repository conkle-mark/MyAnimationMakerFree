package com.bniproductions.android.myanimationmaker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class GifPlayerActivity extends Activity{
	private String path;
	private String DTAG = "GifPlayerActivity";
	
	GifView gifView;
	
	private int gifWidth;
	private int gifHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		//Log.d(DTAG, "maxMemory: "+maxMemory);

		setContentView(R.layout.sample_gif_view);
		
		gifView = (GifView) findViewById(R.id.gifView);
		
		Intent intent = getIntent();
		path = intent.getStringExtra("animPath");
		
		gifView.setGif(path);
		
		init();
		
		gifView.play();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gif_player_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.close_player:
			{
				returnToFrameSlider();
				return true;
			}
			case R.id.stop_player:
			{
				gifView.pause();
				return true;
			}
			case R.id.start_player:
			{
				gifView.play();
				return true;
			}
		}
		return false;
	}
	
	private boolean init(){
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();		

		display.getSize(size);
		
		gifWidth = gifView.getGifWidth();
		gifHeight = gifView.getGifHeight();

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(gifWidth, gifHeight);
		
		layoutParams.gravity = Gravity.CENTER;
		gifView.setLayoutParams(layoutParams);
		
		return false;
	}
	
	private void returnToFrameSlider(){
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		System.gc();
		finish();
	}

	public void onGifClick(View v) {
		GifView gif = (GifView) v;
		
		if(gif.getPlayFlag()){
			gif.pause();
		}else{
			gif.play();
		}
	}
}
