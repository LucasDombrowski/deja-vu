package fr.iutlens.mmi.demo.game

import android.annotation.SuppressLint
import android.graphics.Region
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.components.DialogScreen
import fr.iutlens.mmi.demo.game.ath.BossBar
import fr.iutlens.mmi.demo.game.ath.Coins
import fr.iutlens.mmi.demo.game.ath.Hearts
import fr.iutlens.mmi.demo.game.ath.LowLife
import fr.iutlens.mmi.demo.game.gameplayResources.Challenge
import fr.iutlens.mmi.demo.game.gameplayResources.Chest
import fr.iutlens.mmi.demo.game.gameplayResources.Collectible
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.gameplayResources.Item
import fr.iutlens.mmi.demo.game.gameplayResources.items.LessFireRateLessDamages
import fr.iutlens.mmi.demo.game.gameplayResources.items.LoyaltyCard
import fr.iutlens.mmi.demo.game.gameplayResources.items.MoreDamagesMoreRate
import fr.iutlens.mmi.demo.game.gameplayResources.items.Wallet
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
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.sprites.Character
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.MainCharacter
import fr.iutlens.mmi.demo.game.transform.CameraTransform
import fr.iutlens.mmi.demo.game.transform.FitTransform
import fr.iutlens.mmi.demo.game.transform.FocusTransform
import fr.iutlens.mmi.demo.ui.theme.MainFont
import fr.iutlens.mmi.demo.utils.Music
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
           val backgroundMusic : Int,
           var onDragStart: ((Offset) -> Unit)? = null,
           var onDragMove:  ((Offset) -> Unit)? = null,
           var onDragEnd : (()->Unit)? = null,
           var onTap: ((Offset)-> Unit)? = null){

    var background = map.tileArea
    val camera = Camera(this)
    val timeSource = TimeSource.Monotonic
    val items = mutableListOf<Item>(
        LessFireRateLessDamages(),
        MoreDamagesMoreRate(),
        Wallet(),
        LoyaltyCard()
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

    var onLeave : ()->Unit = {}

    var onRestart : ()->Unit = {}
    fun copyCharacterList() : MutableList<Character>{
        return characterList.toMutableList()
    }

    fun resetCollectibles(){
        with(collectibleList.iterator()){
            forEach {
                it.sprite.invisible()
            }
        }
        collectibleList = mutableListOf()
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
        addSprite(controllableCharacter!!.pathIndicator)
        controllableCharacter!!.pathIndicator.invisible()
        setupControls()
    }

    fun setupControls(){
        onTap = {
            (x,y)->
            if(!pause) {
                controllableCharacter!!.tapMovingBehavior(x, y)
            }
        }
        onDragStart = {
            controllableCharacter!!.dragStartBehavior(it.x, it.y)
        }
        onDragMove = {
            (x,y)->
            controllableCharacter!!.dragMovingBehavior(x, y)
        }
        onDragEnd = {
            controllableCharacter!!.dragEndBehavior()
        }
    }
    fun addCharacter(character: Character, blindVisible : Boolean = false){
        addSprite(character.sprite)
        if(blindVisible){
            addNotBlindedSprite(character.sprite)
        }
        characterList.add(character)
    }

    fun deleteCharacter(character: Character){
        characterList.remove(character)
    }

    fun addSprite(sprite: Sprite){
        spriteList.add(0,sprite)
    }

    fun addNotBlindedSprite(sprite : Sprite){
        notBlindedSpriteList.add(0,sprite)
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
        }
        if(map.currentRoom() !is LongRoom && map.currentRoom() !is LargeRoom){
            camera.moveTo(
                map.currentRoom().getRoomCenter().first,
                map.currentRoom().getRoomCenter().second
            )
        }
    }

    fun nextRoom(){
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
            map = map,
            backgroundMusic = backgroundMusic
        )
    }

    fun killAllEnemies(){
        while (characterList.any { it is Enemy }){
            characterList.filter {
                it is Enemy
            }.first().die()
        }
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

    var ended = false

    var blinded = false

    var blindColor = Color.Black

    val notBlindedSpriteList : MutableSpriteList = mutableSpriteListOf()
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun View(modifier: Modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        Canvas(modifier = modifier
            .pointerInput(key1 = this) {
                if (onTap != null) detectTapGestures {
                    onTap?.invoke(transform.getPoint(it))
                }
            }
            .pointerInput(key1 = this) {
                if (onDragMove != null) detectDragGestures(onDragStart = {
                    onDragStart?.invoke(transform.getPoint(it))
                }, onDragEnd = {
                    onDragEnd?.invoke()
                }) { change, dragAmount ->
                    onDragMove?.invoke(transform.getPoint(change.position))
                }
            }
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }) {
            drawRect(
                Color.Black
            )
            this.withTransform({ transform(transform.getMatrix(size)) }) {
                if(blinded){

                    val path = Path()
                    path.addOval(
                        Rect(
                            offset = Offset(
                                controllableCharacter!!.sprite.x - (controllableCharacter!!.viewingDistance.toFloat()*map.tileArea.w)/2,
                                controllableCharacter!!.sprite.y - (controllableCharacter!!.viewingDistance.toFloat()*map.tileArea.w)/2
                            ),
                            size = Size(controllableCharacter!!.viewingDistance.toFloat()*map.tileArea.w,controllableCharacter!!.viewingDistance.toFloat()*map.tileArea.w)
                        )
                    )
                    if(controllableCharacter!!.viewingDistance>0) {
                        path.addOval(
                            Rect(
                                offset = Offset(
                                    controllableCharacter!!.pathIndicator.x - map.tileArea.w / 2,
                                    controllableCharacter!!.pathIndicator.y - map.tileArea.w / 2
                                ),
                                size = Size(map.tileArea.w.toFloat(), map.tileArea.w.toFloat())
                            )
                        )
                    }
                    background.paint(this, elapsed)
                    spriteList.paint(this, elapsed)
                    drawRect(
                        topLeft = Offset.Zero,
                        color = blindColor,
                        size = Size(
                            (map.tileArea.sizeX*map.tileArea.w).toFloat(),
                            (map.tileArea.sizeY*map.tileArea.h).toFloat()
                        )
                    )
                    clipPath(path, ClipOp.Intersect){
                        background.paint(this, elapsed)
                        spriteList.paint(this, elapsed)
                    }
                    notBlindedSpriteList.paint(this,elapsed)
                } else {
                    background.paint(this, elapsed)
                    spriteList.paint(this, elapsed)
                }
            }
            // Dessin proprement dit. On précise la transformation à appliquer avant
        }

        // Gestion du rafraîssement automatique si update et animationDelay sont défnis
        update?.let { myUpdate ->
            animationDelayMs?.let { delay ->
                LaunchedEffect(elapsed) {
                    //Calcul du temps avant d'afficher la prochaine image, et pause si nécessaire)
                    val current = (timeSource.markNow() - start).inWholeMilliseconds
                    val next = elapsed + delay
                    if (next > current) delay(next - current)
                    myUpdate(this@Game)
                }
            }
        }
    }

    var ath = mutableStateMapOf("hearts" to mutableListOf<Heart>(), "boss" to mutableListOf<Heart>())
    var coins = mutableStateOf(0)

    var dropProbability = 1
    var heartDropProbability = 1
    var superCoinDropProbability = 1
    var goldHeartDropProbability = 1
    @Composable
    fun Ath(){
        val configuration = LocalConfiguration.current
        val screenWidth = with(configuration){
            this.screenWidthDp
        }
        if(controllableCharacter!!.hearts.size<=1 || controllableCharacter!!.hearts[1].filled<=0f) {
            LowLife()
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
                        .background(Color(0, 0, 0, 128), shape = CircleShape)
                        .padding((screenWidth / 125).dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.home_icon),
                            contentDescription = "Menu",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            if(ath["boss"]!!.isNotEmpty()){
                BossBar(hearts = ath["boss"]!!)
            }
        }

    }

    var item = mutableStateMapOf<String,Any>("show" to false, "image" to 0, "name" to "", "description" to "", "onPick" to {})
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
        val screenHeight = with(configuration){
            screenHeightDp
        }
        val fontSize = with(density){
            (screenWidth/40).sp
        }
        controllableCharacter!!.currentAnimationSequenceIndex = 0
        DialogScreen(text = item["description"] as String, onEnd = {
            item["show"] = false
            pause = false
            (item["onPick"] as ()->Unit).invoke()
            item["onPick"] = {}
        }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ItemImage(id = item["image"] as Int, item["name"] as String, (screenWidth*0.15).dp)
                Spacer(modifier = Modifier.height((screenHeight/60).dp))
                Text(
                    text = item["name"] as String,
                    color = Color.White,
                    fontSize = fontSize,
                    style = TextStyle(
                        fontFamily = MainFont,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height((screenHeight/30).dp))
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

        var active by remember {
            mutableStateOf(false)
        }

        val transitionDuration = 350

        val menuOffsetY by animateDpAsState(targetValue = if (active) 0.dp else screenHeight.dp, animationSpec = tween(
            durationMillis = transitionDuration,
            easing = LinearEasing
        ),
            label = "Book Slide"
        )

        LaunchedEffect(key1 = menu["open"]){
            Music.reduceMusicVolume()
            active = true
        }

        val scope = rememberCoroutineScope()



        Box(modifier=modifier.background(Color(0,0,0,128))){
            BoxWithConstraints(modifier = Modifier
                .offset(y = menuOffsetY)
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
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
                            active = false
                            scope.launch {
                                delay(transitionDuration.toLong())
                                pause = false
                                menu["open"] = false
                                Music.normalMusicVolume()
                            }
                        }
                        Spacer(modifier = Modifier.height((screenHeight*0.001).dp))
                        MenuButton(text = "QUITTER", width = maxPageWidth*6/10) {
                            onLeave()
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

    var musicTrack = mutableStateOf(backgroundMusic)
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
        } else if(gameOver.value == true){
            GameOver()
        } else {
            Challenge()
            Ath()
        }
        Music(musicTrack.value)
    }

    var challenge : MutableState<Challenge ?> = mutableStateOf(null)

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun Challenge(){
        val scope = rememberCoroutineScope()
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        val screenWidth = with(configuration){
            screenWidthDp
        }
        val fontSize = with(density){
            (screenWidth.dp/50).toSp()
        }
        val fontSizeDp = with(density){
            fontSize.toDp()
        }
        if(challenge.value!=null && challenge.value!!.name != "") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = challenge.value!!.name,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Color(0, 0, 0, 128))
                        .padding(fontSizeDp / 2),
                    color = Color.White,
                    fontSize = fontSize,
                    style = TextStyle(
                        fontFamily = MainFont
                    )
                )
            }
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
    
    var gameOver = mutableStateOf(false)

    fun gameOver(){
        gameOver.value = true
        ended = true
        with(characterList.iterator()){
            forEach {
                if(it is Enemy){
                    it.action.cancel()
                }
            }
        }
        onDragMove = {

        }
        onDragStart = {

        }
        onDragEnd = {

        }
        onTap = {

        }
    }
    @Composable
    fun GameOver(){
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        val screenWidth = configuration.screenWidthDp
        val screenHeight = configuration.screenHeightDp
        val titleSize = with(density){
            (screenWidth.dp/15).toSp()
        }
        val subtitleSize = with(density){
            (screenWidth.dp/30).toSp()
        }
        val optionSize = with(density){
            (screenWidth.dp/40).toSp()
        }
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color(0, 0, 0, 128))
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GAME OVER",
                    fontSize = titleSize,
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = MainFont,
                    )
                )
                Spacer(modifier = Modifier.height(screenHeight.dp/20))
                Text(
                    text = "REJOUER ?",
                    fontSize = subtitleSize,
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = MainFont,
                    )
                )
                Spacer(modifier = Modifier.height(screenHeight.dp/20))
                Row {
                    Text(
                        text = "OUI",
                        fontSize = optionSize,
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = MainFont,
                        ),
                        modifier = Modifier.clickable {
                            pause = false
                            onRestart()
                        }
                    )
                    Spacer(modifier = Modifier.width(screenWidth.dp/30))
                    Text(
                        text = "NON",
                        fontSize = optionSize,
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = MainFont,
                        ),
                    )

                }

            }
        }
    }

    init {
        initiate()
    }
}

