package com.okcoker.mobiledoc_android_renderer.markers

import com.okcoker.mobiledoc_android_renderer.data.MarkerType
import org.json.JSONArray

internal class Marker {
    lateinit var type: MarkerType
    lateinit var markupMarkerIds: List<Int>
    var closeCount: Int = 0
    lateinit var value: String

    companion object {
        @JvmStatic
        fun fromJSON(jsonArray: JSONArray): Marker {
            return Marker().apply {
                val map = MarkerType.values().associateBy(MarkerType::type)
                type = map.getValue(jsonArray.getInt(0))
                val markerIds = arrayListOf<Int>()


                jsonArray.getJSONArray(1)?.let { idList ->
                    (0 until idList.length()).map { index ->
                        val id = idList.getInt(index)

                        markerIds.add(id)
                    }
                }

                markupMarkerIds = markerIds

                closeCount = jsonArray.getInt(2)
                value = jsonArray.optString(3) ?: ""
            }
        }
    }
}
