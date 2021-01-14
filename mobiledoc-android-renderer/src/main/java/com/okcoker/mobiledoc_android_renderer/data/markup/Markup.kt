package com.okcoker.mobiledoc_android_renderer.data.markup

import android.content.Context
import android.graphics.Typeface
import android.text.style.*
import android.util.Log
import com.okcoker.mobiledoc_android_renderer.MobileDocRendererConfig
import com.okcoker.mobiledoc_android_renderer.utils.SimpleSpanBuilder
import org.json.JSONArray

typealias MarkupAttribute = Pair<MarkupAttributeName, String>
interface MarkupInterface {
    var tagName: MarkupTagName
    var attributes: List<MarkupAttribute>
    fun render(context: Context, config: MobileDocRendererConfig, value: String, markups: List<MarkupInterface>): SimpleSpanBuilder.Span
}

internal class Markup: MarkupInterface {
    override lateinit var tagName: MarkupTagName
    override lateinit var attributes: List<MarkupAttribute>

    override fun render(context: Context, config: MobileDocRendererConfig, value: String, parentMarkups: List<MarkupInterface>): SimpleSpanBuilder.Span {
        val markups = mutableListOf<MarkupInterface>(this)
        markups.addAll(parentMarkups)

        return buildStyledSpan(context, config, value, markups)
    }

    companion object {
        @JvmStatic
        fun fromJSON(jsonArray: JSONArray): Markup {
            return Markup().apply {
                val tagMap = MarkupTagName.values().associateBy(MarkupTagName::type)
                val attrMap = MarkupAttributeName.values().associateBy(MarkupAttributeName::type)

                tagName = tagMap.getValue(jsonArray.getString(0))

                val tempAttributes = ArrayList<MarkupAttribute>()

                jsonArray.optJSONArray(1)?.let { attributeList ->
                    (0 until attributeList.length() step 2).map { index ->
                        val attrName = attrMap.getValue(attributeList.optString(index))
                        val value = attributeList.optString(index + 1)
                        val attribute = MarkupAttribute(attrName, value)

                        tempAttributes.add(attribute)
                    }
                }

                attributes = tempAttributes.toList()
            }
        }

        @JvmStatic
        fun buildStyledSpan(context: Context, config: MobileDocRendererConfig, value: String, markups: List<MarkupInterface>): SimpleSpanBuilder.Span {
            val styles = arrayListOf<CharacterStyle>()

            markups.forEach {
                when (it.tagName) {
                    MarkupTagName.B,
                    MarkupTagName.STRONG -> {
                        styles.add(StyleSpan(Typeface.BOLD))
                    }

                    MarkupTagName.I,
                    MarkupTagName.EM -> {
                        styles.add(StyleSpan(Typeface.ITALIC))
                    }

                    MarkupTagName.CODE -> {
                        styles.add(TypefaceSpan("monospace"))
                    }

                    MarkupTagName.S -> {
                        styles.add(StrikethroughSpan())
                    }

                    MarkupTagName.SUB -> {
                        styles.add(SubscriptSpan())
                    }

                    MarkupTagName.SUP -> {
                        styles.add(SuperscriptSpan())
                    }

                    MarkupTagName.U -> {
                        styles.add(UnderlineSpan())
                    }

                    MarkupTagName.A -> {
                        //                    styles.add(ForegroundColorSpan(context.resources.getColor(config.linkColor)))

                        it.attributes.forEach { attr ->
                            if (attr.first == MarkupAttributeName.HREF && attr.second.isNotEmpty()) {
                                Log.d("link", "${attr.first} : ${attr.second}")
                                styles.add(URLSpan(attr.second))
                            }
                        }
                    }

                    MarkupTagName.SPAN -> {
                        // Empty on purpose
                    }
                }
            }

            return SimpleSpanBuilder.Span(value, *(styles.toTypedArray()))
        }
    }
}