package com.tencent.demo.buglyprodemo;

import java.util.ArrayList;
import java.util.List;

public class LeakKeeper {

    private static final List<Object> list = new ArrayList<Object>();

    public static void leakObj(Object obj) {
        list.add(obj);
    }
}
