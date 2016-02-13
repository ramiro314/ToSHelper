package com.nullaxis.tos;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by Ramiro on 1/29/16.
 */
public class ToSApp extends Application {

    private HttpProxyCacheServer proxy;
    public static final String PREFS_NAME = "ToSSettings";
    private static final String LOG_TAG = "ToSApp";

    public static HttpProxyCacheServer getProxy(Context context) {
        ToSApp app = (ToSApp) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy(context)) : app.proxy;
    }

    private HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(getVideoCacheSize(context) * 1024 * 1024)
                .build();
    }

    public static int getVideoCacheSize(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt("videoCacheSize", 512);
    }

    public static void setVideoCacheSize(Context context, int videoCacheSize) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt("videoCacheSize", videoCacheSize);
        Log.d(LOG_TAG, "New videoCacheSize: " + videoCacheSize);
        editor.commit();
    }
}
