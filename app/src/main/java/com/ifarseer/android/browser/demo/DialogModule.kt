package com.ifarseer.android.browser.demo

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.ifarseer.android.browser.FSBrowserComponent
import com.ifarseer.android.browser.FSBrowserJSCallback
import com.ifarseer.android.browser.FSBrowserModule
import com.ifarseer.android.browser.annotation.BrowserModule
import com.ifarseer.android.browser.annotation.BrowserNativeMethod
import org.json.JSONObject

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 26/01/2018.
 */
@BrowserModule(name = "dialog")
class DialogModule(private val context: Context, component: FSBrowserComponent) : FSBrowserModule(component) {
    override fun getName(): String {
        return "dialog"
    }

    /**
     * js： itomix.invoke('dialog.showAlertDialog', JSON.stringify({ title: 'title', message: 'alert' }), 'success', 'failure');
     */
    @BrowserNativeMethod(name = "showAlertDialog")
    fun showAlertDialog(json: String, callback: FSBrowserJSCallback?) {
        val jsonObj = JSONObject(json)
        MaterialDialog.Builder(context)
                .title(jsonObj.getString("title"))
                .content(jsonObj.getString("message"))
                .positiveText("确定")
                .negativeText("取消")
                .onPositive(fun (dialog, which) {
                    callback?.onSuccess(JSONObject().put("result", "success").toString())
                    dialog.dismiss()
                })
                .onNegative(fun (dialog, which) {
                    callback?.onFailure(JSONObject().put("result", "failure").toString())
                    dialog.dismiss()
                })
                .show()
    }

    /**
     *  js:itomix.invoke('dialog.showConfirmDialog', JSON.stringify({title:'title', message:'confirm'}), 'success', 'failure');
     */
    @BrowserNativeMethod(name = "showConfirmDialog")
    fun showConfirmDialog(json: String, callback: FSBrowserJSCallback?) {
        val jsonObj = JSONObject(json)
        MaterialDialog.Builder(context)
                .title(jsonObj.getString("title"))
                .content(jsonObj.getString("confirm"))
                .positiveText("确定")
                .onPositive(fun (dialog, which) {
                    callback?.onSuccess(JSONObject().put("result", "success").toString())
                    dialog.dismiss()
                })
                .show()
    }
}