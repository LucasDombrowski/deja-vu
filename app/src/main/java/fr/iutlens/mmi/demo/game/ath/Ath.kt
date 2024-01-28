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

@Composable
fun Heart(permanent: Boolean, filled: Float){
    Log.i("New Heart","$permanent, $filled")
    Box(modifier = Modifier
        .width(32.dp)
        .height(32.dp)) {
        val image = when{
            permanent->when(filled){
                1f->R.drawable.permanent_heart_3_3
                0.66f->R.drawable.permanent_heart_2_3
                0.33f->R.drawable.permanent_heart_1_3
                else->R.drawable.empty_heart
            }
            else->R.drawable.transparent
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