package com.ifarseer.android.browser.tool

import android.util.Log

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */
class LogTool {

    companion object {
        fun verbose(tag: String?, message: String?) {
            Log.v(tag, message)
        }

        fun info(tag: String?, message: String?) {
            Log.i(tag, message)
        }

        fun debug(tag: String?, message: String?) {
            Log.d(tag, message)
        }

        fun warn(tag: String?, message: String?) {
            Log.w(tag, message)
        }

        fun error(tag: String?, message: String?) {
            Log.e(tag, message)
        }
    }
}