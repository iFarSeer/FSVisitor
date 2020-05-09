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

    fun onSuccess(message : String) {
        module.component.invokeJSMethod(successCallback, message)
        LogTool.info(FSBrowserConstants.TAG, String.format("%s(%s)", successCallback, message))
    }

    fun onFailure(message : String) {
        LogTool.info(FSBrowserConstants.TAG, String.format("%s(%s)", failureCallback, message))
        module.component.invokeJSMethod(failureCallback, message)
    }
}