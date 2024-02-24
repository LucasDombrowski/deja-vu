package fr.iutlens.mmi.demo.game

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.components.DialogScreen
import fr.iutlens.mmi.demo.game.ath.BossBar
import fr.iutlens.mmi.demo.game.ath.Coins
import fr.iutlens.mmi.demo.game.ath.Hearts
import fr.iutlens.mmi.demo.game.gameplayResources.Chest
import fr.iutlens.mmi.demo.game.gameplayResources.Collectible
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.gameplayResources.Item
import fr.iutlens.mmi.demo.game.gameplayResources.items.LessFireRateLessDamages
import fr.iutlens.mmi.demo.game.gameplayResources.items.MoreDamagesMoreRate
import fr.iutlens.mmi.demo.game.map.Camera
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.rooms.LargeRoom
import fr.iutlens.mmi.demo.game.map.rooms.LongRoom
import fr.iutlens.mmi.demo.game.map.rooms.ShopRoom
import fr.iutlens.mmi.demo.game.map.rooms.TreasureRoom
import fr.iutlens.mmi.demo.game.screens.ItemImage
import fr.iutlens.mmi.demo.game.screens.MenuButton
import fr.iutlens.mmi.demo.game.screens.MenuItem
import fr.iutlens.mmi.demo.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.MutableSpriteList
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Character
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.MainCharacter
import fr.iutlens.mmi.demo.game.transform.CameraTransform
import fr.iutlens.mmi.demo.game.transform.FitTransform
import fr.iutlens.mmi.demo.game.transform.FocusTransform
import fr.iutlens.mmi.demo.ui.theme.Dogica
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
open class Game(val map : Map,
           var spriteList : MutableSpriteList = MutableSpriteList(list = mutableListOf()),
           var controllableCharacter : MainCharacter ?=null,
           var transform: CameraTransform = FitTransform(map.tileArea),
           var onDragStart: ((Offset) -> Unit)? = null,
           var onDragMove:  ((Offset) -> Unit)? = null,
           var onTap: ((Offset)-> Unit)? = null){

    var background = map.tileArea
    val camera = Camera(this)
    val timeSource = TimeSource.Monotonic
    val items = mutableListOf<Item>(
        LessFireRateLessDamages(),
        MoreDamagesMoreRate()
    )

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
    var animationDelayMs: Int ?= null

    /**
     * Update : action à réaliser entre deux images
     */
    var update: ((Game)-> Unit) ? = null


    var characterList : MutableList<Character> = mutableListOf()

    var collectibleList : MutableList<Collectible> = mutableListOf()

    var onEnd : ()->Unit = {}
    fun copyCharacterList() : MutableList<Character>{
        return characterList.toMutableList()
    }

    fun resetCollectibles(){
        with(collectibleList.iterator()){
            forEach {
                it.destroy()
            }
        }
    }
    /**
     * Invalidate demande une nouvelle image, en général parce que les données du jeu ont changé
     */
    fun invalidate() {
        elapsed = (timeSource.markNow() - start).inWholeMilliseconds
    }

    fun setupControllableCharacter(){
        controllableCharacter = MainCharacter(x = map.characterStartPosition().first, y = map.characterStartPosition().second, game = this)
        addCharacter(controllableCharacter!!)
        ath["hearts"] = controllableCharacter!!.hearts
        addSprite(controllableCharacter!!.targetIndicator)
        addSprite(controllableCharacter!!.pathIndicator)
        controllableCharacter!!.targetIndicator.invisible()
        controllableCharacter!!.pathIndicator.invisible()
        setupControls()
    }

    fun setupControls(){
        onTap = {
            (x,y)->
            if(!pause) {
                var targetChange = false
                for (character in characterList) {
                    if (character.inBoundingBox(x, y) && character is Enemy) {
                        targetChange = true
                        if (character != controllableCharacter!!.target) {
                            controllableCharacter!!.target = character
                            controllableCharacter!!.setupTargetFollow()
                        } else {
                            controllableCharacter!!.target = null
                        }
                    }
                }
                if (!targetChange) {
                    controllableCharacter!!.tapMovingBehavior(x, y)
                }
            }
        }
        onDragMove = {
            (x,y)->
            controllableCharacter!!.dragMovingBehavior(x, y)
        }
    }
    fun addCharacter(character: Character){
        addSprite(character.sprite)
        characterList.add(character)
    }

    fun deleteCharacter(character: Character){
        characterList.remove(character)
    }

    fun addSprite(sprite: Sprite){
        spriteList.add(sprite)
    }

    fun sortSpriteList(){
        spriteList.sortBy {
            it == controllableCharacter!!.sprite
        }
    }

    fun deleteSprite(sprite : Sprite){
        if(sprite is BasicSprite) {
            sprite.invisible()
        }
        spriteList.remove(sprite)
    }
    fun switchRoom(ndx : Int){
        controllableCharacter!!.stun()
        map.currentRoom = ndx
        if(map.currentRoom() is TreasureRoom){
            map.currentRoom().open()
            val chest = Chest(items)
            chest.setup(
                map.currentRoom().getRoomCenter().first,
                map.currentRoom().getRoomCenter().second,
                this
            )
        }
        if(map.currentRoom() is ShopRoom){
            map.currentRoom().open()
            (map.currentRoom() as ShopRoom).setup(this)
        }
        if(map.currentRoom() is LargeRoom){
            when((map.currentRoom() as LargeRoom).enterSide){
                "top"->camera.moveTo(
                    (map.currentRoom() as LargeRoom).getFirstHalfCenter().first,
                    (map.currentRoom() as LargeRoom).getFirstHalfCenter().second
                )
                else->camera.moveTo(
                    (map.currentRoom() as LargeRoom).getSecondHalfCenter().first,
                    (map.currentRoom() as LargeRoom).getSecondHalfCenter().second
                )
            }

        }
        if(map.currentRoom() is LongRoom){
            when((map.currentRoom() as LongRoom).enterSide){
                "left"->camera.moveTo(
                    (map.currentRoom() as LongRoom).getFirstHalfCenter().first,
                    (map.currentRoom() as LongRoom).getFirstHalfCenter().second
                )
                else->camera.moveTo(
                    (map.currentRoom() as LongRoom).getSecondHalfCenter().first,
                    (map.currentRoom() as LongRoom).getSecondHalfCenter().second
                )
            }

            Log.i("enterSide/exitSide","${(map.currentRoom() as LongRoom).enterSide}/${(map.currentRoom() as LongRoom).exitSide}")
        }
        if(map.currentRoom() !is LongRoom && map.currentRoom() !is LargeRoom){
            camera.moveTo(
                map.currentRoom().getRoomCenter().first,
                map.currentRoom().getRoomCenter().second
            )
        }
    }

    fun nextRoom(){
        map.rooms!!.forEach {
            Log.i("corners","${it.topLeftCorner},${it.bottomRightCorner}")
        }
        Log.i("map string","${map.mapString}")
        if(map.currentRoom+1<map.rooms!!.size){
            switchRoom(map.currentRoom+1)
        }
    }

    fun reloadBackground(){
        background = map.tileArea
    }

    fun spawnBoss(){
        val minMaxCoordinates = map.currentRoom().getMinMaxCoordinates()
        var xVal = map.currentRoom().getRoomCenter().first
        var yVal = map.currentRoom().getRoomCenter().second
        while(map.inForbiddenArea(xVal,yVal)){
            xVal = (minMaxCoordinates.first.first + Math.random() * (minMaxCoordinates.second.first - minMaxCoordinates.first.first)).toFloat()
            yVal = (minMaxCoordinates.first.second + Math.random() * (minMaxCoordinates.second.second - minMaxCoordinates.first.second)).toFloat()
        }
        map.boss!!.spawn(xVal,yVal)
    }


    open fun copy() : Game{
        return Game(
            map = map
        )
    }

    var pause = false;
    /**
     * View
     * Composant affichant le jeu décrit dans cette classe
     * Si des actions sont prévues pour des clics ou le drag and drop, elles sont aussi configurées
     * Si un rafraîchissement automatique est prévu (update et animationDelayMS non nuls), il est planifié
     *
     * @param modifier
     */
    @Composable
    fun View(modifier: Modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {

        // gestion des évènements
        Canvas(modifier = modifier
            .pointerInput(key1 = this) {
                if (onTap != null) detectTapGestures {
                    onTap?.invoke(transform.getPoint(it))
                }
            }
            .pointerInput(key1 = this) {
                if (onDragMove != null) detectDragGestures(onDragStart = {
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

    var ath = mutableStateMapOf("hearts" to mutableListOf<Heart>(), "boss" to mutableListOf<Heart>())
    var coins = mutableStateOf(10)
    @Composable
    fun Ath(){
        val configuration = LocalConfiguration.current
        val screenWidth = with(configuration){
            this.screenWidthDp
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        ath["hearts"]?.let { Hearts(hearts = it) }
                        Spacer(modifier = Modifier.height(10.dp))
                        Coins(n = coins)
                    }

                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier
                        .width((screenWidth / 15).dp)
                        .aspectRatio(1f)
                        .clickable {
                            menu["open"] = true
                            pause = true;
                        }
                        .background(Color.White, shape = CircleShape)
                        .padding((screenWidth / 150).dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.home_icon),
                            contentDescription = "Menu",
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            if(ath["boss"]!!.isNotEmpty()){
                BossBar(hearts = ath["boss"]!!)
            }
        }
    }

    var item = mutableStateMapOf<String,Any>("show" to false, "image" to 0, "name" to "", "description" to "")
    @Composable
    fun Item(modifier: Modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        ){
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        val screenWidth = with(configuration){
            this.screenWidthDp
        }
        val fontSize = with(density){
            (screenWidth/50).sp
        }
        controllableCharacter!!.currentAnimationSequenceIndex = 0
        DialogScreen(text = item["description"] as String, onEnd = {
            item["show"] = false
            pause = false
        }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ItemImage(id = item["image"] as Int, item["name"] as String, (screenWidth*0.15).dp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = item["name"] as String,
                    color = Color.White,
                    fontSize = fontSize,
                    style = TextStyle(
                        fontFamily = Dogica,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    var menu = mutableStateMapOf("open" to false)
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun Menu(modifier: Modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()){
        controllableCharacter!!.currentAnimationSequenceIndex = 0
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val screenHeight = configuration.screenHeightDp
        val bookWidth = when{
            screenWidth>screenHeight->(screenHeight + (screenWidth-screenHeight)/4).dp
            else->(screenWidth + (screenHeight-screenWidth)/4).dp
        }
        var item : MutableState<Item?> = remember {
            mutableStateOf(null)
        }

        @Composable
        fun LeftPage(content : @Composable ()->Unit){
            Box(modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(),
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth(0.59f)
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                ) {
                    val columnWidth = this.maxWidth
                    FlowColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .align(Alignment.Center)
                            .offset(y = columnWidth / 20)
                    ){
                        content()
                    }
                }
            }
        }

        @Composable
        fun Items(){
            val reversedItem = controllableCharacter!!.items.reversed()
            for(i in 0..24){
                if(reversedItem.size>i){
                    MenuItem(id = reversedItem[i].image, imageDescription = reversedItem[i].name){
                        item.value = reversedItem[i]
                    }
                }
            }
        }


        Box(modifier=modifier.background(Color(0,0,0,128))){
            BoxWithConstraints(modifier = Modifier
                .width(bookWidth)
                .fillMaxHeight()
                .align(Alignment.Center)){
                val maxPageWidth = this.maxWidth/2
                Image(painter = painterResource(id = R.drawable.pause_menu),
                    contentDescription = "Menu pause",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.925f)
                        .fillMaxHeight()
                ){
                    if(item.value==null){
                        LeftPage {
                            Items()
                        }
                    } else {
                        LeftPage {
                            Image(
                                painter = painterResource(id = item.value!!.image),
                                contentDescription = item.value!!.name,
                                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    Column(
                        modifier
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        MenuButton(text = "REPRENDRE", width = maxPageWidth*6/10) {
                            pause = false
                            menu["open"] = false
                        }
                        Spacer(modifier = Modifier.height((screenHeight*0.001).dp))
                        MenuButton(text = "PARAMETRES", width = maxPageWidth*6/10) {
                            
                        }
                        Spacer(modifier = Modifier.height((screenHeight*0.001).dp))
                        MenuButton(text = "QUITTER", width = maxPageWidth*6/10) {
                            
                        }

                    }
                }

            }
        }
    }

    var cinematic = mutableStateOf(
        Pair(
            Cinematic(game = this),
            false
        )
    )
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun GameScreen(){
        View()
        if(menu["open"] == true){
            Menu()
        } else if(item["show"] == true){
            Item()
        } else if(cinematic.value.second) {
            controllableCharacter!!.currentAnimationSequenceIndex = 0
            cinematic.value.first.Display()
        } else {
            Ath()
        }

    }

    fun initiate(){
        setupControllableCharacter()
        addSprite(camera.sprite)
        setupCamera()
    }

    fun setupCamera(){
        transform = FocusTransform(background,camera.sprite,15f)
    }

    init {
        initiate()
    }
}

