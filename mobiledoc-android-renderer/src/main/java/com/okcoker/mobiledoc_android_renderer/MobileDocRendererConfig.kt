package com.okcoker.mobiledoc_android_renderer

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes

data class MobileDocRendererConfig(
    @ColorRes val linkColor: Int = R.color.linkColor,
    @DimenRes val sectionSpacing: Int = R.dimen.section_spacing
)