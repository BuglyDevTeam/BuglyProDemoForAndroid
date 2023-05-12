package com.tencent.demo.buglyprodemo;

import android.app.Application;
import android.os.Build;
import android.os.Debug;
import com.tencent.bugly.library.Bugly;
import com.tencent.bugly.library.BuglyAppVersionMode;
import com.tencent.bugly.library.BuglyBuilder;
import com.tencent.bugly.library.BuglyLogLevel;
import com.tencent.bugly.library.BuglyMonitorName;
import com.tencent.rmonitor.base.plugin.listener.ICustomDataCollector;
import com.tencent.rmonitor.base.plugin.listener.ICustomDataCollectorForIssue;
import com.tencent.rmonitor.custom.ICustomDataEditor;
import com.tencent.rmonitor.custom.ICustomDataEditorForIssue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class BuglyWrapper {

    // 自定义维度的上报KEY
    private static final String KEY_ACTIVITY_SWITCH_SEQ = ICustomDataEditor.STRING_ARRAY_PARAM_1;
    private static final String KEY_CURRENT_SCENE = ICustomDataEditor.STRING_PARAM_1;
    private static final String KEY_PSS = ICustomDataEditor.NUMBER_PARAM_1;
    private static final String KEY_GLOBAL_ALLOC_SIZE = ICustomDataEditor.NUMBER_PARAM_2;
    private static final String KEY_GLOBAL_ALLOC_COUNT = ICustomDataEditor.NUMBER_PARAM_3;

    private final Random random = new Random(System.currentTimeMillis());

    private static volatile BuglyWrapper instance = null;

    public static BuglyWrapper getInstance() {
        if (instance == null) {
            synchronized (BuglyWrapper.class) {
                if (instance == null) {
                    instance = new BuglyWrapper();
                }
            }
        }
        return instance;
    }

    public void changeResumedActivity(String activityName) {
        // 更新全局的自定义数据
        ICustomDataEditor editor = Bugly.getGlobalCustomDataEditor();
        if (editor != null) {
            editor.addStringToSequence(KEY_ACTIVITY_SWITCH_SEQ, activityName);
        }
    }

    public void initBugly(Application application) {
        String appKey = "1e5ab6b3-b6fa-4f9b-a3c2-743d31dffe86";
        String appID = "a278f01047";
        BuglyBuilder buglyBuilder = new BuglyBuilder(appID, appKey);

        buglyBuilder.userId = getUserID();
        buglyBuilder.uniqueId = getDeviceID();
        buglyBuilder.buildNumber = getBuildNumber();
        buglyBuilder.appVersion = getAppVersion();
        buglyBuilder.appVersionType = BuglyAppVersionMode.DEBUG;
        buglyBuilder.deviceModel = Build.MODEL;
        buglyBuilder.logLevel = BuglyLogLevel.LEVEL_DEBUG;

        buglyBuilder.debugMode = true;
        buglyBuilder.enableCrashProtect = true;
        buglyBuilder.enableAllThreadStackAnr = true;
        buglyBuilder.enableAllThreadStackCrash = true;

        Bugly.init(application, buglyBuilder);

        // 设置质量监控的自定义数据
        Bugly.putUserData(application, "testKey", "testValue");
        // 设置自定义文件
        File file = getCustomFilePath(application);
        Bugly.setAdditionalAttachmentPaths(new String[]{file.getPath()});

        // 设置性能监控的自定义数据采集器
        Bugly.addCustomDataCollector(customDataCollectorForIssue);
        Bugly.addCustomDataCollector(customDataCollectorForMetric);
    }

    private String getUserID() {
        return "10000" + random.nextInt();
    }

    private String getDeviceID() {
        return UUID.randomUUID().toString();
    }

    private String getAppVersion() {
        return "1.0.0";
    }

    private String getBuildNumber() {
        return "1";
    }

    private File getCustomFilePath(Application application) {
        // 生成一个本地自定义文件示例
        byte[] bytes = new byte[1024];
        String testData = "This is a custom file test, you can write any content in files.\n"
                + "Make sure files after zip smaller than 10MB!";
        File filesDir = application.getFilesDir();
        File file = new File(filesDir, "test.txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(testData);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private final ICustomDataCollector customDataCollectorForMetric = new ICustomDataCollector() {
        @Override
        public void collectCustomData(String pluginName, String scene, ICustomDataEditor editor) {
            if (BuglyMonitorName.FLUENCY_METRIC.equals(pluginName)) {
                editor.putStringParam(KEY_CURRENT_SCENE, scene);
            } else if (BuglyMonitorName.MEMORY_METRIC.equals(pluginName)) {
                editor.putNumberParam(KEY_PSS, Debug.getPss());
                editor.putNumberParam(KEY_GLOBAL_ALLOC_SIZE, Debug.getGlobalAllocSize());
                editor.putNumberParam(KEY_GLOBAL_ALLOC_COUNT, Debug.getGlobalAllocCount());
            }
        }
    };

    private final ICustomDataCollectorForIssue customDataCollectorForIssue = new ICustomDataCollectorForIssue() {
        @Override
        public void collectCustomData(String pluginName, String scene, ICustomDataEditorForIssue editor) {
            if (BuglyMonitorName.LOOPER_STACK.equals(pluginName)) {
                editor.putStringParam(KEY_CURRENT_SCENE, scene);
                editor.putUserData("isDebuggerConnected", String.valueOf(Debug.isDebuggerConnected()));
                editor.putUserData("GlobalGcInvocationCount", String.valueOf(Debug.getGlobalGcInvocationCount()));
                editor.putUserData("ThreadGcInvocationCount", String.valueOf(Debug.getThreadGcInvocationCount()));
            } else if (BuglyMonitorName.MEMORY_BIG_BITMAP.equals(pluginName)) {
                editor.putNumberParam(KEY_PSS, Debug.getPss());
                editor.putNumberParam(KEY_GLOBAL_ALLOC_SIZE, Debug.getGlobalAllocSize());
                editor.putNumberParam(KEY_GLOBAL_ALLOC_COUNT, Debug.getGlobalAllocCount());
                editor.putUserData("GlobalFreedCount", String.valueOf(Debug.getGlobalFreedCount()));
                editor.putUserData("GlobalFreedSize", String.valueOf(Debug.getGlobalFreedSize()));
            } else if (BuglyMonitorName.MEMORY_JAVA_LEAK.equals(pluginName)) {
                editor.putNumberParam(KEY_PSS, Debug.getPss());
                editor.putNumberParam(KEY_GLOBAL_ALLOC_SIZE, Debug.getGlobalAllocSize());
                editor.putNumberParam(KEY_GLOBAL_ALLOC_COUNT, Debug.getGlobalAllocCount());
                editor.putUserData("LoadedClassCount", String.valueOf(Debug.getLoadedClassCount()));
            }
        }
    };
}
