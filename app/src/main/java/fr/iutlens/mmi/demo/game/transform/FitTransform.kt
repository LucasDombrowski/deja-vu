package fr.iutlens.mmi.demo.game.transform

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import fr.iutlens.mmi.demo.game.sprite.Sprite
import kotlin.math.min

/**
 * FitTransform zoome sur un sprite autant que possible, pour qu'il occupe tout l'écran sans déborder
 * Le sprite ciblé sera toujours au centre de l'écran
 *
 * @property sprite cible
 * @constructor Créer une camera zoomant sur le sprite
 */
class FitTransform(val sprite: Sprite) : CameraTransform {
    private val transform = Matrix()
    private val reverse = Matrix()

    override fun getMatrix(size: Size): Matrix {
        val boundingBox = sprite.boundingBox
        val scale = min(size.width/ boundingBox.width(), size.height/ boundingBox.height())

        transform.reset()
        transform.translate(size.width/2, size.height/2)
        transform.scale(scale,scale)
        transform.translate(-boundingBox.centerX(), -boundingBox.centerY())

        reverse.setFrom(transform)
        reverse.invert()
        return transform
    }

    override fun getPoint(offset: Offset) = reverse.map(offset)
}
