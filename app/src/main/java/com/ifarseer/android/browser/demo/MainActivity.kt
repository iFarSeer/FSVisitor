package com.ifarseer.android.browser.demo

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.webkit.ConsoleMessage
import com.ifarseer.android.browser.FSBrowserComponent
import com.ifarseer.android.browser.FSBrowserConstants
import com.ifarseer.android.browser.OnBrowseLoadListener
import com.ifarseer.android.browser.tool.LogTool
import com.ifarseer.android.browser.view.FSBrowserView
import com.ifarseer.android.browser.view.FSSimpleWebSettingsCallback
import com.ifarseer.android.browser.view.FSWebView

class MainActivity : AppCompatActivity() {

    private lateinit var component: FSBrowserComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val browserView = findViewById<FSBrowserView>(R.id.browserView)
        val loadingView = LayoutInflater.from(this).inflate(R.layout.browser_loading, null, false)
        val refreshView = LayoutInflater.from(this).inflate(R.layout.browser_refresh, null, false)
        refreshView.setOnClickListener {
            browserView.reload()
        }
        browserView.setLoadingView(loadingView)
        browserView.setRefreshView(refreshView)

        component = FSBrowserComponent(browserView, "FSApp", "invokeJS")
        component.addModule(UserModule(component))
        component.addModule(DialogModule(this, component))
        component.addModule(ToastModule(this, component))

        browserView.loadListener = object : OnBrowseLoadListener {
            override fun onLoadStarted(fsWebView: FSWebView, url: String?, favicon: Bitmap?) {
                LogTool.debug(FSBrowserConstants.TAG, "onLoadStarted")
            }

            override fun onLoadSuccess(fsWebView: FSWebView, url: String?) {
                LogTool.debug(FSBrowserConstants.TAG, "onLoadSuccess")
            }

            override fun onLoadFailed(fsWebView: FSWebView, errorCode: Int?, description: String?, url: String?) {
                LogTool.debug(FSBrowserConstants.TAG, "onLoadFailed")
            }
        }

        browserView.setFSWebSettingsCallback(object : FSSimpleWebSettingsCallback() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                LogTool.debug(FSBrowserConstants.TAG, "consoleMessage = " + consoleMessage?.message())
                return super.onConsoleMessage(consoleMessage)
            }
        })

        browserView.loadUrl("file:///android_asset/browser.html")
    }

    fun onUserChanged(view: View) {
        component.getModule(UserModule::class.java)?.onUserChanged(10002, "lemon", "小慢")
    }
}
