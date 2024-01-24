package fr.iutlens.mmi.demo.game.sprite

import android.graphics.RectF
import androidx.compose.ui.graphics.drawscope.DrawScope
import fr.iutlens.mmi.demo.utils.SpriteSheet

/**
 * Une tiled area représente une grille de sprite définie à partir d'une feuille de sprite
 * et d'un tableau à deux dimensions (data) de numéros de sprite. Dans chaque case de la grille, le
 * sprite avec le numéro indiqué est dessiné.
 * Un objet de la classe TiledArea peut être utilisé comme un Sprite (on peut donc le dessiner)
 * et comme une TileMap (on peut accéder au contenu de chaque case, aux dimensions etc...)
 *
 * @property sprite feuille de sprite
 * @property data indices dans la feuille
 * @constructor Crée une grille de sprite à partir de la feuille de sprite (spriteSheet) et d'un
 * tableau des numéros de sprites (data)
 */
class TiledArea(private val sprite: SpriteSheet, private val data: TileMap) : Sprite, TileMap by data {
    /**
     * W largeur d'une case, en pixels
     */
    val w  = sprite.spriteWidth-1

    /**
     * H hauteur d'une case, en pixels
     */
    val h  = sprite.spriteHeight-1

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                sprite.paint(
                    drawScope,
                    data[x, y],
                    (x * w).toFloat(),
                    (y * h).toFloat()
                )
            }
        }
    }

    override val boundingBox = RectF(0f,0f,w*sizeX.toFloat(),h*sizeY.toFloat())
    override fun update() {
    }
}

/**
 * Retourne une TiledArea construite sur la feuille de sprite et un tableau
 *
 * @param data
 */
fun SpriteSheet.tiledArea(data: TileMap) = TiledArea(this,data)

/**
 * Retourne une TiledArea construite sur la feuille de sprite et le tableau
 *
 * @param data
 */
fun Int.tiledArea(data: TileMap) = SpriteSheet[this]!!.tiledArea(data)