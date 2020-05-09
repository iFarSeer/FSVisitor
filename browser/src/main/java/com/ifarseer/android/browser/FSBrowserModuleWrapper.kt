package com.ifarseer.android.browser

import com.ifarseer.android.browser.annotation.BrowserModule
import com.ifarseer.android.browser.annotation.BrowserNativeMethod
import com.ifarseer.android.browser.tool.LogTool
import com.ifarseer.android.browser.tool.MethodTool
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */
class FSBrowserModuleWrapper(private val module: FSBrowserModule) {
    var name: String? = null
        get() {
            if (field != null) return field
            if (module.javaClass.getAnnotation(BrowserModule::class.java) == null) {
                throw IllegalArgumentException(String.format("FSBrowserModule: %s must use the annotation named BrowserModule.", module.javaClass.simpleName))
            }
            return module.javaClass.getAnnotation(BrowserModule::class.java)?.name
        }
    private val nativeMethodMap = ConcurrentHashMap<String, Method>()


    init {
        val targetMethods = module.javaClass.declaredMethods
        LogTool.debug(FSBrowserConstants.TAG, String.format("FSBrowserModule: %s has such method:", name))
        targetMethods.forEach(fun(method: Method) {
            val annotation = method.getAnnotation(BrowserNativeMethod::class.java)
            if (annotation != null) {
                val signature = MethodTool.generateSignature(method)
                LogTool.debug(FSBrowserConstants.TAG, String.format("FSBrowserModule: methodName = %s, signature = %s.", method.name, signature))
                if (nativeMethodMap.containsKey(signature)) {
                    throw IllegalArgumentException(String.format("FSBrowserModule: %s only support register the method(%s) once.", name, signature))
                }
                nativeMethodMap.put(signature, method)
            }
        })
    }

    fun invoke(methodName: String, args: String, successCallback: String, failureCallback: String) {
        var signature = MethodTool.buildSignature(methodName, Void.TYPE, arrayOf<Class<*>>(String::class.java, FSBrowserJSCallback::class.java))
        LogTool.debug(FSBrowserConstants.TAG, String.format("FSBrowserModuleWrapper: invoke method's signature = %s", signature))
        var method = nativeMethodMap[signature]
        method?.isAccessible = true

        if (method == null) {
            LogTool.debug(FSBrowserConstants.TAG, String.format("FSBrowserModuleWrapper: %s do not have the method(%s) named %s.", name, signature, methodName))
            module.component.invokeJSMethod(failureCallback)
            return
        }
        method.invoke(module, args, FSBrowserJSCallback(module, successCallback, failureCallback))
    }
}