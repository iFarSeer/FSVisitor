package com.ifarseer.android.browser.demo

import android.content.Context
import android.text.TextUtils
import com.ifarseer.android.browser.FSBrowserComponent
import com.ifarseer.android.browser.FSBrowserJSCallback
import com.ifarseer.android.browser.FSBrowserModule
import com.ifarseer.android.browser.annotation.BrowserJSMethod
import com.ifarseer.android.browser.annotation.BrowserModule
import com.ifarseer.android.browser.annotation.BrowserNativeMethod
import com.ifarseer.android.browser.tool.LogTool
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.InnerResultCallbcak
import com.sunmi.peripheral.printer.SunmiPrinterService
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

    companion object {
        const val CODE_FAILURE = 0
        const val CODE_SUCCESS = 1
        const val PRINT_STATUS_START = "start"
        const val PRINT_STATUS_FAILURE = "failure"
        const val PRINT_STATUS_SUCCESS = "success"
    }

    private var printerService: SunmiPrinterService? = null

    fun connect(context: Context, callback: (code: Int) -> Unit) {
        InnerPrinterManager.getInstance().bindService(context, object : InnerPrinterCallback() {
            override fun onConnected(service: SunmiPrinterService?) {
                printerService = service
                callback(CODE_SUCCESS)
            }

            override fun onDisconnected() {
                printerService = null
                callback(CODE_FAILURE)
            }
        })
    }

    fun disconnect(context: Context,callback: (code: Int) -> Unit){
        InnerPrinterManager.getInstance().unBindService(context, object : InnerPrinterCallback() {
            override fun onConnected(service: SunmiPrinterService?) {
                printerService = null
                callback(CODE_SUCCESS)
            }

            override fun onDisconnected() {
                printerService = null
                callback(CODE_FAILURE)
            }
        })
    }

    @BrowserNativeMethod(name = "print")
    fun print(json: String, callback: FSBrowserJSCallback?) {
        onPrintStatusChanged(PRINT_STATUS_START)
        printerService?.printText(json, object : InnerResultCallbcak(){
            override fun onRunResult(isSuccess: Boolean) {
                //返回接⼝执⾏的情况(并⾮真实打印):成功或失败
                LogTool.info(getName(), "onRunResult:isSuccess = $isSuccess")
            }

            override fun onPrintResult(code: Int, msg: String?) {
                //事务模式下真实的打印结果返回
                onPrintStatusChanged(PRINT_STATUS_SUCCESS, code, msg ?: "")
                LogTool.info(getName(), "onPrintResult:code = $code, msg = $msg")
            }

            override fun onReturnString(result: String?) {
                //部分接⼝会异步返回查询数据
//                onPrintStatusChanged(PRINT_STATUS_FAILURE)
                LogTool.info(getName(), "onReturnString:result = $result")
            }

            override fun onRaiseException(code: Int, msg: String?) {
                //接⼝执⾏失败时，返回的异常状态
                onPrintStatusChanged(PRINT_STATUS_FAILURE, code, msg ?: "")
                LogTool.info(getName(), "onRaiseException:code = $code, msg = $msg")
            }
        })
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