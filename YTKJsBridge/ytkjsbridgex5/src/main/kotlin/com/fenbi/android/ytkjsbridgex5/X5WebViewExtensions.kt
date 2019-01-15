package com.fenbi.android.ytkjsbridgex5

import android.annotation.SuppressLint
import android.os.Build
import com.fenbi.android.ytkjsbridge.JsCallback
import com.fenbi.android.ytkjsbridge.YTKJsBridge
import com.tencent.smtt.sdk.WebView

/**
 * @author zheng on 1/4/19
 */

private const val TAG_KEY_YTK_JS_BRIDGE = -10001

val WebView.ytkJsBridge: YTKJsBridge
    @SuppressLint("JavascriptInterface")
    get() {
        val bridge = getTag(TAG_KEY_YTK_JS_BRIDGE)
        return if (bridge != null) bridge as YTKJsBridge
        else throw IllegalStateException("You must call WebView.initYTKJsBridge() before using YTKJsBridge")
    }

var WebView.debugMode: Boolean
    get() = ytkJsBridge.debugMode
    set(value) {
        ytkJsBridge.debugMode = value
    }

@SuppressLint("JavascriptInterface")
fun WebView.initYTKJsBridge() {
    if (!settings.javaScriptEnabled) {
        settings.javaScriptEnabled = true
    }
    val bridge = YTKJsBridge().also {
        it.jsEvaluator = { script ->
            if (Build.VERSION.SDK_INT >= 19) {
                evaluateJavascript(script, null)
            } else {
                loadUrl("javascript:$script")
            }
        }
    }
    setTag(TAG_KEY_YTK_JS_BRIDGE, bridge)
    addJavascriptInterface(bridge.javascriptInterface, YTKJsBridge.BRIDGE_NAME)
}

fun WebView.addYTKJavascriptInterface(obj: Any, namespace: String = "") {
    ytkJsBridge.addYTKJavascriptInterface(obj, namespace)
}

fun <T> WebView.call(methodName: String, vararg args: Any?, callback: (T?) -> Unit) {
    ytkJsBridge.call<T>(methodName, *args, callback = callback)
}

fun <T> WebView.call(methodName: String, vararg args: Any?, callback: JsCallback<T>?) {
    ytkJsBridge.call(methodName, *args, callback = callback)
}

suspend fun <T> WebView.call(methodName: String, vararg args: Any?): T? {
    return ytkJsBridge.call(methodName, *args)
}

inline fun <reified T> WebView.getJsInterface(): T {
    return ytkJsBridge.getJsInterface()
}
