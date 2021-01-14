package com.okcoker.mobiledoc_android_renderer

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.okcoker.mobiledoc_android_renderer.data.MarkerType
import com.okcoker.mobiledoc_android_renderer.data.RenderCallback
import com.okcoker.mobiledoc_android_renderer.data.card.Card
import com.okcoker.mobiledoc_android_renderer.data.card.CardEnvironment
import com.okcoker.mobiledoc_android_renderer.data.card.CardInterface
import com.okcoker.mobiledoc_android_renderer.data.markup.Markup
import com.okcoker.mobiledoc_android_renderer.data.markup.MarkupInterface
import com.okcoker.mobiledoc_android_renderer.data.markup.MarkupSectionType
import com.okcoker.mobiledoc_android_renderer.data.markup.MarkupTagName
import com.okcoker.mobiledoc_android_renderer.markers.Atom
import com.okcoker.mobiledoc_android_renderer.markers.AtomEnvironment
import com.okcoker.mobiledoc_android_renderer.markers.AtomInterface
import com.okcoker.mobiledoc_android_renderer.markers.Marker
import com.okcoker.mobiledoc_android_renderer.sections.CardSection
import com.okcoker.mobiledoc_android_renderer.sections.ListSection
import com.okcoker.mobiledoc_android_renderer.sections.MarkupSection
import com.okcoker.mobiledoc_android_renderer.sections.SectionInterface
import com.okcoker.mobiledoc_android_renderer.utils.SimpleSpanBuilder
import org.json.JSONArray
import org.json.JSONObject

/**
 * Render for mobiledoc v0.3.1
 */

class MobileDocRender(val result: List<View>, val teardown: RenderCallback)

