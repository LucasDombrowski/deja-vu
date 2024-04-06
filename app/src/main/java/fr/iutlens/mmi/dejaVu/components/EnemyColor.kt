package fr.iutlens.mmi.dejaVu.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.dejaVu.game.sprite.BasicSprite
import fr.iutlens.mmi.dejaVu.game.sprite.Sprite

@Composable
fun EnemyColor(sprite : BasicSprite){
    val firstSprite = sprite.copy()
    val secondSprite = sprite.copy()
    val thirdSprite = sprite.copy()
    secondSprite.midLifeColor()
    thirdSprite.lowLifeColor()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    @Composable
    fun EnemyImage(image : Bitmap, colorFilter : ColorFilter){
        val imageSize = screenWidth*2/10
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = null,
            colorFilter = colorFilter,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(imageSize))
    }
    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.4f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        EnemyImage(image = firstSprite.spriteSheet.get(firstSprite.ndx)!!, colorFilter = firstSprite.colorFilter)
        EnemyImage(image = secondSprite.spriteSheet.get(secondSprite.ndx)!!, colorFilter = secondSprite.colorFilter)
        EnemyImage(image = thirdSprite.spriteSheet.get(thirdSprite.ndx)!!, colorFilter = thirdSprite.colorFilter)

    }

}