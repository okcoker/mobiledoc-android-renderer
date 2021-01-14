package com.okcoker.mobiledoc_android_renderer.sections

import android.content.Context
import android.view.View
import com.okcoker.mobiledoc_android_renderer.MobileDocRendererConfig
import com.okcoker.mobiledoc_android_renderer.data.markup.MarkupSectionType
import org.json.JSONArray

internal class CardSection(var index: Int = 0): SectionInterface {
    override val type = MarkupSectionType.CARD

    override fun render(context: Context?, config: MobileDocRendererConfig): View {
        return View(context)
    }

    companion object {
        @JvmStatic
        fun fromJSON(jsonArray: JSONArray): CardSection {
            return CardSection().apply {
                index = jsonArray.getInt(1)
            }
        }
    }
}