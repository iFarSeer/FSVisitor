package com.ifarseer.android.browser.demo

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.webkit.ConsoleMessage
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fs.android.sunmi.scaner.ScannerFragment
import com.fs.android.sunmi.scaner.ScannerViewModel
import com.ifarseer.android.browser.FSBrowserComponent
import com.ifarseer.android.browser.FSBrowserConstants
import com.ifarseer.android.browser.OnBrowseLoadListener
import com.ifarseer.android.browser.tool.LogTool
import com.ifarseer.android.browser.view.FSBrowserView
import com.ifarseer.android.browser.view.FSSimpleWebSettingsCallback
import com.ifarseer.android.browser.view.FSWebView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnScanListener {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var component: FSBrowserComponent
    private lateinit var printerModule: PrinterModule
    private lateinit var scannerModule: ScannerModule
    private lateinit var toastModule: ToastModule
    private lateinit var dialogModule: DialogModule

    private lateinit var scannerViewModel: ScannerViewModel
    private lateinit var scannerFragment: ScannerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        scannerViewModel = ViewModelProviders.of(this).get(ScannerViewModel::class.java)
        scannerViewModel.scanResult.observe(this, Observer {
            it?.apply {
                Log.d("ScannerViewModel", "接受数据变化 it = $it")
                removeScannerFragment()
                scannerModule.onScanResult(it)
            }
        })

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
        scannerModule = ScannerModule(this, component)
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
//        browserView.loadUrl("https://live.bilibili.com/activity/summer-2020-half/index.html?room_id=5318#/rank")
    }

    override fun onDestroy() {
        super.onDestroy()
        printerModule.disconnect(this) {
            LogTool.debug(TAG, "printerModule disconnect: result = $it")
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return removeScannerFragment()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStartScan() {
        container.post {
            addScannerFragment()
        }
    }

    private fun addScannerFragment() {
        if (!this::scannerFragment.isInitialized) {
            scannerFragment = ScannerFragment.newInstance()
        }
        if (supportFragmentManager.findFragmentByTag("scanner") == null) {
            container.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().replace(R.id.container, scannerFragment, "scanner").commit()
        }
    }

    private fun removeScannerFragment(): Boolean {
        return if (this::scannerFragment.isInitialized
                && supportFragmentManager.findFragmentByTag("scanner") != null) {
            supportFragmentManager.beginTransaction().remove(scannerFragment).commit()
            container.visibility = View.GONE
            true
        } else false
    }
}
