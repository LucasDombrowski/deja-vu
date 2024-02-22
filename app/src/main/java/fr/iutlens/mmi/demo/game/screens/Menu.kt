package fr.iutlens.mmi.demo.game.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.ui.theme.Dogica

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MenuButton(text: String, width : Dp, action : ()->Unit){
    val density = LocalDensity.current
    val fontSize = with(density){
        (width*1/15).toSp()
    }
    BoxWithConstraints(modifier = Modifier
        .width(width)
        .clickable {
            action()
        }){
        Image(
            painter = painterResource(id = R.drawable.menu_button_background),
            contentDescription = "Bouton",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth)
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center).offset(y = width*1/60),
            color = Color.Black,
            fontSize = fontSize,
            style = TextStyle(
                fontFamily = Dogica,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun MenuItem(id : Int, name: String){
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
    ){
        Image(painter = painterResource(id = id),
            contentDescription = name,
            contentScale = ContentScale.Fit)
    }
}