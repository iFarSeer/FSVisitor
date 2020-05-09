package com.ifarseer.android.browser

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */
open abstract class FSBrowserModule(val component: FSBrowserComponent) {

    abstract fun getName(): String

    open fun destroy() {}

    fun getFunctionName(methodName: String): String {
        return String.format("%s.%s", getName(), methodName)
    }
}