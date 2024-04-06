package fr.iutlens.mmi.dejaVu.game.sprite.sprites.characters.bosses

import android.util.Log
import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.MainCharacterPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.NarratorPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.NinjaBossPart
import fr.iutlens.mmi.dejaVu.game.sprite.BasicSprite
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.Boss
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.Projectile
import fr.iutlens.mmi.dejaVu.utils.Music
import fr.iutlens.mmi.dejaVu.utils.rotationFromPoint
import fr.iutlens.mmi.dejaVu.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.random.Random

class NinjaBoss(x: Float, y: Float, game: Game) : Boss(
    endCinematicParts = listOf(
        NinjaBossPart("Petit effronté, comment as-tu pu ?", true),
        MainCharacterPart("… Tu ne m’en as pas laissé le choix, j’ai uniquement répondu à ta provocation."),
        NinjaBossPart("Tu n’as pas conscience des conséquences qu’auront tes actes.", true),
        NarratorPart("Ainsi sont les derniers mots du Corbeau. Notre héros, grand vainqueur de ce combat acharné, s’éloigna lentement du corps de son ennemi.")
    ),
    sprite = BasicSprite(R.drawable.first_boss,x,y,0),
    game = game,
    basicAnimationSequence = listOf(0,1),
    speed = 0.07f,
    hearts = setBasicHearts(60),
    leftAnimationSequence = listOf(0,1),
    topAnimationSequence = listOf(25,26,27,28,29),
    bottomAnimationSequence = listOf(20,21,22,23,24),
    rightAnimationSequence = listOf(5,6),
    target = game.controllableCharacter!!,
    image = R.drawable.idle_first_boss
) {


    val startCinematic = Cinematic(listOf(
        MainCharacterPart("Quel silence.."),
        NarratorPart("Un vent fort brusqua Blaise, il sentit alors une main se poser sur son épaule. L’atmosphère changea brutalement rendant le corps de Blaise vulnérable."),
        MainCharacterPart("Qui êtes-vous ?"),
        NinjaBossPart("Je suis le maître de ces lieux, on me surnomme le Corbeau, décline ton identité soldat."),
        MainCharacterPart("Moi ? Comment dire.. je ne suis qu’un simple voyageur…"),
        NinjaBossPart("Un voyageur armé, pour qui me prenez-vous pauvre fou ?  Soldats, éliminez cet intrus !",true),
        NarratorPart("Un silence envahit la salle."),
        NinjaBossPart("Pourquoi personne ne répond?! Ne me dis pas.. tu les as tous tués? Très bien, je vais te tuer de mes propres mains.", true)
    ),game){
        game.screenEffect = {}
        game.blinded = false
        game.controllableCharacter!!.recoverView()
        game.musicTrack.value = R.raw.boss
        game.ath["boss"] = hearts
        GlobalScope.launch {
            delay(patternTime/5)
            randomPattern()
        }
    }


    val projectile : Projectile = Projectile(BasicSprite(R.drawable.projectiles, sprite.x, sprite.y,5), range = 8f, speed = 0.1f, friendly = false, damages =  0.5f, knockback = 0.2f, sound = R.raw.ninja_shot)
    override fun copy() : NinjaBoss{
        return NinjaBoss(sprite.x,sprite.y, game)
    }

    var pattern : Int  ? = null

    val patternTime = 5000L

    var patternCountdown = GlobalScope.launch {  }

    val contactDamages = 1f

    val contactKnockback = 0.5f

    val pauseCheckDelay = 33L

    override fun spawn(x: Float, y:Float){
        game.addCharacter(this,true)
        changePos(x,y)
        game.blinded = true
        game.controllableCharacter!!.totalBlind()
        game.cinematic.value = Pair(
            startCinematic,true
        )
    }

    fun attackAnimation(){
        if(!target!!.remainingInvulnerability) {
            val soundVolume = 0.25f
            Music.playSound(R.raw.ninja_boss_attack, leftVolume = soundVolume, rightVolume = soundVolume)
            animate = false
            val leftAttackAnimationSequence = listOf<Int>(30, 31, 32, 33, 34)
            val rightAttackAnimationSequence = listOf<Int>(35, 36, 37, 38, 39)
            val xDifferences = sprite.x - game.controllableCharacter!!.sprite.x
            val animationSequence = when {
                xDifferences > 0 -> rightAttackAnimationSequence
                else -> leftAttackAnimationSequence
            }
            GlobalScope.launch {
                for (image in animationSequence) {
                    sprite.ndx = image
                    delay(animationDelay)
                }
                animate = true
            }
        }
    }

    fun randomPattern(){
        action.cancel()
        disablePathFollowing()
        patternCountdown.cancel()
        reflect = false
        sprite.normalColor()
        game.invalidate()
        if(alive) {
            val patterns = listOf<() -> Unit>(
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
            while (patternIndex == pattern) {
                patternIndex = (patterns.indices).random()
            }
            Log.i("pattern index","$patternIndex")
            pattern = patternIndex
            patterns[patternIndex].invoke()
        }
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
                    attackAnimation()
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
            while (game.pause){
                delay(pauseCheckDelay)
            }
            randomPattern()
        }
    }

    fun blind(){
        game.blinded = true
        targetable = false
        game.controllableCharacter!!.totalBlind()
        invisible {
            game.controllableCharacter!!.recoverView()
            sprite.normalColor()
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
                        minMaxCoordinates.first.first-game.map.tileArea.w,
                        targetCoordinates.second
                    )
                    else -> Pair(
                        minMaxCoordinates.second.first+game.map.tileArea.w,
                        targetCoordinates.second
                    )
                }
                val endCoordinates = when(left){
                    true->Pair(
                        minMaxCoordinates.second.first+game.map.tileArea.w,
                        targetCoordinates.second
                    )
                    else -> Pair(
                        minMaxCoordinates.first.first-game.map.tileArea.w,
                        targetCoordinates.second
                    )
                }
                sprite.ndx = when(left){
                    true->leftAnimationSequence.first()
                    else->rightAnimationSequence.first()
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
                        minMaxCoordinates.first.second-game.map.tileArea.h
                    )
                    else->Pair(
                        targetCoordinates.first,
                        minMaxCoordinates.second.second+game.map.tileArea.h
                    )
                }
                val endCoordinates = when(top){
                    true->Pair(
                        targetCoordinates.first,
                        minMaxCoordinates.second.second+game.map.tileArea.h
                    )
                    else->Pair(
                        targetCoordinates.first,
                        minMaxCoordinates.first.second-game.map.tileArea.h
                    )
                }
                sprite.ndx = when(top){
                    true->topAnimationSequence.first()
                    else->bottomAnimationSequence.first()
                }
                dash(startCoordinates,endCoordinates){
                    game.blinded = false
                    land()
                }
            }
        }
    }

    fun dash(startCoordinates : Pair<Float,Float>, endCoordinates : Pair<Float,Float>, dashSpeed : Float = 0.5f, action : ()->Unit){
        intangible = true
        changePos(startCoordinates.first,startCoordinates.second)
        val dashDelay = 500L
        val dashCheckDelay = 33L
        val oldSpeed = speed
        speed = dashSpeed
        GlobalScope.launch {
            delay(dashDelay)
            while (sprite.x.roundToInt() != endCoordinates.first.roundToInt() || sprite.y.roundToInt() != endCoordinates.second.roundToInt()){
                moveTo(endCoordinates.first,endCoordinates.second)
                if(inBoundingBox(target!!.sprite.x, target!!.sprite.y)){
                    attackAnimation()
                    target!!.healthDown(contactDamages, contactKnockback, currentDirection)
                }
                Log.i("currently dash","true")
                delay(dashCheckDelay)
            }
            speed = oldSpeed
            action()
        }
    }

    fun land(){
        val landCheckDelay = 33L
        GlobalScope.launch {
            while (game.map.inForbiddenArea(sprite.x,sprite.y)){
                val globalIndex = game.map.getMapIndexFromPosition(sprite.x, sprite.y)
                val closestAvailableTile = closestAvailableTile(globalIndex.first, globalIndex.second)
                val closestAvailableTileCoordinates = game.map.getPositionFromMapIndex(closestAvailableTile!!.first, closestAvailableTile!!.second)
                val aimedCoordinates = Pair(
                    closestAvailableTileCoordinates.first+game.map.tileArea.w/2,
                    closestAvailableTileCoordinates.second+game.map.tileArea.h/2
                )
                moveTo(aimedCoordinates.first, aimedCoordinates.second)
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
        val soundVolume = 0.25f
        Music.playSound(R.raw.ninja_boss_dash, leftVolume = soundVolume, rightVolume = soundVolume)
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
                dashSpeed = speed*2
            ){
                projectiles()
            }
        }
    }

    fun projectiles(){
        val projectilesDelay = 1500L
        GlobalScope.launch {
            repeat(3){
                while (game.pause){
                    delay(pauseCheckDelay)
                }
                if(alive) {
                    projectileWave()
                    delay(projectilesDelay)
                }
            }
            land()
        }
    }

    fun projectileWave(){
        var radiant = 0f
        val radiantStep = (2*PI)/8
        val limit = 2*PI
        while (radiant<=limit){
            val projectileDirection = rotationFromPoint(
                sprite.x,
                sprite.y,
                target!!.sprite.x,
                target!!.sprite.y,
                radiant
            )
            projectile.fireProjectile(game,sprite.x,sprite.y,projectileDirection[0],projectileDirection[1])
            radiant = (radiant+radiantStep).toFloat()
        }
    }

}