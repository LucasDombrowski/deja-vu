package fr.iutlens.mmi.demo.game.sprite.sprites.characters

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.game.sprite.sprites.Projectile
import fr.iutlens.mmi.demo.utils.getCenter
import fr.iutlens.mmi.demo.utils.getDistance
import fr.iutlens.mmi.demo.utils.rotationFromPoint
import fr.iutlens.mmi.demo.utils.setInterval
import kotlin.math.PI

class RangeNinja(x: Float, y:Float, game: Game) : Enemy(
    sprite = BasicSprite(R.drawable.isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(1),
    speed = 0.05f,
    hearts = setBasicHearts(6),
    leftAnimationSequence = listOf(3,4,5),
    topAnimationSequence = listOf(9,10,11),
    bottomAnimationSequence = listOf(0,1,2),
    rightAnimationSequence = listOf(6,7,8),
    target = game.controllableCharacter!!,
    fireRate = 3000
){
    val projectile : Projectile = Projectile(BasicSprite(R.drawable.tear, sprite.x, sprite.y), range = 4f, speed = 0.4f, friendly = false, damages =  0.5f, knockback = 0.2f)
    override var action = setInterval(0,fireRate){
        if(getDistance(sprite.x, sprite.y, target!!.sprite.x, target!!.sprite.y)>projectile.range){
            moveTo(target!!.sprite.x, target!!.sprite.y)
        } else {
            movingAction.cancel()
            val center = getCenter(target!!.sprite.x, target!!.sprite.y, sprite.x, sprite.y)
            val firstProjectile = rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1], (PI/6).toFloat())
            val secondProjectile = rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1],(-PI/6).toFloat())
            projectile.aimTarget(target!!, sprite.x, sprite.y)
            projectile.fireProjectile(game,sprite.x, sprite.y, firstProjectile[0], firstProjectile[1])
            projectile.fireProjectile(game,sprite.x,sprite.y,secondProjectile[0],secondProjectile[1])
        }
        if(target!!.inBoundingBox(sprite.x, sprite.y)){
            target!!.healthDown(projectile.damages, 20f, currentDirection)
        }
    }
}