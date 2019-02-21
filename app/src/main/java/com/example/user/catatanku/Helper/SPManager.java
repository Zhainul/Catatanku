package com.example.user.catatanku.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SPManager {

    public static final String SP_KEY = "MyManager";
    public static final String SP_EXIT = "SudahLogin";
    private static final String SP_EMAIL = "email";
    private static final String SP_VIEW = "MyView";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;


    public SPManager(Context context){
        mSharedPreferences = context.getSharedPreferences(SP_KEY,Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void save_login(String keyLogin,boolean value){
        mEditor.putBoolean(keyLogin,value);
        mEditor.commit();
    }

    public void save_email(String email){
        mEditor.putString(SP_EMAIL,email);
        mEditor.commit();
    }

    public void save_view(boolean grid){
        mEditor.putBoolean(SP_VIEW,grid);
        mEditor.commit();
    }

    public boolean get_view(){
        return mSharedPreferences.getBoolean(SP_VIEW,false);
    }

    public String getSpEmail(){
        return mSharedPreferences.getString(SP_EMAIL,"");
    }

    public void clearSP(){
        mEditor.clear();
        mEditor.commit();
    }

    public boolean sudah_login(){
        return mSharedPreferences.getBoolean(SP_EXIT,false);
    }
}
