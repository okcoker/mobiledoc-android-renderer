package com.okcoker.mobiledoc_android_renderer.data.card

import android.content.Context
import android.view.View
import com.okcoker.mobiledoc_android_renderer.data.RenderCallback
import org.json.JSONArray
import org.json.JSONObject

data class CardEnvironment(val name: String,
                           val isInEditor: Boolean,
                           val context: Context,
                           var onTearDown: (RenderCallback) -> Unit ,
                           val didRender: (RenderCallback) -> Unit)

typealias CardRenderer = (env: CardEnvironment,
                                   options: Any?,
                                   payload: JSONObject) -> View?

// @todo I dont really like how custom cards have to have an empty JSONObject() payload attached
// to them When they will be passed a payload from the Card object created from the mobiledoc
// during render time via CardRenderer
interface CardInterface {
    val name: String
    val type: String
    var payload: JSONObject
    val render: CardRenderer
}

internal val nullRender: CardRenderer = { _,_,_ -> null }

internal class Card(override val name: String, override val render: CardRenderer = nullRender): CardInterface {
    override val type = "Native"
    override var payload = JSONObject()

    companion object {
        @JvmStatic
        fun fromJSON(jsonArray: JSONArray): Card {
            val name = jsonArray.getString(0)
            val payloadData = jsonArray.getJSONObject(1)

            return Card(name, nullRender).apply {
                payload = payloadData
            }
        }
    }
}