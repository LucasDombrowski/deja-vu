package fr.iutlens.mmi.demo.game.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.ui.theme.Dogica

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MenuButton(text: String, action : ()->Unit){
    Text(text = text,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clickable {
                action()
            }
            .background(Color.White)
            .padding(3.dp, 5.dp)
            .widthIn(min = 150.dp, max = 150.dp),
        style = TextStyle(
            fontFamily = Dogica,
            fontWeight = FontWeight.Bold
        ))
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