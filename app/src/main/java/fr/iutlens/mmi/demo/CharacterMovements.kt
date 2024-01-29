package fr.iutlens.mmi.demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.TileMap
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.CloseNinja
import fr.iutlens.mmi.demo.game.sprite.tiledArea
import fr.iutlens.mmi.demo.game.sprite.toMutableTileMap
import fr.iutlens.mmi.demo.game.transform.FitTransform
import fr.iutlens.mmi.demo.game.transform.FocusTransform
import fr.iutlens.mmi.demo.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.demo.utils.loadSpritesheet
import java.lang.StringBuilder
import java.util.Random

fun createMap(row: Int, col: Int): String {

    val theMap = StringBuilder()

    fun randomTile(): Char{
        val tiles = listOf('!', '!', '!', '!', '_')
        val randInd = (0 until tiles.size).random()
        return tiles[randInd]
    }

    val door = kotlin.random.Random.nextInt(1, 4)
    val topWallDoor = "0122222O3333345"

    for (i in 1..row) {
        when(i) {
            1 -> if(door==1) {
                    theMap.append(topWallDoor)
                }else {
                    theMap.append("012222233333345")
                }
            4 -> for (j in 1..col) {
                    when(j) {
                        1 -> if (door==2) {
                                theMap.append('Q')
                            } else {
                                theMap.append('I')
                            }
                        col -> if(door==3){
                                theMap.append('R')
                            }else{
                                theMap.append('L')
                            }
                        else -> theMap.append(randomTile())
                    }

                 }
            row -> theMap.append("6788888P99999AB")
            else -> for (j in 1..col) {
                when(j){
                    1 -> if(i==2) {
                            theMap.append('C')
                        }else if(i==3) {
                            theMap.append('I')
                        }else if(i==5){
                            theMap.append('E')
                        }else if(i==6){
                            theMap.append('K')
                        }
                    col -> if(i==2) {
                            theMap.append('F')
                        }else if(i==3) {
                            theMap.append('L')
                        }else if(i==5){
                            theMap.append('D')
                        }else if(i==6){
                            theMap.append('J')
                        }
                    else -> theMap.append(randomTile())
                }

            }
        }
        theMap.appendln()
    }
    return theMap.toString()
}
fun oneEra(nbOfRooms: Int): List<String> {
    val allRooms = mutableListOf<String>()

    for (i in 1..nbOfRooms) {
        allRooms.add(createMap(7,15))
    }

    return allRooms
}

var firstEra = kotlin.random.Random.nextInt(7, 11);
var allRooms = oneEra(firstEra)

fun testCharacter(): Game {
    val map = allRooms[0].trimIndent().toMutableTileMap(
        "012345"+
                "6789AB"+
                "CDEFGH" +
                "IJKLMN" +
                "OPQRST" +
                "UVWXYZ" +
                "!-*/=_")
    val tileMap = R.drawable.decor.tiledArea(map)
    val staticSprite = BasicSprite(R.drawable.transparent, 1f*((tileMap.w*tileMap.sizeX)/2),1f*((tileMap.h*tileMap.sizeY)/2))
    val game = Game(
        background = tileMap,
        map = tileMap,
        spriteList = mutableSpriteListOf(staticSprite),
        transform = FitTransform(tileMap),
    )
    game.setupControllableCharacter()
    game.addCharacter(CloseNinja(0f,0f,game))
    return game
}
@Preview(
    showBackground = true,
    widthDp = 815,
    heightDp = 375
)
@Composable
fun GetCharacterPreview(){
    LocalContext.current.loadSpritesheet(R.drawable.isaac, 3, 4)
    LocalContext.current.loadSpritesheet(R.drawable.decor,6,7)
    val game = testCharacter()
    MyApplicationTheme {
        game.View(modifier= Modifier.fillMaxSize())
    }
}