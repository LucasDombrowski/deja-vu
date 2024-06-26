package fr.iutlens.mmi.dejaVu.game.map.shop

import android.util.Log
import androidx.compose.ui.graphics.Color
import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Item
import fr.iutlens.mmi.dejaVu.game.sprite.ImageSprite
import fr.iutlens.mmi.dejaVu.game.sprite.TextSprite
import fr.iutlens.mmi.dejaVu.getCurrentActivityContext
import fr.iutlens.mmi.dejaVu.utils.Music

class ShopItem(val item : Item, var price: Int) {
    var textSprite = TextSprite(price.toString(),150f, Color.White,false,0f,0f)
    var imageSprite = ImageSprite(item.image, 100, 0f,0f)

    var active = true

    fun display(game: Game, x:Float, y:Float){
        imageSprite.x = x
        imageSprite.y = y
        textSprite.x = x
        textSprite.y = imageSprite.boundingBox.bottom + game.map.tileArea.h/6
        game.addSprite(imageSprite)
        game.addSprite(textSprite)
    }

    fun resetSprites(tileWidth: Int){
        textSprite = TextSprite(price.toString(),(tileWidth/3).toFloat(), Color.White,false,0f,0f)
        imageSprite = ImageSprite(item.image, tileWidth/2, 0f,0f)
    }

    fun copy() : ShopItem{
        return ShopItem(item,price)
    }

    fun inImageBox(x: Float, y:Float) : Boolean{
        return x in imageSprite.boundingBox.left..imageSprite.boundingBox.right && y in imageSprite.boundingBox.top..imageSprite.boundingBox.bottom
    }

    fun inTextBox(x: Float, y: Float) : Boolean{
        return x in textSprite.boundingBox.left..textSprite.boundingBox.right && y in textSprite.boundingBox.top..textSprite.boundingBox.bottom
    }

    fun inItemBox(x : Float, y : Float) : Boolean{
        return inImageBox(x,y) || inTextBox(x,y)
    }

    fun buy(game: Game){
        if(game.coins.value>=price) {
            if(!item.major){
                val soundVolume = 0.25f
                Music.playSound(R.raw.purchase_item, leftVolume = soundVolume, rightVolume = soundVolume)
            }
            game.coins.value-=price
            item.get(game)
            game.deleteSprite(imageSprite)
            game.deleteSprite(textSprite)
            active = false
        }
    }

    fun refreshPrice(){
        textSprite = TextSprite(price.toString(),75f, Color.White,false,0f,0f)
    }
}