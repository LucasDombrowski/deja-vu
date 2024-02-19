package fr.iutlens.mmi.demo.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.AccessController.getContext

var writing : Job ?= null
var imageAnimation : Job ?= null
val fontSize = 16.sp

@Composable
fun DialogBox(text : String, boxWidth : Double, boxHeight: Double){

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
    Box(modifier = Modifier
        .width(boxWidth.dp)
        .height(boxHeight.dp)
        .background(Color.White)
        .padding(5.dp)
        ){
        WritingText(text = currentText, {
            stopWriting()
        })
        WritingAnimation(images.toList(), isWriting,
            Modifier
                .align(Alignment.BottomEnd)
                .width(200.dp)
                .height(200.dp)
                .offset(x = 100.dp, y = 50.dp))
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun WritingText(text: String, stopWriting : ()->Unit){
    val scope = rememberCoroutineScope()
    if(text.isNotEmpty()) {
        var currentText by remember {
            mutableStateOf(text[0].toString())
        }

        var previousText by remember {
            mutableStateOf(text)
        }

        if(text!=previousText){
            scope.launch {
                while (writing != null){
                    writing!!.cancel()
                    writing = null
                    delay(10)
                }
                previousText = text
                currentText = previousText[0].toString()
            }

        }

        if (writing == null) {
            writing = setInterval(0, 10) {
                if (currentText.length >= text.length) {
                    writing!!.cancel()
                    writing = null
                    stopWriting()
                } else {
                    currentText += text[currentText.length]
                }
            }
        }

        Text(
            text = currentText,
            color = Color.Black,
            textAlign = TextAlign.Left,
            fontSize = fontSize
        )
    }
}

@Composable
fun WritingAnimation(images : List<Bitmap>, writing: Boolean, modifier: Modifier){
    var imageIndex by remember {
        mutableIntStateOf(0)
    }
    if(imageAnimation == null) {
        imageAnimation = setInterval(0, 200) {
            Log.i("image","true")
            if (imageIndex >= images.size - 1) {
                imageIndex = 0
            } else {
                imageIndex++
            }
        }
    } else if(!writing){
        imageAnimation!!.cancel()
        imageAnimation = null
    }
    Image(bitmap = images[imageIndex].asImageBitmap(),
        contentDescription = "Writing",
        contentScale = ContentScale.Fit,
        modifier = modifier)


}