package com.tencent.demo.buglyprodemo;

public class CostJob {
    public static Runnable generateCostJob(final long costInMs) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(costInMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        return runnable;
    }
}
