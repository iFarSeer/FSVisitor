package com.ifarseer.android.browser.view

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.ifarseer.android.browser.FSBrowserConstants
import com.ifarseer.android.browser.OnBrowseLoadListener
import com.ifarseer.android.browser.R
import com.ifarseer.android.browser.tool.LogTool

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 26/01/2018.
 */
class FSBrowserView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mContentView: FrameLayout? = null
    private var mFSWebView: FSWebView? = null
    private var mLoadingView: RelativeLayout? = null
    private var mRefreshView: RelativeLayout? = null
    var loadListener: OnBrowseLoadListener? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.fs_browser_view, null, false)
        mContentView = view.findViewById(R.id.contentView)
        mLoadingView = view.findViewById(R.id.loadingView)
        mRefreshView = view.findViewById(R.id.refreshView)

        mLoadingView?.visibility = View.GONE
        mRefreshView?.visibility = View.GONE

        //拦截事件，防止事件穿透到底层视图
        mLoadingView?.setOnClickListener {}
        mRefreshView?.setOnClickListener {}

        mFSWebView = FSWebView(context, attrs, defStyleAttr)
        mContentView?.addView(mFSWebView)

        addView(view)
    }

    fun setLoadingView(view: View) {
        mLoadingView?.removeAllViews()
        mLoadingView?.addView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    fun setRefreshView(view: View) {
        mRefreshView?.removeAllViews()
        mRefreshView?.addView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    fun setFSWebSettingsCallback(callback: FSWebSettingsCallback?) {
        var wrapper = object : FSWebSettingsCallback {

            var loadResult : Int = 0;
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                LogTool.debug(FSBrowserConstants.TAG, "onPageStarted")
                callback?.onPageStarted(view, url, favicon)
                loadListener?.onLoadStarted(view as FSWebView, url, favicon)
                mLoadingView?.visibility = View.VISIBLE
                mRefreshView?.visibility = View.GONE
                loadResult = 0
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                LogTool.debug(FSBrowserConstants.TAG, "onPageFinished")
                callback?.onPageFinished(view, url)

                if (loadResult == 0) {
                    loadListener?.onLoadSuccess(view as FSWebView, url)
                    mLoadingView?.visibility = View.GONE
                    mRefreshView?.visibility = View.GONE
                    loadResult = 1
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                LogTool.debug(FSBrowserConstants.TAG, "shouldOverrideUrlLoading")
               return callback?.shouldOverrideUrlLoading(view, request) ?: false
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                LogTool.debug(FSBrowserConstants.TAG, "onReceivedError")
                callback?.onReceivedError(view, errorCode, description, failingUrl)
                loadListener?.onLoadFailed(view as FSWebView, errorCode, description, failingUrl)
                if (loadResult == 0) {
                    mLoadingView?.visibility = View.GONE
                    mRefreshView?.visibility = View.VISIBLE
                    loadResult = -1;
                }
            }

            override fun openFileChooser(uploadMsg: ValueCallback<Any?>) {
                LogTool.debug(FSBrowserConstants.TAG, "openFileChooser")
                callback?.openFileChooser(uploadMsg)
            }

            override fun openFileChooser(uploadMsg: ValueCallback<Any?>, acceptType: String) {
                LogTool.debug(FSBrowserConstants.TAG, "openFileChooser")
                callback?.openFileChooser(uploadMsg, acceptType)
            }

            override fun openFileChooser(uploadMsg: ValueCallback<Any?>, acceptType: String, capture: String) {
                LogTool.debug(FSBrowserConstants.TAG, "openFileChooser")
                callback?.openFileChooser(uploadMsg, acceptType, capture)
            }

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: WebChromeClient.FileChooserParams?): Boolean {
                LogTool.debug(FSBrowserConstants.TAG, "onShowFileChooser")
                return callback?.onShowFileChooser(webView, filePathCallback, fileChooserParams) ?: false
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                LogTool.debug(FSBrowserConstants.TAG, "onProgressChanged")
                callback?.onProgressChanged(view, newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                LogTool.debug(FSBrowserConstants.TAG, "onReceivedTitle")
                callback?.onReceivedTitle(view, title)
            }

            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                LogTool.debug(FSBrowserConstants.TAG, "onJsAlert")
                return callback?.onJsAlert(view, url, message, result) ?: false
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                LogTool.debug(FSBrowserConstants.TAG, "onJsConfirm")
                return callback?.onJsConfirm(view, url, message, result) ?: false
            }

            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                LogTool.debug(FSBrowserConstants.TAG, "onJsPrompt")
                return callback?.onJsPrompt(view, url, message, defaultValue, result)?: false
            }

            override fun onJsTimeout(): Boolean {
                LogTool.debug(FSBrowserConstants.TAG, "onJsTimeout")
                return callback?.onJsTimeout() ?: false
            }

            override fun onJsBeforeUnload(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                LogTool.debug(FSBrowserConstants.TAG, "onJsBeforeUnload")
                return callback?.onJsBeforeUnload(view, url, message, result) ?: false
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                LogTool.debug(FSBrowserConstants.TAG, "onConsoleMessage")
                return callback?.onConsoleMessage(consoleMessage) ?: false
            }
        }
        mFSWebView?.setFSWebSettingsCallback(wrapper)
    }

    fun setUserAgent(userAgent: String) {
        mFSWebView?.setUserAgent(userAgent)
    }

    fun addJavascriptInterface(obj: Any, interfaceName: String) {
        mFSWebView?.addJavascriptInterface(obj, interfaceName)
    }

    fun loadUrl(url: String) {
        mFSWebView?.loadUrl(url)
    }

    fun reload() {
        mFSWebView?.reload()
    }

    fun destroy(){
        mContentView?.removeAllViews()
        mFSWebView?.destroy()
    }
}