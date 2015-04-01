package com.example.dillon.barcodescanneruser;

import android.app.Application;
import android.content.Context;

/**
 * Created by Jevon on 30/03/2015.
 */
public class MyApplication extends Application {
    private static MyApplication appInstance=null;

    @Override
    public void onCreate(){
        super.onCreate();
        appInstance=this;
    }

    public static MyApplication getInstance(){
        return appInstance;
    }

    public static Context getAppContext(){
        return appInstance.getApplicationContext();
    }
}
