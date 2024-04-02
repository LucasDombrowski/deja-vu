package fr.iutlens.mmi.demo.game.screens.cinematic

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import fr.iutlens.mmi.demo.components.DialogScreen
import fr.iutlens.mmi.demo.game.Game

open class Cinematic(val parts : List<CinematicPart> = listOf(), val game: Game, val onEnd : ()->Unit = {}) {

    @Composable
    fun Display(){
        game.pause = true
        if(parts.isNotEmpty()) {
            var partIndex = 0
            var part by remember {
                mutableStateOf(parts[partIndex])
            }
            Log.i("partText",part.text)
            DialogScreen(text = part.text, onEnd = {
                if(partIndex>=parts.size-1){
                    game.pause = false
                    game.cinematic.value = Pair(
                        this,
                        false
                    )
                    onEnd()
                } else {
                    partIndex++
                    part = parts[partIndex]
                }
            }, name = part.name) {
                BoxWithConstraints(modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.5f)){
                    val boxWithConstraintsScope = this
                    Image(painter = painterResource(id = part.image),
                        contentDescription = "Personnage",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxSize()
                            .scale(scaleX = when(part.left){
                                true->1f
                                else->-1f
                            }, scaleY = 1f)
                            .offset(
                                x = -boxWithConstraintsScope.maxWidth/4,
                                y = boxWithConstraintsScope.maxHeight/3
                            )
                    )
                }

            }
        }
    }
}