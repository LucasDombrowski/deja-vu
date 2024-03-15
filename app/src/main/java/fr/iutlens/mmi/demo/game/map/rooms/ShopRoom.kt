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
    override var composition = create(false).trimIndent()
    val shop = Shop()
    val shopItems = shop.getItems()
    var cinematic : Cinematic ? = null

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

    override fun refresh(){
        composition = create(false).trimIndent()
        toList()
    }
}