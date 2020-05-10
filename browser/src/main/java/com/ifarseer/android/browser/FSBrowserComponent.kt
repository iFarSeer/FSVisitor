package com.ifarseer.android.browser

import android.app.Activity
import com.ifarseer.android.browser.tool.LogTool
import com.ifarseer.android.browser.view.FSBrowserView
import com.ifarseer.android.browser.view.FSWebView
import java.util.concurrent.ConcurrentHashMap

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */

class FSBrowserComponent(val browserView: FSBrowserView, private val jsDomain: String, private val jsEnterMethodName: String) {

    private val moduleMap = ConcurrentHashMap<Class<out FSBrowserModule>, FSBrowserModule>()
    private val wrapperMap = ConcurrentHashMap<String, FSBrowserModuleWrapper>()

    init {
        browserView.addJavascriptInterface(FSBrowserJSInterface(this), jsDomain)
    }

    fun addModule(module: FSBrowserModule) {
        if (moduleMap.contains(module.javaClass)) {
            throw IllegalArgumentException(String.format("FSBrowserComponent only support register the module(%s) once.", module.getName()))
        }
        moduleMap.put(module.javaClass, module)
        var wrapper = FSBrowserModuleWrapper(module)
        wrapperMap.put(wrapper.name as String, wrapper)
    }

    fun getModuleWrapper(domain: String): FSBrowserModuleWrapper? {
        return wrapperMap[domain] ?: kotlin.run {
            LogTool.warn(FSBrowserConstants.TAG, "FSBrowserComponent do not contain the FSBrowserModule named %s." + domain)
            return null
        }
    }

    fun <T : FSBrowserModule> getModule(clazz: Class<T>): T? {
        val module = moduleMap[clazz] ?: kotlin.run {
            LogTool.warn(FSBrowserConstants.TAG, "FSBrowserComponent do not contain the FSBrowserModule classed %s." + clazz.simpleName)
            return null
        }
        return clazz.cast(module)
    }

    fun invokeJSMethod(functionName: String) {
        invokeJSMethod(functionName, "{}")
    }

    fun invokeJSMethod(functionName: String, args: String) {
        (browserView.context as Activity).runOnUiThread {
            var content = String.format("javascript:%s.%s(\"%s\", %s)", jsDomain, jsEnterMethodName, functionName, args);
            LogTool.debug(FSBrowserConstants.TAG, "invokeJSMethod: content = " + content)
            browserView.loadUrl(content)
        }
    }

    fun captureScreen() = browserView.captureScreen()

    fun destroy() {
        moduleMap.values.forEach(fun(module: FSBrowserModule) {
            module.destroy()
        })
        moduleMap.clear()
        wrapperMap.clear()
        browserView.destroy()
    }
}