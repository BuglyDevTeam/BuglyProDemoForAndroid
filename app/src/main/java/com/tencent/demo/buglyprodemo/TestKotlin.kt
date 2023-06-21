package com.tencent.demo.buglyprodemo

class TestKotlin {
    fun testAndroidXUtil() {
        val a = androidx.collection.CircularIntArray()
        a.addFirst(12)
        for (i in 1..10) {
            a.addLast(i)
        }
        val b = androidx.collection.LruCache<String, String>(12)
        b.put("Good", "Name")
        b.resize(-1)
    }
}