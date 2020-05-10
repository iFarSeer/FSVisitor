package com.ifarseer.android.browser.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView


/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */

class FSWebView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    companion object {
        val CACHE_PATH = "/data/data/%s/cache"
        val CACHE_MAX_SIZE = 8 * 1024 * 1024L
        val DATABASES_DIR = "databases"
    }

    private var webSettingsCallback: FSWebSettingsCallback? = null

    init {
        isVerticalFadingEdgeEnabled = false
        isHapticFeedbackEnabled = false
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowContentAccess = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setAppCacheEnabled(true)
        settings.setAppCachePath(String.format(CACHE_PATH, context.packageName))
        settings.setAppCacheMaxSize(CACHE_MAX_SIZE)

        var databasePath = context.getDir(DATABASES_DIR, Context.MODE_PRIVATE).path
        settings.databasePath = databasePath
        settings.builtInZoomControls = false
        settings.databaseEnabled = true
        settings.pluginState = WebSettings.PluginState.ON
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.allowContentAccess = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.allowFileAccessFromFileURLs = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            enableSlowWholeDocumentDraw()
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true)
        }
    }


    fun setFSWebSettingsCallback(callback: FSWebSettingsCallback?) {
        webSettingsCallback = callback
        val fsWebViewClient = FSWebViewClient()
        val fsWebChromeClient = FSWebChromeClient()
        fsWebViewClient.webSettingsCallback = callback
        fsWebChromeClient.webSettingsCallback = callback

        webViewClient = fsWebViewClient
        webChromeClient = fsWebChromeClient
    }

    fun setUserAgent(userAgent: String) {
        settings.userAgentString = userAgent
    }

    fun captureScreen(): Bitmap {
        val height = (contentHeight * scale + 0.5).toInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    override fun destroy() {
        onPause()
        pauseTimers()
        super.destroy()
    }
}
