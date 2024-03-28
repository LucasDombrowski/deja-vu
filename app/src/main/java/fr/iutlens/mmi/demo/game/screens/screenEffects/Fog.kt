package fr.iutlens.mmi.demo.game.screens.screenEffects

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.R
import kotlinx.coroutines.delay

@Composable
fun Fog(animate : Boolean = true){
    val configuration = LocalConfiguration.current
    val screenWidth = with(configuration){
        screenWidthDp.dp
    }
    var fogOffsetX by remember {
        mutableStateOf(0.dp)
    }
    val fogAnimationDelay = 33L

    val fogOffsetXReduction = screenWidth/500
    LaunchedEffect(key1 = fogOffsetX, key2 = animate){
        delay(fogAnimationDelay)
        if(animate) {
            if (fogOffsetX - fogOffsetXReduction < -screenWidth) {
                fogOffsetX = 0.dp
            } else {
                fogOffsetX -= fogOffsetXReduction
            }
        }
    }

    val fogOpacity = 0.15f

    val fogBgColor = Color(10,90,70)

    @Composable
    fun FogImage(modifier: Modifier, offsetX : Dp){
        Image(
            painter = painterResource(id = R.drawable.fog),
            contentDescription = "Fog",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .offset(x = offsetX))
    }
    
    Box(modifier = Modifier
        .fillMaxSize()
        .graphicsLayer(alpha = fogOpacity)){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(fogBgColor),
        )
        FogImage(Modifier.align(Alignment.Center),offsetX = fogOffsetX)
        FogImage(Modifier.align(Alignment.Center),offsetX = screenWidth+fogOffsetX)
    }
}