package com.tencent.demo.buglyprodemo;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class TestActivityLeakActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_leak);
        Button button = (Button) findViewById(R.id.test_activity_leak);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerLeak();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BuglyWrapper.getInstance().changeResumedActivity(this.getClass().getSimpleName());
    }

    private void triggerLeak() {
        LeakKeeper.leakObj(this);
        try {
            Thread.sleep(1000);
        } catch (Throwable t) {
            // do nothing
        }
        onBackPressed();
    }
}