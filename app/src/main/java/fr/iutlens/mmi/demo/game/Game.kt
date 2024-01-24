package fr.iutlens.mmi.demo.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.MutableSpriteList
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.TileMap
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.MainCharacter
import fr.iutlens.mmi.demo.game.transform.CameraTransform
import kotlinx.coroutines.delay
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
           val spriteList : MutableSpriteList,
           val transform: CameraTransform,
           var onDragStart: ((Offset) -> Unit)? = null,
           var onDragMove:  ((Offset) -> Unit)? = null,
           var onTap: ((Offset)-> Unit)? = null){

    val timeSource = TimeSource.Monotonic

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
    var animationDelayMs: Int? = null

    /**
     * Update : action à réaliser entre deux images
     */
    var update: ((Game)-> Unit)? = null

    /**
     * Invalidate demande une nouvelle image, en général parce que les données du jeu ont changé
     */
    fun invalidate() {
        elapsed = (timeSource.markNow() - start).inWholeMilliseconds
    }

    fun setupControllableCharacter(){
        val character = MainCharacter(x = 1f*((map.w*map.sizeX)/2), y = 1f*((map.h*map.sizeY)/2), game = this)
        spriteList.add(character.sprite)
        onTap = {
            (x,y)->
            character.moveTo(x,y)
        }
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
            animationDelayMs?.let {delay ->
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
}

