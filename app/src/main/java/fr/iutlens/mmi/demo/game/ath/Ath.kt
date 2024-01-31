package fr.iutlens.mmi.demo.game.ath

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.utils.setInterval
import kotlin.math.round
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.Sprite

@Composable
fun Heart(permanent: Boolean, filled: Int){
    Box(modifier = Modifier
        .width(24.dp)
        .height(24.dp)) {
        val image = when{
            permanent->when(filled){
                3->R.drawable.permanent_heart_3_3
                2->R.drawable.permanent_heart_2_3
                1->R.drawable.permanent_heart_1_3
                else->R.drawable.empty_heart
            }
            else->when(filled){
                3->R.drawable.temporary_heart_3_3
                2->R.drawable.temporary_heart_2_3
                else->R.drawable.temporary_heart_1_3
            }
        }
        Image(
            painter = painterResource(id = image),
            contentDescription = "Heart",
            contentScale = ContentScale.Fit
        )
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

