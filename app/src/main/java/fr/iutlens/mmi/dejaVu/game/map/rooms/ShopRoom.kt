package fr.iutlens.mmi.dejaVu.game.map.rooms

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.boot.checkedTutorials
import fr.iutlens.mmi.dejaVu.boot.commitBooleanSharedPreferences
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.LoyaltyCard
import fr.iutlens.mmi.dejaVu.game.map.Map
import fr.iutlens.mmi.dejaVu.game.map.Room
import fr.iutlens.mmi.dejaVu.game.map.shop.Shop
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.TutorialShop
import java.lang.StringBuilder

class ShopRoom(map : Map, enter: String ?= null, exit: String? = null) : Room(
    row = 7,
    col = 15,
    open = true,
    enter = enter,
    exit = exit,
    map = map,
    enemies = 0..0
) {
    override var composition = create(false).trimIndent()
    val shop = Shop()
    val shopItems = shop.getItems().toMutableList()
    var cinematic : Cinematic ? = null

    fun setup(game: Game){
        if(hasLoyaltyCard(game)){
            for(shopItem in shopItems){
                shopItem.price/=2
                shopItem.refreshPrice()
            }
        }
        var xVal = getRoomCenter().first
        val yVal = getRoomCenter().second
        val xStep = shopItems[0].imageSprite.bitmap.width*3/2
        val listCenter = shopItems.size/2
        xVal-=listCenter*xStep
        for(shopItem in shopItems){
            shopItem.display(game,xVal,yVal)
            xVal+=xStep
        }
        cinematic = createCinematic(game)
        val shopkeeperX = getRoomCenter().first
        val shopkeeperY = yVal - shopItems[0].imageSprite.bitmap.height - shopItems[0].textSprite.bitmap.height - game.map.tileArea.h/6
        game.shopkeeper.sprite.x = shopkeeperX
        game.shopkeeper.sprite.y = shopkeeperY
        game.addCharacter(game.shopkeeper)
    }

    fun createCinematic(game: Game) : Cinematic{
        val shopkeeperImage = R.drawable.cinematic_shopkeeper
        return Cinematic(listOf(
            CinematicPart(
                "...",
                R.drawable.cinematic_character,
                true,
                name = "Blaise",
                imageSliceX = 2,
                imageAnimationDelay = 200
            ),
            CinematicPart(
                "Tiens ? Il n’y a pas d’ennemis ici ? Quel soulagement, un peu de repos me fera du bien !",
                R.drawable.cinematic_character,
                true,
                name = "Blaise",
                imageSliceX = 2,
                imageAnimationDelay = 200
            ),
            CinematicPart(
                "Bienvenu petit étranger.",
                shopkeeperImage,
                false,
                name = "Marchand"
            ),
            CinematicPart(
                "AH !",
                R.drawable.cinematic_character,
                true,
                name = "Blaise",
                imageSliceX = 2,
                imageAnimationDelay = 200
            ),
            CinematicPart(
                "Range cette arme je te prie, je ne suis qu’un simple marchand.",
                shopkeeperImage,
                false,
                name = "Marchand"
            ),
            CinematicPart(
                "Au beau milieu d’un champ de bambou ?",
                R.drawable.cinematic_character,
                true,
                name = "Blaise",
                imageSliceX = 2,
                imageAnimationDelay = 200
            ),
            CinematicPart(
                "Ah les jeunes toujours là pour remuer le couteau dans la plaie, disons que j’ai été banni du château. C’est une longue histoire.",
                shopkeeperImage,
                false,
                name = "Marchand"
            ),
            CinematicPart(
                "Et c’est censé me rassurer ?",
                R.drawable.cinematic_character,
                true,
                name = "Blaise",
                imageSliceX = 2,
                imageAnimationDelay = 200
            ),
            CinematicPart(
                "Sois tranquille, tu trouveras ici de quoi poursuivre ton aventure, nous discuterons de moi plus tard. Tant que tu payes tu peux rester aussi longtemps que tu peux.",
                shopkeeperImage,
                false,
                name = "Marchand"
            ),
            CinematicPart(
                "Super… Merci j’imagine ?",
                R.drawable.cinematic_character,
                true,
                name = "Blaise",
                imageSliceX = 2,
                imageAnimationDelay = 200
            ),
        ) + if(game.firstTime && !game.shopTutorial && !checkedTutorials["shop"]!!) { TutorialShop(game).parts } else {
            listOf()
        },game){
            game.shopTutorial = true
            if(!checkedTutorials["shop"]!!){
                commitBooleanSharedPreferences("shopTutorial",true)
            }
            game.controllableCharacter!!.temporaryMovingInteraction = {
                    x, y ->
                with(shopItems.iterator()){
                    forEach {
                        if(it.inItemBox(x,y) && it.active){
                            it.buy(game)
                        }
                    }
                }
            }
        }
    }

    fun launchCinematic(game: Game){
        if(cinematic!=null) {
            game.cinematic.value = Pair(cinematic!!, true)
        }
    }

    override fun refresh(){
        composition = create(false).trimIndent()
        toList()
    }

    fun hasLoyaltyCard(game: Game) : Boolean{
        for(item in game.controllableCharacter!!.items){
            if(item is LoyaltyCard){
                return true
            }
        }
        return false
    }

    override fun create(obstacles: Boolean) : String {

        val theMap = StringBuilder()
        for(i in 0..<row){
            when(i){
                0->{
                    emptyLine(theMap)
                    for(j in 0..<col){
                        when(j){
                            0->{
                                theMap.append("K")
                                theMap.append("0")
                            }
                            col-1->{
                                theMap.append("1")
                                theMap.append("K")
                            }
                            (col-1)/2->{
                                if(enter=="top"){
                                    theMap.append(door(true))
                                } else if(exit=="top"){
                                    theMap.append(door(false))
                                } else {
                                    theMap.append("2")
                                }
                            }
                            else->theMap.append("2")
                        }
                    }
                }
                row-1->{
                    for(j in 0..<col){
                        when(j){
                            0->{
                                theMap.append("K")
                                theMap.append("4")
                            }
                            (col-1)/2->{
                                if(enter=="bottom"){
                                    theMap.append(door(true))
                                } else if(exit=="bottom"){
                                    theMap.append(door(false))
                                } else {
                                    theMap.append("7")
                                }
                            }
                            col-1->{
                                theMap.append("5")
                                theMap.append("K")
                            }
                            else->theMap.append("7")
                        }
                    }
                }
                else->{
                    for(j in 0..<col){
                        when(j){
                            0->{
                                theMap.append("K")
                                when(i){
                                    (row-1)/2->{
                                        if(enter=="left"){
                                            theMap.append(door(true))
                                        } else if(exit=="left"){
                                            theMap.append(door(false))
                                        } else {
                                            theMap.append("6")
                                        }
                                    }
                                    else->theMap.append("6")
                                }
                            }
                            1->when(i){
                                1->theMap.append("Y")
                                row-2->theMap.append("(")
                                else->theMap.append("&")
                            }
                            col-1->{
                                when(i){
                                    (row-1)/2->{
                                        if(enter=="right"){
                                            theMap.append(door(true))
                                        }  else if(exit=="right"){
                                            theMap.append(door(false))
                                        } else {
                                            theMap.append("3")
                                        }
                                    }
                                    else->theMap.append("3")
                                }
                                theMap.append("K")
                            }
                            col-2->{
                                when(i){
                                    1->theMap.append("Z")
                                    row-2->theMap.append("-")
                                    else->theMap.append("X")
                                }
                            }
                            else->when(i){
                                1->theMap.append("W")
                                row-2->theMap.append("é")
                                else->theMap.append("è")
                            }
                        }
                    }
                }
            }
            theMap.appendLine()
        }
        emptyLine(theMap)

        return theMap.toString()
    }
}