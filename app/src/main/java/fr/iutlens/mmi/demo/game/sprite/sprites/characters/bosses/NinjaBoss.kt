package fr.iutlens.mmi.demo.game.sprite.sprites.characters.bosses

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.demo.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Boss
import fr.iutlens.mmi.demo.game.sprite.sprites.Projectile
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.degreesToRadiant
import fr.iutlens.mmi.demo.utils.getCenter
import fr.iutlens.mmi.demo.utils.getDistance
import fr.iutlens.mmi.demo.utils.rotationFromPoint
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.min
import kotlin.random.Random

class NinjaBoss(x: Float, y: Float, game: Game) : Boss(
    endCinematicParts = listOf(
        CinematicPart(
            "Élisa, la femme de chambre de Mme de Rênal, n’avait pas manqué de devenir amoureuse du jeune précepteur ; elle en parlait souvent à sa maîtresse. L’amour de Mlle Élisa avait valu à Julien la haine d’un des valets. Un jour, il entendit cet homme qui disait à Élisa : Vous ne voulez plus me parler depuis que ce précepteur crasseux est entré dans la maison. Julien ne méritait pas cette injure ; mais, par instinct de joli garçon, il redoubla de soins pour sa personne. La haine de M. Valenod redoubla aussi. Il dit publiquement que tant de coquetterie ne convenait pas à un jeune abbé. À la soutane près, c’était le costume que portait Julien.",
            R.drawable.cinematic_character,
            true,
            name = "???"
        ),
    ),
    sprite = BasicSprite(R.drawable.first_boss,x,y,0),
    game = game,
    basicAnimationSequence = listOf(0,1),
    speed = 0.06f,
    hearts = setBasicHearts(60),
    leftAnimationSequence = listOf(0,1),
    topAnimationSequence = listOf(0,1),
    bottomAnimationSequence = listOf(0,1),
    rightAnimationSequence = listOf(0,1),
    target = game.controllableCharacter!!,
) {

    val startCinematic = Cinematic(listOf(
        CinematicPart(
            "Élisa, la femme de chambre de Mme de Rênal, n’avait pas manqué de devenir amoureuse du jeune précepteur ; elle en parlait souvent à sa maîtresse. L’amour de Mlle Élisa avait valu à Julien la haine d’un des valets. Un jour, il entendit cet homme qui disait à Élisa : Vous ne voulez plus me parler depuis que ce précepteur crasseux est entré dans la maison. Julien ne méritait pas cette injure ; mais, par instinct de joli garçon, il redoubla de soins pour sa personne. La haine de M. Valenod redoubla aussi. Il dit publiquement que tant de coquetterie ne convenait pas à un jeune abbé. À la soutane près, c’était le costume que portait Julien.",
            R.drawable.cinematic_character,
            true,
            name = "???"
        ),
    ),game){
        randomPattern()
        game.musicTrack.value = R.raw.boss
    }

    val projectile : Projectile = Projectile(BasicSprite(R.drawable.projectiles, sprite.x, sprite.y,5), range = 4f, speed = 0.1f, friendly = false, damages =  0.5f, knockback = 0.2f)
    override fun copy() : NinjaBoss{
        return NinjaBoss(sprite.x,sprite.y, game)
    }

    var pattern : Int  ? = null

    val patternTime = 5000L

    var patternCountdown = GlobalScope.launch {  }

    val contactDamages = 1f

    val contactKnockback = 0.2f

    override fun spawn(x: Float, y:Float){
        game.ath["boss"] = hearts
        game.addCharacter(this,true)
        changePos(x,y)
        game.cinematic.value = Pair(
            startCinematic,true
        )
    }

    fun randomPattern(){
        action.cancel()
        disablePathFollowing()
        patternCountdown.cancel()
        reflect = false
        sprite.normalColor()
        game.invalidate()
        val patterns = listOf<()->Unit>(
            {
                attackPlayer()
                setPatternCountdown()
            },
            {
                blind()
            },
            {
                setPatternCountdown()
                reflectShots()
            },
            {
                dashToCenter()
            }
        )
        var patternIndex = (patterns.indices).random()
        while (patternIndex == pattern){
            patternIndex = (patterns.indices).random()
        }
        pattern = patternIndex
        patterns[patternIndex].invoke()
    }
    fun reflectShots(){
        reflect = true;
        sprite.reverseColor()
        game.invalidate()
        attackPlayer()
    }

    fun attackPlayer(){
        disablePathFollowing()
        action.cancel()
        if(!game.ended) {
            action = setInterval(0, 100) {
                if (!inBoundingBox(target!!.sprite.x, target!!.sprite.y)) {
                    moveTo(target!!.sprite.x, target!!.sprite.y)
                    if (!isPathFree(target!!.sprite.x, target!!.sprite.y)) {
                        followPlayer()
                    }
                } else {
                    target!!.healthDown(contactDamages, contactKnockback, currentDirection)
                    randomPattern()
                }
            }
        }
    }

    fun followPlayer(){
        action.cancel()
        setupPath(target!!.sprite.x, target!!.sprite.y)
        pathFollow = true
        action = setInterval(0,100){
            if(isPathFree(target!!.sprite.x, target!!.sprite.y) || !pathFollow){
                attackPlayer()
            }
        }
    }

    fun setPatternCountdown(){
        patternCountdown = GlobalScope.launch {
            delay(patternTime)
            randomPattern()
        }
    }

    fun blind(){
        game.blinded = true
        targetable = false
        game.controllableCharacter!!.totalBlind()
        invisible {
            targetable = true
            game.controllableCharacter!!.recoverView()
            dashPosition()
        }
    }

    fun dashPosition(){
        val minMaxCoordinates = game.map.currentRoom().getMinMaxCoordinates()
        val targetCoordinates = Pair(
            target!!.sprite.x,
            target!!.sprite.y
        )
        val horizontal = Random.nextBoolean()
        when(horizontal){
            true->{
                val left = Random.nextBoolean()
                val startCoordinates = when(left){
                    true->Pair(
                        minMaxCoordinates.first.first,
                        targetCoordinates.second
                    )
                    else -> Pair(
                        minMaxCoordinates.second.first,
                        targetCoordinates.second
                    )
                }
                val endCoordinates = when(left){
                    true->Pair(
                        minMaxCoordinates.second.first,
                        targetCoordinates.second
                    )
                    else -> Pair(
                        minMaxCoordinates.first.first,
                        targetCoordinates.second
                    )
                }
                dash(startCoordinates,endCoordinates){
                    game.blinded = false
                    land()
                }
            }
            else->{
                val top = Random.nextBoolean()
                val startCoordinates = when(top){
                    true->Pair(
                        targetCoordinates.first,
                        minMaxCoordinates.first.second
                    )
                    else->Pair(
                        targetCoordinates.first,
                        minMaxCoordinates.second.second
                    )
                }
                val endCoordinates = when(top){
                    true->Pair(
                        targetCoordinates.first,
                        minMaxCoordinates.second.second
                    )
                    else->Pair(
                        targetCoordinates.first,
                        minMaxCoordinates.first.second
                    )
                }
                dash(startCoordinates,endCoordinates){
                    game.blinded = false
                    land()
                }
            }
        }
    }

    fun dash(startCoordinates : Pair<Float,Float>, endCoordinates : Pair<Float,Float>, dashSpeed : Float = 0.3f, action : ()->Unit){
        intangible = true
        changePos(startCoordinates.first,startCoordinates.second)
        val dashDelay = 500L
        val dashCheckDelay = 33L
        val oldSpeed = speed
        speed = dashSpeed
        GlobalScope.launch {
            delay(dashDelay)
            while (sprite.x != endCoordinates.first || sprite.y != endCoordinates.second){
                moveTo(endCoordinates.first,endCoordinates.second)
                if(inBoundingBox(target!!.sprite.x, target!!.sprite.y)){
                    target!!.healthDown(contactDamages, contactKnockback, currentDirection)
                }
                delay(dashCheckDelay)
            }
            speed = oldSpeed
            action()
        }
    }

    fun land(){
        val globalIndex = game.map.getMapIndexFromPosition(sprite.x, sprite.y)
        val closestAvailableTile = closestAvailableTile(globalIndex.first, globalIndex.second)
        val closestAvailableTileCoordinates = game.map.getPositionFromMapIndex(closestAvailableTile!!.first, closestAvailableTile!!.second)
        val landCheckDelay = 33L
        GlobalScope.launch {
            while (game.map.inForbiddenArea(sprite.x,sprite.y)){
                moveTo(closestAvailableTileCoordinates.first, closestAvailableTileCoordinates.second)
                delay(landCheckDelay)
            }
            targetable = true
            intangible = false
            flying = false
            randomPattern()
        }
    }

    fun invisible(action : ()->Unit){
        val invisibleStep = 0.25f
        var invisibleValue = 1f
        val stepDelay = 33L
        val startDelay = 750L
        val endDelay = 1000L
        GlobalScope.launch {
            delay(startDelay)
            while (invisibleValue>=0f){
                sprite.setTransparencyLevel(invisibleValue)
                game.invalidate()
                invisibleValue-=invisibleStep
                delay(stepDelay)
            }
            sprite.invisible()
            delay(endDelay)
            sprite.setTransparencyLevel(1f)
            sprite.visible()
            action()
        }
    }

    fun dashToCenter(){
        val delay = 500L
        val roomCenter = game.map.currentRoom().getRoomCenter()
        GlobalScope.launch {
            delay(delay)
            dash(
                Pair(
                    sprite.x,
                    sprite.y
                ),
                Pair(
                    roomCenter.first,
                    roomCenter.second
                ),
                dashSpeed = speed
            ){
                projectiles()
            }
        }
    }

    fun projectiles(){
        val projectilesDelay = 1000L
        GlobalScope.launch {
            repeat(3){
                projectileWave()
                delay(projectilesDelay)
            }
            randomPattern()
        }
    }

    fun projectileWave(){
        val center = getCenter(target!!.sprite.x, target!!.sprite.y, sprite.x, sprite.y)
        var radiant = 0f
        val radiantStep = (2* PI)/6
        val limit = 2* PI
        while (radiant<=limit){
            val projectileDirection = rotationFromPoint(
                target!!.sprite.x,
                target!!.sprite.y,
                center[0],
                center[1],
                (PI / 2).toFloat()
            )
            projectile.fireProjectile(game,sprite.x,sprite.y,projectileDirection[0],projectileDirection[1])
            radiant = (radiant+radiantStep).toFloat()
        }
    }

}