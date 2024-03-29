package fr.iutlens.mmi.demo.game.levels

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Item
import fr.iutlens.mmi.demo.game.gameplayResources.items.NinjaBoots
import fr.iutlens.mmi.demo.game.gameplayResources.items.NinjaScarf
import fr.iutlens.mmi.demo.game.gameplayResources.items.NinjaShuriken
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.demo.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.demo.game.screens.screenEffects.Fog
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.CloseNinja
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.RangeNinja
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.TeleportNinja
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.bosses.NinjaBoss
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Ninja : Game(
    map = Map(
        roomInterval = 8..10,
        drawable = R.drawable.ninja_level,
        treasureRooms = 1
    ),
    backgroundMusic = R.raw.first_level,
    screenEffect = {
        Fog(animate = it)
    }
) {
    init {
        map.enemies = listOf(
            CloseNinja(0f,0f,this),
            RangeNinja(0f,0f,this),
            TeleportNinja(0f,0f,this)
        )
        items += mutableListOf<Item>(
            NinjaScarf(),
            NinjaShuriken(),
            NinjaBoots()
        )
        map.boss = NinjaBoss(0f,0f,this)
        controllableCharacter!!.changeProjectileSkin(
            4
        ){
            projectile -> setInterval(0,33){
                if(projectile.sprite.rotate>=360f){
                    projectile.sprite.rotate = 0f
                } else {
                    projectile.sprite.rotate+=36f
                }
            }
        }
        cinematic.value = Pair(
            Cinematic(
                listOf(
                    CinematicPart(
                        "Élisa, la femme de chambre de Mme de Rênal, n’avait pas manqué de devenir amoureuse du jeune précepteur ; elle en parlait souvent à sa maîtresse. L’amour de Mlle Élisa avait valu à Julien la haine d’un des valets. Un jour, il entendit cet homme qui disait à Élisa : Vous ne voulez plus me parler depuis que ce précepteur crasseux est entré dans la maison. Julien ne méritait pas cette injure ; mais, par instinct de joli garçon, il redoubla de soins pour sa personne. La haine de M. Valenod redoubla aussi. Il dit publiquement que tant de coquetterie ne convenait pas à un jeune abbé. À la soutane près, c’était le costume que portait Julien.",
                        R.drawable.cinematic_character,
                        true,
                        name = "Blaise"
                    )
                ),
                this
            ){
             Music.mute = false
            }, true
        )
        GlobalScope.launch {
            delay(5000)
            switchRoom(map.rooms!!.size-2)
        }
    }

    override fun copy() : Ninja{
        return Ninja()
    }
}