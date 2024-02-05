package fr.iutlens.mmi.demo.game.ath

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Heart

@Composable
fun Heart(permanent: Boolean, filled: Float){
    Box(modifier = Modifier
        .width(24.dp)
        .height(24.dp)) {
        val image = when{
            permanent->when(filled){
                1f->R.drawable.permanent_heart_4_4
                0.75f->R.drawable.permanent_heart_3_4
                0.5f->R.drawable.permanent_heart_2_4
                0.25f->R.drawable.permanent_heart_1_4
                else->R.drawable.empty_heart
            }
            else->when(filled){
                1f->R.drawable.temporary_heart_4_4
                0.75f->R.drawable.temporary_heart_3_4
                0.5f->R.drawable.temporary_heart_2_4
                else->R.drawable.temporary_heart_1_4
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

@Composable
fun BossBar(hearts : MutableList<Heart>){
    val maxHealth = hearts.size*1f
    val currentHealth = hearts.fold(0f){
        acc, heart ->  acc + heart.filled
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        horizontalArrangement = Arrangement.Center) {
        Box(modifier = Modifier
            .fillMaxWidth(0.25f)
            .fillMaxHeight()){
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(Color(0,0,0,128)))
            Box(modifier = Modifier.fillMaxWidth(currentHealth/maxHealth).fillMaxHeight().background(Color.Red))
        }

    }
}

