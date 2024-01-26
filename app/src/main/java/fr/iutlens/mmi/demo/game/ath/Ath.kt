package fr.iutlens.mmi.demo.game.ath

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.Sprite

@Composable
fun Ath(game: Game){
    var hearts by remember {
        mutableStateOf(game.controllableCharacter!!.hearts)
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()){
        Hearts(hearts = hearts)
    }
}

@Composable
fun Heart(permanent: Boolean, filled: Float){
    Box(modifier = Modifier
        .width(32.dp)
        .height(32.dp)) {
        if (permanent) {
            Image(
                painter = painterResource(id = R.drawable.heart_pixel_art_254x254),
                contentDescription = "Heart",
                contentScale = ContentScale.Fit
            )
        } else {
            return
        }
    }
}

@Composable
fun Hearts(hearts: MutableList<Heart>){
    Row {
        for(heart in hearts){
            fr.iutlens.mmi.demo.game.ath.Heart(permanent = heart.permanent, filled = heart.filled)

        }
    }
}

@Composable
fun MyItem(permanent: Boolean, item: Sprite){
    Box(modifier = Modifier
        .width(40.dp)
        .height(40.dp)
    ) {
        if (permanent) {
            Image(
                painter = painterResource(id = ),
                contentDescription = "Item",
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun MyItems(myItems: MutableList<MyItem>) {
    Row {
        for(myItem in myItems) {
            fr.iutlens.mmi.demo.game.ath.Heart()
        }
    }
}