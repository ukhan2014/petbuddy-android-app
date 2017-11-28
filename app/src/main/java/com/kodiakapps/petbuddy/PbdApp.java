package com.kodiakapps.petbuddy;

import android.app.Application;

/**
 * Created by usman on 11/26/17.
 */

public class PbdApp extends Application {
    private static final int WIFI_PATH_PBD_DIRECT = 0;          // Direct wifi conn, PBD in AP mode
    private static final int WIFI_PATH_PBD_ON_HOME_WIFI = 1;    // PBD in STA mode

    protected static int wifiPath;

    public int getWifiPath() {
        return wifiPath;
    }

    public void setWifiPath(int path) {
        this.wifiPath = path;
    }
}