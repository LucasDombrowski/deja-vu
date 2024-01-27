package fr.iutlens.mmi.demo.game.sprite

import android.graphics.RectF
import androidx.compose.ui.graphics.drawscope.DrawScope

operator fun Iterable<Sprite>.get(x: Float, y: Float) = firstOrNull { it.boundingBox.contains(x,y) }
fun Iterable<Sprite>.paint(drawScope: DrawScope, frameCount: Long){
    for(sprite in this) sprite.paint(drawScope,frameCount)
}

fun Iterable<Sprite>.boundingBox() = this.map{it.boundingBox}
    .reduce { result, box -> result.apply { union(box)} }

fun Iterable<Sprite>.update() {for(sprite in this) sprite.update()}

/**
 * Liste de sprites, utilisable à la fois comme un sprite et une liste
 *
 * @property list
 * @constructor Crée une SpriteList à partir d'un liste de sprite
 */
open class SpriteList(open val list: Iterable<Sprite>) : Iterable<Sprite> by list{
    open fun paint(drawScope: DrawScope, elapsed: Long)  = list.paint(drawScope,elapsed)
    val boundingBox: RectF get() = list.boundingBox()
    fun update()  = list.update()
}

/**
 * Liste de sprite modifiable
 *
 * @property list
 * @constructor Create empty Mutable sprite list
 */
class MutableSpriteList(override val list: MutableList<Sprite>) : SpriteList(list), MutableList<Sprite> by list {
    override fun iterator() = list.listIterator()
    override fun paint(drawScope: DrawScope, elapsed: Long){
        val listCopy = MutableSpriteList(list = list.toMutableList())
        with(listCopy.iterator()){
            forEach {
                it.paint(drawScope,elapsed)
            }
        }
    }

}

fun Iterable<Sprite>.asSpriteList() = SpriteList(this)
fun MutableList<Sprite>.asMutableSpriteList() = MutableSpriteList(this)

/**
 * Crée une liste de sprite (SpriteList) à partir du ou des sprites passés en paramètre
 *
 * @param sprite
 */
fun spriteListOf(vararg sprite : Sprite) = SpriteList(sprite.toList())

/**
 * Crée une liste de sprite modifiable (MutableSpriteList) à partir du ou des sprites passés en paramètre
 *
 * @param sprite
 */
fun mutableSpriteListOf(vararg sprite : Sprite) = MutableSpriteList(sprite.toMutableList())