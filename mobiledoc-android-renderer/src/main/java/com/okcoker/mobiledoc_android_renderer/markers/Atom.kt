package com.okcoker.mobiledoc_android_renderer.markers

import android.view.View
import com.okcoker.mobiledoc_android_renderer.data.RenderCallback
import org.json.JSONArray
import org.json.JSONObject

typealias AtomRenderer = (env: AtomEnvironment,
                          options: Any?,
                          payload: JSONObject?,
                          value: String?) -> View

typealias Save = ((String, JSONObject) -> Unit)?
data class AtomEnvironment(val name: String,
                           var isInEditor: Boolean = false,
                           var onTearDown: ((RenderCallback) -> Unit)? = null,
                           val save: Save? = null)

interface AtomInterface {
    val name: String
    val type: String
    val payload: JSONObject
    val render: AtomRenderer?
}

internal class Atom(override val name: String, override val payload: JSONObject, override val render: AtomRenderer?): AtomInterface {
    override val type = "Native"

    companion object {
        @JvmStatic
        fun fromJSON(jsonArray: JSONArray): Atom {
            val name = jsonArray.getString(0)
            val payload = jsonArray.getJSONObject(1)

            return Atom(name, payload, null)
        }
    }
}
