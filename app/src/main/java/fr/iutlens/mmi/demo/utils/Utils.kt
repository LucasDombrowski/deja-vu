package fr.iutlens.mmi.demo.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.compose.ui.graphics.ColorMatrix
import androidx.core.content.ContextCompat.getDrawable
import kotlinx.coroutines.Delay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/*
fun getStringResourceByName(context: Context, aString: String?): String {
    val packageName = context.packageName
    val resId = context.resources.getIdentifier(aString, "string", packageName)
    return context.getString(resId)
}*/

fun loadImage(context: Context, id: Int): Bitmap? {

//		Drawable blankDrawable = context.getResources().getDrawable(id);
//		Bitmap b =((BitmapDrawable)blankDrawable).getBitmap();
    return BitmapFactory.decodeResource(context.resources, id)
}

fun loadImages(context: Context, id1: Int, id2: Int): Bitmap? {
    val blankDrawable = getDrawable(context,id1)
    val b = (blankDrawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val c = Canvas(b)
    c.drawBitmap(loadImage(context, id2) ?: return null, 0f, 0f, null)
    return b
}

fun createCroppedBitmap(
    src: Bitmap,
    left: Int, top: Int,
    width: Int, height: Int
): Bitmap = if (Build.VERSION.SDK_INT > 22) {

    Bitmap.createBitmap(src, left+1, top+1, width-2, height-2)
    //bug: returns incorrect region for some version,  so must do it manually
} else {
    val offset = 0
    val pixels = IntArray((width-2) * (height-2))
    src.getPixels(pixels, offset, (width-2), (left+1), (top+1), (width-2), (height-2))
    Bitmap.createBitmap(pixels, (width-2), (height-2), src.config)
}

fun setInterval(delayTime: Long, awaitTime: Long, action: ()->Unit): Job{
    return GlobalScope.launch {
        delay(delayTime)
        while (true){
            delay(awaitTime)
            action()
        }
    }
}

fun getDistance(x1 : Float, y1 : Float, x2 : Float, y2 : Float) : Float{
    return sqrt((x2-x1).pow(2)+(y2-y1).pow(2))
}

fun getCenter(x1: Float, y1: Float, x2: Float, y2: Float) : List<Float>{
    return listOf(
        (x1+x2)/2, (y1+y2)/2
    )
}
fun rotationFromCenter(x: Float, y: Float, value: Float) : List<Float>{
    return listOf(
        cos(value)*x - sin(value)*y,
        sin(value)*x + cos(value)*y
    )
}

fun getTranslation(x1: Float, y1: Float, x2: Float, y2: Float) : List<Float>{
    return listOf(
        x2-x1,
        y2-y1
    )
}

fun rotationFromPoint(x : Float, y: Float, rotationX: Float, rotationY: Float, value: Float) : List<Float>{
    val centerRotation = rotationFromCenter(rotationX, rotationY, value)
    val centerTranslation = getTranslation(centerRotation[0], centerRotation[1], rotationX, rotationY)
    return listOf(
        rotationFromCenter(x,y, value)[0] + centerTranslation[0],
        rotationFromCenter(x,y,value)[1] + centerTranslation[1]
    )
}



