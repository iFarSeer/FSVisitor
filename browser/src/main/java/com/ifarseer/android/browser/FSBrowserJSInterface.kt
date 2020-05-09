package com.ifarseer.android.browser

import android.webkit.JavascriptInterface
import com.ifarseer.android.browser.tool.LogTool
import java.util.*

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */
class FSBrowserJSInterface(private val component: FSBrowserComponent) {

    @JavascriptInterface
    fun invoke(name: String, json: String, success: String, failure: String) {
        var index = name.indexOf(".")
        LogTool.debug(FSBrowserConstants.TAG, String.format("FSBrowserJSInterface: name=%s, json=%s, success=%s, failure=%s.", name, json, success, failure))
        if (index > 0) {
            val domain = name.substring(0, index).toLowerCase()
            val methodName = name.substring(index + 1)
            val wrapper = component.getModuleWrapper(domain)
            try {
                if (wrapper == null) {
                    component.invokeJSMethod(wrapperMessage(String.format("FSBrowserJSInterface: invoke %s failed.", name)))
                    return
                }
                wrapper.invoke(methodName, json, success, failure)
            } catch (e: Exception) {
                e.printStackTrace()
                component.invokeJSMethod(wrapperMessage(String.format("FSBrowserJSInterface: invoke %s failed, %s.", name, e.message)))
            }
        } else {
            component.invokeJSMethod(wrapperMessage(String.format("FSBrowserJSInterface: invoke %s failed, the method name must contains (.).", name)))
        }
    }

    private fun wrapperMessage(msg: String): String {
        return String.format(Locale.getDefault(), "{message: '%s'}", msg)
    }
}