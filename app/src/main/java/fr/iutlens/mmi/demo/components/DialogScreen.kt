package fr.iutlens.mmi.demo.components

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.utils.Music

@Composable
fun DialogScreen(text : String, onEnd : ()->Unit, name : String ? = null, content : @Composable() ()->Unit){

    var fullText by remember {
        mutableStateOf(text)
    }

    LaunchedEffect(key1 = text){
        Music.reduceMusicVolume()
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

    var textSequence = generateTextSequence()
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

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun DialogScreenBox(text : String){
        DialogBox(text = text, boxWidth = boxWidth, textWidth = textWidth,  fontSize = fontSize, lineHeight = lineHeight, name = name)
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .pointerInput(key1 = "DialogScreen") {
            detectTapGestures {
                Music.stopSound(R.raw.text_sound_effect)
                if (textSequenceIndex < textSequence.size - 1) {
                    textSequenceIndex++
                    currentText = textSequence[textSequenceIndex]
                } else {
                    Music.normalMusicVolume()
                    onEnd()
                }
            }
        }
        .background(Color(0, 0, 0, 128))){
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
    }
}