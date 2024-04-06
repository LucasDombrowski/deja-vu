package fr.iutlens.mmi.dejaVu.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.core.view.ViewCompat
import fr.iutlens.mmi.dejaVu.boot.checkedTutorials
import fr.iutlens.mmi.dejaVu.boot.commitBooleanSharedPreferences
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.levels.Ninja
import fr.iutlens.mmi.dejaVu.game.screens.Ending
import fr.iutlens.mmi.dejaVu.game.screens.Introduction
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.NinjaIntro
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.TutorialMovements
import fr.iutlens.mmi.dejaVu.utils.Music
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Level(game: Game, onEnd : ()->Unit, onRestart : ()->Unit, onLeave : ()->Unit){
    var enabled by remember {
        mutableStateOf(false)
    }

    val transitionDuration = 1000

    val alpha by animateFloatAsState(targetValue = if (enabled) 0f else 1f, label = "Fade In", animationSpec = tween(
        durationMillis = transitionDuration,
        easing = LinearEasing
    ))

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = game ){
        enabled = true
        val cinematic = Cinematic(
            parts = NinjaIntro(game).parts +
            if(game.firstTime && !checkedTutorials["moving"]!!){ TutorialMovements(game).parts } else { listOf()
            },
            game = game,
            onEnd = {
                Music.mute = false
                if(!checkedTutorials["moving"]!!){
                    commitBooleanSharedPreferences("movingTutorial",true)
                }
            }
        )
        game.cinematic.value = Pair(
            cinematic,
            true
        )
    }

    game.onRestart = {
        enabled = false
        scope.launch {
            delay(transitionDuration.toLong())
            game.pause = false
            onRestart()
        }
    }



    game.onLeave = {
        enabled = false
        scope.launch {
            delay(transitionDuration.toLong())
            onLeave()
        }

    }

    var introduction by remember {
        mutableStateOf(game.firstTime)
    }

    var ending by remember {
        mutableStateOf(false)
    }

    game.onEnd = {
        enabled = false
        scope.launch {
            delay(transitionDuration.toLong())
            enabled = true
            ending = true
        }
    }


    if(introduction){
        Introduction {
            enabled = false
            scope.launch {
                delay(transitionDuration.toLong())
                introduction = false
                enabled = true
            }

        }
    } else if(ending){
        Ending {
            enabled = false
            scope.launch {
                delay(transitionDuration.toLong())
                onLeave()
            }
        }
    } else {
        game.GameScreen()
    }

    Box(modifier = Modifier
        .graphicsLayer(alpha = alpha)
        .fillMaxSize()
        .background(Color.Black)){
    }
}