package com.bniproductions.android.myanimationmaker;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class EditBoardTwoView extends View {

    enum LineType {
        AIR,
        SOLID
    }

    private int mColor = 000000;
    private Drawable mDrawable;
    private float mWidth = 4;
    private Bitmap mSourceBM;
    private BitmapDrawable bmDrawable;

    private Canvas mCanvas;
    private Path mPath = new Path();
    public Paint mPaint = new Paint();
    //private ArrayList<PathPaintBrush> ppb;

    // airbrush variables
    float mPreviousX;
    float mPreviousY;

    private ArrayList<Pair<Path, Paint>> pathspaints = new ArrayList<Pair<Path, Paint>>();
    //private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private ArrayList<Pair<Path, Paint>> undonePaths = new ArrayList<Pair<Path, Paint>>();
    private ArrayList<PathBrush> pathBrushes = new ArrayList<PathBrush>();
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private ArrayList<ShapeDrawable> mBrushes = new ArrayList<ShapeDrawable>();
    private ArrayList<ArrayList<ShapeDrawable>> mBrushesList = new ArrayList<ArrayList<ShapeDrawable>>();

    private ArrayList<ArrayList<ShapeDrawable>> undoneBrushesList = new ArrayList<ArrayList<ShapeDrawable>>();
    private ArrayList<LineType> currentLineType = new ArrayList<LineType>();
    private ArrayList<LineType> undoneLineType = new ArrayList<LineType>();
    private ArrayList<Point> splashPoints = new ArrayList<Point>();

    DrawFrameActivity drawFrameActivity;
    /*
     * debugging vars
     */
    private String DTAG = "EditBoardTwoView";

    /*
     * line types
     */
    private int line_type = SOLID_LINE;
    private static int AIRBRUSH = 2;
    private static int SOLID_LINE = 1;
    private Shape brushShape;
    private ShapeDrawable mBrushDrawable;
    //private Paint airbrush = new Paint();
    private int strokeRadius;

    int steps;
    float stepSize;
    float segmentLength;
    float ydist;
    float xdist;

    int currentX;
    int currentY;

    public EditBoardTwoView(Context context) {
        super(context);
        init(null, 0);
    }

    public EditBoardTwoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EditBoardTwoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //mCanvas = new Canvas();
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        if (line_type == SOLID_LINE) {
            setSolidLine();
        } else if (line_type == AIRBRUSH) {
            setAirbrush();
        }
        drawFrameActivity = (DrawFrameActivity) this.getContext();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (line_type == SOLID_LINE) {

            for (Pair<Path, Paint> p : pathspaints) {

                canvas.drawPath(p.first, p.second);
            }

            canvas.drawPath(mPath, mPaint);
        } else if (line_type == AIRBRUSH) {


            xdist = mX - mPreviousX;
            ydist = mY - mPreviousY;

            // get the length
            segmentLength = (float) Math.sqrt(xdist * xdist + ydist * ydist);
            // derive a suitable step size from stroke width
            //stepSize = Math.max(strokeRadius / 10, 1f);
            // calculate the number of steps we need to take
            // NOTE: this draws a bunch of evenly spaced splashes from the start point
            // to JUST BEFORE the end point. The end point will be drawn by the start point of the
            // next stroke, or by the touch_up method. If we drew both the start and
            // end point there it would be doubled up
            //steps = Math.max(Math.round(segmentLength / stepSize), 2);

            for (Point p : splashPoints) {

                mBrushDrawable.setBounds(p.x - strokeRadius, p.y - strokeRadius, p.x + strokeRadius, p.y + strokeRadius);
                mBrushDrawable.draw(canvas);
                mBrushes.add(mBrushDrawable);

            }
            mPreviousX = mX;
            mPreviousY = mY;

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * draws the brush to the canvas, centered around x and y
     * <p/>
     * for airbrush
     */
    private void drawSplash(int x, int y, Canvas c) {
        //mBrushDrawable.setBounds(x - strokeRadius, y - strokeRadius, x + strokeRadius, y + strokeRadius);
        //Log.d(DTAG, "drawSplash - x: "+x+" y: "+y);
        //mBrushes.add(mBrushDrawable);
        //mBrushDrawable.draw(c);//mCanvas
        //setAirbrush();
    }

    private void drawSplash(int x, int y) {
        //mBrushDrawable.setBounds(x - strokeRadius, y - strokeRadius, x + strokeRadius, y + strokeRadius);

        //mBrushes.add(mBrushDrawable);
        //mBrushDrawable.draw(mCanvas);//mCanvas
        //setAirbrush();
    }

    private void touch_start(float x, float y) {
        if (line_type == SOLID_LINE) {

            currentLineType.add(LineType.SOLID);

            undonePaths.clear();// we clear the undonePaths because we are drawing
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        } else if (line_type == AIRBRUSH) {
            //mPath.reset();
            currentLineType.add(LineType.AIR);

            mPreviousX = x;
            mPreviousY = y;

            undonePaths.clear();// we clear the undonePaths because we are drawing

            mX = x;
            mY = y;
        }
    }

    private void touch_move(float x, float y) {
        if (line_type == SOLID_LINE) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            // if change in dx or dy is greater than or equal  to 4 (pixels?)
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
                //invalidate();
            }
        } else if (line_type == AIRBRUSH) {

            float dx = Math.abs(x - mX);

            float dy = Math.abs(y - mY);
            // if change in dx or dy is greater than or equal  to 4 (pixels?)
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                Log.d(DTAG, "in TOUCH_TOLERANCE");
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
                splashPoints.add(new Point(Math.round(mX), Math.round(mY)));

            } else {
                Log.d(DTAG, "not in TOUCH_TOLERANCE");
                mX = x;
                mY = y;
            }
        }
        invalidate();
    }

    private void touch_up(MotionEvent event) {
        if (line_type == SOLID_LINE) {
            mPath.lineTo(event.getX(), event.getY());

            // kill this so we don't double draw
            Paint newPaint = new Paint(mPaint);
            pathspaints.add(new Pair<Path, Paint>(mPath, newPaint));
            mPath = new Path();
        } else if (line_type == AIRBRUSH) {

            mBrushesList.add(mBrushes);
            mBrushes = new ArrayList<ShapeDrawable>();
        }

        drawFrameActivity.setBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(pointX, pointY);

                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(pointX, pointY);
                break;
            case MotionEvent.ACTION_UP:
                invalidate();
                touch_up(event);
                break;
        }
        return true;
    }

    /*
     * undo last path draw, and store in undone paths
     *  paths is ArrayList of Pairs <Path, Paint>
     *
     * undo last
     */
    public void onClickUndo() {
        if (currentLineType.size() != 0) {
            if (line_type == SOLID_LINE) {
                if (pathspaints.size() > 0) {
                    undonePaths.add(pathspaints.remove((pathspaints.size() - 1)));
                    undoneLineType.add(currentLineType.remove(currentLineType.size() - 1));
                    if (currentLineType.size() > 0) {
                        if (currentLineType.get(currentLineType.size() - 1) == LineType.AIR) {
                            line_type = AIRBRUSH;
                        } else {
                            line_type = SOLID_LINE;
                        }
                    }
                }
            } else if (line_type == AIRBRUSH) {
                if (mBrushesList.size() > 0) {
                    Log.d(DTAG, "onClickUndo - mBrushesList.size(): " + mBrushesList.size());
                    Log.d(DTAG, "mBrushesList.get(mBrushesList.size() - 1).get(0).toString() " + mBrushesList.get(mBrushesList.size() - 1).get(0).toString());
                    //mBrushesList.remove(mBrushesList.size() - 1);
                    Log.d(DTAG, "onClickUndo - mBrushesList.size(): " + mBrushesList.size());
                    /*
                    for(int i = mBrushesList.size() - 1; i >= 0; i--) {
                        undoneBrushesList.add(mBrushesList.get(mBrushesList.size() - 1));
                    }
                    */
                    Log.d(DTAG, "j starts at: " + mBrushesList.get(mBrushesList.size() - 1).size());
                    for (int j = mBrushesList.get(mBrushesList.size() - 1).size() - 1; j >= 0; j--) {

                        mBrushesList.get(mBrushesList.size() - 1).remove(j);
                    }
                    Log.d(DTAG, "j ends at: " + mBrushesList.get(mBrushesList.size() - 1).size());
                    mBrushesList.remove(mBrushesList.size() - 1);
                    Log.d(DTAG, "onClickUndo - mBrushesList.size(): " + mBrushesList.size());
                    undoneLineType.add(currentLineType.remove(currentLineType.size() - 1));
                    if (currentLineType.size() > 0) {
                        if (currentLineType.get(currentLineType.size() - 1) == LineType.AIR) {
                            line_type = AIRBRUSH;
                        } else {
                            line_type = SOLID_LINE;
                        }
                    }
                }
            }
            invalidate();
        }
    }

    /*
     * redo last undo
     * paths is ArrayList of Pairs <Path, Paint>
     */
    public void onClickRedo() {
        if (undoneLineType.size() > 0) {
            if (undoneLineType.get(undoneLineType.size() - 1) == LineType.AIR) {
                line_type = AIRBRUSH;
            } else {
                line_type = SOLID_LINE;
            }

            if (line_type == SOLID_LINE) {
                if (undonePaths.size() > 0) {
                    pathspaints.add(undonePaths.remove(undonePaths.size() - 1));
                    currentLineType.add(undoneLineType.remove(undoneLineType.size() - 1));
                    if (currentLineType.get(currentLineType.size() - 1) == LineType.AIR) {
                        line_type = AIRBRUSH;
                    } else {
                        line_type = SOLID_LINE;
                    }
                }

            } else if (line_type == AIRBRUSH) {
                if (undoneBrushesList.size() > 0) {
                    mBrushesList.add(undoneBrushesList.remove(undoneBrushesList.size() - 1));
                    currentLineType.add(undoneLineType.remove(undoneLineType.size() - 1));
                    if (currentLineType.get(currentLineType.size() - 1) == LineType.AIR) {
                        line_type = AIRBRUSH;
                    } else {
                        line_type = SOLID_LINE;
                    }
                }
            }
        }
        invalidate();
    }

    /**
     * Sets the Canvas Bitmap we intend to modify
     */
    public boolean setCanvasBitmapDrawable(BitmapDrawable bitmapdrawable, Bitmap sourceBM, int left_bound, int top_bound) {
        // supply bounds as parameters
        mSourceBM = sourceBM;
        //mSourceBM = convertToMutable(this, mSourceBM);

        bmDrawable = bitmapdrawable;
        mCanvas = new Canvas(mSourceBM);
        //bmDrawable.setBounds(left_bound, top_bound, bitmapdrawable.getIntrinsicWidth()+left_bound, bitmapdrawable.getIntrinsicHeight()+top_bound);
        invalidate();
        return true;
    }

    /*
     * try to set previous frame image as background
     */
    public boolean setBackGroundBitmapDrawable(BitmapDrawable bm, Bitmap sourceBM, int left_bound, int top_bound) {
        mCanvas = new Canvas(sourceBM);
        return true;
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getColor() {
        return mColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this
     * color is the font color.
     * <p/>
     * <p/>
     * The example color attribute value to use.
     */
    public void setColor(int color) {
        mColor = color;
        //mPaint.setColor(mColor);
        if (line_type == SOLID_LINE) {
            setSolidLine();
        } else {
            setAirbrush();
        }
    }

    public void setLineWidth(float width) {
        mWidth = width;
        //mPaint.setStrokeWidth(mWidth);
        if (line_type == SOLID_LINE) {
            setSolidLine();
        } else {
            setAirbrush();
        }
    }

    public float getLineWidth() {
        return mWidth;
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getDrawable() {
        return mDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view,
     * this drawable is drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setDrawable(Drawable exampleDrawable) {
        mDrawable = exampleDrawable;
    }

    /*
     * used in DrawBoardActivity saveToStoryBoard
     * saves the current image on the canvas
     */
    public Bitmap getBitmap() {

        Bitmap returnedBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(returnedBitmap);

        Drawable bgDrawable = this.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        this.draw(canvas);
        return returnedBitmap;
    }

    public void setSolidLine() {
        line_type = SOLID_LINE;
        mPaint.setShader(null);
        //mPaint.
        mPaint = new Paint(mPaint);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mWidth);
    }

    public void setAirbrush() {
        // mBrush is ShapeDrawable
        line_type = AIRBRUSH;
        brushShape = new OvalShape();
        mBrushDrawable = new ShapeDrawable(brushShape);
        //mPaint = mBrushDrawable.getPaint();
        // to here

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mWidth);

        mPaint.setAntiAlias(true);

        strokeRadius = (int) (mPaint.getStrokeWidth() / 2);

        mBrushDrawable = new ShapeDrawable(brushShape);
        // it's being reset
        mPaint = mBrushDrawable.getPaint();

        mPaint.setColor(mColor);

        // radial gradient shader with a transparency falloff, if you don't want this,
        // just set a color on the paint and remove the setShader call
        Shader shader = new RadialGradient(strokeRadius, strokeRadius, strokeRadius,
                mColor,
                Color.argb(0, 255, 255, 255),
                Shader.TileMode.CLAMP);

        mPaint.setShader(shader);
        mPaint.setAlpha(0x10);
    }

    /*
     * displays memory usage
     */
    protected void displayMemoryUsage(String message) {
        int usedKBytes = (int) (Debug.getNativeHeapAllocatedSize() / 1024L);
        String usedMegsString = String.format("%s - usedMemory = Memory Used: %d KB", message, usedKBytes);
    }

    /*
     * A class to replace Pair<Path, Paint> in order to encapsulate AirBrush
     */
    class PathBrush {
        Path path;
        ShapeDrawable brush;

        public PathBrush(Path p, ShapeDrawable b) {
            Path path = p;
            ShapeDrawable brush = b;
        }

        public Path getPath(){
            return path;
        }

        public void setPath(Path path){
            this.path = path;
        }

        public ShapeDrawable getBrush(){
            return brush;
        }

        public void setBrush(ShapeDrawable sd){
            brush = sd;
        }
    }
}
