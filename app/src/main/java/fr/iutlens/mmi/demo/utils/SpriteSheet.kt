package fr.iutlens.mmi.demo.utils

import android.content.Context
import android.graphics.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorFilter.Companion.lighting
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import java.util.HashMap

/**
 * SpriteSheet représente une feuille de sprites, c'est à dire une image découpée
 * en plusieurs cases, que l'on peut utiliser comme autant d'images distinctes en y
 * accédant par un numéro
 *
 * @property bitmap image à découper
 * @property sizeX nombre de colonnes
 * @property sizeY nombre de lignes
 * @constructor Create empty Sprite sheet
 */
class SpriteSheet(val bitmap: Bitmap, val sizeX: Int, val sizeY: Int) {
    /**
     * SpriteWidth : largeur d'un sprite
     */
    val spriteWidth = bitmap.width/sizeX

    /**
     * SpriteHeight : hauteur d'u sprite
     */
    val spriteHeight = bitmap.height/sizeY

    /**
     * sprite : tableau des sprites
     */
    private val sprite: Array<Bitmap?> = Array(sizeX * sizeY) {
        val i = it % sizeX
        val j = it / sizeX
        createCroppedBitmap(bitmap, i * spriteWidth, j * spriteHeight, spriteWidth, spriteHeight)
    }

    /**
     * Obtient un sprite de la feuille
     *
     * @param ndx position du sprite dans la feuille
     */
    operator fun get(ndx: Int) = sprite[ndx]

    /**
     * Affiche l'image
     *
     * @param drawScope contexte de dessin
     * @param ndx numéro de l'image dans la feuille
     * @param x abscisse
     * @param y ordonnée
     */
    fun paint(drawScope: DrawScope, ndx: Int, x: Float, y: Float, colorFilter: ColorFilter ?= null) {
        get(ndx)?.let{drawScope.drawImage(image = it.asImageBitmap(), topLeft = Offset(x,y), colorFilter = colorFilter)}
    }

    companion object {
        private val map =  HashMap<Int, SpriteSheet>()

        fun load(id: Int, sizeX: Int, sizeY: Int, context: Context) {
            loadImage(context, id)?.let {
                map[id] = SpriteSheet(it, sizeX, sizeY)
            } ?: throw NoSuchElementException("Image resource not fount (id=$id)")
        }

        operator fun get(id: Int): SpriteSheet? {
            return map[id]
        }
    }
}

/**
 * Charge et découpe une image en feuille de sprite
 *
 * @param id identifiant de la ressource image
 * @param sizeX nombre de colonnes
 * @param sizeY nombre de lignes
 */
fun Context.loadSpritesheet(id: Int, sizeX: Int, sizeY: Int) =
    SpriteSheet.load(id, sizeX, sizeY, this)