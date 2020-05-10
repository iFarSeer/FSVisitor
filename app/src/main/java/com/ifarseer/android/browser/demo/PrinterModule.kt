package com.ifarseer.android.browser.demo

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.fs.android.sunmi.printer.FSPrinter
import com.ifarseer.android.browser.FSBrowserComponent
import com.ifarseer.android.browser.FSBrowserJSCallback
import com.ifarseer.android.browser.FSBrowserModule
import com.ifarseer.android.browser.annotation.BrowserJSMethod
import com.ifarseer.android.browser.annotation.BrowserModule
import com.ifarseer.android.browser.annotation.BrowserNativeMethod
import org.json.JSONObject

/**
 * 扫描模块
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */
@BrowserModule(name = "printer")
class PrinterModule(component: FSBrowserComponent) : FSBrowserModule(component) {
    override fun getName(): String {
        return "printer"
    }

    fun connect(context: Context, callback: (code: Int) -> Unit) {
        FSPrinter.connect(context.applicationContext, callback)
    }

    fun disconnect(context: Context, callback: (code: Int) -> Unit) {
        FSPrinter.disconnect(context.applicationContext, callback)
    }

    @BrowserNativeMethod(name = "print")
    fun print(json: String, callback: FSBrowserJSCallback?) {
//        FSPrinter.printText("dfjsfjsadlkfsfsdfjsdlkjfksdfjlsdjffsdjfjskld")
        Handler(Looper.getMainLooper()).post {
            FSPrinter.printBitmap(component.captureScreen(), 1)
        }
        onPrintStatusChanged("${FSPrinter.sunmiPrinter}")
    }

    @BrowserJSMethod(name = "onPrintStatusChanged")
    private fun onPrintStatusChanged(status: String, code: Int = -1, message: String = "") {
        val jsonObj = JSONObject()
        jsonObj.put("status", status)
        if (code != -1 && !TextUtils.isEmpty(message)) {
            jsonObj.put("code", code)
            jsonObj.put("message", message)
        }
        component.invokeJSMethod("onPrintStatusChanged", jsonObj.toString())
    }
}