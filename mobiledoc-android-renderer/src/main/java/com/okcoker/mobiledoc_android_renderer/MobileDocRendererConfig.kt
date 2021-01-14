package com.okcoker.mobiledoc_android_renderer

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes

// @todo figure out how to allow people to customize their views.
// Should these be individual values? CharacterStyles? Mixed?
// I think we should definitely have sensible defaults
data class MobileDocRendererConfig(
    @ColorRes val linkColor: Int = R.color.linkColor,
    @DimenRes val sectionSpacing: Int = R.dimen.section_spacing
)