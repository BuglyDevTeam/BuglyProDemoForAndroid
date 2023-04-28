package com.tencent.demo.buglyprodemo;

import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class TestFragmentLeakActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment_leak);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BuglyWrapper.getInstance().changeResumedActivity(this.getClass().getSimpleName());
    }

    private void initView() {
        Button btLeft = findViewById(R.id.bt_left);
        Button btRight = findViewById(R.id.bt_right);

        btLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new LeftFragment());
            }
        });

        btRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new RightFragment());
            }
        });
    }

    private void changeFragment(Fragment fragment) {
        //实例化碎片管理器对象
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (fm.getFragments().size() > 0) {
            ft.remove(fm.getFragments().get(0));
        }
        //选择fragment替换的部分
        ft.replace(R.id.cl_content, fragment);
        ft.commit();
    }
}