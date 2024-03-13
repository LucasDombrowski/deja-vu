package fr.iutlens.mmi.demo.game.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ItemImage(id: Any, imageDescription: Any, size: Dp){
    Box(
        modifier = Modifier.width(size).aspectRatio(1f).offset(y = size/8),
        contentAlignment = Alignment.Center,
    ){
        Image(painter = painterResource(id = id as Int), contentDescription = imageDescription as String, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxWidth().fillMaxHeight())
    }
}