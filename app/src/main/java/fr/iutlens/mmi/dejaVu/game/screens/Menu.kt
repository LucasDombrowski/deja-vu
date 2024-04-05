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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.primex.core.ExperimentalToolkitApi
import com.primex.core.blur.legacyBackgroundBlur
import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.ui.theme.MainFont
import fr.iutlens.mmi.dejaVu.utils.Music
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

@Composable
fun MenuButton(modifier: Modifier = Modifier,text: String, width : Dp, clickable: Boolean = true, action : ()->Unit){
    var enabled by remember {
        mutableStateOf(clickable)
    }
    LaunchedEffect(clickable){
        enabled = clickable
    }
    val density = LocalDensity.current
    val fontSize = with(density){
        (width*1/12).toSp()
    }
    val soundVolume = 0.25f
    var image by remember {
        mutableIntStateOf(R.drawable.menu_button_background)
    }
    BoxWithConstraints(modifier = modifier
        .width(width)
        .pointerInput(text) {
            detectTapGestures(
                onTap = {
                    if (enabled) {
                        enabled = false
                        Music.playSound(
                            R.raw.press_button,
                            leftVolume = soundVolume,
                            rightVolume = soundVolume
                        )
                        action()
                    }
                },
                onPress = {
                    if(enabled) {
                        image = R.drawable.menu_button_active_background
                        awaitRelease()
                        image = R.drawable.menu_button_background
                    }
                }
            )
        }){
        Image(
            painter = painterResource(id = image),
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

    val logoWidth = screenWidth/3

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

    var credits by remember {
        mutableStateOf(false)
    }

    var clickable by remember {
        mutableStateOf(true)
    }

    @Composable
    fun MainMenuButtons(onStart: () -> Unit, onLeave : ()->Unit){
        val buttonWidth = screenWidth/6
        val spacerHeight = screenHeight/100
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuButton(text = "Nouvelle partie", width = buttonWidth, clickable = clickable) {
                clickable = false
                onStart()
            }
            Spacer(modifier = Modifier.height(spacerHeight))
            MenuButton(text = "Crédits", width = buttonWidth, clickable = clickable) {
                clickable = false
                credits = true
            }
            Spacer(modifier = Modifier.height(spacerHeight))
            MenuButton(text = "Quitter", width = buttonWidth, clickable = clickable) {
                clickable = false
                onLeave()
            }
        }
    }

    LaunchedEffect(key1 = "Fade In"){
        start = false
    }

    val spacerHeight = screenHeight/20
    val paddingValue = screenWidth/20


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(paddingValue)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
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
    if(credits) {
        Credits{
            clickable = true
            credits = false
        }
    }
    Box(
        modifier = Modifier
            .graphicsLayer(alpha = alpha)
            .fillMaxSize()
            .background(Color.Black)
    )
    Music.mute = false
    Music(R.raw.title_screen)
}

@OptIn(ExperimentalToolkitApi::class)
@Composable
fun Credits(onBack : ()->Unit){
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current
    var enable by remember {
        mutableStateOf(false)
    }

    val transitionDuration = 250

    val alpha by animateFloatAsState(targetValue = if (enable) 1f else 0f, label = "Fade In", animationSpec = tween(
        durationMillis = transitionDuration,
        easing = LinearEasing
    )
    )
    LaunchedEffect(null){
        enable = true
    }
    @Composable
    fun CreditRole(role : String, name: String){
        val roleFontSize = with(density){
            (screenWidth/60).toSp()
        }
        val nameFontSize = with(density){
            (screenWidth/50).toSp()
        }
        val spacing = screenHeight/40
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = role,
                fontSize = roleFontSize,
                color = Color.White,
                style = TextStyle(
                    fontFamily = MainFont
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(spacing))
            Text(
                text = name,
                fontSize = nameFontSize,
                color = Color.White,
                style = TextStyle(
                    fontFamily = MainFont
                ),
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun CreditRoles(modifier: Modifier){
        val spacing = screenHeight/20
        Column(
            modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CreditRole(name = "Lucas Dombrowski", role = "Lead Programmer")
            Spacer(modifier = Modifier.height(spacing))
            CreditRole(role = "Sound designer, Composer, Level Artist", name = "Nathan Pietrzak")
            Spacer(modifier = Modifier.height(spacing))
            CreditRole(role = "Game Designer, Narrative Designer, Game Artist", name = "Mio Crepel")
            Spacer(modifier = Modifier.height(spacing))
            CreditRole(role = "Assets sources", name = "Zapsplat.com")
        }
    }

    val scope = rememberCoroutineScope()
    
    val spacing = screenHeight/15

    Box(
        modifier = Modifier
            .graphicsLayer(
                alpha = alpha
            )
            .legacyBackgroundBlur(radius = 12.5f)
            .fillMaxSize()
            .background(Color(0, 0, 0, 128))
    ){
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            CreditRoles(modifier = Modifier)
            Spacer(modifier = Modifier.height(spacing))
            MenuButton(text = "Retour", width = screenWidth/6) {
                scope.launch {
                    enable = false
                    delay(transitionDuration.toLong())
                    onBack()
                }
            }
        }
    }
}