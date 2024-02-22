package fr.iutlens.mmi.demo.game.sprite

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toArgb
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.getCurrentActivityContext
import kotlin.math.roundToInt

class TextSprite(val text: String, val textSize: Float, val textColor: Color, val bold : Boolean = false, var x: Float, var y: Float) : Sprite {

    var bitmap : Bitmap = textToBitmap()
    private val w2 = bitmap.width / 2f
    private val h2 = bitmap.height / 2f


    override fun update() {
    }

    override val boundingBox get() = RectF(x - w2, y - h2, x + w2, y + h2)
    fun textToBitmap() : Bitmap{
        val context = getCurrentActivityContext()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor.toArgb()
        paint.typeface = when(bold){
            false->with(context){
                this.resources.getFont(R.font.dogica)
            }
            else->with(context){
                this.resources.getFont(R.font.dogicabold)
            }
        }
        val baseline: Float = -paint.ascent()
        val width : Int = paint.measureText(text).roundToInt()
        val height : Int = baseline.roundToInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(text,0f,baseline,paint)
        return image
    }

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.withTransform({
            translate(x,y)
        }){
            this.drawImage(bitmap.asImageBitmap(), topLeft = Offset(-w2,-h2))
        }
    }



}