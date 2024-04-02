package fr.iutlens.mmi.dejaVu.game.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.dejaVu.ui.theme.MainFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Introduction(onEnd : ()->Unit){
    @Composable
    fun IntroductionPart(modifier : Modifier, title : String, subtitle : String, text : String, onEnd : ()->Unit){
        @Composable
        fun WritingText(text : String, fontSize : TextUnit, animate : Boolean = true, typingDelay : Long, onEnd : ()->Unit ){
            var currentText by remember {
                mutableStateOf("")
            }

            LaunchedEffect(text){
                currentText=when(animate){
                    true->""
                    else->text
                }
            }


            LaunchedEffect(currentText){
                Thread.sleep(typingDelay)
                if(currentText.length<text.length){
                    currentText+=text[currentText.length]
                } else if(animate){
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
                lineHeight = fontSize*1.5
            )
        }

        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp
        val density = LocalDensity.current
        val titleFontSize = with(density){
            (screenWidth/40).toSp()
        }
        val subtitleFontSize = with(density){
            (screenWidth/50).toSp()
        }
        val textFontSize = with(density){
            (screenWidth/75).toSp()
        }

        val paddingValue = screenWidth/20

        var titleSet by remember {
            mutableStateOf(false)
        }

        var subtitleSet by remember {
            mutableStateOf(false)
        }

        val paragraphDelay = 500L

        val scope = rememberCoroutineScope()

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValue)
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                WritingText(text = title, fontSize = titleFontSize, animate = !titleSet, typingDelay = 10) {
                    scope.launch {
                        delay(paragraphDelay)
                        titleSet = true
                    }
                }
                if(titleSet){
                    WritingText(text = subtitle, fontSize = subtitleFontSize, animate = !subtitleSet, typingDelay = 10) {
                        scope.launch {
                            delay(paragraphDelay)
                            subtitleSet = true
                        }
                        }
                }
                if(subtitleSet){
                    WritingText(text = text, fontSize = textFontSize, typingDelay = 5) {
                        scope.launch {
                            delay(paragraphDelay)
                            onEnd()
                        }
                    }
                }
            }
        }
    }

    val parts = listOf(
        mapOf("title" to "","subtitle" to "","text" to "Il existe quelque part au tréfond de la Terre une bibliothèque. Au fil des siècles, nombre de légendes extravagantes, sordides et parfois grotesques ont émergé. Démons, monstres, fous, bêtes sauvages, dieux. Tout être surnaturel subsisterait ci-bas. \n" +
                "On raconte même que ce lieu pourrait causer la fin du monde. Tous les pays rêvaient de s'accaparer de cet endroit mystérieux. Toutefois, le monde changea quand quelqu’un foula ces terres interdites."),
        mapOf("title" to "Mardi 2 avril ????, 22h46", "subtitle" to "Bibliothèque souterraine, couloir n° ????", "text" to "Des bruits de pas, il s’agit là de la seule chose qui résonna dans les couloirs infinis. L’architecture semblait être un mélange d’influences des quatre coins du monde. \n" +
                "“On dirait le croisement des mondes et des siècles, la véritable histoire de la Terre” pensa tout bas le garçon.\n" +
                "Basile, un être humain des plus ordinaires avait, par hasard, aussi malchanceux soit-il, souhaité explorer des ruines anciennes. Son exploration fut de courte durée à la suite d’une brusque chute dans une fosse. Le voilà donc errant depuis une dizaine d’heures vers un espoir de retrouver un jour la surface."),
        mapOf("title" to "Mercredi 3 avril ????, 02h04", "subtitle" to "Bibliothèque souterraine, salle des archives", "text" to "Une immense pièce s’offre au jeune homme affaibli par la marche, la soif et la faim. Dans son champ de vision, des livres à perte de vue, des bibliothèques hautes de centaines de mètres, mais où avait-il pu atterrir?\n" +
                "“Je vais me reposer ici...Tiens?” Soudain, au centre de la pièce surgit un livre, bien plus petit que les millions d’autres qui sommeillaient ici. Sur la couverture était inscrit : Déjà vu.\n" +
                "Quelques pages échappèrent des doigts de Basile, le livre se mit à briller de plus en plus. Le garçon se sentit comme absorbé, cela signifie-t-il sa fin?"),
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

    IntroductionPart(modifier = Modifier.pointerInput(key1 = null){
                                                       detectTapGestures {
                                                           nextPart()
                                                       }
    }, title = parts[partIndex]["title"]!!, subtitle = parts[partIndex]["subtitle"]!!, text = parts[partIndex]["text"]!!) {
        nextPart()
    }

}