package com.tencent.demo.buglyprodemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.bugly.launch.AppLaunchProxy;
import com.tencent.bugly.library.Bugly;
import com.tencent.bugly.library.BuglyConstants;
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
        initButton();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final int value = random.nextInt(4);
                if (value <= 2) {
                    costJobOne();
                }
                if (random.nextBoolean()) {
                    costJobOne();
                }
                if (random.nextBoolean()) {
                    costJobTwo(null);
                }
                if (random.nextBoolean()) {
                    callFunctionC();
                }
                if (random.nextBoolean()) {
                    callFunctionE();
                }
                AppLaunchProxy.getAppLaunch().reportAppFullLaunch();
            }
        }, 500L);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BuglyWrapper.getInstance().changeResumedActivity(this.getClass().getSimpleName());
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
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // testAndroidX();
                        // Bugly.testCrash(BuglyConstants.JAVA_CRASH);
                        testJavaCrash();
                    }
                });
                thread.start();
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

    private void testJavaCrash() {
        ArrayList<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        StringBuilder full = new StringBuilder();
        for(int i = 0; i < 4; i++) {
            String value = list.get(i);
            full.append(",").append(value);
        }
        Log.i("Test", full.toString());
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
        if (random.nextBoolean()) {
            callFunctionA();
        }
        if (random.nextBoolean()) {
            callFunctionB("testLongLag");
        }
        if (random.nextBoolean()) {
            callFunctionC();
        }
        if (random.nextBoolean()) {
            callFunctionD("testLongLag");
        }
        if (random.nextBoolean()) {
            callFunctionE();
        }
    }

    private void callFunctionA() {
        AppLaunchProxy.getAppLaunch().spanStart("FunctionA", null);
        sleep(400);
        callFunctionB("FunctionA");
        AppLaunchProxy.getAppLaunch().spanEnd("FunctionB");
    }

    private void callFunctionB(String parent) {
        AppLaunchProxy.getAppLaunch().spanStart("FunctionB", parent);
        sleep(300);
        AppLaunchProxy.getAppLaunch().spanEnd("FunctionB");
    }

    private void callFunctionC() {
        AppLaunchProxy.getAppLaunch().spanStart("FunctionC", null);
        sleep(300);
        callFunctionD("FunctionC");
        AppLaunchProxy.getAppLaunch().spanEnd("FunctionC");
    }

    private void callFunctionD(String parent) {
        AppLaunchProxy.getAppLaunch().spanStart("FunctionD", parent);
        sleep(200);
        AppLaunchProxy.getAppLaunch().spanEnd("FunctionD");
    }

    private void callFunctionE() {
        AppLaunchProxy.getAppLaunch().spanStart("FunctionE", null);
        sleep(200);
        callFunctionA();
        AppLaunchProxy.getAppLaunch().spanEnd("FunctionE");
    }

    private void testJavaCatchError() {
        showToast("测试Java捕获错误");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String content = "a illegal string.";
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    double price  = jsonObject.getDouble("price");
                } catch (Throwable t) {
                    Bugly.handleCatchException(Thread.currentThread(), t,
                            "testJavaCatchError", content.getBytes(), true);
                }
            }
        });
        thread.start();
        showToast("Java捕获错误上报成功！");
    }

    private void testCustomError() {
        showToast("测试自定义错误");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Bugly.postException(4, "Flutter Exception",
                    "Framework ERROR - Null check operator used on a null value",
                    "ScrollPosition.minScrollExtent (package:flutter/src/widgets/scroll_position.dart:139)\n" +
                        "ScrollPosition._updateSemanticActions (package:flutter/src/widgets/scroll_position.dart:663)\n" +
                        "ScrollPosition.notifyListeners (package:flutter/src/widgets/scroll_position.dart:967)\n" +
                        "ScrollPosition.forcePixels (package:flutter/src/widgets/scroll_position.dart:380)\n" +
                        "ScrollPositionWithSingleContext.jumpTo (package:flutter/src/widgets/scroll_position_with_single_context.dart:198)\n" +
                        "ScrollController.jumpTo (package:flutter/src/widgets/scroll_controller.dart:173)\n" +
                        "_MatchRecommendWidgetState._buildList (package:qflutter_match_friend/src/pages/match/widget/match_recommend_widget.dart:82)\n" +
                        "_MatchRecommendWidgetState.build.<anonymous closure> (package:qflutter_match_friend/src/pages/match/widget/match_recommend_widget.dart:68)\n" +
                        "Consumer.buildWithChild (package:provider/src/consumer.dart:180)\n" +
                        "SingleChildStatelessWidget.build (package:nested/nested.dart:259)\n" +
                        "StatelessElement.build (package:flutter/src/widgets/framework.dart:4739)", null);
            }
        });
        thread.start();
        showToast("自定义错误上报成功！");
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

    private static void testAndroidX() {
        androidx.collection.LruCache<String, String> cache =
                new androidx.collection.LruCache<String, String>(12);
        cache.put("Good", "Name");
        cache.resize(-1);
    }
}