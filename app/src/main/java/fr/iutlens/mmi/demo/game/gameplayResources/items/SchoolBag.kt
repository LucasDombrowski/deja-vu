package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Collectible
import fr.iutlens.mmi.demo.game.gameplayResources.Item
import fr.iutlens.mmi.demo.game.gameplayResources.collectibles.Coin
import fr.iutlens.mmi.demo.game.gameplayResources.collectibles.GoldHeartDrop
import fr.iutlens.mmi.demo.game.gameplayResources.collectibles.HalfGoldHeartDrop
import fr.iutlens.mmi.demo.game.gameplayResources.collectibles.HalfHeartDrop
import fr.iutlens.mmi.demo.game.gameplayResources.collectibles.HeartDrop
import fr.iutlens.mmi.demo.game.gameplayResources.collectibles.SuperCoin
import fr.iutlens.mmi.demo.game.map.rooms.ShopRoom

class SchoolBag : Item(
    image = R.drawable.school_bag,
    name = "Sac d'école",
    description = "Les ennemis tués ont 100% de chances de faire tomber un collectible à leurs morts. Laisse tomber entre un et trois collectibles au ramassage.",
    effects = {
        game ->
        game.dropProbability = 2
        val collectibles = listOf<Collectible>(
            Coin(game),
            GoldHeartDrop(game),
            HalfGoldHeartDrop(game),
            HalfHeartDrop(game),
            HeartDrop(game),
            SuperCoin(game)
        )
        val minCoordinates = game.map.currentRoom().getMinMaxCoordinates().first
        val maxCoordinates = game.map.currentRoom().getMinMaxCoordinates().second
        var currentCoordinates: Pair<Float, Float>
        fun onShopItem(coordinates : Pair<Float,Float>) : Boolean{
            if(game.map.currentRoom() !is ShopRoom){
                return false
            } else {
                with((game.map.currentRoom() as ShopRoom).shopItems){
                    forEach {
                        if(it.inImageBox(coordinates.first, coordinates.second)){
                            return true
                        }
                    }
                }
                return false
            }
        }

        repeat((1..3).random()){
            currentCoordinates = Pair(
                (minCoordinates.first.toInt()..maxCoordinates.first.toInt()).random().toFloat(),
                (minCoordinates.second.toInt()..maxCoordinates.second.toInt()).random().toFloat()
            )
            while (game.map.inForbiddenArea(currentCoordinates.first, currentCoordinates.second) || onShopItem(currentCoordinates)){
                currentCoordinates = Pair(
                    (minCoordinates.first.toInt()..maxCoordinates.first.toInt()).random().toFloat(),
                    (minCoordinates.second.toInt()..maxCoordinates.second.toInt()).random().toFloat()
                )
            }
            collectibles.random().setup(currentCoordinates.first, currentCoordinates.second)

        }
    }
) {
}