package com.okcoker.mobiledocexample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.okcoker.mobiledoc_android_renderer.MobileDocRenderer
import com.okcoker.mobiledoc_android_renderer.data.card.defaults.DefaultHTML
import com.okcoker.mobiledoc_android_renderer.data.card.defaults.DefaultImage
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mobiledoc = getJsonDataFromAsset(this, "audiomack.json") ?: return
        val container = findViewById<LinearLayout>(R.id.container)
        val customCards = listOf(DefaultHTML(), DefaultImage())

        val renderer = MobileDocRenderer(mobiledoc, cards = customCards)

        renderer.render(this).result.forEach { v ->
            container.addView(v)
        }
    }

    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
}