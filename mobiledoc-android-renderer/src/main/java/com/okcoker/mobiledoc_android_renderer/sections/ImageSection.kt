package com.okcoker.mobiledoc_android_renderer.sections

import android.content.Context
import android.view.View
import com.okcoker.mobiledoc_android_renderer.MobileDocRendererConfig
import com.okcoker.mobiledoc_android_renderer.data.markup.MarkupSectionType
import org.json.JSONArray

internal class ImageSection(var url: String): SectionInterface {
    override val type = MarkupSectionType.IMAGE

    override fun render(context: Context?, config: MobileDocRendererConfig): View {
        return View(context)
    }

    companion object {
        @JvmStatic
        fun fromJSON(jsonArray: JSONArray): ImageSection {
            val url = jsonArray.getString(1)

            return ImageSection(url)
        }
    }
}