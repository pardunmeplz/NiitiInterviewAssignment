package com.example.interviewassignment_niiti

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import java.lang.Exception

val cache : HashMap<String,Bitmap> = HashMap() // URL -> Img

class ImageFromURL(private val view:ImageView) {
    fun setURL(url:String, animate:Boolean= true){

        default()
        // check cache
        if (url in cache){
            view.setImageBitmap(cache[url])
            return
        }
        //fetch
        doAsync {
            val image = try {
                val stream = java.net.URL(url).openStream()
                BitmapFactory.decodeStream(stream)
            } catch (e:Exception) { null }

            onComplete {
                if (image == null) {
                    default()
                    return@onComplete
                }
                if (animate) completeAnimation()
                cache[url] = image
                view.setImageBitmap(image)
            }
        }
    }
    private fun default() {
        view.setImageResource(R.drawable.ic_launcher_foreground)
    }
    private fun completeAnimation(){
        view.animate().apply {
            duration = 500
            rotationYBy(360f)
        }
    }
}