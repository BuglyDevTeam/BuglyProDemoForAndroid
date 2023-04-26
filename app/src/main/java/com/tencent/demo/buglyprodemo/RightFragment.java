package com.tencent.demo.buglyprodemo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class RightFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LeakKeeper.leakObj(this);
        return inflater.inflate(R.layout.fragment_right, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fragment 2", "onDestroy");
    }
}
