package fr.iutlens.mmi.demo.game.transform


import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix

import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea

/**
 * FocusTransform suit un sprite se déplaçant sur une TiledArea
 *
 * @property tiledArea Décor du sprite
 * @property sprite Sprite à cibler
 * @property minTiles nombre minimum de tuiles à afficher (horizontalement comme verticalement)
 * @constructor Crée une camera suivant le sprite, en affichant toujours au moins minTiles tuiles
 * de tiledArea dans chaque direction
 */
class FocusTransform(val tiledArea: TiledArea, var sprite: Sprite, var minTiles: Int) :
    CameraTransform {

    private val transform = Matrix()
    private val reverse = Matrix()

    override fun getPoint(offset: Offset) = reverse.map(offset)

    override fun getMatrix(size: Size): Matrix {
        val boundingBox = sprite.boundingBox
        val tilesX = size.width / tiledArea.w
        val tilesY = size.height / tiledArea.h
        val sizeTiles = tilesX

        val scale = (sizeTiles / minTiles)

        transform.reset()
        transform.translate(size.width/2, size.height/2)
        transform.scale(scale, scale)
        transform.translate(-boundingBox.centerX(), -boundingBox.centerY())

        reverse.setFrom(transform)
        reverse.invert()

        return  transform
    }

}
