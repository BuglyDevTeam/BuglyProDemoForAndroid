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

    private static BuglyWrapper instance = null;

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
        String appKey = "4b9b0739-1c9b-46a7-95b5-0c4aaff4e00a";
        String appID = "3b2e6d73d3";
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

    private ICustomDataCollector customDataCollectorForMetric = new ICustomDataCollector() {
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

    private ICustomDataCollectorForIssue customDataCollectorForIssue = new ICustomDataCollectorForIssue() {
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
