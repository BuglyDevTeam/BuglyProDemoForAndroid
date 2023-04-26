package com.tencent.demo.buglyprodemo;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import com.tencent.bugly.library.Bugly;
import com.tencent.rmonitor.custom.ICustomDataEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TestLagActivity extends ListActivity {

    private static final String TAG = "TestListView";

    private static final int LIST_DATA_SIZE = 500;

    private static final long DELAY_INTERVAL = 1000L;

    private static final int TWO = 2;

    /**
     * Called when the activity is first created.
     */
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final ArrayList<String> listData = new ArrayList<String>();

    private MyAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareListData();

        myAdapter = new MyAdapter(this, android.R.layout.simple_list_item_1, listData);

        setListAdapter(myAdapter);

        getListView().setOnScrollListener(onScrollListener);

        handler.postDelayed(scrollTask, DELAY_INTERVAL);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bugly.getGlobalCustomDataEditor().putStringParam(ICustomDataEditor.STRING_PARAM_1,
                this.getClass().getName());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void prepareListData() {
        listData.clear();
        for (int i = 0; i < LIST_DATA_SIZE; i++) {
            listData.add(String.format(Locale.getDefault(), "list item data [%d]", i));
        }
    }

    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                Bugly.exitScene("TestLagActivity_Scroll_List");
            } else {
                Bugly.enterScene("TestLagActivity_Scroll_List");
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            // do nothing
        }
    };


    private Runnable scrollTask = new Runnable() {

        static final int MAX_SCROLL_COUNT = 5;
        static final int DEST_POS_START = LIST_DATA_SIZE;
        static final int DEST_POS_END = 0;

        private int count = 0;

        @Override
        public void run() {
            count++;
            if (count % TWO == 1) {
                getListView().smoothScrollToPosition(DEST_POS_START);
            } else {
                getListView().smoothScrollToPosition(DEST_POS_END);
            }
            if (count < MAX_SCROLL_COUNT) {
                handler.postDelayed(scrollTask, DELAY_INTERVAL);
            } else {
                TestLagActivity.this.finish();
            }
            if (count > 1) {
                myAdapter.startCost(true);
            }
        }
    };

    static class MyAdapter extends ArrayAdapter<String> {

        private boolean startCost = false;

        private final Random random = new Random(System.currentTimeMillis());

        public MyAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if (startCost) {
                CostJob.generateCostJob(200 + random.nextInt(400)).run();
            }
            return view;
        }

        public void startCost(boolean startCost) {
            this.startCost = startCost;
        }
    }
}