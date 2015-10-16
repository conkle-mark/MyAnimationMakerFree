package com.bniproductions.android.myanimationmaker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private final static  String TAG = "AboutActivity";
    Toolbar toolbar;
    TextView textView;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textView = (TextView) findViewById(R.id.about_text);
        textView.setMovementMethod(new ScrollingMovementMethod());

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.myanimationicon);

        AboutNavigationFragment navigationFragment = (AboutNavigationFragment)
                getSupportFragmentManager().findFragmentById(R.id.about_fragment);

        navigationFragment.setUp(R.id.about_fragment, (DrawerLayout) findViewById(R.id.about_drawer_layout), toolbar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed");
    }
}
