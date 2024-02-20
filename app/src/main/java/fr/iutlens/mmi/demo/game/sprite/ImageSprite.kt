package fr.iutlens.mmi.demo.game.sprite

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.getCurrentActivityContext

class ImageSprite(val imageId : Int, val imageSize: Int, val x: Float, val y: Float) : Sprite {

    val bitmap : Bitmap = imageToBitmap()
    private val w2 = bitmap.width / 2f
    private val h2 = bitmap.height / 2f

    override val boundingBox get() = RectF(x - w2, y - h2, x + w2, y + h2)
    fun imageToBitmap() : Bitmap{
        val context = getCurrentActivityContext()
        val bitmap = BitmapFactory.decodeResource(context.resources,imageId)
        val sizeX = imageSize
        val aspectRatio = bitmap.height/bitmap.width
        val sizeY = sizeX*aspectRatio
        val bitmapImage = Bitmap.createScaledBitmap(bitmap,sizeX,sizeY,true)
        return bitmapImage
    }

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.withTransform({
            translate(x,y)
        }){
            this.drawImage(bitmap.asImageBitmap(), topLeft = Offset(-w2,-h2))
        }
    }

    override fun update() {

    }
}