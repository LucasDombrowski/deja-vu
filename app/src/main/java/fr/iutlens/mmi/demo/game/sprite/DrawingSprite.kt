package fr.iutlens.mmi.demo.game.sprite

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform

class DrawingSprite(id: Int, x: Float, y: Float, ndx: Int = 0, val drawColor: androidx.compose.ui.graphics.Color) : BasicSprite(id,x,y,ndx) {
    val lastPositions = mutableListOf<Pair<Float,Float>>()

    var drawing = false
    init {
        repeat(10){
            lastPositions.add(Pair(x,y))
        }
    }
    fun newPosition(){
        if(drawing) {
            lastPositions.removeLast()
            lastPositions.add(0, Pair(x, y))
        }
    }
    fun resetPositions(){
        lastPositions.replaceAll {
            Pair(x,y)
        }
    }
    override fun paint(drawScope: DrawScope, elapsed: Long) {
        if(drawing) {
            for (i in 0..<lastPositions.size - 1) {
                drawScope.drawLine(
                    color = drawColor,
                    start = Offset(lastPositions[i].first, lastPositions[i].second),
                    end = Offset(lastPositions[i + 1].first, lastPositions[i + 1].second),
                    strokeWidth = 10f
                )
            }
        }
        drawScope.withTransform({
            scale(scaleX, scaleY)
            rotate(rotate, Offset(x, y))
            translate(x, y)
        }) {
            spriteSheet.paint(this, ndx, -w2, -h2, colorFilter = colorFilter)
        }
    }
}