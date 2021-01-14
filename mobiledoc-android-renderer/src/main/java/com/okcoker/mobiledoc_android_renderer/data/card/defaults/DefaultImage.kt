package com.okcoker.mobiledoc_android_renderer.data.card.defaults

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import com.okcoker.mobiledoc_android_renderer.data.card.CardEnvironment
import com.okcoker.mobiledoc_android_renderer.data.card.CardInterface
import com.okcoker.mobiledoc_android_renderer.data.card.CardRenderer
import org.json.JSONObject
import java.net.URL

private class DownloadImageTask(private val imageView: ImageView): AsyncTask<String, Void, Bitmap>() {
    override fun doInBackground(vararg urls: String?): Bitmap? {
        val urlDisplay = urls[0]
        var bitmap: Bitmap? = null

        try {
            val inputStream = URL(urlDisplay).openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("Error", e.message ?: "")
            e.printStackTrace()
        }

        return bitmap
    }

    override fun onPostExecute(result: Bitmap?) {
        if (result != null) {
            imageView.setImageBitmap(result)
        }
    }
}

private val renderer: CardRenderer = { env: CardEnvironment, options: Any?, payload: JSONObject ->
    try {
        val imageUrl = payload.getString("src")
        val imageView = ImageView(env.context)
        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300)

        DownloadImageTask(imageView).execute(imageUrl)

        imageView
    }
    catch (e: Exception) {
        null
    }
}


class DefaultImage(override val name: String = "image", override var render: CardRenderer = renderer): CardInterface {
    override val type = "Native"
    override var payload = JSONObject()
}
