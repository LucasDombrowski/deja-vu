package fr.iutlens.mmi.dejaVu.game.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.ui.theme.MainFont
import fr.iutlens.mmi.dejaVu.utils.Music
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MenuButton(text: String, width : Dp, action : ()->Unit){
    var enabled by remember {
        mutableStateOf(true)
    }
    val density = LocalDensity.current
    val fontSize = with(density){
        (width*1/12).toSp()
    }
    val soundVolume = 0.25f
    BoxWithConstraints(modifier = Modifier
        .width(width)
        .pointerInput(text) {
            detectTapGestures(
                onTap = {
                    if(enabled) {
                        enabled = false
                        Music.playSound(
                            R.raw.press_button,
                            leftVolume = soundVolume,
                            rightVolume = soundVolume
                        )
                        action()
                    }
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
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = width * 1 / 100),
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
        modifier = Modifier
            .fillMaxWidth(0.2f)
            .aspectRatio(1f)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ){
        Image(painter = painterResource(id = id as Int), contentDescription = imageDescription as String, contentScale = ContentScale.Fit, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight())
    }
}

@Composable
fun MainMenu(onStart : ()->Unit, onLeave : ()->Unit){
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = with(configuration){
        screenWidthDp.dp
    }
    val screenHeight = with(configuration){
        screenHeightDp.dp
    }

    val logoWidth = screenWidth/4


    @Composable
    fun MainMenuButtons(onStart: () -> Unit, onLeave : ()->Unit){
        val buttonWidth = screenWidth/6
        val spacerHeight = screenHeight/100
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuButton(text = "Nouvelle partie", width = buttonWidth) {
                onStart()
            }
            Spacer(modifier = Modifier.height(spacerHeight))
            MenuButton(text = "Quitter", width = buttonWidth) {
                onLeave()
            }
        }
    }

    val spacerHeight = screenHeight/50
    val paddingValue = screenWidth/20

    var start by remember {
        mutableStateOf(true)
    }

    val transitionDuration = 1000

    val alpha by animateFloatAsState(targetValue = if (start) 1f else 0f, label = "Fade Out", animationSpec = tween(
        durationMillis = transitionDuration,
        easing = LinearEasing
    )
    )

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = "Fade In"){
        start = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(paddingValue)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ){
            Image(painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(logoWidth))
            Spacer(modifier = Modifier.height(spacerHeight))
            MainMenuButtons(
                onStart = {
                    start = true
                    scope.launch {
                        delay(transitionDuration.toLong())
                        onStart()
                    }
                },
                onLeave = onLeave
            )
        }
    }
    Box(
        modifier = Modifier
            .graphicsLayer(alpha = alpha)
            .fillMaxSize()
            .background(Color.Black)
    )
}