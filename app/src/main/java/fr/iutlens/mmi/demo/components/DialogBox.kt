package fr.iutlens.mmi.demo.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.getCurrentActivityContext
import fr.iutlens.mmi.demo.ui.theme.MainFont
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DialogBox(text : String, boxWidth : Dp, textWidth : Dp, fontSize: TextUnit, lineHeight: TextUnit, name : String ? = null){

    
    var currentText by remember {
        mutableStateOf(text)
    }

    var isWriting by remember {
        mutableStateOf(true)
    }

    if(currentText!=text){
        currentText = text
        isWriting = true
    }

    fun stopWriting(){
        isWriting = false
    }

    val image = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.writing_animation)
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

    val dialogBox = when(name){
        null->R.drawable.dialog_box
        else->R.drawable.dialog_box_name
    }

    BoxWithConstraints(modifier = Modifier
        .width(boxWidth)
        .padding(5.dp)
        ){
        val textHeight = boxWidth/5
        Image(painter = painterResource(id = dialogBox),
            contentDescription = "Dialog Box",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth())
        if(name!=null){
            Text(
                text = name,
                fontSize = fontSize*1.25,
                modifier = Modifier.align(Alignment.TopCenter)
                    .offset(x = -boxWidth*295/1000, y = textHeight/9),
                style = TextStyle(
                    fontFamily = MainFont
                ),
                textAlign = TextAlign.Center
            )
        }
        WritingText(text = currentText, fontSize = fontSize, lineHeight = lineHeight, modifier = Modifier
            .align(Alignment.Center)
            .width(textWidth)) {
            stopWriting()
        }
        WritingAnimation(images.toList(), isWriting,
            Modifier
                .align(Alignment.BottomEnd)
                .height(textHeight))

    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun WritingText(text: String, modifier : Modifier, fontSize : TextUnit, lineHeight: TextUnit, stopWriting : ()->Unit){
    if(text.isNotEmpty()) {
        var currentText by remember {
            mutableStateOf("")
        }
        LaunchedEffect(key1 = text){
            currentText=""
            Music.playSound(R.raw.text_sound_effect, loop = -1)
        }

        LaunchedEffect(key1 = currentText){
            Thread.sleep(5)
            if(currentText!=text && currentText.length<text.length){
                currentText += text[currentText.length]
            } else {
                stopWriting()
                Music.stopSound(R.raw.text_sound_effect)
            }
        }


        Text(
            text = currentText,
            color = Color.Black,
            textAlign = TextAlign.Left,
            fontSize = fontSize,
            lineHeight = lineHeight,
            style = TextStyle(
                fontFamily = MainFont,
            ),
            modifier = modifier
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun WritingAnimation(images : List<Bitmap>, writing: Boolean, modifier: Modifier){
    var imageIndex by remember {
        mutableIntStateOf(0)
    }
    var animate by remember {
        mutableStateOf(writing)
    }
    var imageAnimation by remember {
        mutableStateOf(
            setInterval(0,100){
                if (imageIndex >= images.size - 1) {
                    imageIndex = 0
                } else {
                    imageIndex++
                }
            }
        )
    }
    if(animate!=writing){
        if(!writing){
            imageAnimation.cancel()
        } else {
            imageAnimation = setInterval(0,100){
                if (imageIndex >= images.size - 1) {
                    imageIndex = 0
                } else {
                    imageIndex++
                }
            }
        }
        animate = writing
    }

    Image(bitmap = images[imageIndex].asImageBitmap(),
        contentDescription = "Writing",
        contentScale = ContentScale.FillHeight,
        modifier = modifier,
        )


}