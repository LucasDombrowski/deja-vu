package fr.iutlens.mmi.demo.game.map.rooms

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.items.LoyaltyCard
import fr.iutlens.mmi.demo.game.gameplayResources.items.OneHeart
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room
import fr.iutlens.mmi.demo.game.map.shop.Shop
import fr.iutlens.mmi.demo.game.map.shop.ShopItem
import fr.iutlens.mmi.demo.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.demo.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.demo.game.screens.cinematic.cinematics.TutorialShop
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
                "Élisa, la femme de chambre de Mme de Rênal, n’avait pas manqué de devenir amoureuse du jeune précepteur ; elle en parlait souvent à sa maîtresse. L’amour de Mlle Élisa avait valu à Julien la haine d’un des valets. Un jour, il entendit cet homme qui disait à Élisa : Vous ne voulez plus me parler depuis que ce précepteur crasseux est entré dans la maison. Julien ne méritait pas cette injure ; mais, par instinct de joli garçon, il redoubla de soins pour sa personne. La haine de M. Valenod redoubla aussi. Il dit publiquement que tant de coquetterie ne convenait pas à un jeune abbé. À la soutane près, c’était le costume que portait Julien.",
                R.drawable.cinematic_character,
                true,
                name = "Blaise"
            ),
            CinematicPart(
                "la femme de chambre de Mme de Rênal, n’avait pas manqué de devenir amoureuse du jeune précepteur ; elle en parlait souvent à sa maîtresse. L’amour de Mlle Élisa avait valu à Julien la haine d’un des valets. Un jour, il entendit cet homme qui disait à Élisa : Vous ne voulez plus me parler depuis que ce précepteur crasseux est entré dans la maison. Julien ne méritait pas cette injure ; mais, par instinct de joli garçon, il redoubla de soins pour sa personne. La haine de M. Valenod redoubla aussi. Il dit publiquement que tant de coquetterie ne convenait pas à un jeune abbé. À la soutane près, c’était le costume que portait Julien.",
                shopkeeperImage,
                false,
                name = "Shopkeeper"
            ),
            CinematicPart(
                "Élisa, la femme de chambre de Mme de Rênal, n’avait pas manqué de devenir amoureuse du jeune précepteur ; elle en parlait souvent à sa maîtresse. L’amour de Mlle Élisa avait valu à Julien la haine d’un des valets. Un jour, il entendit cet homme qui disait à Élisa : Vous ne voulez plus me parler depuis que ce précepteur crasseux est entré dans la maison. Julien ne méritait pas cette injure ; mais, par instinct de joli garçon, il redoubla de soins pour sa personne. La haine de M. Valenod redoubla aussi. Il dit publiquement que tant de coquetterie ne convenait pas à un jeune abbé. À la soutane près, c’était le costume que portait Julien.",
                R.drawable.cinematic_character,
                true,
                name = "Blaise"
            )
        ),game){
            game.controllableCharacter!!.temporaryMovingInteraction = {
                    x, y ->
                if(game.firstTime && !game.shopTutorial){
                    game.shopTutorial = true
                    game.cinematic.value = Pair(
                        TutorialShop(game),
                        true
                    )
                }
                with(shopItems.iterator()){
                    forEach {
                        if(it.inImageBox(x,y) && it.active){
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