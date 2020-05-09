package com.ifarseer.android.browser.demo

import android.content.Context
import android.widget.Toast
import com.ifarseer.android.browser.FSBrowserComponent
import com.ifarseer.android.browser.FSBrowserJSCallback
import com.ifarseer.android.browser.FSBrowserModule
import com.ifarseer.android.browser.annotation.BrowserModule
import com.ifarseer.android.browser.annotation.BrowserNativeMethod

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 26/01/2018.
 */
@BrowserModule(name = "toast")
class ToastModule(private val context: Context, component: FSBrowserComponent) : FSBrowserModule(component) {

    override fun getName(): String {
        return "toast"
    }

    @BrowserNativeMethod(name = "show")
    fun show(json: String, callback: FSBrowserJSCallback?) {
        Toast.makeText(context, json, Toast.LENGTH_LONG).show()
    }
}