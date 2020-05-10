package com.ifarseer.android.browser

import com.ifarseer.android.browser.tool.LogTool

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */
class FSBrowserJSCallback(private val module: FSBrowserModule, private val successCallback: String, private val failureCallback: String) {

    fun onSuccess(message: String): Boolean {
        return if (successCallback.isNotEmpty()) {
            module.component.invokeJSMethod(successCallback, message)
            LogTool.info(FSBrowserConstants.TAG, String.format("%s(%s)", successCallback, message))
            true
        } else {
            LogTool.warn(FSBrowserConstants.TAG, " Not invoke onSuccess callback: successCallback is Empty")
            false
        }
    }

    fun onFailure(message: String): Boolean {
        return if (successCallback.isNotEmpty()) {
            LogTool.info(FSBrowserConstants.TAG, String.format("%s(%s)", failureCallback, message))
            module.component.invokeJSMethod(failureCallback, message)
            true
        } else {
            LogTool.warn(FSBrowserConstants.TAG, " Not invoke onFailure callback: successCallback is Empty")
            false
        }
    }
}