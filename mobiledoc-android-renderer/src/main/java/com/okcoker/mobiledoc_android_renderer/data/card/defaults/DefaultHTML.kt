package com.okcoker.mobiledoc_android_renderer.data.card.defaults

import android.graphics.Color
import android.webkit.WebView
import android.widget.LinearLayout
import com.okcoker.mobiledoc_android_renderer.data.card.CardEnvironment
import com.okcoker.mobiledoc_android_renderer.data.card.CardInterface
import com.okcoker.mobiledoc_android_renderer.data.card.CardRenderer
import org.json.JSONObject

private val renderer: CardRenderer = { env: CardEnvironment, options: Any?, payload: JSONObject ->
    try {
        val webview = WebView(env.context).apply {
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            setBackgroundColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 900)

            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
            }
        }
        val html = payload.getString("html")


        webview.loadData(html, "text/html", "utf-8")

        webview
    }
    catch (e: Exception) {
        null
    }
}


class DefaultHTML(override val name: String = "html", override val render: CardRenderer = renderer): CardInterface {
    override val type = "Native"
    override var payload = JSONObject()
}