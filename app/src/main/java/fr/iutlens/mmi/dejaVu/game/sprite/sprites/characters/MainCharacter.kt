package fr.iutlens.mmi.dejaVu.game.sprite.sprites.characters

import androidx.compose.ui.graphics.Color
import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Heart
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Item
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.OneHeart
import fr.iutlens.mmi.dejaVu.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.dejaVu.game.map.rooms.LargeRoom
import fr.iutlens.mmi.dejaVu.game.map.rooms.LongRoom
import fr.iutlens.mmi.dejaVu.game.sprite.BasicSprite
import fr.iutlens.mmi.dejaVu.game.sprite.DrawingSprite
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.Character
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.Enemy
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.Projectile
import fr.iutlens.mmi.dejaVu.utils.Music
import fr.iutlens.mmi.dejaVu.utils.getDistance
import fr.iutlens.mmi.dejaVu.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round

class MainCharacter(x: Float, y:Float, game: Game) : Character(
    sprite =  BasicSprite(R.drawable.blaise,x,y,0),
    game = game,
    basicAnimationSequence = listOf(0,1),
    speed = 0.125f,
    invulnerability = 750,
    hearts = setBasicHearts(5),
    leftAnimationSequence = listOf(12,13),
    topAnimationSequence = listOf(20,21,22,23),
    rightAnimationSequence = listOf(8,9),
    bottomAnimationSequence = listOf(16,17,18,19),
    fireRate = 500,
    animationDelay = 200L
    ){

    val targetIndicator : BasicSprite = BasicSprite(R.drawable.target_indicator, sprite.x, sprite.y)

    val pathIndicator : DrawingSprite = DrawingSprite(R.drawable.path_indicator, sprite.x, sprite.y, drawColor = Color(255,255,255,192))

    var dragAction = false

    var previousViewingDistance = 4

    var viewingDistance = 4

    var previousPathIndicatorTile = game.map.getMapIndexFromPosition(sprite.x, sprite.y)

    var directProjectileBehaviors : MutableList<()->Unit> = mutableListOf()

    var projectile : Projectile = Projectile(BasicSprite(R.drawable.projectiles, sprite.x, sprite.y,4), range = 4f, speed = 0.1f, friendly = true, damages =  1f, knockback = 0.5f, sound = R.raw.hero_shoot)

    var items : MutableList<Item> = mutableListOf()

    var autoFire : Job = setInterval(0,fireRate){
        if(game.pause){
            stopFiring()
        } else {
            getClosestEnemy()
            if(target!=null) {
                if (!game.blinded || distanceWith(target!!) < viewingDistance * game.map.tileArea.w) {
                    fireToTarget()
                }
            }
        }
    }

    val targetIndicatorCheckInterval = 33

    var targetIndicatorMoving = setInterval(0, targetIndicatorCheckInterval.toLong()){
        if(!game.pause) {
            if (target != null) {
                targetIndicator.visible()
                moveTargetIndicator()
            } else if (!game.characterList.any { it is Enemy }) {
                targetIndicator.x = sprite.x
                targetIndicator.y = sprite.y
            }
        }
    }

    override fun basicAnimation() : List<Int>{
        basicAnimationSequence = when(previousDirection){
            "left"-> listOf(4,5)
            "right"-> listOf(0,1)
            else->basicAnimationSequence
        }
        return basicAnimationSequence
    }

    fun totalBlind(){
        previousViewingDistance = viewingDistance
        viewingDistance = 0
    }

    fun recoverView(){
        viewingDistance = previousViewingDistance
    }

    fun targetDifference() : Float{
        return getDistance(
            target!!.sprite.x,
            target!!.sprite.y,
            target!!.sprite.x,
            target!!.sprite.boundingBox.bottom
        )/2f
    }

    fun targetOffsetY() : Float{
        return target!!.sprite.y + targetDifference()
    }

    fun aimOffset() : Pair<Float,Float>{
        return Pair(
            targetIndicator.x,
            targetIndicator.y - targetDifference()
        )
    }

    fun moveTargetIndicator(){
        if(targetTooFar()){
            val steps = getTargetMoveSteps()
            val xStep = steps.first
            val yStep = steps.second
            targetIndicator.x+=xStep
            targetIndicator.y+=yStep
        } else{
            val aimedX = target!!.sprite.x
            val aimedY = targetOffsetY()
            val moveSpeed = 0.3f*((game.map.tileArea.w + game.map.tileArea.h)/2)
            if(targetIndicator.x!=aimedX || targetIndicator.y!=aimedY){
                val xChangeValue = when{
                    abs(aimedX-targetIndicator.x)>moveSpeed->moveSpeed
                    else->(abs(aimedX-targetIndicator.x)%moveSpeed)
                }
                val yChangeValue = when{
                    abs(aimedY-targetIndicator.y)>moveSpeed->moveSpeed
                    else->(abs(aimedY-targetIndicator.y)%moveSpeed)
                }
                when{
                    aimedX<targetIndicator.x->targetIndicator.x-=xChangeValue
                    aimedX>targetIndicator.x->targetIndicator.x+=xChangeValue
                    else->targetIndicator.x
                }
                when{
                    aimedY<targetIndicator.y->targetIndicator.y-=yChangeValue
                    aimedY>targetIndicator.y->targetIndicator.y+=yChangeValue
                    else->targetIndicator.y
                }
            }
        }
        game.invalidate()
    }

    fun targetTooFar() : Boolean{
        val moveSpeed = 0.3f*((game.map.tileArea.w + game.map.tileArea.h)/2)
        val aimedX = target!!.sprite.x
        val aimedY = targetOffsetY()
        return getDistance(targetIndicator.x, targetIndicator.y, targetIndicator.x, aimedY) > moveSpeed && getDistance(targetIndicator.x, targetIndicator.y, aimedX, targetIndicator.y) > moveSpeed
    }

    fun getTargetMoveSteps() : Pair<Float,Float>{
        val aimedX = target!!.sprite.x
        val aimedY = targetOffsetY()
        val steps = targetMoveStep()
        val xStep = when{
            aimedX<targetIndicator.x->-steps[0]
            else->steps[0]
        }
        val yStep = when{
            aimedY<targetIndicator.y->-steps[1]
            else->steps[1]
        }
        return Pair(xStep,yStep)
    }

    fun targetMoveStep() : List<Float>{
        val moveSpeed = 0.3f*((game.map.tileArea.w + game.map.tileArea.h)/2)
        val aimedX = target!!.sprite.x
        val aimedY = targetOffsetY()
        val vectorX = abs( round(aimedX) - round(targetIndicator.x))
        val vectorY = abs(round(aimedY) - round(targetIndicator.y))
        return if (vectorX == 0f && vectorY == 0f) {
            listOf(0f, 0f)
        } else if (vectorX == 0f) {
            listOf(0f, moveSpeed)
        } else if(vectorY==0f){
            listOf(moveSpeed,0f)
        } else if(vectorX==vectorY){
            listOf(moveSpeed,moveSpeed)
        } else if(vectorX>vectorY){
            listOf(moveSpeed,moveSpeed/(vectorX/vectorY))
        } else {
            listOf(moveSpeed/(vectorY/vectorX),moveSpeed)
        }
    }

    fun stopFiring(){
        autoFire.cancel()
        autoFire = setInterval(0,fireRate){
            if(!game.pause){
                fitToFireRate()
            }
        }
    }

    var moving = false

    var temporaryMovingInteraction : (x: Float, y: Float) -> Unit = {
        x, y ->  
    }


    var dragStartBehavior : (x:Float, y:Float)->Unit = {
        x, y ->
        pathIndicator.drawing = true
        movePathIndicator(x,y,true)


    }

    var dragEndBehavior : ()->Unit = {
        if(dragAction){
            dragAction = false
            erasePathDrawing()
        }
    }

    var tapMovingBehavior : (x: Float, y:Float)->Unit = {
        x,y->
        movePathIndicator(x,y)
    }

    var dragMovingBehavior : (x: Float, y:Float)->Unit = {
            x,y->
            movePathIndicator(x,y)
    }

    var dragStandBehavior : Job = GlobalScope.launch {  }


    fun movePathIndicator(x: Float, y: Float, reset: Boolean = false){
        dragStandBehavior.cancel()
        dragAction = true
        if(mobile) {
            pathIndicator.visible()
            pathIndicator.x = x
            pathIndicator.y = y
            if(reset || pathIndicator.lastPositions.size==0){
                pathIndicator.resetPositions()
            } else {
                pathIndicator.newPosition()
            }
            game.invalidate()
            if(!moving){
                moveToPathIndicator()
            }
        }
        dragStandBehavior = GlobalScope.launch {
            delay(100)
            dragEndBehavior()
        }
    }

    fun erasePathDrawing(){
        if(!dragAction){
            pathIndicator.erase()
        }
    }

    fun pathIndicatorTile() : Pair<Int,Int>{
        return game.map.getMapIndexFromPosition(pathIndicator.x, pathIndicator.y)
    }
    fun inIndicatorBoundingBox(x : Float = sprite.x, y: Float = sprite.y) : Boolean{
        return x in pathIndicator.boundingBox.left..pathIndicator.boundingBox.right && y in pathIndicator.boundingBox.top..pathIndicator.boundingBox.bottom
    }

    fun moveToPathIndicator(){
        moving = true
        GlobalScope.launch {
            if(currentTile()!=pathIndicatorTile()){
                if(isPathFree(pathIndicator.x, pathIndicator.y) && !indicatorEndFollowingPath()){
                    if(pathFollow) {
                        disablePathFollowing()
                        moveTo(pathIndicator.x, pathIndicator.y)
                    } else if(pathIndicatorTile()!=previousPathIndicatorTile){
                        previousPathIndicatorTile = pathIndicatorTile()
                        moveTo(pathIndicator.x, pathIndicator.y)
                    }
                    delay(66)
                    moveToPathIndicator()
                } else {
                    if(pathIndicatorTile()!=previousPathIndicatorTile){
                        disablePathFollowing()
                        previousPathIndicatorTile=pathIndicatorTile()
                        if(game.blinded && getDistance(sprite.x, sprite.y, pathIndicator.x, pathIndicator.y) > viewingDistance*game.map.tileArea.w){
                            disablePathFollowing()
                            moveTo(pathIndicator.x, pathIndicator.y)
                        } else {
                            setupPath(pathIndicator.x, pathIndicator.y)
                        }
                        delay(66)
                        moveToPathIndicator()
                    } else if(!pathFollow){
                        pathIndicator.invisible()
                        delay(66)
                        moving = false
                    } else {
                        delay(66)
                        moveToPathIndicator()
                    }
                }
            } else {
                moveTo(pathIndicator.x, pathIndicator.y)
                pathIndicator.invisible()
                delay(66)
                moving = false
            }
        }
    }

    fun indicatorEndFollowingPath() : Boolean{
        return currentPath.toList().isNotEmpty() && currentPath.last() == pathIndicatorTile()
    }

    override fun changePos(x: Float, y: Float){
        val yCheckValue = y + (sprite.boundingBox.bottom - sprite.boundingBox.top)/3
        if(game.map.inForbiddenArea(
                x,
                yCheckValue
        )){
            GlobalScope.launch {
                if(pathFollow){
                    val currentTile = game.map.getMapIndexFromPosition(sprite.x,sprite.y)
                    val currentTileFloatCoordinates = game.map.getPositionFromMapIndex(currentTile.first, currentTile.second)
                    val currentTileCenter = Pair(
                        currentTileFloatCoordinates.first+game.map.tileArea.w/2,
                        currentTileFloatCoordinates.second+game.map.tileArea.h/2
                    )
                    changePos(currentTileCenter.first,currentTileCenter.second)
                } else {
                    stun()
                    delay(33)
                    restart()
                }
            }
        } else if(game.map.inOpenDoor(x,y) && game.map.currentRoom().open){
            disablePathFollowing()
            stun()
            val soundVolume = 0.5f
            Music.playSound(R.raw.door, leftVolume = soundVolume, rightVolume = soundVolume)
            currentAnimationSequence = basicAnimation()
            GlobalScope.launch {
                sprite.setTransparencyLevel(0.75f)
                game.invalidate()
                delay(100)
                sprite.setTransparencyLevel(0.5f)
                game.invalidate()
                delay(100)
                sprite.setTransparencyLevel(0.25f)
                game.invalidate()
                delay(100)
                sprite.setTransparencyLevel(0f)
                game.invalidate()
                delay(100)
                game.map.currentRoom().close()
                while (!game.map.nextRoom().characterInStartPosition(game)) {
                    game.map.nextRoom().placeCharacter(game)
                    pathIndicator.x = sprite.x
                    pathIndicator.y = sprite.y
                    sprite.setTransparencyLevel(1f)
                }
                if(game.map.currentRoom() is LongRoom || game.map.currentRoom() is LargeRoom){
                    temporaryMovingInteraction = {
                            x, y ->
                    }
                }
                game.nextRoom()
            }
        } else {
            temporaryMovingInteraction(x,yCheckValue)
            if(inSolidCharacterBoundingBox(x,yCheckValue) || inSolidSpriteBoundingBox(x,yCheckValue)){
                GlobalScope.launch {
                    if(pathFollow){
                        val currentTile = game.map.getMapIndexFromPosition(sprite.x,sprite.y)
                        val currentTileFloatCoordinates = game.map.getPositionFromMapIndex(currentTile.first, currentTile.second)
                        val currentTileCenter = Pair(
                            currentTileFloatCoordinates.first+game.map.tileArea.w/2,
                            currentTileFloatCoordinates.second+game.map.tileArea.h/2
                        )
                        changePos(currentTileCenter.first,currentTileCenter.second)
                    } else {
                        stun()
                        delay(33)
                        restart()
                    }
                }
            } else {
                sprite.x = x
                sprite.y = y
            }
            val collectibleListCopy = game.collectibleList.toList()
            with(collectibleListCopy.iterator()){
                forEach {
                    if(inBoundingBox(it.sprite.x, it.sprite.y)){
                        val soundVolume = 0.125f
                        it.collectEffect()
                        Music.playSound(it.sound, leftVolume = soundVolume, rightVolume = soundVolume)
                        it.destroy()
                    }
                }
            }
        }
        game.invalidate()
    }

    override fun isPathFree(x: Float, y: Float): Boolean {
        val yCheckValue = sprite.y + (sprite.boundingBox.bottom - sprite.boundingBox.top)/3
        val startTile = game.map.getMapIndexFromPosition(sprite.x, yCheckValue)
        val aimedTile = game.map.getMapIndexFromPosition(x, y)
        var startCol = when {
            sprite.x < x -> startTile.second
            else -> aimedTile.second
        }
        var startRow = when {
            yCheckValue < y -> startTile.first
            else -> aimedTile.first
        }
        var endCol = when {
            sprite.x < x -> aimedTile.second
            else -> startTile.second
        }
        var endRow = when {
            yCheckValue < y -> aimedTile.first
            else -> startTile.first
        }

        if(abs(startTile.first - aimedTile.first)<=1 && bottomTilesNotAvailable(startTile,aimedTile)){
            endRow++
        }


        for (i in startRow..endRow) {
            for (j in startCol..endCol) {
                if (!isAvailableTile(row = i, col = j)) {
                    return false
                }
            }
        }
        return true
    }

    fun bottomTilesNotAvailable(startTile : Pair<Int,Int>, endTile: Pair<Int,Int>) : Boolean{
        return !isAvailableTile(row = startTile.first+1, col = startTile.second) ||
                (when{
                    endTile.second<startTile.second->!isAvailableTile(row = startTile.first+1, col = startTile.second-1)
                    else->!isAvailableTile(row = startTile.first+1, col = startTile.second+1)
                })
    }

    fun inSolidCharacterBoundingBox(x: Float,y: Float) : Boolean{
        for(character in game.characterList){
            if(character.inBoundingBox(x,y) && character !is MainCharacter && character.solid){
                return true
            }
        }
        return false
    }

    fun inSolidSpriteBoundingBox(x : Float,y : Float) : Boolean{
        for(solidSprite in game.solidSpriteList){
            if(solidSprite.boundingBox.left < x && solidSprite.boundingBox.right > x && solidSprite.boundingBox.top < y && solidSprite.boundingBox.bottom > y){
                return true
            }
        }
        return false
    }
    fun fitToFireRate(){
        autoFire.cancel()
        autoFire = setInterval(0,fireRate){
            if(game.pause){
                stopFiring()
            } else {
                getClosestEnemy()
                if(target!=null) {
                    if (!game.blinded || distanceWith(target!!) < viewingDistance * game.map.tileArea.w) {
                        fireToTarget()
                    }
                }
            }
        }
    }

    fun fireToTarget(){
        if (target is Enemy && target!!.alive && !target!!.sprite.isInvisible()) {
            projectile.fireProjectile(
                game,
                sprite.x,
                sprite.y,
                aimOffset().first,
                aimOffset().second
            )
            with(directProjectileBehaviors.iterator()) {
                forEach {
                    it()
                }
            }
        }
        game.invalidate()
    }

    fun refreshHeathBar(){
        val newHearts : MutableList<Heart> = mutableListOf()
        for(heart in hearts){
            newHearts.add(heart.copy())
        }
        newHearts.sortBy {
            !it.permanent
        }
        hearts = newHearts
        game.ath["hearts"] = newHearts
    }

    fun blink(){
        GlobalScope.launch {
            if(remainingInvulnerability && !game.pause){
                delay(100)
                sprite.invisible()
                delay(100)
                sprite.visible()
                blink()
            } else {
                while (sprite.isInvisible()){
                    sprite.visible()
                }
            }
        }
    }

    fun getClosestEnemy(){
        val distances : MutableMap<Float,Enemy> = mutableMapOf<Float,Enemy>()
        with(game.characterList.iterator()) {
            forEach {
                if(it is Enemy && it.targetable) {
                    if(!game.blinded || distanceWith(it) < viewingDistance*game.map.tileArea.w) {
                        distances[getDistance(sprite.x, sprite.y, it.sprite.x, it.sprite.y)] = it
                    }
                }
            }
        }
        if (distances.isEmpty()) {
            target = null
        } else {
            target = distances.toSortedMap().values.toList().first()
        }
    }

    fun unblockPosCoordinates(coordinatesAdd: Float = realSpeed()) : Pair<Float,Float>{
        val characterCoordinates = Pair(sprite.x,sprite.y)
        return when{
            isValidMainCharacterCoordinates(characterCoordinates.first+coordinatesAdd,characterCoordinates.second)->Pair(characterCoordinates.first+coordinatesAdd,characterCoordinates.second)
            isValidMainCharacterCoordinates(characterCoordinates.first-coordinatesAdd,characterCoordinates.second)->Pair(characterCoordinates.first-coordinatesAdd,characterCoordinates.second)
            isValidMainCharacterCoordinates(characterCoordinates.first,characterCoordinates.second+coordinatesAdd)->Pair(characterCoordinates.first,characterCoordinates.second+coordinatesAdd)
            isValidMainCharacterCoordinates(characterCoordinates.first,characterCoordinates.second-coordinatesAdd)->Pair(characterCoordinates.first,characterCoordinates.second-coordinatesAdd)
            else->unblockPosCoordinates(coordinatesAdd+realSpeed())
        }
    }

    fun isValidMainCharacterCoordinates(x: Float, y: Float) : Boolean{
        val yCheckValue = y + (sprite.boundingBox.bottom - sprite.boundingBox.top)/3
        return !game.map.inForbiddenArea(x,yCheckValue) && !inSolidCharacterBoundingBox(x,yCheckValue) && !inSolidSpriteBoundingBox(x,yCheckValue)
    }

    fun changeProjectileSkin(ndx: Int, animation : (Projectile)->Job =
        {
        projectile->GlobalScope.launch {
            return@launch
        }
        }
    ){
        projectile.sprite.ndx = ndx
        projectile.animation = animation
    }



}