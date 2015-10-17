package com.bniproductions.android.myanimationmaker;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

public class FrameSliderActivity extends AppCompatActivity {

    /*
     * CONSTANTS
     */
    private int SECS = 1;
    private int MINS = 2;
    private static final String DTAG = "FrameSliderActivity";
    private static final int DRAW_FRAME_REQUEST = 2;
    private static final int LAUNCH_ANIM_REQUEST = 3;
    /*
     * screen height and width
     */
    protected int screen_height;
    protected int screen_width;

    /*
     * image width aND HEIGHT
     */
    protected int image_width;
    protected int image_height;

    /*
     * rowHeight, the height of the row of frames
     */
    private int rowHeight;

    /*
     * row height for the the thumb nails
     */
    private int thumbRowHeight;

    /*
     * enum of launch draw frame mode, from slider, or thumbs
     */
    private enum ThumbsOrSlider {
        THUMBS,
        SLIDER
    }

    private ThumbsOrSlider thumbs_or_slider;

    /*
     * frames per second
     */
    protected int frame_rate;

    /*
     * animation length in seconds
     */
    protected int animation_length_secs;

    /*
     * animation length in minutes, using floats
     */
    protected float animation_length_minutes;

    /*
     * length type, 1 == SECS, 2 == MINS
     */
    private int length_type;

    /*
     * number of frames
     */
    public int no_of_frames;

    /*
     * this is the root directory for the application
     */
    protected String rootDirectory;

    /*
     * file name, the file is a text file with metadata abouut the animation
     */
    protected String file_name;

    /*
     * Thumbnail directory
     */
    protected String thumbDir;

    /*
     * the name of the folder that holds the frame jpgs
     */
    protected String masterDir;

    /*
     * The grid that holds the frames
     */
    private TwoWayGridView myGridView;

	/*
     * The master
	 */

    /*
     * init is whether the animation has been initialize
     */
    protected boolean init = false;

    /*
     * about is whether the about dialog fragemnt has been viewed
     */
    protected boolean about = false;

    /*
     * frame_changed is reurned form DrawFrameActivity to indicate if we need to recreate
     * thegridview
     */
    private boolean frame_changed = false;

    /*
     * animation gif path
     */
    private String animPath;

    /*
     * LruCache memory Cache for bitmaps
     */
    private BitmapLruCache mMemoryCache;

    /*
     * global list of used colors
     */
    private ArrayList<Integer> usedColors = new ArrayList<Integer>();

    /*
    progress dialog for asynctask
     */
    ProgressDialog progressDialog;

    public ArrayList<String> projectNames = new ArrayList<String>();
    private JSONArray jsonArrayProjects;

    private TwoWayGridView twoWayGridView;

    boolean grid_limited = true;

    private boolean repeat = true;

    Intent intent;
    int position;

    private boolean file_exists;

    Toolbar toolbar;

