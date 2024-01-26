package fr.iutlens.mmi.demo.game.sprite


import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.utils.SpriteSheet


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
                       var ndx : Int = 0,
                       var action: (BasicSprite.()->Unit)? = null) : Sprite {

    constructor(id: Int,  x: Float, y: Float, ndx : Int=0, action: (BasicSprite.()->Unit)? = null) :
            this(SpriteSheet[id]!!, x, y,ndx, action)

    // taille du sprite en pixels, divisée par deux (pour le centrage)
    private val w2 = spriteSheet.spriteWidth / 2f
    private val h2 = spriteSheet.spriteHeight / 2f

    override fun paint(drawScope: DrawScope, elapsed: Long) =
        drawScope.withTransform({translate(x,y)}){
            spriteSheet.paint(this, ndx, -w2, -h2)
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