package fr.iutlens.mmi.demo.components

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
@Composable
fun DialogScreen(text : String, onEnd : ()->Unit, content : @Composable() ()->Unit){

    var fullText by remember {
        mutableStateOf(text)
    }

    val boxWidth = LocalConfiguration.current.screenWidthDp*0.8
    val boxHeight = boxWidth*0.1
    val pxBoxWidth = with(LocalDensity.current) {
        boxWidth.dp.toPx()
    }
    val pxBoxHeight = with(LocalDensity.current) {
        boxHeight.dp.toPx()
    }
    val pxFontSize = with(LocalDensity.current) {
        fontSize.toPx()
    }

    fun getMaxChars(): Int {
        return ((pxBoxHeight / pxFontSize) * (pxBoxWidth / pxFontSize)).toInt()
    }

    fun getFirstSpace(text: String, index: Int): Int {
        return text.indexOf("", index)
    }

    fun getNextWordIndex(text: String, index: Int): Int {
        val firstSpace = getFirstSpace(text, index)
        for (i in firstSpace..<text.length) {
            if (text[i].toString() != "") {
                return i
            }
        }
        return -1
    }

    fun generateTextSequence() : List<String>{
        val textSequence : MutableList<String> = mutableListOf()
        var textPart = fullText
        if(textPart.length<=getMaxChars()){
            textSequence.add(textPart)
        } else {
            var endIndex = getNextWordIndex(textPart, getMaxChars()-1)
            while (endIndex<textPart.length){
                textSequence.add(textPart.substring(0,endIndex))
                textPart = textPart.substring(endIndex)
                if(textPart.length>=getMaxChars()) {
                    endIndex = getNextWordIndex(textPart, getMaxChars() - 1)
                }
            }
            textSequence.add(textPart)
        }
        return textSequence.toList()
    }

    Log.i("generated text sequence","${generateTextSequence()}")
    var textSequence = generateTextSequence()
    Log.i("text sequence","$textSequence")
    var textSequenceIndex = 0

    var currentText by remember {
        mutableStateOf(textSequence[textSequenceIndex])
    }
    if(fullText!=text){
        fullText = text
        textSequence = generateTextSequence()
        currentText = textSequence[textSequenceIndex]
    }

    @Composable
    fun DialogScreenBox(text : String){
        Log.i("Recompose","true")
        DialogBox(text = text, boxWidth = boxWidth, boxHeight = boxHeight)
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .pointerInput(key1 = "DialogScreen") {
            detectTapGestures {
                if (textSequenceIndex < textSequence.size - 1) {
                    textSequenceIndex++
                    currentText = textSequence[textSequenceIndex]
                } else {
                    onEnd()
                }
            }
        }){
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