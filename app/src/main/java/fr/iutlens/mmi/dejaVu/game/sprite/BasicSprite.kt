package fr.iutlens.mmi.dejaVu.game.sprite


import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.dejaVu.utils.SpriteSheet


/**
 * Représente un sprite défini par une feuille de sprite et une numéro de sprite (ndx) et une position (x,y)
 * Le comportement du sprite peut être défini via la propriété action
 *
 * @property spriteSheet feuille de sprite utilisée pour dessiner ce sprite
 * @property x position en x (en pixel dans les coordonnées de référence)
 * @property y position en y (en pixel dans les coordonnées de référence)
 * @property ndx numéro du sprite dans la feuille
 * @property action action à réaliser entre deux images
 * @constructor Crée un sprite à partir de la feuille (spriteSheet), la position (x,y) et le numéro
 * de l'image dans la feuille. On peut préciser en plus une action à réaliser entre deux images pour
 * animer le sprite
 */
open class BasicSprite(val spriteSheet: SpriteSheet,
                       var x: Float, var y: Float,
                       var scaleX : Float = 1f, var scaleY : Float = 1f,
                       var rotate : Float = 0f,
                       var colorFilter : ColorFilter = ColorFilter.colorMatrix(ColorMatrix()),
                       var ndx : Int = 0,
                       var action: (BasicSprite.()->Unit)? = null,
) : Sprite {

    constructor(id: Int,  x: Float, y: Float, ndx : Int=0, action: (BasicSprite.()->Unit)? = null) :
            this(spriteSheet = SpriteSheet[id]!!, x = x, y = y, ndx = ndx, action = action)

    // taille du sprite en pixels, divisée par deux (pour le centrage)
    val w2 = spriteSheet.spriteWidth / 2f
    val h2 = spriteSheet.spriteHeight / 2f

    var permanentColor = colorFilter

    var colorMatrix = ColorMatrix()
    override fun paint(drawScope: DrawScope, elapsed: Long) =
        drawScope.withTransform({
            scale(scaleX,scaleY, Offset(x,y))
            rotate(rotate, Offset(x,y))
            translate(x,y)
        }){
            spriteSheet.paint(this, ndx, -w2, -h2, colorFilter = colorFilter)
        }

    fun invisible(){
        scaleX = 0f
        scaleY = 0f
    }

    fun visible(){
        scaleX = 1f
        scaleY = 1f
    }

    fun isInvisible() : Boolean{
        return scaleX==0f && scaleY==0f
    }

    fun copy() : BasicSprite{
        return BasicSprite(spriteSheet,x,y,scaleX,scaleY,ndx = ndx, colorFilter = colorFilter, action=action, rotate = rotate)
    }

    fun copyReset() : BasicSprite{
        return BasicSprite(spriteSheet,x,y, ndx = ndx, action = action)
    }

    fun normalColor(){
        colorFilter = ColorFilter.colorMatrix(ColorMatrix())
        makeCurrentColorPermanent()
    }
    fun semiWhiteColor(){
        colorFilter = ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
            1f,0f,0f,0.5f,0f,
            0f,1f,0f,0.5f,0f,
            0f,0f,1f,0.5f,0f,
            0f,0f,0f,1f,0f
        )))
    }

    fun midLifeColor(){
        colorMatrix = ColorMatrix(floatArrayOf(
            1f,0f,0f,0f,0f,
            0f,1f,0f,0f,0f,
            0f,0f,0.5f,0f,0f,
            0f,0f,0f,1f,0f
        ))
        colorFilter = ColorFilter.colorMatrix(colorMatrix)
        makeCurrentColorPermanent()
    }
    fun lowLifeColor(){
        colorMatrix = ColorMatrix(floatArrayOf(
            1f,0f,0f,0f,0f,
            0f,0.5f,0f,0f,0f,
            0f,0f,0.5f,0f,0f,
            0f,0f,0f,1f,0f
        ))
        colorFilter = ColorFilter.colorMatrix(colorMatrix)
        makeCurrentColorPermanent()
    }

    fun reverseColor(){
        colorMatrix =
            ColorMatrix(floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
            ))
        colorFilter = ColorFilter.colorMatrix(colorMatrix)
        makeCurrentColorPermanent()
    }

    fun makeCurrentColorPermanent(){
        permanentColor = colorFilter
    }

    fun permanentColor(){
        colorFilter = permanentColor
    }

    fun setTransparencyLevel(n: Float){
        colorMatrix.values[18] = 1f*n
        colorFilter = ColorFilter.colorMatrix(colorMatrix)
    }

//rectangle occuppé par le sprite
    override val boundingBox get() = RectF(x - w2, y - h2, x + w2, y + h2)
    override fun update() {action?.invoke(this)}

}

/**
 * Construction d'un sprite à partir d'une feuille de sprite (désignée par son numéro de ressource)
 *
 * @param x
 * @param y
 * @param ndx
 * @param action
 */
fun Int.toSprite(x: Float, y: Float, ndx : Int=0, action: (BasicSprite.()->Unit)? = null) =
    BasicSprite(this, x, y,ndx, action)