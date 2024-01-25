package fr.iutlens.mmi.demo.game.gameplayResources

class Heart(val permanent: Boolean, var filled : Float = 1f) {
}

fun setBasicHearts(n: Int) : MutableList<Heart>{
    val hearts = mutableListOf<Heart>()
    repeat(n){
        hearts.add(Heart(true))
    }
    return hearts
}