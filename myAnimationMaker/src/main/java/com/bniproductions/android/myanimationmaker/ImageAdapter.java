package com.bniproductions.android.myanimationmaker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageAdapter extends ArrayAdapter<FrameCell>{

	  private final static String TAG = "ImageAdapter";
	  public Context mContext;
	  Bitmap bmp = null;

	  private ArrayList<FrameCell> mFrameCells;

	  	public ImageAdapter(Context c, int res, ArrayList<FrameCell> frameCells){
	  		super(c, res, frameCells);
			  mContext = c;
			  mFrameCells = frameCells;
	  	}

	  	public int getCount() {
			 //return fileList.size();
			 return mFrameCells.size();
	  	}

		@Override
		public FrameCell getItem(int position) {
			return mFrameCells.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 131216 code to return board_views
			View fCell = convertView;
			FCellHolder fCellHolder = null;
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			String frame_no;
			int length = 0;

	        if (convertView == null) {  // if it's not recycled, initialize some attributes

	        	//fCell = new View(mContext);
	        	fCell= (LinearLayout) inflater.inflate(R.layout.board_views, null);
	        	
	        	// Layout params shouldn't be hard coded
	        	fCell.setLayoutParams(new TwoWayGridView.LayoutParams(170, 200));
	        	
	        	fCellHolder = new FCellHolder();
	        	fCellHolder.frame_number = (TextView) fCell.findViewById(R.id.title);
	        	fCellHolder.fImage = (ImageView) fCell.findViewById(R.id.image);
	        	
	        	fCell.setTag(fCellHolder);

	        } else {
	        	fCellHolder = (FCellHolder)fCell.getTag();
	        }
	        
	        frame_no = (String) mFrameCells.get(position).getTextView().getText();
        	length = frame_no.length();
        	frame_no = frame_no.substring(0, length - 4);
        	fCellHolder.frame_number.setText("FRAME "+frame_no);
			fCellHolder.frame_number.setTextColor(Color.WHITE);
	        
        	fCellHolder.fImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        	fCellHolder.fImage.setPadding(16, 16, 16, 16);
        	fCellHolder.fImage.setFocusable(false);
			//System.gc();
        	bmp = BitmapFactory.decodeFile(mFrameCells.get(position).getImageFile().toString());
        	fCellHolder.fImage.setImageBitmap(bmp);

	        return fCell;
		}
		
		private static class FCellHolder {
		    TextView frame_number;
		    ImageView fImage;
		}
}
