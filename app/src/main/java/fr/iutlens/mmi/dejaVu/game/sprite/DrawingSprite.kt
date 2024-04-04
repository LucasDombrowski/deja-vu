package fr.iutlens.mmi.dejaVu.game.sprite

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DrawingSprite(id: Int, x: Float, y: Float, ndx: Int = 0, val drawColor: androidx.compose.ui.graphics.Color) : BasicSprite(id,x,y,ndx) {
    var lastPositions = mutableListOf<Pair<Float,Float>>()

    var drawing = false

    val maxPointNumber = 15
    fun newPosition(){
        if(drawing) {
            lastPositions.removeLast()
            lastPositions.add(0, Pair(x, y))
        }
    }
    fun resetPositions(){
        val positionsList = mutableListOf<Pair<Float,Float>>()
        repeat(maxPointNumber){
            positionsList.add(Pair(x,y))
        }
        lastPositions = positionsList
    }

    fun erase(){
        drawing = false
        GlobalScope.launch {
            repeat(lastPositions.size){
                if(!drawing && lastPositions.isNotEmpty()){
                    lastPositions.removeLast()
                    delay(33)
                } else {
                    cancel()
                }
            }
            drawing = true
        }
    }
    override fun paint(drawScope: DrawScope, elapsed: Long) {
        var maxStrokeWidth = (h2/maxPointNumber)*lastPositions.size
        val reduceStep = maxStrokeWidth/lastPositions.size
        for (i in 0..<lastPositions.size - 1) {
            if(i+1<lastPositions.size) {
                drawScope.drawLine(
                    color = drawColor,
                    start = Offset(lastPositions[i].first, lastPositions[i].second),
                    end = Offset(lastPositions[i + 1].first, lastPositions[i + 1].second),
                    strokeWidth = maxStrokeWidth
                )
            }
            maxStrokeWidth-=reduceStep
        }
        drawScope.withTransform({
            scale(scaleX, scaleY)
            rotate(rotate, Offset(x, y))
            translate(x, y)
        }) {
            spriteSheet.paint(this, ndx, -w2, -h2, colorFilter = colorFilter)
        }
    }
    init {
        resetPositions()
    }
}