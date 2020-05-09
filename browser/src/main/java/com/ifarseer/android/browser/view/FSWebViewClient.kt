package com.ifarseer.android.browser.view

import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.*

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */

class FSWebViewClient : WebViewClient() {

    var webSettingsCallback: FSWebSettingsCallback? = null
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        webSettingsCallback?.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        webSettingsCallback?.onPageFinished(view, url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return webSettingsCallback?.shouldOverrideUrlLoading(view, request) ?: super.shouldOverrideUrlLoading(view, request)
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        webSettingsCallback?.onReceivedError(view, errorCode, description, failingUrl)
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        handler?.proceed()
        super.onReceivedSslError(view, handler, error)
    }
}