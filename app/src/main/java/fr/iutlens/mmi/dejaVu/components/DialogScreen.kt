package fr.iutlens.mmi.dejaVu.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.screens.MenuButton
import fr.iutlens.mmi.dejaVu.utils.Music

@Composable
fun DialogScreen(text : String, onEnd : ()->Unit, name : String ? = null, highlightedWords : List<String> = listOf(), italicWords : List<String> = listOf(), onSkip : ()->Unit = {}, last: Boolean = true, resetMusic : Boolean = true, content : @Composable() ()->Unit){

    var fullText by remember {
        mutableStateOf(text)
    }

    LaunchedEffect(key1 = text){
        Music.reduceMusicVolume()
    }

    var higherMusic by remember {
        mutableStateOf(last)
    }

    LaunchedEffect(last){
        higherMusic = last
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val boxWidth = with(configuration){
        (this.screenWidthDp*0.6).dp
    }
    val pxTextWidth = with(density){
        boxWidth.toPx()*0.7
    }
    val textWidth = with(density){
        pxTextWidth.toFloat().toDp()
    }

    val pxFontSize = pxTextWidth/30

    val fontSize = with(density){
        pxFontSize.toFloat().toSp()
    }

    val screenWidth = configuration.screenWidthDp.dp

    val lineHeight = fontSize*1.25

    val lineWidth = (pxTextWidth / pxFontSize)



    fun getMaxChars(): Int {
        return (6 * lineWidth).toInt()
    }

    fun splitChars() : List<Char>{
        return listOf<Char>('!','.',',','?',':',';')
    }

    fun getFirstSplit(text: String, index: Int): Int {
        for(i in index..<text.length){
            if(text[i] in splitChars()){
                return i
            }
        }
        return -1
    }

    fun getNextWordIndex(text: String, index: Int): Int {
        val firstSplit = getFirstSplit(text, index)
        if(firstSplit!=-1) {
            for (i in firstSplit..<text.length) {
                if (text[i] !in splitChars() && text[i] != ' ') {
                    return i
                }
            }
        }
        return -1
    }

    fun generateTextSequence() : List<String>{
        val localTextSequence : MutableList<String> = mutableListOf()
        var textPart = fullText
        if(textPart.length<=getMaxChars()){
            localTextSequence.add(textPart)
        } else {
            var endIndex = getNextWordIndex(textPart, getMaxChars()-1)
            while (endIndex<textPart.length && endIndex!=-1){
                if(textPart.length>getMaxChars()) {
                    if(endIndex==-1){
                        break
                    }
                    localTextSequence.add(textPart.substring(0,endIndex))
                    textPart = textPart.substring(endIndex)
                    endIndex = getNextWordIndex(textPart, getMaxChars() - 1)
                }
            }
            localTextSequence.add(textPart)
        }
        return localTextSequence.toList()
    }

    var textSequence by remember {
        mutableStateOf(generateTextSequence())
    }
    var textSequenceIndex by remember {
        mutableStateOf(0)
    }


    var currentText by remember {
        mutableStateOf(textSequence[textSequenceIndex])
    }

    if(fullText!=text){
        fullText = text
        textSequenceIndex = 0
        textSequence = generateTextSequence()
        currentText = textSequence[textSequenceIndex]
    }

    var instant by remember {
        mutableStateOf(false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun DialogScreenBox(text : String){
        DialogBox(text = text, boxWidth = boxWidth, textWidth = textWidth,  fontSize = fontSize, lineHeight = lineHeight, name = name, highlightedWords = highlightedWords, italicWords = italicWords, instant, onWritingStop = {
            instant = true
        })
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .pointerInput(key1 = "DialogScreen") {
            detectTapGestures {
                Music.stopSound(R.raw.text_sound_effect)
                if(!instant){
                    instant = true
                } else {
                    instant = false
                    if (textSequenceIndex + 1 < textSequence.size) {
                        textSequenceIndex++
                        currentText = textSequence[textSequenceIndex]
                    } else {
                        if (higherMusic && resetMusic) {
                            Music.normalMusicVolume()
                        }
                        onEnd()
                    }
                }
            }
        }
        .background(Color(0, 0, 0, 128))
        .navigationBarsPadding()
        .statusBarsPadding()){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            content()
            DialogScreenBox(text = currentText)
        }
        MenuButton(modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = -screenWidth / 40, y = screenWidth / 50),text = "Passer", width = screenWidth/8) {
            Music.stopSound(R.raw.text_sound_effect)
            if(resetMusic) {
                Music.normalMusicVolume()
            }
            onSkip()
        }
    }
}