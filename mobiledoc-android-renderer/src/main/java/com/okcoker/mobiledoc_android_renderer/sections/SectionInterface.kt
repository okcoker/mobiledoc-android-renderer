package com.okcoker.mobiledoc_android_renderer.sections

import android.content.Context
import android.view.View
import com.okcoker.mobiledoc_android_renderer.MobileDocRendererConfig
import com.okcoker.mobiledoc_android_renderer.data.markup.MarkupSectionType

interface SectionInterface {
    val type: MarkupSectionType
    fun render(context: Context?, config: MobileDocRendererConfig): View
}
