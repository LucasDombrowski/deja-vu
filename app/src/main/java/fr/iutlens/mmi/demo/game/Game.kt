package fr.iutlens.mmi.demo.game

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.game.ath.Hearts
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.MutableSpriteList
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.game.sprite.sprites.Character
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.MainCharacter
import fr.iutlens.mmi.demo.game.transform.CameraTransform
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.TimeSource

/**
 * Game regroupe les éléments constitutifs d'un jeu
 *
 * @property background Le sprite à afficher en le fond
 * @property spriteList Les sprites à afficher (dans l'ordre de la liste)
 * @property transform La transformation (changement de coordonnées, déplacement) à appliquer
 * @property onDragStart Action à réaliser quand commence un drag and drop. Icompatible avec onTap
 * @property onDragMove Action à réaliser quand on bouge pendant le drag and drop. Incompatible avec onTap
 * @property onTap Action à réaliser quand on clique. Incompatible avec onDrag
 * @constructor Créé un jeu définit par sprite de fond (background), une liste de sprite à afficher
 * par dessus (spriteList) et un point de vue (transform)
 * Il est possible de préciser en plus les interactions (onDrag/onTap)
 */
class Game(val background : Sprite,
           val map : TiledArea,
           var spriteList : MutableSpriteList,
           var controllableCharacter : MainCharacter ?=null,
           val transform: CameraTransform,
           var onDragStart: ((Offset) -> Unit)? = null,
           var onDragMove:  ((Offset) -> Unit)? = null,
           var onTap: ((Offset)-> Unit)? = null){

    val timeSource = TimeSource.Monotonic

    var refresh = false
    /**
     * Start Instant du début du jeu, utiliser pour calculer le temps écoulé
     */
    val start = timeSource.markNow()

    /**
     * Elapsed Mesure le temps écoulé entre début du jeu et la dernière demande d'affichage
     */
    var elapsed by mutableLongStateOf(0L)

    /**
     * Nombre de milliseconde souhaité entre deux images
     */
    var animationDelayMs: Int = 33

    /**
     * Update : action à réaliser entre deux images
     */
    var update: ((Game)-> Unit)? = null


    var characterList : MutableList<Character> = mutableListOf()
    /**
     * Invalidate demande une nouvelle image, en général parce que les données du jeu ont changé
     */
    fun invalidate() {
        elapsed = (timeSource.markNow() - start).inWholeMilliseconds
    }

    fun setupControllableCharacter(){
        controllableCharacter = MainCharacter(x = 1f*((map.w*map.sizeX)/2), y = 1f*((map.h*map.sizeY)/2), game = this)
        addCharacter(controllableCharacter!!)
        ath["hearts"] = controllableCharacter!!.hearts
        onTap = {
            (x,y)->
            for(character in characterList){
                if(character.inBoundingBox(x,y) && character is Enemy){
                    controllableCharacter!!.target = character
                }
            }
                controllableCharacter!!.moveTo(x,y)
        }
    }

    fun addCharacter(character: Character){
        addSprite(character.sprite)
        characterList.add(character)
    }

    fun deleteCharacter(character: Character){
        characterList.remove(character)
    }

    fun addSprite(sprite: BasicSprite){
        spriteList.add(sprite)
    }

    fun deleteSprite(sprite : BasicSprite){
        spriteList.remove(sprite)
    }

    fun refresh(){
        spriteList = spriteList.copy()
    }




    /**
     * View
     * Composant affichant le jeu décrit dans cette classe
     * Si des actions sont prévues pour des clics ou le drag and drop, elles sont aussi configurées
     * Si un rafraîchissement automatique est prévu (update et animationDelayMS non nuls), il est planifié
     *
     * @param modifier
     */
    @Composable
    fun View(modifier: Modifier) {

        // gestion des évènements
        Canvas(modifier = modifier.pointerInput(key1 = this) {
            if (onTap!= null) detectTapGestures {
                onTap?.invoke(transform.getPoint(it))
                invalidate()
            }
            if (onDragMove!= null) detectDragGestures(onDragStart = {
                onDragStart?.invoke(transform.getPoint(it))
            }) { change, dragAmount ->
                onDragMove?.invoke(transform.getPoint(change.position))
            }
        }) {
            // Dessin proprement dit. On précise la transformation à appliquer avant
            this.withTransform({ transform(transform.getMatrix(size)) }) {
                background.paint(this, elapsed)
                spriteList.paint(this, elapsed)
            }
        }
        // Gestion du rafraîssement automatique si update et animationDelay sont défnis
        update?.let{myUpdate->
            animationDelayMs.let {delay ->
                LaunchedEffect(elapsed){
                    //Calcul du temps avant d'afficher la prochaine image, et pause si nécessaire)
                    val current = (timeSource.markNow()-start).inWholeMilliseconds
                    val next = elapsed+ delay
                    if (next>current) delay(next-current)
                    myUpdate(this@Game)
                }
            }
        }


    }

    var ath = mutableStateMapOf("hearts" to mutableListOf<Heart>())
    @Composable
    fun Ath(){
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight().padding(20.dp)){
            ath["hearts"]?.let { Hearts(hearts = it) }
        }
    }
}

