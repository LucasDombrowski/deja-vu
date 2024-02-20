package fr.iutlens.mmi.demo.game.map.rooms

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.items.OneHeart
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room
import fr.iutlens.mmi.demo.game.map.shop.Shop
import fr.iutlens.mmi.demo.game.map.shop.ShopItem
import fr.iutlens.mmi.demo.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.demo.game.screens.cinematic.CinematicPart
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

    val shop = Shop()
    val shopItems = shop.getItems()
    var cinematic : Cinematic ? = null

    override fun create() : String {
        val theMap = StringBuilder()
        for (i in 1..row) {
            when (i) {
                1 -> if (exit=="top" || enter=="top") {
                    if(exit=="top" && open){
                        theMap.append("0122222U3333345")
                    } else {
                        theMap.append("0122222O3333345")
                    }
                } else {
                    theMap.append("012222233333345")
                }

                4 -> for (j in 1..col) {
                    when (j) {
                        1 -> if (exit=="left" || enter=="left") {
                            if(exit=="left" && open){
                                theMap.append('W')
                            } else {
                                theMap.append('Q')
                            }
                        } else {
                            theMap.append('I')
                        }

                        col -> if (exit=="right" || enter=="right") {
                            if(exit=="right" && open){
                                theMap.append('X')
                            } else {
                                theMap.append('R')
                            }

                        } else {
                            theMap.append('L')
                        }

                        else -> theMap.append("!")
                    }
                }

                row -> if(exit=="bottom" || enter=="bottom"){
                    if(exit=="bottom" && open){
                        theMap.append("6788888V99999AB")
                    } else {
                        theMap.append("6788888P99999AB")
                    }

                } else {
                    theMap.append("6788888899999AB")
                }
                else -> for (j in 1..col) {
                    when (j) {
                        1 -> if (i == 2) {
                            theMap.append('C')
                        } else if (i == 3) {
                            theMap.append('I')
                        } else if (i == 5) {
                            theMap.append('E')
                        } else if (i == 6) {
                            theMap.append('K')
                        }

                        col -> if (i == 2) {
                            theMap.append('F')
                        } else if (i == 3) {
                            theMap.append('L')
                        } else if (i == 5) {
                            theMap.append('D')
                        } else if (i == 6) {
                            theMap.append('J')
                        }

                        else -> theMap.append('!')
                    }
                }
            }
            theMap.appendLine()
        }

        val mapList = theMap.lines().map {
            it.split("")
        }

        val mapChars = mutableListOf<List<Char>>()
        with(mapList.iterator()){
            forEach {
                val newRow = mutableListOf<Char>();
                with(it.iterator()){
                    forEach {
                        if(it!=""){
                            newRow.add(it.single())
                        }
                    }
                }
                if(!newRow.isEmpty()){
                    mapChars.add(newRow)
                }
            }
        }
        return theMap.toString()
    }
    fun setup(game: Game){
        var xVal = getRoomCenter().first
        val yVal = getRoomCenter().second
        val xStep = shopItems[0].imageSprite.bitmap.width*3/2
        val listCenter = shopItems.size/2
        xVal-=listCenter*xStep
        for(shopItem in shopItems){
            shopItem.display(game,xVal,yVal)
            xVal+=xStep
        }
        game.controllableCharacter!!.temporaryMovingInteraction = {
            x, y ->
            with(shopItems.iterator()){
                forEach {
                    if(it.inImageBox(x,y)){
                        it.buy(game)
                    }
                }
            }
        }
        cinematic = createCinematic(game)
    }

    fun createCinematic(game: Game) : Cinematic{
        return Cinematic(listOf(
            CinematicPart(
                "Élisa, la femme de chambre de Mme de Rênal, n’avait pas manqué de devenir amoureuse du jeune précepteur ; elle en parlait souvent à sa maîtresse. L’amour de Mlle Élisa avait valu à Julien la haine d’un des valets. Un jour, il entendit cet homme qui disait à Élisa : Vous ne voulez plus me parler depuis que ce précepteur crasseux est entré dans la maison. Julien ne méritait pas cette injure ; mais, par instinct de joli garçon, il redoubla de soins pour sa personne. La haine de M. Valenod redoubla aussi. Il dit publiquement que tant de coquetterie ne convenait pas à un jeune abbé. À la soutane près, c’était le costume que portait Julien.",
                R.drawable.cinematic_character,
                true
            ),
            CinematicPart(
                "la femme de chambre de Mme de Rênal, n’avait pas manqué de devenir amoureuse du jeune précepteur ; elle en parlait souvent à sa maîtresse. L’amour de Mlle Élisa avait valu à Julien la haine d’un des valets. Un jour, il entendit cet homme qui disait à Élisa : Vous ne voulez plus me parler depuis que ce précepteur crasseux est entré dans la maison. Julien ne méritait pas cette injure ; mais, par instinct de joli garçon, il redoubla de soins pour sa personne. La haine de M. Valenod redoubla aussi. Il dit publiquement que tant de coquetterie ne convenait pas à un jeune abbé. À la soutane près, c’était le costume que portait Julien.",
                R.drawable.cinematic_character,
                false
            ),
            CinematicPart(
                "Élisa, la femme de chambre de Mme de Rênal, n’avait pas manqué de devenir amoureuse du jeune précepteur ; elle en parlait souvent à sa maîtresse. L’amour de Mlle Élisa avait valu à Julien la haine d’un des valets. Un jour, il entendit cet homme qui disait à Élisa : Vous ne voulez plus me parler depuis que ce précepteur crasseux est entré dans la maison. Julien ne méritait pas cette injure ; mais, par instinct de joli garçon, il redoubla de soins pour sa personne. La haine de M. Valenod redoubla aussi. Il dit publiquement que tant de coquetterie ne convenait pas à un jeune abbé. À la soutane près, c’était le costume que portait Julien.",
                R.drawable.cinematic_character,
                true
            )
        ),game)
    }

    fun launchCinematic(game: Game){
        if(cinematic!=null) {
            game.cinematic.value = Pair(cinematic!!, true)
        }
    }
}