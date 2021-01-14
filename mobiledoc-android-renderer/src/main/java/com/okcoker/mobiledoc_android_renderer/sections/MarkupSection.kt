package com.okcoker.mobiledoc_android_renderer.sections

import android.content.Context
import android.view.View
import android.widget.TextView
import com.okcoker.mobiledoc_android_renderer.MobileDocRendererConfig
import com.okcoker.mobiledoc_android_renderer.data.markup.MarkupSectionTagName
import com.okcoker.mobiledoc_android_renderer.data.markup.MarkupSectionType
import com.okcoker.mobiledoc_android_renderer.markers.Marker
import org.json.JSONArray

internal class MarkupSection(var markers: List<Marker>, var tagName: MarkupSectionTagName = MarkupSectionTagName.P): SectionInterface {
    override val type = MarkupSectionType.MARKUP

    override fun render(context: Context?, config: MobileDocRendererConfig): View {
//        return when (tagName) {
//            MarkupSectionTagName.H3 -> {
        val textView = TextView(context)
//        textView.text = markers[0].value
//
//        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
////        params.bottomMargin = config.sectionSpacing
//        textView.layoutParams = params
//        textView.setPadding(0, 0, 0, config.sectionSpacing)

        return textView
//            }
//            else -> View(context)
//        }
    }

    companion object {
        @JvmStatic
        fun fromJSON(jsonArray: JSONArray): MarkupSection {
            val tagNameMap = MarkupSectionTagName.values().associateBy(MarkupSectionTagName::type)
            val tagName = tagNameMap.getValue(jsonArray.getString(1))

            val tempMarkers = ArrayList<Marker>()
            jsonArray.getJSONArray(2)?.let { idList ->
                (0 until idList.length()).map { index ->
                    val marker = Marker.fromJSON(idList.getJSONArray(index))

                    tempMarkers.add(marker)
                }
            }

            val markers = tempMarkers.toList()

            return MarkupSection(markers, tagName)
        }
    }
}