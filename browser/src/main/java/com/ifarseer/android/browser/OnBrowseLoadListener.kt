package com.ifarseer.android.browser

import android.graphics.Bitmap
import com.ifarseer.android.browser.view.FSWebView

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 26/01/2018.
 */
interface OnBrowseLoadListener {

    fun onLoadStarted(fsWebView: FSWebView, url: String?, favicon: Bitmap?)

    fun onLoadSuccess(fsWebView: FSWebView, url: String?)

    fun onLoadFailed(fsWebView: FSWebView, errorCode: Int?, description: String?, url: String?)
}