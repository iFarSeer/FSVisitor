package com.ifarseer.android.browser.annotation

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class BrowserNativeMethod(val name :String)