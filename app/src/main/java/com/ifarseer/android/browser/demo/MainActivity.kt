package com.ifarseer.android.browser.demo

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var component: FSBrowserComponent
    private lateinit var printerModule: PrinterModule
    private lateinit var scannerModule: ScannerModule
    private lateinit var toastModule: ToastModule
    private lateinit var dialogModule: DialogModule

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


        component = FSBrowserComponent(browserView, "itomix", "invokeJS")
        toastModule = ToastModule(this, component)
        dialogModule = DialogModule(this, component)
        scannerModule = ScannerModule(component)
        printerModule = PrinterModule(component)
        component.addModule(toastModule)
        component.addModule(dialogModule)
        component.addModule(scannerModule)
        component.addModule(printerModule)
        printerModule.connect(this) {
            LogTool.debug(TAG, "printerModule connect:result = $it")
        }

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

    override fun onDestroy() {
        super.onDestroy()
        printerModule.disconnect(this) {
            LogTool.debug(TAG, "printerModule disconnect: result = $it")
        }
    }

    fun onScanResult(view: View) {
        component.getModule(ScannerModule::class.java)?.onScanResult(10002, "lemon", "小慢")
    }

}