class MobileDocRenderer(mobiledoc: String,
                        private val config: MobileDocRendererConfig = MobileDocRendererConfig(),
                        private val atoms: List<AtomInterface>? = null,
                        private val cards: List<CardInterface>? = null,
                        private val sections: List<SectionInterface>? = null,
                        private val markups: List<MarkupInterface>? = null) {

    private val renderCallbacks = ArrayList<RenderCallback>()
    private val mobiledocObject = JSONObject(mobiledoc)

    fun render(context: Context): MobileDocRender {
//        val ta = context.obtainStyledAttributes(null, R.styleable.MAR)
//
//        val color = ta.getColor(R.styleable.MAR_mar_link_color, Color.BLUE)
//
//        Log.d("MAR->isBlue", "${color == Color.BLUE}")
//        ta.recycle()
        val renderedSections = renderSections(context)

        renderCallbacks.forEach { callback ->
            callback.invoke()
        }

        val teardown = { }

        return MobileDocRender(renderedSections, teardown)
    }

    private fun renderSections(context: Context): List<View> {
        val sectionsList = mobiledocObject.getJSONArray("sections")
        val views = arrayListOf<View>()

        (0 until sectionsList.length()).map { index ->
            val sectionArgs = sectionsList.getJSONArray(index)
            val sectionTypeMap = MarkupSectionType.values().associateBy(MarkupSectionType::type)
            val type = sectionTypeMap.getValue(sectionArgs.getInt(0))

            val view = when (type) {
                MarkupSectionType.MARKUP -> renderMarkupSection(context, sectionArgs)
                MarkupSectionType.IMAGE -> renderImageSection(context, sectionArgs)
                MarkupSectionType.LIST -> renderListSection(context, sectionArgs)
                MarkupSectionType.CARD -> renderCardSection(context, sectionArgs)
            }

            if (view == null) {
                Log.d("MAR->renderSection", "$type $index")
            }

            view?.let {
                views.add(it)
            }
        }

        return views.toList()
    }

    private fun renderMarkupSection(context: Context, sectionArgs: JSONArray): View {
        val section = MarkupSection.fromJSON(sectionArgs)
        val markers = section.markers
        val customSection = sections?.find { (it as? MarkupSection)?.tagName === section.tagName }

        // Since the sections consist of like h1, h2, p, etc. we may not even want to
        // allow custom section renders as to prevent the user from erroring when renderMarkersOnElement
        // is most likely going to return a SpannableString that HAS to be in a TextView
        //
        // HTML is obviously more flexible as many elements can be children of many other elements
        // Since this is the first native renderer, we can explore whether or not we should remove
        // first first part of this ternary statement below
        val sectionView = customSection?.render(context, config) ?: section.render(context, config)

        return renderMarkersOnElement(context, sectionView, markers)
    }


    private fun renderImageSection(context: Context, sectionArgs: JSONArray): View {
//        val section = ImageSectionInterface.fromJSON(sectionArgs)


        return View(context)
    }

    private fun renderListSection(context: Context, sectionArgs: JSONArray): View {
        val section = ListSection.fromJSON(sectionArgs)
        val listMarkers = section.markers
        val list = section.render(context, config)

        renderMarkersOnElement(context, list, listMarkers)

        return list
    }

    private fun renderCardSection(context: Context, sectionArgs: JSONArray): View? {
        val section = CardSection.fromJSON(sectionArgs)
        val index = section.index
        val cardsRoot = mobiledocObject.getJSONArray("cards")
        val cardList = arrayListOf<CardInterface>()

        (0 until cardsRoot.length()).map { index ->
            val cardData = cardsRoot.getJSONArray(index)
            val card = Card.fromJSON(cardData)

            cardList.add(card)
        }

        val card = cardList[index]
        val name = card.name

        val customCard = cards?.find { card ->
            card.name == name
        }

        val cardEnvironment = CardEnvironment(name, false, context, { callback ->
            registerRenderCallback(callback)
        }) { callback ->
            registerRenderCallback(callback)
        }

        return customCard?.render?.invoke(cardEnvironment, null, card.payload) ?: card.render(cardEnvironment, null, card.payload)
    }

    private fun renderMarkersOnElement(context: Context, sectionView: View, markers: List<Marker>): View {
        val ssb = SimpleSpanBuilder("")
        val spans = arrayListOf<SimpleSpanBuilder.Span>()
        val markupsRoot = mobiledocObject.getJSONArray("markups")
        val markupList = arrayListOf<Markup>()

        (0 until markupsRoot.length()).map { index ->
            val markup = Markup.fromJSON(markupsRoot.getJSONArray(index))

            markupList.add(markup)
        }

        val runningMarkerIdList = ArrayList<Int>()

        markers.forEachIndexed { index, marker ->
            val type = marker.type
            val markerIds = marker.markupMarkerIds
            var closeCount = marker.closeCount
            val value = marker.value
            val noopMarkerId = -1

            val injectSpan = {
                // -1 will basically equal the html span tag where no
                // character styles should be applied because it's a
                // just a floating HTML Text Node
                runningMarkerIdList.add(noopMarkerId)
                closeCount += 1
            }

            markerIds.forEach { markerId ->
                val markup = markupList.getOrNull(markerId)

                if (markup != null) {
                    runningMarkerIdList.add(markerId)
                    return@forEach
                }

                injectSpan()
            }

            if (markerIds.isEmpty()) {
                injectSpan()
            }

            val enclosedTagName = markupList.getOrNull(runningMarkerIdList.last())?.tagName
            val parentMarkups = runningMarkerIdList.subList(0, runningMarkerIdList.size - 1).mapNotNull { id ->
                markupList.getOrNull(id)
            }
            val customMarkup = markups?.find { markup -> markup.tagName == enclosedTagName }
            val defaultMarkup = markupList.getOrNull(runningMarkerIdList.last())
            val currentSpan = customMarkup?.render(context, config, value, parentMarkups)
                    ?: defaultMarkup?.render(context, config, value, parentMarkups)
                    ?: Markup().apply {
                        tagName = MarkupTagName.SPAN
                        attributes = listOf()
                    }.render(context, config, value, parentMarkups)

//            Log.d("MAR->renderMarkers", "$value : ${defaultMarkup?.tagName} : ${parentMarkups.map { m -> m.tagName }}")

            when (type) {
                MarkerType.TEXT -> {

                }
                MarkerType.ATOM -> {
                    renderAtomSection(index, value)?.let {
                        Log.e("MAR->renderAtomSection", "Not implemented")
                    }
                }
            }

            spans.add(currentSpan)

            for (x in 0 until closeCount) {
                runningMarkerIdList.removeAt(runningMarkerIdList.size - 1)
            }
        }

        spans.forEach {
            ssb.plus(it)
        }

        if (sectionView is TextView) {
            sectionView.text = ssb.build()
            sectionView.setLinkTextColor(context.resources.getColor(config.linkColor))
            sectionView.linksClickable = true
            spans.removeAll(spans)
        }
        else {
            Log.e("MAR->", "not textview $sectionView")
        }

        return sectionView
    }


    private fun renderAtomSection(atomIndex: Int, value: String): View? {
        val atomsRoot = mobiledocObject.getJSONArray("atoms")
        val atomList = arrayListOf<AtomInterface>()

        (0 until atomsRoot.length()).map { index ->
            val atom = Atom.fromJSON(atomsRoot.getJSONArray(index))

            atomList.add(atom)
        }

        val atom = atoms?.find { a ->  a.name == atomList.getOrNull(atomIndex)?.name }


        if (atom != null) {
            val env = AtomEnvironment(
                atom.name,
                false
            )

            return atom.render?.invoke(env, null, null, value)
        }

        return null
    }

    private fun registerRenderCallback(cb: RenderCallback) {
        renderCallbacks.add(cb)
    }
}

























