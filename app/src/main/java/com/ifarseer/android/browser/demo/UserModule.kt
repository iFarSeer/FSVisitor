package com.ifarseer.android.browser.demo

import com.ifarseer.android.browser.FSBrowserComponent
import com.ifarseer.android.browser.FSBrowserJSCallback
import com.ifarseer.android.browser.FSBrowserModule
import com.ifarseer.android.browser.annotation.BrowserJSMethod
import com.ifarseer.android.browser.annotation.BrowserModule
import com.ifarseer.android.browser.annotation.BrowserNativeMethod
import org.json.JSONObject

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */
@BrowserModule(name = "user")
class UserModule(component: FSBrowserComponent) : FSBrowserModule(component) {
    override fun getName(): String {
        return "test"
    }

    @BrowserNativeMethod(name = "getUserInfo")
    fun getUserInfo(json: String, callback: FSBrowserJSCallback?) {
        var jsonObj = JSONObject()
        jsonObj.put("userId", 10001)
        jsonObj.put("userName", "zhaosc")
        jsonObj.put("nickName", "积木小玩家")
        callback?.onSuccess(jsonObj.toString())
    }

    @BrowserJSMethod(name = "onUserChanged")
    fun onUserChanged(userId: Int, userName: String, nickName: String) {
        var jsonObj = JSONObject()
        jsonObj.put("userId", userId)
        jsonObj.put("userName", userName)
        jsonObj.put("nickName", nickName)
        component.invokeJSMethod("onUserChanged", jsonObj.toString())
    }
}