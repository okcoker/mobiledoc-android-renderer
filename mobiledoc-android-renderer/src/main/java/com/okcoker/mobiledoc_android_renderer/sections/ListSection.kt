package com.okcoker.mobiledoc_android_renderer.sections

import android.content.Context
import android.widget.LinearLayout
import com.okcoker.mobiledoc_android_renderer.MobileDocRendererConfig
import com.okcoker.mobiledoc_android_renderer.data.list.ListSectionTagName
import com.okcoker.mobiledoc_android_renderer.data.markup.MarkupSectionType
import com.okcoker.mobiledoc_android_renderer.markers.Marker
import org.json.JSONArray

internal class ListSection(var markers: List<Marker>, var tagName: ListSectionTagName = ListSectionTagName.OL): SectionInterface {
    override val type = MarkupSectionType.LIST

    override fun render(context: Context?, config: MobileDocRendererConfig): LinearLayout {
        val list = LinearLayout(context)
        list.orientation = LinearLayout.VERTICAL

        return list
    }

    companion object {
        @JvmStatic
        fun fromJSON(jsonArray: JSONArray): ListSection {
            val tagNameMap = ListSectionTagName.values().associateBy(ListSectionTagName::type)
            val tagName = tagNameMap.getValue(jsonArray.getString(1))

            val tempMarkers = ArrayList<Marker>()
            jsonArray.getJSONArray(2)?.let { idList ->
                (0 until idList.length()).map { index ->
                    val marker = Marker.fromJSON(idList.getJSONArray(index))

                    tempMarkers.add(marker)
                }
            }

            val markers = tempMarkers.toList()

            return ListSection(markers, tagName)
        }
    }
}
