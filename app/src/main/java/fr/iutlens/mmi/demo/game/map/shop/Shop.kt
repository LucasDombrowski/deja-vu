package fr.iutlens.mmi.demo.game.map.shop

import fr.iutlens.mmi.demo.game.gameplayResources.items.OneHeart

class Shop {
    val shopItems : List<ShopItem> = listOf(
        ShopItem(
            OneHeart(),
            10
        )
    )
    fun getItems() : List<ShopItem>{
        val list = mutableListOf<ShopItem>()
        repeat(3){
            list.add(shopItems.random().copy())
        }
        return list.toList()
    }
}