package com.sakthisugars.salesandmarketing;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by user on 9/27/2016.
 */
public class Session {
    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }
    public void setusename(String usename) {
        prefs.edit().putString("usename", usename).commit();
        //prefsCommit();
    }

    public String getusename() {
        String usename = prefs.getString("usename","");
        return usename;
    }
}
