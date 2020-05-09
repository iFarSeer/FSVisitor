package com.ifarseer.android.browser.demo

import com.ifarseer.android.browser.FSBrowserComponent
import com.ifarseer.android.browser.FSBrowserJSCallback
import com.ifarseer.android.browser.FSBrowserModule
import com.ifarseer.android.browser.annotation.BrowserJSMethod
import com.ifarseer.android.browser.annotation.BrowserModule
import com.ifarseer.android.browser.annotation.BrowserNativeMethod
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder

/**
 * 扫描模块
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */
@BrowserModule(name = "scanner")
class ScannerModule(private var scanListener: OnScanListener, component: FSBrowserComponent) : FSBrowserModule(component) {
    override fun getName(): String {
        return "scanner"
    }

    @BrowserNativeMethod(name = "scan")
    fun scan(json: String, callback: FSBrowserJSCallback?) {
        scanListener.onStartScan()
        val jsonObj = JSONObject()
        jsonObj.put("userId", 10001)
        jsonObj.put("userName", "zhaosc")
        jsonObj.put("nickName", "积木小玩家")
        callback?.onSuccess(jsonObj.toString())
    }

    @BrowserJSMethod(name = "onScanResult")
    fun onScanResult(content: String) {
        val jsonObj = JSONObject()
        jsonObj.put("content", URLDecoder.decode(content, "utf-8"))
        component.invokeJSMethod("onScanResult", jsonObj.toString())
    }
}

interface OnScanListener {
    fun onStartScan()
}