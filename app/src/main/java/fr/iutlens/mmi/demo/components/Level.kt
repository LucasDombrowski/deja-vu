package fr.iutlens.mmi.demo.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
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
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.items.HandHeart
import fr.iutlens.mmi.demo.game.gameplayResources.items.LoyaltyCard
import fr.iutlens.mmi.demo.game.gameplayResources.items.SchoolBag
import fr.iutlens.mmi.demo.game.gameplayResources.items.Torch
import fr.iutlens.mmi.demo.game.gameplayResources.items.Wallet
import fr.iutlens.mmi.demo.utils.Music
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

    LaunchedEffect(key1 = "Fade In" ){
        enabled = true
    }

    game.onEnd = {
        onEnd()
    }
    game.onRestart = {
        onRestart()
    }

    val scope = rememberCoroutineScope()

    game.onLeave = {
        enabled = false
        scope.launch {
            delay(transitionDuration.toLong())
            onLeave()
        }

    }

    game.GameScreen()
    Box(modifier = Modifier
        .graphicsLayer(alpha = alpha)
        .fillMaxSize()
        .background(Color.Black)){
    }
}