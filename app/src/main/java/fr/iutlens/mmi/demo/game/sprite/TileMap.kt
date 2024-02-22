package fr.iutlens.mmi.demo.game.sprite

import android.util.Log


/**
 * TileMap représente un tableau à deux dimensions d'entiers
 */
interface TileMap {
    operator fun get(x : Int , y : Int) : Int
    val sizeX : Int
    val sizeY : Int
}

fun TileMap.indexToCoord(ndx: Int) = ndx.mod(sizeX) to ndx/sizeX
fun TileMap.coordToIndex(x: Int, y:Int) = x+y*sizeX

/**
 * Contains indique si coord désigne une position valide dans une TileMap
 *
 * @param coord
 */
operator fun TileMap.contains(coord: Pair<Int,Int>) = coord.first in 0..<sizeX && coord.second in 0..<sizeY

/**
 * ArrayTileMap utilise un tableau pour représenter une TileMap
 * A noter que ArrayTileMap est modifiable
 *
 * @constructor Crée une copie modifiable d'une TileMap
 *
 * @param from TileMap copiée dans ArrayTileMap
 */
class ArrayTileMap(from : TileMap) : TileMap {
    override val sizeX = from.sizeX
    override val sizeY = from.sizeY
    val data = IntArray(sizeX*sizeY){
        val (x,y) = indexToCoord(it)
        from[x,y]
    }
    override fun get(x: Int, y: Int): Int = data[coordToIndex(x,y)]

    /**
     * Définie la valeur de la case de coordonnées x,y
     *
     * @param x
     * @param y
     * @param value
     */
    operator fun set(x: Int, y: Int, value:Int) {data[coordToIndex(x,y)]= value}
}

/**
 * Construit une ArrayTileMap (modifiable) à partir d'une TileMap
 *
 */
fun TileMap.toMutableTileMap() = ArrayTileMap(this)

/**
 * Construit une TileMap en découpant une chaîne en lignes.
 * La valeur de chaque case est la position du caractère correspondant dans la
 * chaîne fournie
 *
 * @param code liste des caractères utilisés pour coder la TileMap
 */


fun String.toTileMap(code: String) = object : TileMap {
    var data = this@toTileMap.split('\n')
    init {
        val maxLength = data.maxBy {
            it.length
        }.length
        val dataCopy = data.toMutableList()
        for(i in 0..<dataCopy.size){
            while (dataCopy[i].length<maxLength){
                dataCopy[i]+="!"
            }
        }
        data = dataCopy.toList()

    }
    override fun get(x: Int, y: Int): Int  = code.indexOf(data[y][x])
    override val sizeX = data[0].length
    override val sizeY = data.size

}


/**
 * Construit une ArrayTileMap en découpant une chaîne en lignes.
 * La valeur de chaque case est la position du caractère correspondant dans la
 * chaîne fournie
 *
 * @param code liste des caractères utilisés pour coder la TileMap
 */
fun String.toMutableTileMap(code: String) = this.toTileMap(code).toMutableTileMap()

/**
 * Transpose inverse les lignes et le colonnes de la carte
 *
 */
fun TileMap.transpose() = object : TileMap {
    override fun get(x: Int, y: Int) = this@transpose[y,x]
    override val sizeX = this@transpose.sizeY
    override val sizeY = this@transpose.sizeX
}
