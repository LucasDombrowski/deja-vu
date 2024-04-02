package fr.iutlens.mmi.dejaVu.game.map.rooms

import fr.iutlens.mmi.dejaVu.game.gameplayResources.Challenge
import fr.iutlens.mmi.dejaVu.game.map.Map
import fr.iutlens.mmi.dejaVu.game.map.Room

class BossRoom(map : Map, enter: String ?= null) :  BasicRoom(enter=enter, exit = null, map = map, challenge = Challenge(
    name = "",
    effect = {},
    reverseEffect = {}
)) {


    override fun copy(): Room {
        return BossRoom(map, enter)
    }

    override fun create(obstacles : Boolean) : String {

        val theMap = StringBuilder()
        for(i in 0..<row){
            when(i){
                0->{
                    emptyLine(theMap)
                    for(j in 0..<col){
                        when(j){
                            0->{
                                theMap.append("K")
                                theMap.append("ü")
                            }
                            col-1->{
                                theMap.append("ï")
                                theMap.append("K")
                            }
                            (col-1)/2->{
                                if(enter=="top"){
                                    theMap.append(door(true))
                                } else if(exit=="top"){
                                    theMap.append(door(false))
                                } else {
                                    theMap.append("ö")
                                }
                            }
                            else->theMap.append("ö")
                        }
                    }
                }
                row-1->{
                    for(j in 0..<col){
                        when(j){
                            0->{
                                theMap.append("K")
                                theMap.append("Ê")
                            }
                            (col-1)/2->{
                                if(enter=="bottom"){
                                    theMap.append(door(true))
                                } else if(exit=="bottom"){
                                    theMap.append(door(false))
                                } else {
                                    theMap.append("Ô")
                                }
                            }
                            col-1->{
                                theMap.append("Û")
                                theMap.append("K")
                            }
                            else->theMap.append("Ô")
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
                                            theMap.append("Î")
                                        }
                                    }
                                    else->theMap.append("Î")
                                }
                            }
                            col-1->{
                                when(i){
                                    (row-1)/2->{
                                        if(enter=="right"){
                                            theMap.append(door(true))
                                        }  else if(exit=="right"){
                                            theMap.append(door(false))
                                        } else {
                                            theMap.append("Â")
                                        }
                                    }
                                    else->theMap.append("Â")
                                }
                                theMap.append("K")
                            }
                            else->theMap.append(randomTile(obstacles))
                        }
                    }
                }
            }
            theMap.appendLine()
        }
        emptyLine(theMap)

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


        if((enter!=null || exit!=null) && obstacles) {
            val result = isPathAvailable(mapChars)
            if (!result) {
                return create()
            }
        }
        return theMap.toString()
    }

    override fun randomTile(obstacles : Boolean): Char {
        return when(obstacles){
            true-> when((1..10).random()){
                1-> 'Ë'
                else->'Ä'
            }

            else-> 'Ä'
        }
    }

    override fun emptyLine(theMap: StringBuilder){
        for(j in 0..col+1){
            theMap.append("Ö")
        }
        theMap.appendLine()
    }


}
