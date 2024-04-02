package fr.iutlens.mmi.dejaVu.game.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.ui.theme.MainFont
import fr.iutlens.mmi.dejaVu.utils.Music
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WritingText(
    text: String,
    fontSize: TextUnit,
    typingDelay: Long,
    animated: Boolean = true,
    onEnd: () -> Unit
) {
    if (animated) {
        var currentText by remember {
            mutableStateOf("")
        }

        LaunchedEffect(text) {
            currentText = ""
            Music.playSound(R.raw.text_sound_effect, loop = -1)
        }

        val punctuationDelay = 250L

        fun phraseEnd(): Boolean {
            return currentText.length > 2 && currentText[currentText.lastIndex - 1] !in listOf(
                '.',
                '!',
                '?'
            ) && currentText.last() in listOf('.', '?', '!', ';', ',') && (currentText.length < text.length && text[currentText.length] !in listOf(
                '.',
                '!',
                '?'
            ))
        }

        LaunchedEffect(currentText) {
            if(phraseEnd()){
                Music.stopSound(R.raw.text_sound_effect)
            }
            Thread.sleep(
                when {
                    phraseEnd() -> punctuationDelay
                    else -> typingDelay
                }
            )
            if(phraseEnd()){
                Music.playSound(R.raw.text_sound_effect, loop = -1)
            }
            if (currentText.length < text.length) {
                currentText += text[currentText.length]
            } else {
                Music.stopSound(R.raw.text_sound_effect)
                onEnd()
            }
        }

        Text(
            text = currentText,
            fontSize = fontSize,
            color = Color.White,
            style = TextStyle(
                fontFamily = MainFont
            ),
            textAlign = TextAlign.Center,
            lineHeight = fontSize * 1.25
        )
    } else {
        Text(
            text = text,
            fontSize = fontSize,
            color = Color.White,
            style = TextStyle(
                fontFamily = MainFont
            ),
            textAlign = TextAlign.Center,
            lineHeight = fontSize * 1.25
        )
    }
}
@SuppressLint("MutableCollectionMutableState")
@Composable
fun IntroductionPart(onClick : ()->Unit, title : String, subtitle : String, text : String, clickable : Boolean = true, onEnd : ()->Unit) {

    val image = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.intro_writing_animation)

    fun writingImages(): MutableList<Bitmap> {
        val images = mutableListOf<Bitmap>()
        repeat(4){
            val slicedWidth = image.width/4
            images.add(
                Bitmap.createBitmap(
                    image,
                    it*slicedWidth,
                    0,
                    slicedWidth,
                    image.height
                )
            )
        }
        return images
    }

    val images by remember {
        mutableStateOf(writingImages().toList())
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current
    val titleFontSize = with(density) {
        (screenWidth / 40).toSp()
    }
    val subtitleFontSize = with(density) {
        (screenWidth / 50).toSp()
    }
    val textFontSize = with(density) {
        (screenWidth / 60).toSp()
    }

    val paddingValue = screenWidth / 20


    val paragraphDelay = 500L

    val scope = rememberCoroutineScope()

    var animateTitle by remember {
        mutableStateOf(title!="")
    }

    var animateSubtitle by remember {
        mutableStateOf(subtitle!="")
    }

    var animateText by remember {
        mutableStateOf(text != "")
    }

    var clickAvailable by remember {
        mutableStateOf(true)
    }

    var writing by remember {
        mutableStateOf(true)
    }

    @Composable
    fun WritingAnimation(writing : Boolean = true){
        var imageIndex by remember {
            mutableIntStateOf(0)
        }
        val animationDelay = 100L
        LaunchedEffect(imageIndex,writing){
            if(writing){
                delay(animationDelay)
                if(imageIndex+1<images.size){
                    imageIndex++
                } else {
                    imageIndex=0
                }
            }
        }

        Image(
            bitmap = images[imageIndex].asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }



    Box(
        modifier = Modifier
            .pointerInput(key1 = clickable) {
                detectTapGestures {
                    Music.stopSound(R.raw.text_sound_effect)
                    if (clickable || clickAvailable) {
                        if (animateTitle || animateSubtitle || animateText) {
                            animateTitle = false
                            animateText = false
                            animateSubtitle = false
                            writing = false
                        } else {
                            if (!clickable) {
                                clickAvailable = false
                            } else {
                                animateTitle = true
                                animateSubtitle = true
                                animateText = true
                                writing = true
                            }
                            onClick()
                        }
                    }
                }
            }
            .fillMaxSize()
            .background(Color.Black)
            .padding(paddingValue)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            WritingText(
                text = title,
                fontSize = titleFontSize,
                typingDelay = 20,
                animated = animateTitle
            ) {
                scope.launch {
                    delay(paragraphDelay)
                    animateTitle = false
                    if(subtitle==""){
                        animateSubtitle = false
                    }
                    if(text==""){
                        animateText = false
                    }
                }
            }
            if (subtitle != "") {
                Spacer(modifier = Modifier.height(screenHeight / 40))
            }
            if (!animateTitle && (subtitle!="" || text!="")) {
                WritingText(
                    text = subtitle,
                    fontSize = subtitleFontSize,
                    typingDelay = 20,
                    animated = animateSubtitle
                ) {
                    scope.launch {
                        delay(paragraphDelay)
                        animateSubtitle = false
                    }
                }
                if (subtitle != "") {
                    Spacer(modifier = Modifier.height(screenHeight / 20))
                }
                if (!animateSubtitle) {
                    WritingText(
                        text = text,
                        fontSize = textFontSize,
                        typingDelay = 10,
                        animated = animateText
                    ) {
                        scope.launch {
                            delay(paragraphDelay)
                            animateText = false
                            writing = false
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .width(screenWidth / 10)
                .aspectRatio(1f)
                .align(Alignment.BottomCenter)
        ){
            WritingAnimation(writing)
        }
    }
}
@Composable
fun Introduction(onEnd : ()->Unit){
    val parts = listOf(
        mapOf("title" to "","subtitle" to "","text" to "Il existe quelque part au tréfond de la Terre une bibliothèque. Au fil des siècles, nombre de légendes extravagantes, sordides et parfois grotesques ont émergé. Démons, monstres, fous, bêtes sauvages, dieux. Tout être surnaturel subsisterait ci-bas. \n" +
                "On raconte même que ce lieu pourrait causer la fin du monde. Tous les pays rêvaient de s'accaparer de cet endroit mystérieux. Toutefois, le monde changea quand quelqu’un foula ces terres interdites."),
        mapOf("title" to "Mardi 2 avril ????, 22h46", "subtitle" to "Bibliothèque souterraine, couloir n° ????", "text" to "Des bruits de pas, il s’agit là de la seule chose qui résonna dans les couloirs infinis. L’architecture semblait être un mélange d’influences des quatre coins du monde. \n" +
                "“On dirait le croisement des mondes et des siècles, la véritable histoire de la Terre” pensa tout bas le garçon.\n" +
                "Blaise, un être humain des plus ordinaires avait, par hasard, aussi malchanceux soit-il, souhaité explorer des ruines anciennes. Son exploration fut de courte durée à la suite d’une brusque chute dans une fosse. Le voilà donc errant depuis une dizaine d’heures vers un espoir de retrouver un jour la surface."),
        mapOf("title" to "Mercredi 3 avril ????, 02h04", "subtitle" to "Bibliothèque souterraine, salle des archives", "text" to "Une immense pièce s’offre au jeune homme affaibli par la marche, la soif et la faim. Dans son champ de vision, des livres à perte de vue, des bibliothèques hautes de centaines de mètres, mais où avait-il pu atterrir?\n" +
                "“Je vais me reposer ici...Tiens?” Soudain, au centre de la pièce surgit un livre, bien plus petit que les millions d’autres qui sommeillaient ici. Sur la couverture était inscrit : Déjà vu.\n" +
                "Quelques pages échappèrent des doigts de Blaise, le livre se mit à briller de plus en plus. Le garçon se sentit comme absorbé, cela signifie-t-il sa fin?"),
        mapOf("title" to "", "subtitle" to "", "text" to "Il avait tort, une incroyable aventure l'attendait.")
    )

    var partIndex by remember {
        mutableIntStateOf(0)
    }

    fun nextPart(){
        if(partIndex+1<parts.size){
            partIndex++
        } else {
            onEnd()
        }
    }

    IntroductionPart(onClick = {
        nextPart()
    }, title = parts[partIndex]["title"]!!, subtitle = parts[partIndex]["subtitle"]!!, text = parts[partIndex]["text"]!!, clickable = partIndex+1<parts.size) {
        nextPart()
    }

    Music.mute = false
    Music(R.raw.intro)
}

@Composable
fun Ending(onEnd: () -> Unit){
    val parts = listOf(
        mapOf(
            "title" to "",
            "subtitle" to "",
            "text" to "L’histoire aurait pu s’arrêter là. Blaise aurait pu rentrer chez lui et raconter sa merveilleuse aventure. Toutefois…."
        ),
        mapOf(
            "title" to "",
            "subtitle" to "",
            "text" to "Notre cher Blaise a oublié qu’un livre comporte plusieurs chapitres."
        ),
        mapOf(
            "title" to "Merci d’avoir joué !",
            "subtitle" to "",
            "text" to ""
        )
    )

    var partIndex by remember {
        mutableIntStateOf(0)
    }

    fun nextPart(){
        if(partIndex+1<parts.size){
            partIndex++
        } else {
            onEnd()
        }
    }

    IntroductionPart(onClick = {
        nextPart()
    }, title = parts[partIndex]["title"]!!, subtitle = parts[partIndex]["subtitle"]!!, text = parts[partIndex]["text"]!!, clickable = partIndex+1<parts.size) {
        nextPart()
    }

    Music.mute = false
    Music(R.raw.intro)
}