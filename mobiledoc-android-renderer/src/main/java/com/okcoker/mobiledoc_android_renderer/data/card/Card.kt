package com.okcoker.mobiledoc_android_renderer.data.card

import android.view.View
import com.okcoker.mobiledoc_android_renderer.data.RenderCallback
import org.json.JSONArray
import org.json.JSONObject

data class CardEnvironment(val name: String,
                           val isInEditor: Boolean,
                           var onTearDown: (RenderCallback) -> Unit ,
                           val didRender: (RenderCallback) -> Unit)

typealias CardRenderer = (env: CardEnvironment,
                                   options: Any?,
                                   payload: JSONObject) -> View

interface CardInterface {
    val name: String
    val type: String
    val payload: JSONObject
    var render: CardRenderer?
}

internal class Card(override val name: String, override val payload: JSONObject, override var render: CardRenderer? = null): CardInterface {
    override val type = "Native"

    companion object {
        @JvmStatic
        fun fromJSON(jsonArray: JSONArray): Card {
            val name = jsonArray.getString(0)
            val payload = jsonArray.getJSONObject(1)

            return Card(name, payload, null)
        }
    }
}