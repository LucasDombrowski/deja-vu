package fr.iutlens.mmi.dejaVu.game.sprite

import android.graphics.RectF
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Sprite représente un élément graphique animable (un sprite)
 */
interface Sprite {
    /**
     * Paint dessine le sprite dans le contexte donné
     * @param drawScope contexte graphique
     * @param elapsed temps écoulé depuis le début du jeu
     */
    fun paint(drawScope: DrawScope, elapsed: Long = 0L)

    /**
     * Bounding box calcule la boîte la plus petite contenant le sprite
     */
    val boundingBox: RectF

    /**
     * Update demande au sprite de réaliser son action (par exemple bouger)
     */
    fun update()
}