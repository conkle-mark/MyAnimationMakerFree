package com.bniproductions.android.myanimationmaker;

/**
 * Created by Mark on 9/18/2015.
 */
public class AboutNavigationDrawerInfo {

    int iconId;
    String title;

    public AboutNavigationDrawerInfo(){

    }

    public AboutNavigationDrawerInfo(int iconId, String title){
        this.iconId = iconId;
        this.title = title;
    }

    int getIconId(){
        return iconId;
    }

    void setIconId(int iconId){
        this.iconId = iconId;
    }

    String getTitle(){
        return title;
    }

    void setTitle(String title){
        this.title = title;
    }
}
