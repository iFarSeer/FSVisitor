package com.ifarseer.android.browser.view

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.view.KeyEvent
import android.view.View
import android.webkit.*

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */

interface FSWebSettingsCallback {

    fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?)

    fun onPageFinished(view: WebView?, url: String?)

    fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean

    fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?)

    // For Android 3.0+
    fun openFileChooser(uploadMsg: ValueCallback<Any?>)
    // For Android 3.0+
    fun openFileChooser(uploadMsg: ValueCallback<Any?>, acceptType: String)
    //For Android 4.1
    fun openFileChooser(uploadMsg: ValueCallback<Any?>, acceptType: String, capture: String)

    fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: WebChromeClient.FileChooserParams?): Boolean

    fun onProgressChanged(view: WebView?, newProgress: Int)

    fun onReceivedTitle(view: WebView?, title: String?)

    fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean

    fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean

    fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean

    fun onJsTimeout(): Boolean

    fun onJsBeforeUnload(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean

    fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean
}

open class FSSimpleWebSettingsCallback :FSWebSettingsCallback {
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
    }

    override fun onPageFinished(view: WebView?, url: String?) {
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return false
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {

    }

    override fun openFileChooser(uploadMsg: ValueCallback<Any?>) {
    }

    override fun openFileChooser(uploadMsg: ValueCallback<Any?>, acceptType: String) {
    }

    override fun openFileChooser(uploadMsg: ValueCallback<Any?>, acceptType: String, capture: String) {
    }

    override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: WebChromeClient.FileChooserParams?): Boolean {
        return false
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
    }

    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        return false
    }

    override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        return false
    }

    override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
        return false
    }

    override fun onJsTimeout(): Boolean {
        return false
    }

    override fun onJsBeforeUnload(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        return false
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        return false
    }
}