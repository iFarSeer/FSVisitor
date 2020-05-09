package com.ifarseer.android.browser.view

import android.net.Uri
import android.view.View
import android.webkit.*

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */

class FSWebChromeClient : WebChromeClient() {

    var webSettingsCallback: FSWebSettingsCallback? = null

    //关键代码，以下函数是没有API文档的

    // For Android 3.0+
    fun openFileChooser(uploadMsg: ValueCallback<Any?>) {
        webSettingsCallback?.openFileChooser(uploadMsg)
    }

    // For Android 3.0+
    fun openFileChooser(uploadMsg: ValueCallback<Any?>, acceptType: String) {
        webSettingsCallback?.openFileChooser(uploadMsg, acceptType)
    }

    //For Android 4.1
    fun openFileChooser(uploadMsg: ValueCallback<Any?>, acceptType: String, capture: String) {
        webSettingsCallback?.openFileChooser(uploadMsg, acceptType, capture)
    }

    override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
        return webSettingsCallback?.onShowFileChooser(webView, filePathCallback, fileChooserParams) ?: super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        webSettingsCallback?.onProgressChanged(view, newProgress)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        webSettingsCallback?.onReceivedTitle(view, title)
    }

    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        return webSettingsCallback?.onJsAlert(view, url, message, result) ?: super.onJsAlert(view, url, message, result)
    }

    override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        return webSettingsCallback?.onJsConfirm(view, url, message, result) ?: super.onJsConfirm(view, url, message, result)
    }

    override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
        return webSettingsCallback?.onJsPrompt(view, url, message, defaultValue, result) ?: super.onJsPrompt(view, url, message, defaultValue, result)
    }

    override fun onJsTimeout(): Boolean {
        return webSettingsCallback?.onJsTimeout() ?: super.onJsTimeout()
    }

    override fun onJsBeforeUnload(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        return webSettingsCallback?.onJsBeforeUnload(view, url, message, result) ?: super.onJsBeforeUnload(view, url, message, result)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        return webSettingsCallback?.onConsoleMessage(consoleMessage) ?: super.onConsoleMessage(consoleMessage)
    }
}