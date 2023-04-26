package com.tencent.demo.buglyprodemo;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.bugly.launch.AppLaunchProxy;
import com.tencent.bugly.library.Bugly;
import com.tencent.bugly.library.BuglyAppVersionMode;
import com.tencent.bugly.library.BuglyBuilder;
import com.tencent.bugly.library.BuglyConstants;
import com.tencent.bugly.library.BuglyLogLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final Random random = new Random(System.currentTimeMillis());

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBugly();
        initButton();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final int value = random.nextInt(4);
                if (value <= 2) {
                    costJobOne();
                }
                AppLaunchProxy.getAppLaunch().reportAppFullLaunch();
            }
        }, 500L);
    }

    private void costJobOne() {
        AppLaunchProxy.getAppLaunch().addTag("has_cost_job_one");
        AppLaunchProxy.getAppLaunch().spanStart("costJobOne", null);
        try {
            Thread.sleep(300);
        } catch (Throwable t) {
            Bugly.handleCatchException(Thread.currentThread(), t, "costJobOne sleep fail", null, true);
        }
        if (random.nextBoolean()) {
            costJobTwo("costJobOne");
        }
        AppLaunchProxy.getAppLaunch().spanEnd("costJobOne");
    }

    private void costJobTwo(String parent) {
        AppLaunchProxy.getAppLaunch().addTag("has_cost_job_two");
        AppLaunchProxy.getAppLaunch().spanStart("costJobTwo", parent);
        try {
            Thread.sleep(200);
        } catch (Throwable t) {
            Bugly.handleCatchException(Thread.currentThread(), t, "costJobTwo sleep fail", null, true);
        }
        AppLaunchProxy.getAppLaunch().spanEnd("costJobTwo");
    }

    private void initButton() {
        Button button = (Button)findViewById(R.id.test_java_crash);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("测试Java崩溃");
                Bugly.testCrash(BuglyConstants.JAVA_CRASH);
            }
        });

        button = (Button)findViewById(R.id.test_native_crash);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("测试Native崩溃");
                Bugly.testCrash(BuglyConstants.NATIVE_CRASH);
            }
        });

        button = (Button)findViewById(R.id.test_anr);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("测试ANR");
                Bugly.testCrash(BuglyConstants.ANR_CRASH);
            }
        });

        button = (Button)findViewById(R.id.test_oom);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                testOOM();
            }
        });

        button = (Button)findViewById(R.id.test_error1);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                testJavaCatchError();
            }
        });

        button = (Button)findViewById(R.id.test_error2);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                testCustomError();
            }
        });

        button = (Button)findViewById(R.id.test_long_lag);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                testLongLag();
            }
        });

        button = (Button)findViewById(R.id.test_looper_stack);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                testScrollLag();
            }
        });

        button = (Button)findViewById(R.id.test_activity_leak);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                testActivityLeak();
            }
        });

        button = (Button)findViewById(R.id.test_java_leak);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                testJavaObjLeak();
            }
        });

        button = (Button)findViewById(R.id.test_fragment_leak);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                testFragmentLeak();
            }
        });

        button = (Button)findViewById(R.id.test_big_bitmap);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                testBigBitmap();
            }
        });
    }

    private String getUserID() {
        return "10000" + random.nextInt();
    }

    private String getDeviceID() {
        return UUID.randomUUID().toString();
    }

    private String getAppVersion() {
        return "4.5." + random.nextInt(9);
    }

    private String getBuildNumber() {
        return String.valueOf(random.nextInt(100));
    }

    private void initBugly() {
        Application application = getApplication();
        String appKey = "4b9b0739-1c9b-46a7-95b5-0c4aaff4e00a";
        String appID = "3b2e6d73d3";

        BuglyBuilder buglyBuilder = new BuglyBuilder(appID, appKey);
        buglyBuilder.appVersion = getAppVersion();
        buglyBuilder.buildNumber = getBuildNumber();
        buglyBuilder.userId = getUserID();
        buglyBuilder.uniqueId = getDeviceID();
        buglyBuilder.appVersionType = BuglyAppVersionMode.DEBUG;
        buglyBuilder.deviceModel = Build.MODEL;
        buglyBuilder.enableCrashProtect = true;
        buglyBuilder.logLevel = BuglyLogLevel.LEVEL_DEBUG;

        Bugly.init(application, buglyBuilder);
    }

    private void testBigBitmap() {
        showToast("测试大图分析");
        Intent intent = new Intent(MainActivity.this, TestBitmapActivity.class);
        startActivity(intent);
    }

    private void testFragmentLeak() {
        showToast("测试Fragment泄露");
        Intent intent = new Intent(MainActivity.this, TestFragmentLeakActivity.class);
        startActivity(intent);
    }

    private void testActivityLeak() {
        showToast("测试Activity泄露");
        Intent intent = new Intent(MainActivity.this, TestActivityLeakActivity.class);
        startActivity(intent);
    }

    private void testScrollLag() {
        showToast("测试滑动卡顿");
        Intent intent = new Intent(MainActivity.this, TestLagActivity.class);
        startActivity(intent);
    }

    private void testJavaObjLeak() {
        showToast("测试Java对象泄露");
        ArrayList<String> list = new ArrayList<>(1000);
        for (int i = 0; i < 50000; i++) {
            list.add(String.format(Locale.getDefault(), "%d: %s", i, UUID.randomUUID().toString()));
        }
        LeakKeeper.leakObj(list);
        Bugly.startInspectLeakObj(list);
    }

    private void testOOM() {
        showToast("测试OOM异常");
        int index = random.nextInt(3);
        if (index == 0) {
            tooManyMemory();
        } else if (index == 1) {
            tooManyFD();
        } else {
            tooManyThread();
        }
    }

    private void tooManyFD() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw new OutOfMemoryError("use too many fd.");
            }
        });
        thread.start();
    }

    private void tooManyThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Thread> list = new ArrayList<>();
                for(;;){
                    Thread thread = new Thread(new CostTask(5000));
                    thread.start();
                    list.add(thread);
                }
            }
        });
        thread.start();
    }

    private void tooManyMemory() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int bufferSize = 1024 * 1024 * 1024;
                List<Object> list = new ArrayList<>();
                for (;;){
                    list.add(new byte[bufferSize]);
                }
            }
        });
        thread.start();
    }

    private void testLongLag() {
        showToast("测试长卡顿");
        sleep(200);
        callFunctionA();
        callFunctionB();
    }

    private void callFunctionA() {
        sleep(400);
        callFunctionB();
    }

    private void callFunctionB() {
        sleep(300);
    }

    private void testJavaCatchError() {
        showToast("测试Java捕获错误");
        String content = "a illegal string.";
        try {
            JSONObject jsonObject = new JSONObject(content);
            double price  = jsonObject.getDouble("price");
        } catch (Throwable t) {
            Bugly.handleCatchException(Thread.currentThread(), t,
                    "testJavaCatchError", content.getBytes(), true);
        }
    }

    private void testCustomError() {
        showToast("测试自定义错误");
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private static class CostTask implements Runnable {

        private long costInMs;

        public CostTask(long costInMs) {
            this.costInMs = costInMs;
        }

        @Override
        public void run() {
            sleep(costInMs);
        }
    }

    private static void sleep(long timeInMs) {
        try {
            Thread.sleep(timeInMs);
        } catch (Throwable t) {
            Bugly.handleCatchException(Thread.currentThread(), t, "sleep", null, false);
        }
    }
}