    OverWriteDirectoryDialogFragment overWriteDirectoryDialog;
    CreateProjectDialogFragment writeDirectoryDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_slider);

        toolbar = (Toolbar) findViewById(R.id.app_bar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.myanimationicon);

        FrameSliderNavigationFragment frameSliderNavigationFragment = (FrameSliderNavigationFragment)
                getSupportFragmentManager().findFragmentById(R.id.slider_fragment);
        frameSliderNavigationFragment.setUp(R.id.slider_fragment, (DrawerLayout) findViewById(R.id.frame_slider_drawer), toolbar);

        Log.d(DTAG, "savedInstanceState null): "+savedInstanceState);
        if (savedInstanceState != null) {
            twoWayGridView = (TwoWayGridView) findViewById(R.id.gridview);
            twoWayGridView.setBackgroundColor(getResources().getColor(R.color.primaryCyan));
            frame_rate = savedInstanceState.getInt("frame_rate", 10);
            animation_length_secs = savedInstanceState.getInt("animation_length");
            animation_length_minutes = savedInstanceState.getFloat("animation_length_minutes");
            grid_limited = savedInstanceState.getBoolean("grid_limited");
            init = savedInstanceState.getBoolean("init");
            about = savedInstanceState.getBoolean("about");
            frame_changed = savedInstanceState.getBoolean("frame_changed");
            masterDir = savedInstanceState.getString("masterDir");
            thumbDir = savedInstanceState.getString("thumbDir");
            file_name = savedInstanceState.getString("file_name");
            animPath = savedInstanceState.getString("animPath");
            screen_height = savedInstanceState.getInt("screen_height");
            screen_width = savedInstanceState.getInt("screen_width");
            rowHeight = savedInstanceState.getInt("rowHeight");
            thumbRowHeight = savedInstanceState.getInt("thumbRowHeight");
            image_height = savedInstanceState.getInt("image_height");
            image_width = savedInstanceState.getInt("image_width");
            repeat = savedInstanceState.getBoolean("repeat");
            rootDirectory = savedInstanceState.getString("rootDirectory");
        } else {
            hideSoftKeyBoard();
            mMemoryCache = new BitmapLruCache();
            twoWayGridView = (TwoWayGridView) findViewById(R.id.gridview);
            twoWayGridView.setBackgroundColor(getResources().getColor(R.color.primaryCyan));
            setScreenDims();

            intent = getIntent();
            position = intent.getIntExtra("position", -1);
            repeat = intent.getBooleanExtra("repeat", false);
            file_name = intent.getStringExtra("file_name");
            frame_rate = intent.getIntExtra("frame_rate", -1);
            animation_length_secs = intent.getIntExtra("animation_length", -1);
            image_height = intent.getIntExtra("image_height", -1);
            image_width = intent.getIntExtra("image_width", -1);
            setFileName(file_name);
            setFrameRate(frame_rate);
            setAnimationLength(animation_length_secs);
            setImageSize(image_height, image_width);

            getMemRequirements();
            checkProjectList();
            readProjectList();
            file_exists = checkDirectory(file_name);
            if(file_exists){
                launchOverWrite();

            }else{
                launchWrite();
            }
            Log.d(DTAG, "after if file exist");
        }
        Log.d(DTAG, "after if savedInstanceState null");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //requestF
    }

    private void hideSoftKeyBoard(){
        Log.d(DTAG, "getCurrentFocus: " + getCurrentFocus());
        if(getCurrentFocus() != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putInt("animation_length", animation_length_secs);
        saveInstanceState.putFloat("animation_length_minutes", animation_length_minutes);
        saveInstanceState.putInt("frame_rate", frame_rate);
        saveInstanceState.putBoolean("init", init);
        saveInstanceState.putBoolean("about", about);
        saveInstanceState.putBoolean("frame_changed", frame_changed);
        saveInstanceState.putBoolean("repeat", repeat);
        saveInstanceState.putString("thumbDir", thumbDir);
        saveInstanceState.putString("masterDir", masterDir);
        saveInstanceState.putString("file_name", file_name);
        saveInstanceState.putString("animPath", animPath);
        saveInstanceState.putInt("screen_height", screen_height);
        saveInstanceState.putInt("screen_width", screen_width);
        saveInstanceState.putInt("image_height", image_height);
        saveInstanceState.putInt("image_width", image_width);
        saveInstanceState.putInt("rowHeight", rowHeight);
        saveInstanceState.putInt("thumbRowHeight", thumbRowHeight);
        saveInstanceState.putString("rootDirectory", rootDirectory);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_animation: {
                createProject();
                return true;
            }
            case R.id.open_animation: {
                openAnimationDialog();
                return true;
            }
            case R.id.build_animation:
                if (file_name != null) {
                    new BuildGif().execute(new Void[0]);
                } else {
                    tToast("You have not opened or created a project yet", 1);
                }
                return true;
            case R.id.open_thumbs:
                if (thumbDir != null) {
                    loadThumbGridView();
                } else {
                    tToast("Thumbs directory is empty, perhaps you haven't opened an animation yet.");
                }
                return true;
            case R.id.open_slider:
                if (masterDir != null) {
                    //loadGridView();
                } else {
                    tToast("Master directory is empty, perhaps you haven't opened an animation yet.");
                }
                return true;
            case R.id.launch_animation:
                if (animPath != null) {
                    memoryDialog();
                } else {
                    tToast("You have either not opened a project, or not created a gif yet", 1);
                }
                return true;
            case R.id.email_gif:

                emailGif();
                return true;
            case R.id.about: {
                about();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case (DRAW_FRAME_REQUEST): {
                System.gc();
                if (resultCode == Activity.RESULT_OK) {
                    frame_changed = data.getBooleanExtra("frame_changed", true);
                    int id = data.getIntExtra("id", -1);
                    int t_or_s = data.getIntExtra("thumbs_or_slider", 1);
                    usedColors = data.getIntegerArrayListExtra("usedColors");
                    animPath = data.getStringExtra("animPath");
                    image_height = data.getIntExtra("frame_height", -1);
                    image_width = data.getIntExtra("frame_width", -1);
                    if (image_height == -1) {
                        Log.d(DTAG, "image_height invalid: " + image_height);
                    } else if (image_width == -1) {
                        Log.d(DTAG, "image_width invalid: " + image_width);
                    }

                    System.gc();

                    if (frame_changed) {
                        if (t_or_s == 1) {
                            thumbs_or_slider = ThumbsOrSlider.SLIDER;
                            //loadGridView();
                        } else if (t_or_s == 2) {
                            thumbs_or_slider = ThumbsOrSlider.THUMBS;
                            loadThumbGridView();
                        }
                        myGridView.getFirstVisiblePosition();
                        myGridView.smoothScrollToPosition(id);
                    }
                }
                break;
            }
            case 265:
                Uri uri = data.getData();
                if (uri != null) {
                    openAnimation(uri.toString());
                } else {
                    tToast("failed to find the project", 1);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(DTAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(DTAG, "onPause");
    }

    /*
    Calculate the available memory, and the memory a full screen bitmap would consume
     */
    private void getMemRequirements() {
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memoryInfo);
        String memMessage = String.format(
                "Memory: Pss=%.2f MB, Private=%.2f MB, Shared=%.2f MB",
                memoryInfo.getTotalPss() / 1024.0,
                memoryInfo.getTotalPrivateDirty() / 1024.0,
                memoryInfo.getTotalSharedDirty() / 1024.0);
    }

    /*
     * Launch the AnimationSettingsDialogFragment
     * Sets frame rate and length of the animation
     */
    protected boolean createProject() {

        intent = new Intent(this, ActivityAnimationSettings.class);
        startActivity(intent);
        init = true;
        return init;
    }

    private boolean readSettingsXML(File animation_settings) {
        String my_frame_rate = "frame_rate";
        String length_min = "animation_length_minutes";
        String length_sec = "animation_length_seconds";
        String color = "used_colors";

        usedColors = new ArrayList<Integer>();

        int type = -1;  // 1 := frame_rate, 2 := length_mins, 3 := length_secs

        if (!animation_settings.exists()) {
            return false;
        } else {
            //InputStream in = null;
            try {
                Log.d(DTAG, "readSettingsXML - inside try");
                FileInputStream fis = new FileInputStream(animation_settings.getAbsolutePath());
                BufferedReader bfr = new BufferedReader(new InputStreamReader(fis));

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(bfr);
                int event;
                try {
                    while ((event = xpp.next()) != XmlPullParser.END_DOCUMENT) {
                        Log.d(DTAG, "readSettingsXML - inside 2nd try and while - event: " + event);
                        if (event == XmlPullParser.START_DOCUMENT) {
                            Log.d(DTAG, "Start document");
                        } else if (event == XmlPullParser.START_TAG) {
                            if (my_frame_rate.equals(xpp.getName())) {
                                type = 1;
                            } else if (length_min.equals(xpp.getName())) {
                                type = 2;
                            } else if (length_sec.equals(xpp.getName())) {
                                type = 3;
                            } else if (color.equals(xpp.getName())) {
                                type = 4;
                            }
                            //Log.d(DTAG, "Start tag "+xpp.getName());
                        } else if (event == XmlPullParser.END_TAG) {
                            //Log.d(DTAG, "End tag "+xpp.getName());
                        } else if (event == XmlPullParser.TEXT) {
                            //Log.d(DTAG, "Text "+xpp.getText());
                            if (type == 1) {
                                frame_rate = Integer.parseInt(xpp.getText());
                            }
                            if (type == 2) {
                                animation_length_minutes = Float.parseFloat(xpp.getText());
                            }
                            if (type == 3) {
                                animation_length_secs = Integer.parseInt(xpp.getText());
                            }
                            if (type == 4) {
                                usedColors.add(Integer.parseInt(xpp.getText()));
                            }
                        }
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (XmlPullParserException e) {

                e.printStackTrace();
            }

            return true;
        }
    }

    /*
     * output the setting into an XML for ready later
     * when opening an animation
     */
    private boolean writeSettingsXML() {

        // usedColors

        String settings = masterDir + "/settings";
        //<?xml version="1.0"?>
        boolean ok_to_write = checkSDCard(this);
        if (ok_to_write) {
            File xml_dir = new File(settings);
            xml_dir.mkdir();
            File file = new File(xml_dir, "settings.xml");
            try {
                // FileOutputStream f = new FileOutputStream(file);
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("<?xml version=\"1.0\"?>\r\n");
                //bw.write("<settings>\r\n");
                bw.write("<frame_rate>" + Integer.toString(frame_rate) + "</frame_rate>\r\n");
                if (length_type == MINS) {
                    bw.write("<animation_length_minutes>" + Float.toString(animation_length_minutes) + "</animation_length_minutes>\r\n");
                } else if (length_type == SECS) {
                    bw.write("<animation_length_seconds>" + Integer.toString(animation_length_secs) + "</animation_length_seconds>\r\n");
                }

                for (int i = 0; i < usedColors.size(); i++) {
                    bw.write("<used_colors>" + usedColors.get(i) + "</used_colors>\r\n");
                }
                //bw.write("</settings>\r\n");
                bw.flush();
                bw.close();
                fw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(DTAG, "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the   manifest? " + e);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(DTAG, "writeSettingsXML at end");
        } else {
            Log.d(DTAG, "writeSettingsXML failed");
        }
        return false;
    }

    protected boolean emailDialogFragment(String gifPath) {
        android.app.FragmentManager fm = getFragmentManager();
        EmailInfoDialogFragment emailDialog = new EmailInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString("gif_path", gifPath);
        emailDialog.setRetainInstance(true);
        emailDialog.show(fm, gifPath);
        return true;
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

    /*
     * Memory message Dialog
     */
    protected void memoryDialog() {
        android.app.FragmentManager fm = getFragmentManager();
        MemoryMessageDialogFragment memDialog = new MemoryMessageDialogFragment();
        memDialog.setRetainInstance(true);
        memDialog.show(fm, "fragment_name");
    }

    /*
     * create animation
     */
    protected byte[] generateGIF() {
        //ArrayList<Bitmap> bitmaps = buildBMList();
        File[] bitmaps = buildBMList();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GifEncoder encoder = new GifEncoder();

        encoder.start(bos);
        encoder.setFrameRate(frame_rate);
        if (repeat) {
            encoder.setRepeat(0);
        } else {
            encoder.setRepeat(1);
        }

        Log.d(DTAG, "bitmaps.size: " + bitmaps.length);
        int i = 0;
        for (File bitmap : bitmaps) {
            Log.d(DTAG, "i: " + i);
            i++;
            encoder.addFrame(convertToBitmap(bitmap));
        }

        encoder.finish();
        return bos.toByteArray();
    }

    protected File[] buildBMList() {
        File frameDir = new File(masterDir);
        Log.d(DTAG, "masterDir: " + masterDir);
        File[] imageFiles;

        if (frameDir.exists()) {
            imageFiles = frameDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jpeg");
                }
            });

            // Let's make sure the files are sorted
            Arrays.sort(imageFiles, new Comparator<File>() {

                @Override
                public int compare(File lhs, File rhs) {

                    String left = lhs.getName();
                    String right = rhs.getName();
                    left = left.substring(0, left.length() - 5);
                    right = right.substring(0, right.length() - 5);
                    try {
                        int i1 = Integer.parseInt(left);
                        int i2 = Integer.parseInt(right);
                        return i1 - i2;
                    } catch (NumberFormatException e) {
                        throw new AssertionError(e);
                    }
                }
            });

            return imageFiles;
        } else {
            return null;
        }
    }

    /*
     * check if project lst exist
     * if not create it
     * read it into a string array
     */
    private boolean checkProjectList() {

        JSONObject jsonObj;
        FileInputStream stream;
        FileOutputStream outStream;
        String jsonStr = null;
        String fileName;
        int index;

        //updateExternalStorageState();
        String dir = Environment.getExternalStorageDirectory().toString();

        dir = dir + "/myanimation_projects";
        rootDirectory = dir;

        File projectList = new File(dir);
        projectList.mkdir();
        File projectListFile = new File(dir, "project_list.json");
        if (projectList.exists()) {
            if (!projectListFile.exists()) {
                //Log.d(DTAG, "projectListFile.exists: " + projectListFile.exists());
                try {
                    FileWriter fw = new FileWriter(projectListFile);
                    BufferedWriter bw = new BufferedWriter(fw);

                    bw.flush();
                    bw.close();
                    fw.close();
                } catch (FileNotFoundException fnfe) {
                    Log.d(DTAG, "checkProjectList() " + fnfe.toString());
                } catch (IOException ioe) {
                    Log.d(DTAG, "checkProjectList() " + ioe.toString());
                }
            } else if (projectListFile.exists()) {
                try {
                    stream = new FileInputStream(projectListFile);

                    FileChannel fc = stream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                    jsonStr = Charset.defaultCharset().decode(bb).toString();
                    stream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (jsonStr != null) {
                    if (jsonStr.equals("")) {

                        String emptyArray = "[]";
                        try {
                            outStream = new FileOutputStream(projectListFile);
                            OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream);
                            outStreamWriter.write(emptyArray);
                            outStreamWriter.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        jsonArrayProjects = new JSONArray();
                    } else {

                        try {
                            jsonArrayProjects = new JSONArray(jsonStr);

                            for (int i = 0; i < jsonArrayProjects.length(); i++) {
                                //projectNames.add(jsonArrayProjects.get(i).toString());
                                fileName = jsonArrayProjects.get(i).toString();
                                index = fileName.lastIndexOf("/");
                                fileName = fileName.substring(index + 1, fileName.length() - 2);
                                //Log.d(DTAG, "projectNames.get(" + i + ") " + projectNames.get(i));
                                projectNames.add(fileName);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.d(DTAG, "jsonStr is null");
                }
            } else {
                Log.d(DTAG, "big json_file_creation_failure");
                tToast("Big failure, Project list is currupted or non-existant", 1);
            }

            return true;
        } else {
            return false;
        }
    }


    /*
     * launch the OpenAnimationDialogFragment
     */
    protected boolean openAnimationDialog() {

        Bundle args = new Bundle();
        args.putStringArrayList("projectNames", projectNames);

        android.app.FragmentManager fm = getFragmentManager();
        OpenAnimationDialogFragment openDialog = new OpenAnimationDialogFragment();
        openDialog.setArguments(args);
        openDialog.setRetainInstance(true);
        openDialog.show(fm, "open_anim_fragment");

        return true;
    }

    /*
     * open animation directory
     */
    protected boolean openAnimation(String file_name) {// just the shortname

        this.file_name = file_name;

        updateExternalStorageState();
        //String dir = Environment.getExternalStorageDirectory().toString();
        String dir = rootDirectory + "/" + file_name;
        File opendir = new File(dir);
        File settingsDir = new File(dir, "settings");
        if (opendir.exists()) {
            setScreenDims();
            if (settingsDir.exists()) {
                File settings_file = new File(settingsDir, "settings.xml");
                if (settings_file.exists()) {
                    length_type = 1; // length in minutes
                    numberOfFrames();
                } else {
                    Log.d(DTAG, "openAnimation - setting.xml DNE");
                }
            } else {
                Log.d(DTAG, "openAnimation - settings directory DNE");
            }

            masterDir = dir;
            thumbDir = masterDir + "/thumbs";

            String settings = masterDir + "/settings";
            boolean ok_to_write = checkSDCard(this);
            if (ok_to_write) {
                File xml_dir = new File(settings);
                xml_dir.mkdir();
                File file = new File(xml_dir, "settings.xml");
                readSettingsXML(file);
            }
            //loadGridView();
            loadThumbGridView();
        } else {
            tToast("That Animation doesn't exist");
            openAnimationDialog();
        }
        animPath = masterDir + "/" + file_name + ".gif";
        //Log.d(DTAG, "open_animation - animPath: " + animPath);

        return true;
    }


    /*
     * launch the drawFrame Activity
     */
    protected void launchDrawFrame(int position, long id) {
        int jpg_file = position;
        jpg_file = position + 1;
        String path = masterDir + "/" + Integer.toString(jpg_file) + ".jpeg";
        Intent intent = new Intent(this, DrawFrameActivity.class);

        intent.putExtra("image_width", image_width);
        intent.putExtra("image_height", image_height);
        intent.putExtra("path", path);
        intent.putExtra("masterDir", masterDir);
        intent.putIntegerArrayListExtra("usedColors", usedColors);
        if (thumbs_or_slider == ThumbsOrSlider.SLIDER) {
            intent.putExtra("thumbs_or_slider", 1);
        } else if (thumbs_or_slider == ThumbsOrSlider.THUMBS) {
            intent.putExtra("thumbs_or_slider", 2);
        } else {
            Log.d(DTAG, "launchDrawFrame - Something has gone horribly wrong");
        }
        //Log.d(DTAG, "launchDrawFrame - id: " + id);
        intent.putExtra("index", position);
        FrameSliderActivity.this.setResult(RESULT_OK);
        startActivityForResult(intent, DRAW_FRAME_REQUEST);
    }

    /*
     * launch animation
     */
    public void launchAnimation() {
        //memoryDialog();
        File animFile = new File(animPath);
        //Log.d(DTAG, "launchAnimation - animPath: " + animPath);
        if (animFile.exists()) {
            Intent intent = new Intent(this, GifPlayerActivity.class);
            Log.d(DTAG, "launchAnimation - animPath: " + animPath);
            intent.putExtra("animPath", animPath);
            FrameSliderActivity.this.setResult(RESULT_OK);
            startActivityForResult(intent, LAUNCH_ANIM_REQUEST);
        } else {
            tToast("You haven't created an animated GIF yet");
        }
    }

    /*
     * frames per second
     */
    public boolean setFrameRate(int rate) {
        // test for a sensical value?
        if (rate == 0) {
            tToast("You must enter a positive value for frame rate");
            createProject();
        }
        frame_rate = rate;
        //Log.d(DTAG, "frame_rate: "+frame_rate);
        return true;
    }

    /*
     * Animation length in seconds
     */
    public boolean setAnimationLength(int length) {
        // test for senical length?
        animation_length_secs = length;
        length_type = SECS;
        numberOfFrames();
        //Log.d(DTAG, "animation_length_secs: "+animation_length_secs);
        //Log.d(DTAG, "no_of_frames: "+no_of_frames);
        return true;
    }

    /*
     * Animation length in minutes
     */
    public boolean setAnimationLength(float length) {
        if (length == 0.0f) {
            tToast("You must enter a positive value for length");
            createProject();
        }
        animation_length_minutes = length;
        length_type = MINS;
        numberOfFrames();
        return true;
    }

    /*
     * calculate number of frames
     */
    private void numberOfFrames() {
        Log.d(DTAG, "frame_rate: " + frame_rate);
        Log.d(DTAG, "animation_length_secs: " + animation_length_secs);
        if (length_type == SECS) {
            no_of_frames = frame_rate * animation_length_secs;
        } else if (length_type == MINS) {
            animation_length_secs = (int) (animation_length_minutes * 60);
            no_of_frames = frame_rate * animation_length_secs;
        }
    }

    /*
     * set the file name fro inputs
     */
    public void setFileName(String name) {
        if (name.equals("")) {
            tToast("You must supply a file name");
            createProject();
        }
        file_name = name;
        this.setTitle("PROJECT: " + file_name);
    }

    private void launchWrite(){
        android.app.FragmentManager fm = getFragmentManager();

        writeDirectoryDialog = new CreateProjectDialogFragment();
        Bundle args = new Bundle();
        args.putString("file_name", file_name);
        writeDirectoryDialog.setArguments(args);
        writeDirectoryDialog.setRetainInstance(true);
        writeDirectoryDialog.show(getSupportFragmentManager(), "write_fragment");
    }

    public void write(){
        createFrames();
        writeSettingsXML();
        loadThumbGridView();
    }

    private void launchOverWrite(){
        // Build a DialogFragment to ask do you wish to overwrite this directory.
        android.app.FragmentManager fm = getFragmentManager();

        overWriteDirectoryDialog = new OverWriteDirectoryDialogFragment();
        Bundle args = new Bundle();
        args.putString("file_name", file_name);
        overWriteDirectoryDialog.setArguments(args);
        overWriteDirectoryDialog.setRetainInstance(true);
        overWriteDirectoryDialog.show(fm, "fragment_name");
    }

    /*
     * determine if this project already exists
     * if not, create it
     */
    public boolean checkDirectory(String file_name) {
        //updateExternalStorageState();
        String dir = rootDirectory;
        dir = dir + "/" + file_name;
        File newdir = new File(dir);

        return newdir.exists();
    }
    /*
    Read the list of projects from the json file
     */
    private void readProjectList() {
        String jsonStr = null;
        FileInputStream inputStream;
        updateExternalStorageState();
        String dir = Environment.getExternalStorageDirectory().toString();
        dir = dir + "/myanimation_projects";
        File projectList = new File(dir);
        projectList.mkdir();
        File projectListFile = new File(dir, "project_list.json");
        if (projectList.exists()) {
            if (projectListFile.exists()) {
                try {
                    inputStream = new FileInputStream(projectListFile);

                    FileChannel fc = inputStream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                    jsonStr = Charset.defaultCharset().decode(bb).toString();

                    inputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void updateProjectList(String filePath) {
        Log.d(DTAG, "filePath: " + filePath);
        String object = filePath;
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("project", filePath);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArrayProjects.length(); i++) {
            try {
                object = jsonArrayProjects.getString(i);
                if (object.equals(filePath)) {
                    Log.d(DTAG, "updateProjectList ");
                    jsonArrayProjects.remove(i);
                    i = jsonArrayProjects.length();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jsonArrayProjects.put(jObject);

        FileInputStream inputStream;
        FileOutputStream outStream;
        String jsonStr = null;
        JSONArray array;
        JSONObject jsonObject;

        updateExternalStorageState();
        String dir = Environment.getExternalStorageDirectory().toString();
        dir = dir + "/myanimation_projects";
        File projectList = new File(dir);
        projectList.mkdir();
        File projectListFile = new File(dir, "project_list.json");
        if (projectList.exists()) {
            if (projectListFile.exists()) {
                try {
                    inputStream = new FileInputStream(projectListFile);
                    FileChannel fc = inputStream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                    jsonStr = Charset.defaultCharset().decode(bb).toString();
                    inputStream.close();

                    array = new JSONArray(jsonStr);
                    jsonObject = new JSONObject();
                    jsonObject.put("project", filePath);
                    array.put(jsonObject);

                    outStream = new FileOutputStream(projectListFile);
                    OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream);
                    outStreamWriter.write(array.toString());
                    outStreamWriter.close();
                    Log.d(DTAG, "updateProjectList - JSONArray: " + array.toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     *  Used when over-writing with a same named project
     */
    public void deleteDirectory() {

        // Close the NavigationDrawer
        DrawerLayout mDrawerLayout;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.frame_slider_drawer);
        mDrawerLayout.closeDrawers();

        String del_dir = rootDirectory;
        del_dir = del_dir + "/" + file_name;
        String thumbs_dir = del_dir + "/" + "thumbs";
        String settings_dir = del_dir + "/" + "settings";

        File dir = new File(del_dir);
        File thumbsDir = new File(thumbs_dir);
        File settingsDir = new File(settings_dir);

        Log.d(DTAG, "deleteDirectory - del_dir: "+del_dir);
        Log.d(DTAG, "deleteDirectory - thumbs_dir: "+thumbs_dir);
        Log.d(DTAG, "deleteDirectory - settings_dir: " + settings_dir);

		/*
		 * Android System and/or FAT32 has problem maybe with deletes
		 * and releasing resource, so rename before delete
		 * + System.currentTimeMillis()
		 */
        final File to = new File(dir.getAbsolutePath());
        dir.renameTo(to);

        final File dir_to = new File(thumbsDir.getAbsolutePath());
        thumbsDir.renameTo(dir_to);

        Log.d(DTAG, "dir_to: "+dir_to.getAbsolutePath());


        final File settings_to = new File(settingsDir.getAbsolutePath());
        settingsDir.renameTo(settings_to);

        Log.d(DTAG, "settings_to: "+settings_to.getAbsolutePath());

        if (to.isDirectory()) {
            String[] children = to.list();
            if(dir_to.isDirectory()){
                Log.d(DTAG, "dir_to is a directory");
                String[] thumbChildren = dir_to.list();
                for(int j = 0; j < thumbChildren.length; j++){
                    new File(dir_to, thumbChildren[j]).delete();
                }
                dir_to.delete();
            }else{
                Log.d(DTAG, "dir_to is not a directory");
            }
            if(settings_to.isDirectory()){
                Log.d(DTAG, "settings_to is a directory");
                String[] settingsXML = settings_to.list();
                for(int k = 0; k < settingsXML.length; k++){
                    new File(settings_to, settingsXML[k]).delete();
                }
                settings_to.delete();
            }else{
                Log.d(DTAG, "setings_to is not a directory");
            }

            for (int i = 0; i < children.length; i++) {
                new File(to, children[i]).delete();
            }
            to.delete();

            createFrames();
            loadThumbGridView();
            writeSettingsXML();
        }
    }

    public boolean createFrames() {
        Log.d(DTAG, "createFrames - no_frames: "+no_of_frames);
        String name;
        setScreenDims();
        //setImageDims(screen_height, screen_width);
        for (int i = 0; i < no_of_frames; i++) {
            name = Integer.toString(i + 1);
            createJPGs(name);
            createThumbJPGs(name);
        }
        return true;
    }

    private void setScreenDims() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_height = size.y;
        screen_width = size.x;
        rowHeight = twoWayGridView.getHeight();
        thumbRowHeight = rowHeight / 3;
    }

    private void createJPGs(String jpg_name) {
        //int yourwidth = (int) (screen_width*.7);
        //int yourheight = (int) (screen_height*.7);

        Bitmap bm = Bitmap.createBitmap(image_width, image_height, Bitmap.Config.ARGB_8888);

        // Make a canvas with which we can draw to the bitmap
        Canvas canvas = new Canvas(bm);
        canvas.drawColor(0xffffffff);
        //Log.d(DTAG, "createJPGs - bm nul: "+bm);
        try {
            if (bm != null) {
                boolean ok_to_write = checkSDCard(this);

                if (ok_to_write) {
                    updateExternalStorageState();

                    masterDir = rootDirectory + "/" + file_name;
                    File newdir = new File(masterDir);
                    if (!newdir.exists()) {
                        newdir.mkdir();
                    }
                    OutputStream fos = null;
                    File file = new File(newdir.getAbsolutePath(), jpg_name + ".jpeg");
                    // if file doesn't exists, then create it
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();
                    bm.recycle();
                } else {
                    Log.d(DTAG, "sdcard_not_ok_to_write");
                }
            }
        } catch (Exception e) {
            System.out.println("Error=" + e);
            e.printStackTrace();
            Log.d(DTAG, "CreateJPGs - " + e);
        }
    }

    public void setImageSize(int height, int width) {
        image_width = width;
        image_height = height;
    }

    public int getScreenHeight() {
        return screen_height;
    }

    public int getScreenWidth() {
        return screen_width;
    }

    private void createThumbJPGs(String thumbName) {
        int yourwidth = (int) (image_width * .07);
        int yourheight = (int) (image_height * .07);
        String thumbs;

        Bitmap bm = Bitmap.createBitmap(yourwidth, yourheight, Bitmap.Config.ARGB_8888);

        // Make a canvas with which we can draw to the bitmap
        Canvas canvas = new Canvas(bm);
        canvas.drawColor(0xffffffff);
        //Log.d(DTAG, "createThumbJPGs - bitmap: "+bm);
        try {
            if (bm != null) {
                boolean ok_to_write = checkSDCard(this);
                if (ok_to_write) {
                    updateExternalStorageState();
                    //String dir = Environment.getExternalStorageDirectory().toString();
                    masterDir = rootDirectory + "/" + file_name;
                    thumbs = masterDir + "/" + "thumbs";
                    thumbDir = thumbs;
                    Log.d(DTAG, "thumbDir " + thumbDir);
                    File thumbdir = new File(thumbs);

                    // make the directory first pass thru
                    if (!thumbdir.exists()) {
                        boolean d = thumbdir.mkdir();
                    }
                    OutputStream fos;
                    File file = new File(thumbdir.getAbsolutePath(), thumbName + ".jpeg");
                    // if file doesn't exists, then create it
                    if (!file.exists()) {
                        boolean f = file.createNewFile();
                        Log.d(DTAG, "creatNewFile: " + f);
                    }
                    fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                    bos.flush();
                    bos.close();
                    bm.recycle();
                } else {
                    Log.d(DTAG, "sdcard_not_ok_to_write");
                }
            }
        } catch (Exception e) {
            System.out.println("Error=" + e);
            e.printStackTrace();
            Log.d(DTAG, "CreateJPGs - " + e);
        }
    }

    /*
    protected void loadGridView() {
        File frameDir = new File(masterDir);
        File[] imageFiles;
        FrameCell fCell;
        TextView view;
        ArrayList<FrameCell> frameCells;
        if (frameDir.exists()) {
            thumbs_or_slider = ThumbsOrSlider.SLIDER;
            frameCells = new ArrayList<FrameCell>();

            myGridView = (TwoWayGridView) findViewById(R.id.gridview);

            myGridView.setRowHeight(rowHeight);
            myGridView.setPadding(0, 0, 0, 0);
            myGridView.setNumRows(1);


            imageFiles = frameDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jpeg");
                }
            });

            // Let's make sure the files are sorted
            Arrays.sort(imageFiles, new Comparator<File>() {

                @Override
                public int compare(File lhs, File rhs) {

                    String left = lhs.getName();
                    String right = rhs.getName();
                    left = left.substring(0, left.length() - 5);
                    right = right.substring(0, right.length() - 5);
                    try {
                        int i1 = Integer.parseInt(left);
                        int i2 = Integer.parseInt(right);
                        return i1 - i2;
                    } catch (NumberFormatException e) {
                        throw new AssertionError(e);
                    }
                }

            });


            int i = 0;
            for (File file : imageFiles) {

                view = new TextView(this);
                view.setText(file.getName());
                fCell = new FrameCell(this, null, file, view);
                frameCells.add(fCell);

                Log.d(DTAG, "loadGridView frameCells.get(" + i + ").toString " + frameCells.get(i).toString());
                i++;
            }

            myGridView.setAdapter(new ImageAdapter(this, 0, frameCells));

            myGridView.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
                public void onItemClick(TwoWayAdapterView<?> parent, View v, int position, long id) {
                    Log.d(DTAG, "onClickListener, position: " + position + ", id: " + id);
                    launchDrawFrame(position, id);
                }
            });
        } else {
            Log.d(DTAG, "loadGridView() failed frameDir.exists() " + frameDir.exists());
        }
    }
    */

    protected void loadThumbGridView() {
        File thumbsDir = new File(thumbDir);
        File[] imageFiles;
        FrameCell fCell;
        TextView textView;
        ArrayList<FrameCell> frameCells;
        Log.d(DTAG, "loadThumbGridView - thumbsDir.exists: "+thumbsDir.exists());
        Log.d(DTAG, "thumbsDir.toString: "+thumbDir.toString());
        if (thumbsDir.exists()) {
            thumbs_or_slider = ThumbsOrSlider.THUMBS;
            frameCells = new ArrayList<FrameCell>();

            myGridView = (TwoWayGridView) findViewById(R.id.gridview);
            myGridView.setBackgroundColor(getResources().getColor(R.color.primaryCyan));

            myGridView.setRowHeight(thumbRowHeight);
            myGridView.setPadding(0, 10, 0, 0);
            myGridView.setNumRows(3);

            imageFiles = thumbsDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jpeg");
                }
            });
            Log.d(DTAG, "loadThumbGridView - imageFiles.size: "+imageFiles.length);
            // Let's make sure the files are sorted
            Arrays.sort(imageFiles, new Comparator<File>() {

                @Override
                public int compare(File lhs, File rhs) {

                    String left = lhs.getName();
                    String right = rhs.getName();
                    left = left.substring(0, left.length() - 5);
                    right = right.substring(0, right.length() - 5);
                    try {
                        int i1 = Integer.parseInt(left);
                        int i2 = Integer.parseInt(right);
                        return i1 - i2;
                    } catch (NumberFormatException e) {
                        throw new AssertionError(e);
                    }
                }

            });

            int i = 0;

            for (File file : imageFiles) {
                Log.d(DTAG, "loadThumbGridView - image_file_name: "+file.getAbsolutePath());
                textView = new TextView(this);
                textView.setText(file.getName());
                fCell = new FrameCell(this, null, file, textView);
                frameCells.add(fCell);
                i++;
            }
            Log.d(DTAG, "frameCells.size: "+frameCells.size());

            myGridView.setAdapter(new ImageAdapter(this, 0, frameCells));
            Log.d(DTAG, "loadThumbGridView after setAdapter");
            myGridView.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
                public void onItemClick(TwoWayAdapterView<?> parent, View v, int position, long id) {

                    launchDrawFrame(position, id);
                }
            });
            Log.d(DTAG, "loadThumbsGridView after setOnClickListener");
        } else {
            Log.d(DTAG, "loadGridView() failed frameDir.exists() " + thumbsDir.exists());
        }
    }

    public static Bitmap convertToBitmap(File file) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeFile(file.toString());
        } catch (java.lang.OutOfMemoryError e) {
            Log.d(DTAG, "convertToBitmap - bmp null");
        }
        return bmp;
    }//convertToBitmap

    /*
    public String readFileName(File file) {
        String name = file.getName();
        return name;
    }
    */
    public static Boolean checkSDCard(Context mContext) {
        String auxSDCardStatus = Environment.getExternalStorageState();
        //Toast.makeText(mContext, Environment.getExternalStorageDirectory().toString(), Toast.LENGTH_LONG).show();
        if (auxSDCardStatus.equals(Environment.MEDIA_MOUNTED))
            return true;
        else if (auxSDCardStatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(
                    mContext,
                    "Warning, the SDCard is in read only mode.\nthis does not result in malfunction"
                            + " of the read aplication", Toast.LENGTH_LONG)
                    .show();
            return true;
        } else if (auxSDCardStatus.equals(Environment.MEDIA_NOFS)) {
            Toast.makeText(
                    mContext,
                    "Error, the SDCard cannot be used, it is not in the correct format or "
                            + "is not formated.", Toast.LENGTH_LONG)
                    .show();
            Log.d(DTAG, "SDCard not in the correct format");
            return false;
        } else if (auxSDCardStatus.equals(Environment.MEDIA_REMOVED)) {
            Toast.makeText(
                    mContext,
                    "Error, the SDCard is not found, to use the reader you need "
                            + "insert a SDCard on the device.",
                    Toast.LENGTH_LONG).show();
            Log.d(DTAG, "SDCard is not found");
            return false;
        } else if (auxSDCardStatus.equals(Environment.MEDIA_SHARED)) {
            Toast.makeText(
                    mContext,
                    "Error, the SDCard is not mounted because is using "
                            + "connected by USB. Plug out and try again.",
                    Toast.LENGTH_LONG).show();
            Log.d(DTAG, "SDCard is not mounted, connected by USB");
            return false;
        } else if (auxSDCardStatus.equals(Environment.MEDIA_UNMOUNTABLE)) {
            Toast.makeText(
                    mContext,
                    "Error, the SDCard cant be mounted.\nThe may be happend when the SDCard is corrupted "
                            + "or crashed.", Toast.LENGTH_LONG).show();
            Log.d(DTAG, "SDCard cant be mounted, corrupted of crashed");
            return false;
        } else if (auxSDCardStatus.equals(Environment.MEDIA_UNMOUNTED)) {
            Toast.makeText(
                    mContext,
                    "Error, the SDCArd is on the device but is not mounted."
                            + "Mount it before use the app.",
                    Toast.LENGTH_LONG).show();
            Log.d(DTAG, "SDCArd is on the device but is not mounted.");
            return false;
        }

        return true;
    }

    BroadcastReceiver mExternalStorageReceiver;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;

    void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        handleExternalStorageState(mExternalStorageAvailable,
                mExternalStorageWriteable);
    }

    private void handleExternalStorageState(boolean mExternalStorageAvailable2,
                                            boolean mExternalStorageWriteable2) {
        if (mExternalStorageAvailable2 && mExternalStorageAvailable2) {
            Log.d(DTAG, "handleExternalStorageState - mExternalStorageAvailable2 & mExternalStorageAvailable2: true");
        } else if (mExternalStorageWriteable2) {
            Log.d(DTAG, "handleExternalStorageState - mExternalStorageWriteable2 true");
        } else if (mExternalStorageAvailable2) {
            Log.d(DTAG, "handleExternalStorageState - mExternalStorageAvailable2 true");
        } else {
            Log.d(DTAG, "handleExternalStorageState - mExternalStorageAvailable2 & mExternalStorageAvailable2: false");
        }
    }

    void startWatchingExternalStorage() {
        mExternalStorageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("test", "Storage: " + intent.getData());
                updateExternalStorageState();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        registerReceiver(mExternalStorageReceiver, filter);
        updateExternalStorageState();
    }

    void stopWatchingExternalStorage() {
        unregisterReceiver(mExternalStorageReceiver);
    }

    public void tToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }

    public void tToast(String s, int time) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }

    private void emailGif() {

        if (animPath != null) {

            Intent i = new Intent(Intent.ACTION_SEND, Uri.parse(animPath));
            i.setType(getMimeType(animPath));
            final Uri uri = Uri.parse(animPath);
            i.setType("text/html");
            i.putExtra(Intent.EXTRA_SUBJECT, "You must manually attach the Gif");
            i.putExtra(Intent.EXTRA_TEXT, "Your animated gif is at: " + uri.toString());
            //i.putExtra(Intent.EXTRA_STREAM, uri); // this is the attachment code that don't work

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                //i.setType(null);
                //i.putExtra(Intent.EXTRA_SUBJECT, "My Gif " + uri.toString());
                //i.putExtra(Intent.EXTRA_TEXT, "Your animated gif is at: " + uri.toString());
                //final Intent restrictedIntent = new Intent(Intent.ACTION_SENDTO);// restricting intent
                //Uri anim = Uri.parse("mailto:?to");//mailto:?to=conkle_mark@yahoo.com
                //Uri anim = Uri.fromParts("mail to", "", null);
                //restrictedIntent.setData(anim);
                //i.setSelector(restrictedIntent);
            }
            //emailDialogFragment(animPath);
            try {
                startActivity(Intent.createChooser(i, "send gif"));
            } catch (android.content.ActivityNotFoundException ex) {
                tToast(ex.toString());
            }
        } else {
            tToast("You either haven't opened a project or you have not created a gif", 1);
        }
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public void onBackPressed() {

        Log.d(DTAG, "onBackPressed");

        android.app.FragmentManager fm = getFragmentManager();

        OnBackPressedDialogFragment onBack = new OnBackPressedDialogFragment();
        onBack.setRetainInstance(true);
        onBack.show(fm, "fragment_name");
    }

    private class BuildGif extends AsyncTask<Void, Void, Boolean> {

        private boolean built;
        private static final String TAG = "BuildGif";


        public BuildGif() {
            built = false;
            progressDialog = new ProgressDialog(FrameSliderActivity.this);
            progressDialog.setMessage("Be Patient, encoding gif file.");
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute - masterDir: " + masterDir);
            Log.d(TAG, "onPreExecute - file_name: " + file_name);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            animPath = masterDir + "/" + file_name + ".gif";

            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(animPath);
                outStream.write(generateGIF());
                outStream.close();
                memoryDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return built;
        }

        @Override
        protected void onPostExecute(Boolean isBuilt) {
            Log.d(TAG, "onPostExecute - animPath: " + animPath);
            progressDialog.dismiss();
        }

    }
}


