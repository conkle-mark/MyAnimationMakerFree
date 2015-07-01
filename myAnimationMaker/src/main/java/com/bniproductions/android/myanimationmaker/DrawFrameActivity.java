package com.bniproductions.android.myanimationmaker;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;

import com.ipaulpro.afilechooser.utils.FileUtils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DrawFrameActivity extends Activity {

    private static final String DTAG = "DrawFrameActivity";

    /*
     * shared preferences for saving line attributes
     */
    public static final String PREFS_NAME = "LineAttributes";
    /*
     * activity codes
     */
    private static final int REQUEST_CODE_CHOOSER = 0;

    protected String image_path;
    protected String dir_path;
    private File imageDir;
    private int frame_height;
    private int frame_width;

    private int size;
    private boolean story_board_changed = false;
    private ArrayList<Integer> indices = new ArrayList<Integer>();
    public ArrayList<Integer> usedColors = new ArrayList<Integer>();
    //private ArrayList<Scene> theScenes = new ArrayList<Scene>();

    boolean mMeasured = false;

    RelativeLayout edit_board_parent;
    private int contentWidth;
    private int contentHeight;

    private int leftPadding;
    private int topPadding;
    private BitmapDrawable bm_drawable;
    private Bitmap boardbm_mutable;
    private Bitmap framebm;

    boolean init;
    // private int paint_color;
    private static int SOLID_LINE = 1;
    private static int AIRBRUSH = 2;

    Bitmap selected_image_bm;

    /*
     * previous frame state
     */
    private boolean is_prev_frame_vis = false;

    /*
     * id is the GridView child id that we are doing work on here
     */
    private int id;

    /*
     * thumbs_or_slider, source view  where did we come from
     */
    private int thumbs_or_slider;

    /*
     * Frame number
     */
    private int frame_no;

    private boolean about = false;
    private String animPath;

    private int backpress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_board_drawer);

        edit_board_parent = (RelativeLayout) findViewById(R.id.my_edit_board_container_drawer);


        // this is the view group for the dummy button
        //final View controlsView = findViewById(R.id.save_close_buttons);
        // this is my custom view
        EditBoardTwoView contentView = (EditBoardTwoView) findViewById(R.id.my_edit_board_drawer);
        if (contentView == null) {
            Log.d(DTAG, "contentView is null " + contentView);
        } else {
            Log.d(DTAG, "contentView is null " + contentView.toString());
        }

        if (savedInstanceState != null) {
            indices = savedInstanceState.getIntegerArrayList("indices");
            story_board_changed = savedInstanceState.getBoolean("story_board_changed");
            image_path = savedInstanceState.getString("path");
            size = savedInstanceState.getInt("size");
            id = savedInstanceState.getInt("id");
            thumbs_or_slider = savedInstanceState.getInt("thumbs_or_slider");
            usedColors = savedInstanceState.getIntegerArrayList("usedColors");
            animPath = savedInstanceState.getString("animPath");
        }
        // get the file from the path, and display
        init = init(contentView);
        /*
		ColorPickerDialogFragment newFragment = ColorPickerDialogFragment.newInstance(contentView.getColor());
		newFragment.show(getFragmentManager(), "colorPicker");
		*/

        if (!init) {
            tToast("Initialization failed");
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putIntegerArrayList("indices", indices);
        saveInstanceState.putBoolean("story_board_changed", story_board_changed);
        saveInstanceState.putString("path", image_path);
        saveInstanceState.putInt("size", size);
        saveInstanceState.putInt("id", id);
        saveInstanceState.putInt("thumbs_or_slider", thumbs_or_slider);
        saveInstanceState.putIntegerArrayList("usedColors", usedColors);
        //saveInstanceState.putInt("index", index);
    }

    /**
     * callback method from QuantityDialogFragment, returning the value of user
     * input.
     */
    public void onUserSelectedColor(int newColor) {
        EditBoardTwoView ebv = (EditBoardTwoView) findViewById(R.id.my_edit_board_drawer);
        ebv.setColor(newColor);
        if (!usedColors.contains(newColor)) {
            usedColors.add(newColor);
        }
    }

    /*
     * return to StoryBoardActivity, with the new board updated
     */
    private void returnToStoryBoard(boolean saved) {
        Log.d(DTAG, "returnToStoryBoard saved = " + saved);
        Intent intent = new Intent();
        intent.putExtra("size", size);
        intent.putExtra("path", image_path);
        intent.putExtra("frame_changed", saved);
        intent.putExtra("id", id);
        intent.putExtra("frame_height", frame_height);
        intent.putExtra("frame_width", frame_width);
        intent.putExtra("thumbs_or_slider", thumbs_or_slider);
        intent.putIntegerArrayListExtra("indices", indices);
        intent.putIntegerArrayListExtra("usedColors", usedColors);
        intent.putExtra("animPath", animPath);
        setResult(RESULT_OK, intent);
        framebm.recycle();
        //selected_image_bm.recycle();
        //boardbm_mutable.recycle();

        System.gc();

        finish();
    }

    /*
     * get the index of the full size, grab the file and push it to the EditBoardView canvas
     */
    private boolean init(final EditBoardTwoView contentView) {
        Intent intent = getIntent();

        // this is the raw thumbnail directory
        image_path = intent.getStringExtra("path");
        dir_path = intent.getStringExtra("masterDir");
        frame_height = intent.getIntExtra("image_height", -1);

        Log.d(DTAG, "frame_height" + frame_height);
        frame_width = intent.getIntExtra("image_width", -1);

        Log.d(DTAG, "frame_width: " + frame_width);
        id = intent.getIntExtra("index", -1);
        frame_no = id + 1;
        thumbs_or_slider = intent.getIntExtra("thumbs_or_slider", 1);
        usedColors = intent.getIntegerArrayListExtra("usedColors");

        this.setTitle("Draw the Frame: FRAME " + frame_no);

        // we create a file to confirm the file exists, and bail if it doesn't exist.
        imageDir = new File(image_path);
        Log.d(DTAG, "init - imageDir.File.getName: " + imageDir.getName());
        if (imageDir.exists()) {
            Log.d(DTAG, "init(): imageDir.exists() " + imageDir.toString());
        } else {
            Log.d(DTAG, "Image File Directory DNE");
            finish();
        }

        // grab the image we want and convert to Bitmap
        // After screenplay edit, insert new scene, images are shifting?
        // Thus, index n gets scene n image instead of scene n + 1?
        // Bitmap framebm;

        contentView.setDrawingCacheEnabled(true);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mMeasured) {
                    contentWidth = contentView.getWidth();
                    contentHeight = contentView.getHeight();

                    // take the file path and convert t Bitmap
                    framebm = BitmapFactory.decodeFile(image_path);

                    // make a mutable bitmap out of it
                    Bitmap boardbm_mutable = convertToMutable(framebm);

                    leftPadding = (contentWidth - framebm.getWidth()) / 2;
                    topPadding = (contentHeight - framebm.getHeight()) / 2;

                    edit_board_parent.setPadding(leftPadding, topPadding, leftPadding, topPadding);
                    // Here your view is already layed out and measured for the first time
                    // set a canvas in EditBoardView with a bitmapdrawable
                    bm_drawable = new BitmapDrawable(getResources(), boardbm_mutable);
                    contentView.setCanvasBitmapDrawable(bm_drawable, boardbm_mutable, 0, 0);

                    // lets try to put a nonMutable Bitmap on the other canvas mCanvas here.
                    // Only if id != 0
                    if (id != 0) {
                        // this call doesn't work
                        //loadLastFrameBitmapToSecondCanvas(contentView, imageDir.getName());
                        setPreviousFrame(imageDir.getName());
                    }
                    mMeasured = true; // Some optional flag to mark, that we already got the sizes
                }
            }
        });
        contentView.setColor(Color.BLACK);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.contains("line_type")) {
            contentView.setLineWidth(settings.getFloat("line_width", 1));
            //doPositiveClick(settings.getFloat("line_width", 10)
            contentView.setColor(settings.getInt("line_color", Color.BLACK));
            if (settings.getBoolean("line_type", false)) {
                doLineTypeClick(-1);
            } else {
                doLineTypeClick(1);
            }
        }
        System.gc();
        //framebm.recycle();
        return true;
    }

    /*
     * trying with ImageView
     */
    @SuppressWarnings("deprecation")
    public void setPreviousFrame(String frame_file_name) {
        String file_name = frame_file_name;
        String file_no;
        String ext = ".jpeg";
        int f_no;
        File previousFrame;

        ImageView iv = (ImageView) findViewById(R.id.previous_frame_drawer);

        file_no = file_name.substring(0, file_name.length() - ext.length());
        f_no = Integer.parseInt(file_no);
        f_no--;// we reduce the number by 1, thus the previous frame
        file_no = Integer.toString(f_no);
        file_no = file_no + ext;
        file_name = dir_path + "/" + file_no;
        previousFrame = new File(file_name);
        if (previousFrame.exists()) {
            framebm = BitmapFactory.decodeFile(file_name);
            framebm = framebm.copy(Bitmap.Config.ARGB_8888, true);

            iv.setImageBitmap(framebm);
            iv.setAlpha(100);
        }
    }

    public void previousFrameVisibility(boolean vis) {

        ImageView iv = (ImageView) findViewById(R.id.previous_frame_drawer);


        if (vis == true) {
            // set visibility VISIBLE

            iv.setVisibility(ImageView.VISIBLE);
            is_prev_frame_vis = true;
        } else {
            // set vis INVISIBLE

            iv.setVisibility(View.GONE); // gone
            is_prev_frame_vis = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHOOSER:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri selectedImage = data.getData();

                        try {
                            // Create a file instance from the URI
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();
                            File file = new File(picturePath);
                            Log.d(DTAG, "picturePath: "+picturePath);
                            //System.gc();
                            readJPGFile(file);
                            break;
                        } catch (Exception e) {
                            Log.e("FileSelectorTestActivit", "File select error", e);
                            break;
                        }
                    }
                } else {
                    tToast("Result of file selection NOT OK :" + RESULT_OK);
                }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        // delayedHide(100);
    }


    @Override
    public void onBackPressed() {
        backpress = (backpress + 1);
        Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();
        if (backpress > 1) {
            super.onBackPressed();
        }
    }

    /*
     * Save EditBoardTwoView canvas back out as jpg
     */
    protected void saveToStoryBoard() throws IOException {
        Bitmap masterBitmap;
        Bitmap thumbBitmap;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //int screenheight = size.y;
        //int screenwidth = size.x;
        int thumbheight = (int) (frame_height * .2);
        int thumbwidth = (int) (frame_width * .2);

        EditBoardTwoView contentView = (EditBoardTwoView) findViewById(R.id.my_edit_board_drawer);

        masterBitmap = contentView.getBitmap();
        //thumbBitmap = contentView.getDrawingCache();

        Options options = new BitmapFactory.Options();
        options.inScaled = false;

        thumbBitmap = Bitmap.createScaledBitmap(masterBitmap, thumbwidth, thumbheight, false);

        if (!imageDir.exists()) {
            tToast("Error: image directory DNE");
            return;
        } else {
            String thumb_path = dir_path + "/" + "thumbs/";
            String file_name = image_path.substring(image_path.lastIndexOf("/") + 1, image_path.length());
            Log.d(DTAG, "file_name: " + file_name);
            thumb_path = thumb_path + file_name;
            OutputStream fos = null;
            OutputStream thumb_fos = null;

            File file = new File(image_path);
            Log.d(DTAG, "image_path: " + image_path);
            File thumb_file = new File(thumb_path);
            Log.d(DTAG, "thumb_path:" + thumb_path);

            fos = new FileOutputStream(file);
            thumb_fos = new FileOutputStream(thumb_file);

            BufferedOutputStream bos = new BufferedOutputStream(fos);
            BufferedOutputStream thumb_bos = new BufferedOutputStream(thumb_fos);

            masterBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, thumb_bos);

            fos.flush();
            thumb_fos.flush();

            fos.close();
            thumb_fos.close();

            bos.flush();
            thumb_bos.flush();

            bos.close();
            thumb_bos.close();

        }
        masterBitmap.recycle();
        thumbBitmap.recycle();
        boardbm_mutable.recycle();
        framebm.recycle();
    }
	
	/* menu Functions */

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.draw_board_menu, menu);
        return true;
    }
    // end onCreateOptionsMenu()

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        EditBoardTwoView contentView = (EditBoardTwoView) findViewById(R.id.my_edit_board_drawer);
        switch (item.getItemId()) {

            case R.id.eraser:

                android.app.FragmentManager fm2 = getFragmentManager();
                EraserDialogFragment eraserWidthDialog = new EraserDialogFragment();
                eraserWidthDialog.setRetainInstance(true);
                eraserWidthDialog.show(fm2, "fragment_name");
                backpress = 0;
                return true;

            case R.id.cancel_draw_frame:
                DrawFrameActivity.this.setResult(RESULT_OK);
                story_board_changed = false;
                returnToStoryBoard(story_board_changed);
                return true;
			/*
			case R.id.save_draw_frame:
				try {
					saveToStoryBoard();
					backpress = 0;
				} catch (FileNotFoundException e) {
					
					e.printStackTrace();
				} catch (IOException e) {
				
					e.printStackTrace();
				}
				
				contentView = (EditBoardTwoView)findViewById(R.id.my_edit_board_drawer);

			      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			      SharedPreferences.Editor editor = settings.edit();
			      editor.putFloat("line_width", contentView.getLineWidth());
			      editor.putInt("line_color", contentView.getColor());

			      editor.commit();
			      init(contentView);
				return true;
			*/
            case R.id.discard_last_draw:
                contentView = (EditBoardTwoView) findViewById(R.id.my_edit_board_drawer);
                contentView.onClickUndo();
                backpress = 0;
                return true;
            case R.id.redo_last_draw:
                contentView = (EditBoardTwoView) findViewById(R.id.my_edit_board_drawer);
                contentView.onClickRedo();
                backpress = 0;
                return true;

            case R.id.set_background_image:
                showChooser(REQUEST_CODE_CHOOSER);
                backpress = 0;
                return true;

            case R.id.save_and_close_draw_frame:
                DrawFrameActivity.this.setResult(RESULT_OK);
                try {
                    saveToStoryBoard();
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                }
                //theScenes.get(index).setIsModified(true);
                story_board_changed = true;
                returnToStoryBoard(story_board_changed);
                return true;
			/*
			case R.id.line_type:
				android.app.FragmentManager ltfm = getFragmentManager();
				LineTypeDialogFragment line_type = new LineTypeDialogFragment();
				line_type.show(ltfm, "line type");
				backpress = 0;
				return true;
				*/
            case R.id.line_color:
                contentView = (EditBoardTwoView) findViewById(R.id.my_edit_board_drawer);
                ColorPickerDialogFragment newFragment = ColorPickerDialogFragment.newInstance(contentView.getColor());
                newFragment.show(getFragmentManager(), "colorPicker");
                backpress = 0;
                return true;
            case R.id.used_colors:
                android.app.FragmentManager cufm = getFragmentManager();
                UsedColorsDialogFragment usedColors = new UsedColorsDialogFragment();
                usedColors.setRetainInstance(false);
                usedColors.show(cufm, "fragment_name");
                backpress = 0;
                return true;
            case R.id.line_width:
                android.app.FragmentManager fm = getFragmentManager();
                LineWidthDialogFragment lineWidthDialog = new LineWidthDialogFragment();
                lineWidthDialog.setRetainInstance(true);
                lineWidthDialog.show(fm, "fragment_name");
                backpress = 0;
                return true;
            case R.id.prev_frame:
                if (is_prev_frame_vis) {
                    item.setTitle(R.string.prev_frame);
                    previousFrameVisibility(false);
                    return true;
                } else {
                    item.setTitle(R.string.hide_previous_frame);
                    previousFrameVisibility(true);
                    return true;
                }
            case R.id.draw_about:
                about();
                backpress = 0;
                return true;
        }
        return false;
    }

    /*
 * launch the about dialog fragment
 */
    protected boolean about() {
        android.app.FragmentManager fm = getFragmentManager();
        AboutDialogFragment aboutDialog = new AboutDialogFragment();
        aboutDialog.setRetainInstance(true);
        aboutDialog.show(fm, "fragment_name");
        about = true;
        return about;
    }

    public void setBackPressed() {
        backpress = 0;
    }

    protected void tToast(String s) {
        // Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, s, duration);
        toast.show();
    }

    protected void tToast(String s, int time) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }

    protected void displayMemoryUsage(String message) {
        //int usedKBytes = (int) (Debug.getNativeHeapAllocatedSize() / 1024L);
        //String usedMegsString = String.format("%s - usedMemory = Memory Used: %d KB", message, usedKBytes);
        //Log.d(DTAG, usedMegsString);
    }


    /**
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocate
     * more memory that there is already allocated.
     *
     * @param imgIn - Source image. It will be released, and should not be used more
     * @return a copy of imgIn, but muttable.
     */
    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }

    public void eraserClick(String string) {
        float eraser_width = 0.f;
        try {
            eraser_width = Float.parseFloat(string);

            if (eraser_width < 0f) {
                tToast("Line width less than 0", 0);
            } else if (eraser_width > 300) {
                tToast("Line width to large, greater than 300", 0);
            } else {
                EditBoardTwoView view = (EditBoardTwoView) findViewById(R.id.my_edit_board_drawer);
                view.setColor(Color.WHITE);
                view.setLineWidth(eraser_width);
            }
        } catch (NumberFormatException e) {
            tToast("No value entered");
        }
    }

    // setLine width
    public void doPositiveClick(String string, int i, boolean b) {
        float line_width = 0.f;
        try {
            line_width = Float.parseFloat(string);
            //float line_width = Float.valueOf(string);
            if (line_width < 0f) {
                tToast("Line width less than 0", 0);
            } else if (line_width > 300) {
                tToast("Line width to large, greater than 300", 0);
            } else {
                EditBoardTwoView view = (EditBoardTwoView) findViewById(R.id.my_edit_board_drawer);
                view.setLineWidth(line_width);
            }
        } catch (NumberFormatException e) {
            tToast("No value entered");
        }
    }

    public void doLineTypeClick(int type) {
        EditBoardTwoView view = (EditBoardTwoView) findViewById(R.id.my_edit_board_drawer);
        if (type == SOLID_LINE) {
            // solid line
            //Log.d(DTAG, "doLine_type_click "+type);
            view.setSolidLine();
        } else if (type == AIRBRUSH) {
            //Log.d(DTAG, "doLineTypeClick type: "+type);
            view.setAirbrush();
        }
    }

    /*
    Here’s a method to calculate a sample size value that is a power of two based on a target width and height:
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        Log.d(DTAG, "calculateInSampleSize - outHeight: "+height);
        Log.d(DTAG, "calculateInSampleSize - outWidth: "+width);

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    protected boolean readJPGFile(File jpg_file) {

        double height_ratio;
        double width_ratio;
        double bm_width;
        double bm_height;
        double ratio_diff;
        int left_bound = 0;
        int top_bound = 0;
        boolean is_image;
        int scale_size = 1;

        is_image = isImageFile(jpg_file);

        if (!is_image) {
            return false;
        }

  /*
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
*/
        final EditBoardTwoView contentView = (EditBoardTwoView) findViewById(R.id.my_edit_board_drawer);
        contentWidth = contentView.getWidth();
        contentHeight = contentView.getHeight();

        Log.d(DTAG, "readJPGFile - contentWidth: "+contentWidth);
        Log.d(DTAG, "readJPGFile - contentHeight: "+contentHeight);

        String filename = jpg_file.toString();

        //final
        BitmapFactory.Options mOptions = new BitmapFactory.Options();

        if(selected_image_bm != null) {
            Log.d(DTAG, "selected_image_bm is not null");
            selected_image_bm.recycle();
            selected_image_bm = null;
        }

        /*
        If set to true, the decoder will return null (no bitmap), but the out... fields will still be set,
        allowing the caller to query the bitmap without having to allocate the memory for its pixels.
         */
        mOptions.inJustDecodeBounds = true;

        selected_image_bm = BitmapFactory.decodeFile(filename, mOptions);

        mOptions.inSampleSize = calculateInSampleSize(mOptions, contentWidth, contentHeight);

        mOptions.inJustDecodeBounds = false;

        selected_image_bm = BitmapFactory.decodeFile(filename, mOptions);

        if(boardbm_mutable != null){
            boardbm_mutable.recycle();
            boardbm_mutable = null;
        }

        boardbm_mutable = convertToMutable(selected_image_bm);
        Log.d(DTAG, "selected_image_bm - isRecycled: "+selected_image_bm.isRecycled());
        Log.d(DTAG, "boardbm_mutable - isRecycled: "+boardbm_mutable.isRecycled());

        //selected_image_bm.recycle();
        //System.gc();
        bm_width = boardbm_mutable.getWidth();
        bm_height = boardbm_mutable.getHeight();

        if (contentWidth < bm_width || contentHeight < bm_height) {
            width_ratio = contentWidth / bm_width;
            height_ratio = contentHeight / bm_height;
            ratio_diff = width_ratio - height_ratio;
            if (ratio_diff < 0) {
                ratio_diff = ratio_diff * -1;
            }

            if (width_ratio > height_ratio) {

                left_bound = (int) (contentWidth * ratio_diff) / 2;

                boardbm_mutable = Bitmap.createScaledBitmap(boardbm_mutable, (int) (contentWidth - (contentWidth * ratio_diff)), contentHeight, true);
            } else if (height_ratio > width_ratio) {

                top_bound = (int) (contentHeight * ratio_diff) / 2;

                boardbm_mutable = Bitmap.createScaledBitmap(boardbm_mutable, contentWidth, (int) (contentHeight - (contentHeight * ratio_diff)), true);
            } else {

                boardbm_mutable = Bitmap.createScaledBitmap(boardbm_mutable, contentWidth, contentHeight, true);
            }

        }

        bm_drawable = new BitmapDrawable(getResources(), boardbm_mutable);
        contentView.setCanvasBitmapDrawable(bm_drawable, boardbm_mutable, left_bound, top_bound);
        return true;
    }

    private boolean isImageFile(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), options);
        if (options.outWidth != -1 && options.outHeight != -1) {
            // This is an image file.
            return true;
        } else {
            // This is not an image file.
            return false;
        }
    }

    private void showChooser(int task) {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.choose_file));
        try {
            startActivityForResult(intent, REQUEST_CODE_CHOOSER);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }
}
