package com.bniproductions.android.myanimationmaker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * TODO: document your custom view class.
 */
public class DrawBoardView extends View {
	private String mExampleString; // TODO: use a default from R.string...
	private int mExampleColor = Color.RED; // TODO: use a default from
											// R.color...
	private float mExampleDimension = 0; // TODO: use a default from R.dimen...
	private Drawable mExampleDrawable;

	private TextPaint mTextPaint;
	private float mTextWidth;
	private float mTextHeight;
	
	private Paint brush = new Paint();
    private Path path = new Path();
    public Button btnEraseAll;
    public LayoutParams params;
    
	private int paddingLeft = getPaddingLeft();
	private int paddingTop = getPaddingTop();
	private int paddingRight = getPaddingRight();
	private int paddingBottom = getPaddingBottom();

	private int contentWidth = getWidth() - paddingLeft - paddingRight;
	private int contentHeight = getHeight() - paddingTop - paddingBottom;
	
	private int ycoord;
	private int xcoord;
	
	Rect mRect;
	
	//Bitmap canvasBitmap;
	BitmapDrawable bmDrawable;
	Canvas drawCanvas;
	
    int viewWidth = 0;
    int viewHeight = 0;

	public DrawBoardView(Context context) {
		super(context);
		if(!isInEditMode()){ // for debugging
		brush.setAntiAlias(true);
        brush.setColor(Color.BLUE);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(10f);
        btnEraseAll=new Button(context);
        btnEraseAll.setText("Erase Everything!!");
        params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        btnEraseAll.setLayoutParams(params);
        
        btnEraseAll.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                //reset the path
                path.reset();
                //invalidate the view
                postInvalidate();
                
            }
        });
		init(null, 0);
		}
	}

	public DrawBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()){ // for debugging
		brush.setAntiAlias(true);
        brush.setColor(Color.BLACK);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(10f);
        btnEraseAll=new Button(context);
        btnEraseAll.setText("Erase Everything!!");
        params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        btnEraseAll.setLayoutParams(params);
        btnEraseAll.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                //reset the path
                path.reset();
                //invalidate the view
                postInvalidate();
                
            }
        });
		init(attrs, 0);
		}
	}


	public DrawBoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if(!isInEditMode()){
		brush.setAntiAlias(true);
        brush.setColor(Color.BLACK);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(10f);
        btnEraseAll=new Button(context);
        btnEraseAll.setText("Erase Everything!!");
        params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        btnEraseAll.setLayoutParams(params);
        btnEraseAll.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                //reset the path
                path.reset();
                //invalidate the view
                postInvalidate();
                
            }
        });
		init(attrs, defStyle);
		}
	}
	
	
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	   int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	   int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	   this.setMeasuredDimension(parentWidth, parentHeight);
	   this.setLayoutParams(new FrameLayout.LayoutParams(parentWidth,parentHeight));
	   super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	
    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
            super.onSizeChanged(xNew, yNew, xOld, yOld);
            viewWidth = xNew;
            viewHeight = yNew;
            /*
            these viewWidth and viewHeight variables
            are the global int variables
            that were declared above
            */
    }
	/*
	public boolean setCanvasBitmap(Bitmap bitmap) {
        canvasBitmap = bitmap;
        this.getLayoutParams();
        
        ycoord = getHeight()/2 - canvasBitmap.getHeight()/2;
        xcoord = getWidth()/2 - canvasBitmap.getWidth()/2;
        if(ycoord < 0){
        	Log.d("ycoord", " < 0");
        	Log.d("DrawBoardView", "getHeight of view: "+getHeight());
        	Log.d("DrawBoardView", "canvasBitmap.getHeight of view: "+canvasBitmap.getHeight());
        	ycoord = ycoord*-1;
        }
        if(xcoord < 0){
        	Log.d("xcoord", " < 0");
        	Log.d("DrawBoardView", "getWidth of view: "+getWidth());
        	Log.d("DrawBoardView", "canvasBitmap.getWidth of view: "+canvasBitmap.getWidth());
        	xcoord = xcoord*-1;
        }
        //drawCanvas = new Canvas(canvasBitmap);
        mRect = new Rect(xcoord, ycoord, canvasBitmap.getWidth()+xcoord, canvasBitmap.getHeight()+ycoord);
        bmDrawable = new BitmapDrawable(getResources(), canvasBitmap);
        bmDrawable.setBounds(mRect);
        invalidate();
        if(canvasBitmap == null){
        	return false;
        }
        return true;
    }
	*/

	private void init(AttributeSet attrs, int defStyle) {
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.DrawBoardView, defStyle, 0);

		mExampleString = a.getString(R.styleable.DrawBoardView_exampleString);
		mExampleColor = a.getColor(R.styleable.DrawBoardView_exampleColor,
				mExampleColor);
		// Use getDimensionPixelSize or getDimensionPixelOffset when dealing
		// with
		// values that should fall on pixel boundaries.
		mExampleDimension = a.getDimension(
				R.styleable.DrawBoardView_exampleDimension, mExampleDimension);

		if (a.hasValue(R.styleable.DrawBoardView_exampleDrawable)) {
			mExampleDrawable = a
					.getDrawable(R.styleable.DrawBoardView_exampleDrawable);
			mExampleDrawable.setCallback(this);
		}

		a.recycle();

		// Set up a default TextPaint object
		mTextPaint = new TextPaint();
		mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Paint.Align.LEFT);

		// Update TextPaint and text measurements from attributes
		invalidateTextPaintAndMeasurements();
	}
	
	private void invalidateTextPaintAndMeasurements() {
		mTextPaint.setTextSize(mExampleDimension);
		mTextPaint.setColor(mExampleColor);
		mTextWidth = mTextPaint.measureText(mExampleString);

		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		mTextHeight = fontMetrics.bottom;
	}
	
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		 	float pointX = event.getX();
	        float pointY = event.getY();
	        // Checks for the event that occurs
	        switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	            path.moveTo(pointX, pointY);
	            return true;
	        case MotionEvent.ACTION_MOVE:
	            path.lineTo(pointX, pointY);
	            break;
	        default:
	            return false;
	        }       
	         // Force a view to draw again
	        postInvalidate();
        return false;
    
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();

        bmDrawable.draw(canvas);

		canvas.drawPath(path, brush);
		canvas.restore();
	}

	/**
	 * Gets the example string attribute value.
	 * 
	 * @return The example string attribute value.
	 */
	public String getExampleString() {
		return mExampleString;
	}

	/**
	 * Sets the view's example string attribute value. In the example view, this
	 * string is the text to draw.
	 * 
	 * @param exampleString
	 *            The example string attribute value to use.
	 */
	public void setExampleString(String exampleString) {
		mExampleString = exampleString;
		invalidateTextPaintAndMeasurements();
	}

	/**
	 * Gets the example color attribute value.
	 * 
	 * @return The example color attribute value.
	 */
	public int getExampleColor() {
		return mExampleColor;
	}

	/**
	 * Sets the view's example color attribute value. In the example view, this
	 * color is the font color.
	 * 
	 * @param exampleColor
	 *            The example color attribute value to use.
	 */
	public void setExampleColor(int exampleColor) {
		mExampleColor = exampleColor;
		invalidateTextPaintAndMeasurements();
	}

	/**
	 * Gets the example dimension attribute value.
	 * 
	 * @return The example dimension attribute value.
	 */
	public float getExampleDimension() {
		return mExampleDimension;
	}

	/**
	 * Sets the view's example dimension attribute value. In the example view,
	 * this dimension is the font size.
	 * 
	 * @param exampleDimension
	 *            The example dimension attribute value to use.
	 */
	public void setExampleDimension(float exampleDimension) {
		mExampleDimension = exampleDimension;
		invalidateTextPaintAndMeasurements();
	}

	/**
	 * Gets the example drawable attribute value.
	 * 
	 * @return The example drawable attribute value.
	 */
	public Drawable getExampleDrawable() {
		return mExampleDrawable;
	}

	/**
	 * Sets the view's example drawable attribute value. In the example view,
	 * this drawable is drawn above the text.
	 * 
	 * @param exampleDrawable
	 *            The example drawable attribute value to use.
	 */
	public void setExampleDrawable(Drawable exampleDrawable) {
		mExampleDrawable = exampleDrawable;
	}
	
	public void resetPath(){
		path.reset();
	}
}
