package fr.iutlens.mmi.demo.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun Level(game: Game, onEnd : ()->Unit, onRestart : ()->Unit){
    game.onEnd = {
        onEnd()
    }
    game.onRestart = {
        onRestart()
    }
    Box(modifier = Modifier
        .fillMaxSize()){
        game.GameScreen()
    }
}