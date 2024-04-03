package fr.iutlens.mmi.dejaVu.game.screens.cinematic

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import fr.iutlens.mmi.dejaVu.components.DialogScreen
import fr.iutlens.mmi.dejaVu.game.Game
import kotlinx.coroutines.delay

open class Cinematic(val parts : List<CinematicPart> = listOf(), val game: Game, val onEnd : ()->Unit = {}) {

    @Composable
    fun Display(){
        game.pause = true
        @Composable
        fun CharacterImage(images: List<Bitmap>, left: Boolean, delay : Long){
            var imageIndex by remember {
                mutableIntStateOf(0)
            }
            if(images.size>1) {
                LaunchedEffect(imageIndex) {
                    delay(delay)
                    if (imageIndex + 1 < images.size) {
                        imageIndex++
                    } else {
                        imageIndex = 0
                    }
                }
            } else if(imageIndex!=0){
                imageIndex = 0
            }
            BoxWithConstraints(modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f)){
                val boxWithConstraintsScope = this
                Image(bitmap = images[imageIndex].asImageBitmap(),
                    contentDescription = "Personnage",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxSize()
                        .scale(
                            scaleX = when (left) {
                                true -> 1f
                                else -> -1f
                            }, scaleY = 1f
                        )
                        .offset(
                            x = -boxWithConstraintsScope.maxWidth / 4,
                            y = boxWithConstraintsScope.maxHeight / 3
                        )
                )
            }
        }
        
        if(parts.isNotEmpty()) {
            var partIndex = 0
            var part by remember {
                mutableStateOf(parts[partIndex])
            }
            val images = mutableListOf<Bitmap>()
            val image = BitmapFactory.decodeResource(LocalContext.current.resources, part.image)
            val xMax = part.imageSliceX
            val yMax = part.imageSliceY
            val xImageStep = image.width/xMax
            val yImageStep = image.height/yMax
            for(i in 0..<yMax){
                for(j in 0..<xMax){
                    images.add(
                        Bitmap.createBitmap(
                            image,
                            j*xImageStep,
                            i*yImageStep,
                            xImageStep,
                            yImageStep
                        )
                    )
                }
            }

            DialogScreen(text = part.text, onEnd = {
                if(partIndex+1>=parts.size){
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
            }, onSkip = {
                game.pause = false
                game.cinematic.value = Pair(
                    this,
                    false
                )
                onEnd()
            }, name = part.name) {
                CharacterImage(images = images, left = part.left, delay = part.imageAnimationDelay)
            }
        }
    }
}