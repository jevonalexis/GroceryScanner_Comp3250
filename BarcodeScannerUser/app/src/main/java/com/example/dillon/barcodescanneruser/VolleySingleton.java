package com.example.dillon.barcodescanneruser;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Jevon on 30/03/2015.
 */
public class VolleySingleton {
    private static VolleySingleton volleyInstance=null;
    private RequestQueue mRequestQueue;
    private VolleySingleton(){
        mRequestQueue= Volley.newRequestQueue(MyApplication.getAppContext());
    }
    public static synchronized VolleySingleton getInstance(){
        if(volleyInstance==null){
            volleyInstance=new VolleySingleton();
        }

        return volleyInstance;
    }

    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }
}
