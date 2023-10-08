package com.bis.mytestbiswajit.utils

import android.util.Log

class Tag {
    companion object Test {

        fun tag( s:String, s2:String) = Log.e(s,s2)
        fun tag( s:String) = Log.e("showLog",s)
    }
}