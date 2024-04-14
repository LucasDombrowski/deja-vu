package fr.iutlens.mmi.dejaVu.game.map.shop

import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.HandHeart
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.SchoolBag
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.SingleGoldHeart
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.SingleHeart
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.Torch
import fr.iutlens.mmi.dejaVu.game.map.Map

class Shop() {
    val shopItems : List<ShopItem> = listOf(
        ShopItem(SingleHeart(),5),
        ShopItem(SingleGoldHeart(),7),
        ShopItem(Torch(),15),
        ShopItem(SchoolBag(),15),
        ShopItem(HandHeart(),15)
    )
    fun getItems() : List<ShopItem>{
        val list = mutableListOf<ShopItem>()
        repeat(3){
            list.add(shopItems.random().copy())
        }
        return list.toList()
    }
}