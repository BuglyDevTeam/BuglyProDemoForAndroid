package com.tencent.demo.buglyprodemo;

import android.app.Application;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.bugly.library.Bugly;
import com.tencent.bugly.library.BuglyBuilder;
import com.tencent.bugly.library.BuglyConstants;

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
        String userID = "test_user_id";
        String uniqueID = "test_unique_id";
        BuglyBuilder buglyBuilder = new BuglyBuilder(appID, appKey);
        buglyBuilder.setProperty(BuglyConstants.BUILD_USER_ID, userID);
        buglyBuilder.setProperty(BuglyConstants.BUILD_UNIQUE_ID, uniqueID);
        Bugly.init(application, buglyBuilder);
    }
}