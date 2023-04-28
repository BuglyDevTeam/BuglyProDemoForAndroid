package com.tencent.demo.buglyprodemo;

import android.app.Application;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BuglyWrapper.getInstance().initBugly(this);
    }
}
