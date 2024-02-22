package fr.iutlens.mmi.demo.game.ath

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.ui.theme.Dogica

@Composable
fun Heart(permanent: Boolean, filled: Float){
    val configuration = LocalConfiguration.current
    val screenWidth = with(configuration){
        this.screenWidthDp
    }
    Box(modifier = Modifier
        .width((screenWidth / 40).dp)
        .aspectRatio(1f)) {
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
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color(0, 0, 0, 128)))
            Box(modifier = Modifier
                .fillMaxWidth(currentHealth / maxHealth)
                .fillMaxHeight()
                .background(Color.Red))
        }

    }
}

@Composable
fun Coins(n: MutableState<Int>){
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(configuration){
        this.screenWidthDp
    }
    val fontSize = with(density){
        (screenWidth/60).dp.toSp()
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .width((screenWidth / 35).dp)
            .aspectRatio(1f)){
            Image(painter = painterResource(id = R.drawable.coin), contentDescription = "Pièce", contentScale = ContentScale.Fit)
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = n.value.toString(), color = Color.White, fontSize = fontSize, style = TextStyle(
            fontFamily = Dogica,
            fontWeight = FontWeight.Normal
        ))
    }
}

