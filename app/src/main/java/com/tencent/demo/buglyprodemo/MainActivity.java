package com.tencent.demo.buglyprodemo;

import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.bugly.library.Bugly;
import com.tencent.bugly.library.BuglyBuilder;
import com.tencent.bugly.library.BuglyLogLevel;
import com.tencent.bugly.library.BuglyMonitorName;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBugly();
    }

    private void initBugly() {
        Application application = getApplication();
        String appKey = "b860b0bc-fe8d-4356-8ba5-2ff74e21c48c";
        String appID = "ae9c799533";

        BuglyBuilder buglyBuilder = new BuglyBuilder(appID, appKey);
        buglyBuilder.appVersion = "4.5.8";
        buglyBuilder.buildNumber = "12";
        buglyBuilder.userId = "test_user_id";
        buglyBuilder.uniqueId = "test_unique_id";
        buglyBuilder.appVersionType = "Gray";
        buglyBuilder.monitorList = BuglyMonitorName.ALL_MONITOR;
        buglyBuilder.deviceModel = Build.MODEL;
        buglyBuilder.enableCrashProtect = true;
        buglyBuilder.logLevel = BuglyLogLevel.LEVEL_DEBUG;
        Bugly.init(application, buglyBuilder);
    }
}