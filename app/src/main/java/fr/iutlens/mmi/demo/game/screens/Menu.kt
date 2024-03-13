package fr.iutlens.mmi.demo.game.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.ui.theme.MainFont

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MenuButton(text: String, width : Dp, action : ()->Unit){
    val density = LocalDensity.current
    val fontSize = with(density){
        (width*1/12).toSp()
    }
    BoxWithConstraints(modifier = Modifier
        .width(width)
        .pointerInput(text){
            detectTapGestures(
                onTap = {
                    action()
                }
            )
        }){
        Image(
            painter = painterResource(id = R.drawable.menu_button_background),
            contentDescription = "Bouton",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth)
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center).offset(y = width*1/100),
            color = Color.Black,
            fontSize = fontSize,
            style = TextStyle(
                fontFamily = MainFont,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun MenuItem(id : Int, imageDescription: String, onClick : ()->Unit = {}){
    Box(
        modifier = Modifier.fillMaxWidth(0.2f).aspectRatio(1f).clickable {
            onClick() },
        contentAlignment = Alignment.Center,
    ){
        Image(painter = painterResource(id = id as Int), contentDescription = imageDescription as String, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxWidth().fillMaxHeight())
    }
}