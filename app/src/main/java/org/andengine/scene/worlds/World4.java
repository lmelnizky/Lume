package org.andengine.scene.worlds;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.base.BaseScene;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.object.Ball;
import org.andengine.object.Circle;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class World4 extends BaseScene {

    private boolean slowMotion = false;
    private boolean variantUsed = false;
    private boolean gameOverDisplayed = false;
    private boolean firstStonesInLevel = true;
    private boolean cameFromLevelsScene;
    private boolean waitingForStonesToDisappear = false;
    private boolean worldFinished = false;

    private static final int FIRST_LAYER = 0; //is used for ground, player and coin
    private static final int SECOND_LAYER = 1; //is used for  stones
    private static final int THIRD_LAYER = 2; //is used for cannons

    private int variant = 1;
    private int variantStage;
    private int timeBetweenStones = 0;
    private int currentDirection = 2;
    private int currentRow = 0;
    private int level;
    private int score = 0;
    private float time = 30*60; //seconds
    private int sideLength;
    private int directionInLevel4 = 1;
    private int xPosLume, yPosLume;
    private int xPosCoin, yPosCoin;

    private long stoneTime;

    private float ratio = resourcesManager.screenRatio;
    private float shootX1, shootX2, shootY1, shootY2;
    private float swipeX1, swipeX2, swipeY1, swipeY2;

    private Random randomGenerator;

    private Sprite lumeSprite;
    private Sprite coinSprite;
    private Sprite[] cannonN, cannonE, cannonS, cannonW;
    private Sprite[] cannonNS, cannonES, cannonSS, cannonWS;
    private Sprite[] cannonNU, cannonEU, cannonSU, cannonWU;
    private ArrayList<Sprite> mirrorStones, mirrorStonesToRemove, crackyStones, crackyStonesToRemove, cannonBallsToRemove;
    private ArrayList<Sprite> stones, stonesToRemove;

    private Scene gameOverScene;
    private IEntity firstLayer, secondLayer, thirdLayer;

    private Sprite v1FirstStone, v2FirstStone, v3FirstStone;
    private Sprite luserSprite;

    private HUD gameHUD;
    private Text scoreText, levelText, timeText;
    private Sprite shootSign, moveSign, snailSign, noSnailSign;
    private PhysicsWorld physicsWorld;

    private static final int SWIPE_MIN_DISTANCE = 10;

    private Text gameOverText;
    private Sprite replaySprite, finishSprite;

    public World4() { //default constructor
        this.level = 1;
        cameFromLevelsScene = false;
        createHUD();
        showLevelText();
    }

    public World4(int level) { //constructor used when selecting a level
        this.level = level;
        cameFromLevelsScene = true;
        createHUD();
        showLevelText();
    }

    @Override
    public void createScene() {

        sideLength = (int) resourcesManager.screenHeight / 9;
        randomGenerator = new Random();
        crackyStones = new ArrayList<Sprite>();
        crackyStonesToRemove = new ArrayList<Sprite>();
        mirrorStones = new ArrayList<Sprite>();
        mirrorStonesToRemove = new ArrayList<Sprite>();
        cannonBallsToRemove = new ArrayList<Sprite>();
        stones = new ArrayList<Sprite>();
        stonesToRemove = new ArrayList<Sprite>();

        createLayers();
        createBackground();
        createMusic();
        createPhysics();
        createBoard();
        createHalves();
        createCannons();
        createLume();
        createHUD();
        if (cameFromLevelsScene) activity.showSlowMoHintWorld();

        resetData();

        this.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (!waitingForStonesToDisappear && !worldFinished) {
                    createStones(level);
                    time -= (slowMotion) ? 0.5f : 1f;
                    int displayTime = (int) Math.round(time/60);
                    timeText.setText(String.valueOf(displayTime));
                    if (time <= 0 && !gameOverDisplayed) {
                        displayGameOverScene();
                    }
                    if (displayTime <= 5) {
                        timeText.setColor(Color.RED);
                    }
                }
            }

            @Override
            public void reset() {

            }
        });

//        levelCompleteWindow = new LevelCompleteWindow(vbom);

//        setOnSceneTouchListener(this);
    }

    public void resetData() {
        time = 30*60;
        timeText.setColor(Color.WHITE);
        scoreText.setColor(Color.WHITE);
        stoneTime = new Date().getTime();
        firstStonesInLevel = true;
    }

    private void showLevelText() {
        Text levelText = new Text(camera.getCenterX(), camera.getCenterY(), resourcesManager.bigFont,
                "Level " + String.valueOf(level), vbom);
        secondLayer.attachChild(levelText);
        levelText.setColor(new Color(1f, 1f, 1f, 1f));
        levelText.registerEntityModifier(new ScaleModifier(0.6f, 0.5f, 1.5f));
        levelText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        levelText.registerEntityModifier(new AlphaModifier(0.6f, 0.6f, 0.6f));
        engine.registerUpdateHandler(new TimerHandler(0.6f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                engine.unregisterUpdateHandler(pTimerHandler);
                levelText.detachSelf();
                levelText.dispose();
            }
        }));
    }

    public void disposeHUD() {
        scoreText.detachSelf();
        scoreText.dispose();
        levelText.detachSelf();
        levelText.dispose();
        timeText.detachSelf();
        timeText.dispose();
        shootSign.detachSelf();
        shootSign.dispose();
        moveSign.detachSelf();
        moveSign.dispose();
        snailSign.detachSelf();
        snailSign.dispose();
        noSnailSign.detachSelf();
        noSnailSign.dispose();

        gameHUD.detachSelf();
        gameHUD.dispose();
    }

    @Override
    public void onBackKeyPressed() {
        ResourcesManager.getInstance().backgroundMusic.stop();
        if (cameFromLevelsScene) {
            disposeHUD();
            SceneManager.getInstance().loadWorlds1to4Scene(engine);
        } else {
            disposeHUD();
            SceneManager.getInstance().loadMenuScene(engine);
        }
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_WORLD4;
    }

    @Override
    public void disposeScene() {
        camera.setHUD(null);
        camera.setChaseEntity(null);
        camera.setCenter(camera.getCenterX(), camera.getCenterY());

        // TODO code responsible for disposing scene
        // removing all game scene objects.
    }

    private void displayGameOverScene() {
        gameOverDisplayed = true;

        gameOverScene = new CameraScene(camera);
        gameOverScene.setBackgroundEnabled(false);

        ResourcesManager.getInstance().backgroundMusic.stop();
        ResourcesManager.getInstance().backgroundMusic.pause();
        ResourcesManager.getInstance().luserSound.play();
        if (cameFromLevelsScene) activity.showSlowMoHintWorld();

        float textY = (yPosLume == 2) ? camera.getCenterY() + sideLength : camera.getCenterY();
        gameOverText = new Text(camera.getCenterX(), textY,
                resourcesManager.smallFont, "L u s e r !", vbom);


        luserSprite = new Sprite(lumeSprite.getX()-lumeSprite.getWidth()*4/10,
                lumeSprite.getY() + lumeSprite.getHeight()*5/10,
                lumeSprite.getWidth(), lumeSprite.getWidth(),
                ResourcesManager.getInstance().finger_luser, vbom);
        secondLayer.attachChild(luserSprite);
        luserSprite.setVisible(false);

        displayGameOverButtons();

        gameOverText.setColor(Color.RED);
        gameOverScene.registerTouchArea(gameOverText);
        gameOverScene.attachChild(gameOverText);

        RotationModifier rotMod = new RotationModifier(0.7f, 180, 720) {
            @Override
            protected void onModifierFinished(IEntity item) {
                //stop things
                //unregisterUpdateHandler(physicsWorld);
                setIgnoreUpdate(true);
                luserSprite.setVisible(true);
                //setChildScene(gameOverScene, false, true, true); //set gameOverScene as a child scene - so game will be paused
                ResourcesManager.getInstance().activity.showLevelHint();
            }
        };
        gameOverText.registerEntityModifier(new ScaleModifier(0.7f, 0.1f, 1.3f));
        //gameOverText.registerEntityModifier(new RotationModifier(2, 180, 720));
        gameOverText.registerEntityModifier(rotMod);

        //stop things
        unregisterUpdateHandler(physicsWorld);
        setChildScene(gameOverScene, false, true, true);


        engine.registerUpdateHandler(new TimerHandler(0.8f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                engine.unregisterUpdateHandler(pTimerHandler);
                //show interstitial setAdVisibility
                if (!cameFromLevelsScene) {
                    ResourcesManager.getInstance().activity.showSingleInterstitial();
                }
            }
        }));
    }

    private void displayGameOverButtons() {
        //add replay Sprite
        replaySprite = new Sprite(camera.getCenterX() + sideLength,
                camera.getHeight()*2/9, sideLength, sideLength,
                ResourcesManager.getInstance().replay_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    //clear child scenes - game will be resumed
                    clearChildScene();
                    setIgnoreUpdate(false);
                    gameOverDisplayed = false;
                    registerUpdateHandler(physicsWorld);
                    disposeHUD();
                    if (cameFromLevelsScene) {
                        SceneManager.getInstance().loadWorld4Scene(engine, level);
                    } else {
                        SceneManager.getInstance().loadWorld4Scene(engine, 0);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
        gameOverScene.registerTouchArea(replaySprite);
        gameOverScene.attachChild(replaySprite);

        //add finish Sprite
        finishSprite = new Sprite(camera.getCenterX() - sideLength,
                camera.getHeight()*2/9, sideLength, sideLength,
                ResourcesManager.getInstance().finish_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    //clear child scenes - game will be resumed
                    clearChildScene();
                    setIgnoreUpdate(false);
                    gameOverDisplayed = false;
                    registerUpdateHandler(physicsWorld);
                    disposeHUD();
                    if (cameFromLevelsScene) {
                        SceneManager.getInstance().loadWorlds1to4Scene(engine);
                    } else {
                        SceneManager.getInstance().loadMenuScene(engine);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
        gameOverScene.registerTouchArea(finishSprite);
        gameOverScene.attachChild(finishSprite);
    }

    private void createLayers() {
        this.attachChild(new Entity()); // First Layer
        this.attachChild(new Entity()); // Second Layer
        this.attachChild(new Entity()); // Third Layer
        firstLayer = this.getChildByIndex(FIRST_LAYER);
        secondLayer = this.getChildByIndex(SECOND_LAYER);
        thirdLayer = this.getChildByIndex(THIRD_LAYER);
    }

    private void createHUD() {
        gameHUD = new HUD();

        scoreText = new Text(20, camera.getHeight()-70, resourcesManager.smallFont, "0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
        scoreText.setAnchorCenter(0, 0);
        scoreText.setText("0");
        gameHUD.attachChild(scoreText);

        levelText = new Text(camera.getWidth()-200, camera.getHeight()-70, resourcesManager.smallFont, "L0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
        levelText.setAnchorCenter(0, 0);
        levelText.setText("L" + String.valueOf(level));
        gameHUD.attachChild(levelText);

        timeText = new Text(camera.getWidth()-65, camera.getHeight()-70, resourcesManager.smallFont, "0123456789", new TextOptions(HorizontalAlign.RIGHT), vbom);
        timeText.setAnchorCenter(0, 0);
        timeText.setText("30");
        gameHUD.attachChild(timeText);

        shootSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*6/8, resourcesManager.cracky_mirror_sign_region, vbom);
        moveSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.move_normal_sign_region, vbom);
        snailSign = new Sprite(camera.getCenterX() + 4*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.snail_sign_region, vbom);
        noSnailSign = new Sprite(camera.getCenterX() + 4*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.no_snail_sign_region, vbom);
        gameHUD.attachChild(shootSign);
        gameHUD.attachChild(moveSign);
        gameHUD.attachChild(snailSign);
        gameHUD.attachChild(noSnailSign);

        slowMotion = activity.isSlowMotion();
        if (!cameFromLevelsScene) slowMotion = false; //in normal game mode anyway false, without changing prefs
        snailSign.setVisible(slowMotion);
        noSnailSign.setVisible(!slowMotion && cameFromLevelsScene);
        camera.setHUD(gameHUD);
    }

    private void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world4_region, vbom));
        this.setBackground(spriteBackground);
    }

    private void createMusic() {
        ResourcesManager.getInstance().backgroundMusic.play();
    }

    private void createBoard() {
        Sprite boardSprite = new Sprite(camera.getCenterX(), camera.getCenterY(), sideLength * 3, sideLength * 3, resourcesManager.board_region, vbom);
        firstLayer.attachChild(boardSprite);
    }

    private void createLume() {
        xPosLume = 1;
        yPosLume = 1;
        lumeSprite = new Sprite(camera.getCenterX() - sideLength, camera.getCenterY() - sideLength, sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.player_region, vbom);
        secondLayer.attachChild(lumeSprite);
    }

    private void createCoin() {
        do {
            xPosCoin = randomGenerator.nextInt(3) + 1;
            yPosCoin = randomGenerator.nextInt(3) + 1;
        } while (xPosCoin == xPosLume && yPosCoin == yPosLume);
        if (coinSprite == null) {
            coinSprite = new Sprite(camera.getCenterX() - sideLength + ((xPosCoin - 1) * sideLength), camera.getCenterY() - sideLength + ((yPosCoin - 1) * sideLength),
                    sideLength * 7 / 8, sideLength * 7 / 8, resourcesManager.coin_region, vbom);
            firstLayer.attachChild(coinSprite);
        } else {
            coinSprite.registerEntityModifier(new ScaleModifier(0.2f,0.7f,1f));
            coinSprite.setPosition(camera.getCenterX() - sideLength + ((xPosCoin - 1) * sideLength), camera.getCenterY() - sideLength + ((yPosCoin - 1) * sideLength));
        }
    }

    private void removeCoin() {
        xPosCoin = 0;
        yPosCoin = 0;
        coinSprite.detachSelf();
        coinSprite.dispose();
        coinSprite = null;
    }

    private void createHalves() {

        final Rectangle shootLeft = new Rectangle(camera.getCenterX() - resourcesManager.screenWidth / 4, camera.getCenterY(),
                resourcesManager.screenWidth / 2, resourcesManager.screenHeight, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    shootX1 = x;
                    shootY1 = y;
                    return true;
                } else if (touchEvent.isActionUp()) {
                    shootX2 = x;
                    shootY2 = y;
                    float deltaX = shootX2 - shootX1;
                    float deltaY = shootY2 - shootY1;
                    //checks if it was a swipe
                    if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE || Math.abs(deltaY) > SWIPE_MIN_DISTANCE) {
                        //checks if it was horizontal or vertical
                        if (Math.abs(deltaX) > Math.abs(deltaY)) { //horizontal swipe
                            if (deltaX > 0) { //left to right
                                createCannonball(4);
                            } else { //right to left
                                createCannonball(2);
                            }
                        } else { //vertical swipe
                            if (deltaY > 0) { //up to down
                                createCannonball(3);
                            } else { //down to up
                                createCannonball(1);
                            }
                        }
                    }


                    return true;
                } else {
                    return false;
                }
            }

            ;
        };

        final Rectangle swipeRight = new Rectangle(camera.getCenterX() + resourcesManager.screenWidth / 4, camera.getCenterY(),
                resourcesManager.screenWidth / 2, resourcesManager.screenHeight, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    swipeX1 = x;
                    swipeY1 = y;
                    return true;
                } else if (touchEvent.isActionUp()) {
                    swipeX2 = x;
                    swipeY2 = y;
                    float deltaX = swipeX2 - swipeX1;
                    float deltaY = swipeY2 - swipeY1;
                    //checks if it was a swipe and not a tap
                    if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE || Math.abs(deltaY) > SWIPE_MIN_DISTANCE) {
                        //horizontal or vertical check
                        if (Math.abs(deltaX) > Math.abs(deltaY)) { //horizontal
                            if (deltaX > 0) { //left to right
                                movePlayer('R');
                            } else { //right to left
                                movePlayer('L');
                            }
                        } else { //vertical
                            if (deltaY > 0) { //up to down
                                movePlayer('U');
                            } else { //down to up
                                movePlayer('D');
                            }
                        }
                    } else { //TAP - show slowMotion
                        if (cameFromLevelsScene) {
                            slowMotion = !slowMotion;
                            activity.setSlowMotion(slowMotion);
                            setSlowMotionMode(); //sets values of current moving stones
                            snailSign.setVisible(slowMotion);
                            noSnailSign.setVisible(!slowMotion);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }

            ;
        };

        shootLeft.setColor(Color.GREEN);
        swipeRight.setColor(Color.RED);
        shootLeft.setAlpha(0.2f);
        swipeRight.setAlpha(0.2f);
        this.registerTouchArea(shootLeft);
        this.registerTouchArea(swipeRight);
        secondLayer.attachChild(shootLeft);
        secondLayer.attachChild(swipeRight);
    }

    private void createCannons() {
        cannonN = new Sprite[3];
        cannonE = new Sprite[3];
        cannonS = new Sprite[3];
        cannonW = new Sprite[3];
        cannonNS = new Sprite[3];
        cannonES = new Sprite[3];
        cannonSS = new Sprite[3];
        cannonWS = new Sprite[3];
        cannonNU = new Sprite[3];
        cannonEU = new Sprite[3];
        cannonSU = new Sprite[3];
        cannonWU = new Sprite[3];

        for (int i = 0; i < cannonN.length; i++) {
            cannonN[i] = new Sprite(camera.getCenterX()-sideLength+sideLength*i, camera.getHeight()-sideLength/2,
                    sideLength, sideLength, resourcesManager.cannon_n_region, vbom);
            cannonE[i] = new Sprite(camera.getWidth()-sideLength/2, camera.getCenterY()-sideLength+sideLength*i,
                    sideLength, sideLength, resourcesManager.cannon_e_region, vbom);
            cannonS[i] = new Sprite(camera.getCenterX()-sideLength+sideLength*i, sideLength/2,
                    sideLength, sideLength, resourcesManager.cannon_s_region, vbom);
            cannonW[i] = new Sprite(sideLength/2, camera.getCenterY()-sideLength+sideLength*i,
                    sideLength, sideLength, resourcesManager.cannon_w_region, vbom);

            cannonNS[i] = new Sprite(camera.getCenterX()-sideLength+sideLength*i, camera.getHeight()-sideLength*3/8,
                    sideLength, sideLength*0.75f, resourcesManager.cannon_n_s_region, vbom);
            cannonES[i] = new Sprite(camera.getWidth()-sideLength*3/8, camera.getCenterY()-sideLength+sideLength*i,
                    sideLength*0.75f, sideLength, resourcesManager.cannon_e_s_region, vbom);
            cannonSS[i] = new Sprite(camera.getCenterX()-sideLength+sideLength*i, sideLength*3/8,
                    sideLength, sideLength*0.75f, resourcesManager.cannon_s_s_region, vbom);
            cannonWS[i] = new Sprite(sideLength*3/8, camera.getCenterY()-sideLength+sideLength*i,
                    sideLength*0.75f, sideLength, resourcesManager.cannon_w_s_region, vbom);

            cannonNU[i] = new Sprite(camera.getCenterX()-sideLength+sideLength*i, camera.getHeight()-sideLength*0.222f,
                    sideLength, sideLength*0.444f, resourcesManager.cannon_n_u_region, vbom);
            cannonEU[i] = new Sprite(camera.getWidth()-sideLength*0.222f, camera.getCenterY()-sideLength+sideLength*i,
                    sideLength*0.444f, sideLength, resourcesManager.cannon_e_u_region, vbom);
            cannonSU[i] = new Sprite(camera.getCenterX()-sideLength+sideLength*i, sideLength*0.222f,
                    sideLength, sideLength*0.444f, resourcesManager.cannon_s_u_region, vbom);
            cannonWU[i] = new Sprite(sideLength*0.222f, camera.getCenterY()-sideLength+sideLength*i,
                    sideLength*0.444f, sideLength, resourcesManager.cannon_w_u_region, vbom);

            secondLayer.attachChild(cannonN[i]);
            secondLayer.attachChild(cannonE[i]);
            secondLayer.attachChild(cannonS[i]);
            secondLayer.attachChild(cannonW[i]);
            secondLayer.attachChild(cannonNS[i]);
            secondLayer.attachChild(cannonES[i]);
            secondLayer.attachChild(cannonSS[i]);
            secondLayer.attachChild(cannonWS[i]);

            thirdLayer.attachChild(cannonNU[i]);
            thirdLayer.attachChild(cannonEU[i]);
            thirdLayer.attachChild(cannonSU[i]);
            thirdLayer.attachChild(cannonWU[i]);

            //setVisibility of small ones to false
            cannonNS[i].setVisible(false);
            cannonES[i].setVisible(false);
            cannonSS[i].setVisible(false);
            cannonWS[i].setVisible(false);
            cannonNU[i].setVisible(false);
            cannonEU[i].setVisible(false);
            cannonSU[i].setVisible(false);
            cannonWU[i].setVisible(false);
        }
    }

    private void animateCannon(int direction, int position) {
        switch (direction){
            case 1:
                cannonN[position].setVisible(false);
                cannonNS[position].setVisible(true);
                cannonNU[position].setVisible(true);
                break;
            case 2:
                cannonE[position].setVisible(false);
                cannonES[position].setVisible(true);
                cannonEU[position].setVisible(true);
                break;
            case 3:
                cannonS[position].setVisible(false);
                cannonSS[position].setVisible(true);
                cannonSU[position].setVisible(true);
                break;
            case 4:
                cannonW[position].setVisible(false);
                cannonWS[position].setVisible(true);
                cannonWU[position].setVisible(true);
                break;
        }
        engine.registerUpdateHandler(new TimerHandler(0.8f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                engine.unregisterUpdateHandler(pTimerHandler);
                switch (direction){
                    case 1:
                        cannonN[position].setVisible(true);
                        cannonNS[position].setVisible(false);
                        cannonNU[position].setVisible(false);
                        break;
                    case 2:
                        cannonE[position].setVisible(true);
                        cannonES[position].setVisible(false);
                        cannonEU[position].setVisible(false);
                        break;
                    case 3:
                        cannonS[position].setVisible(true);
                        cannonSS[position].setVisible(false);
                        cannonSU[position].setVisible(false);
                        break;
                    case 4:
                        cannonW[position].setVisible(true);
                        cannonWS[position].setVisible(false);
                        cannonWU[position].setVisible(false);
                        break;
                }

            }
        }));
    }

    private void createCannonball(int direction) {
        ITextureRegion textureRegion = resourcesManager.cannonball_region;
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        final Sprite cannonball;
        Ball ball;
        float slowMotionFactor = (slowMotion) ? 0.5f : 1f;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
//        float speed = 1.4f*sideLength/5;
        float speed = (level == 4) ? resourcesManager.screenWidth*4/400*slowMotionFactor :
                resourcesManager.screenWidth*6/400*slowMotionFactor;
        switch (direction) {
            case 1: //up to down
                x = lumeSprite.getX();
                y = lumeSprite.getY() - lumeSprite.getHeight()-1;
                yVel = -speed;
                break;
            case 2: //right to left
                x = lumeSprite.getX() - lumeSprite.getWidth()-1;
                y = lumeSprite.getY();
                xVel = -speed;
                break;
            case 3: //down to up
                x = lumeSprite.getX();
                y = lumeSprite.getY() + lumeSprite.getHeight()+1;
                yVel = speed;
                break;
            case 4: //left to right
                x = lumeSprite.getX() + lumeSprite.getWidth()+1;
                y = lumeSprite.getY();
                xVel = speed;
                break;
        }
        cannonball = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {

                final Circle cannonCircle, lumeCircle;
                cannonCircle = new Circle(this.getX(), this.getY(), this.getWidth()/2);
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth()/2);

                if (cannonCircle.collision(lumeCircle) && !gameOverDisplayed) {
                    displayGameOverScene();
                    score = 0;
                }

                for (Sprite crackyStone : crackyStones) { //loop for cracky stones
                    final Circle stoneCircle;
                    stoneCircle = new Circle(crackyStone.getX(), crackyStone.getY(), crackyStone.getWidth() / 2);
                    if (cannonCircle.collision(stoneCircle)) {
                        crackyStonesToRemove.add(crackyStone);
                        stonesToRemove.add(crackyStone);
                        cannonBallsToRemove.add(this);
                        final Sprite cannonBalltoRemove = this;

                        engine.runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                if (cannonBallsToRemove.size()+crackyStonesToRemove.size() > 0) {
                                    if (cannonBallsToRemove.size() > 0) {
                                        cannonBalltoRemove.detachSelf();
                                        cannonBalltoRemove.dispose();
                                    }

                                    cannonBallsToRemove.clear();
                                    for (Sprite sprite : crackyStonesToRemove) {
                                        sprite.detachSelf();
                                        sprite.dispose();
                                    }
                                    stonesToRemove.clear();
                                    crackyStonesToRemove.clear();
                                }
                            }
                        });
                    }
                }
                for (Sprite mirrorStone : mirrorStones) {
                    final Circle stoneCircle; //making this circle smaller so that ball rebounds later
                    stoneCircle = new Circle(mirrorStone.getX(), mirrorStone.getY(), mirrorStone.getWidth() / 8);

                    if (cannonCircle.collision(stoneCircle)) {
                        mirrorStonesToRemove.add(mirrorStone);
                        stonesToRemove.add(mirrorStone);

                        this.setPosition(mirrorStone.getX(), mirrorStone.getY());
                        rebound(this); //makes the cannonball rebound in the opposite direction

                        engine.runOnUpdateThread(new Runnable() { //destroys the stone
                            @Override
                            public void run() {
                                if (mirrorStonesToRemove.size() > 0) {
                                    for (Sprite sprite : mirrorStonesToRemove) {
                                        sprite.detachSelf();
                                        sprite.dispose();
                                    }
                                    stonesToRemove.clear();
                                    mirrorStonesToRemove.clear();
                                }
                            }
                        });
                    }
                }
                stones.removeAll(stonesToRemove);
                crackyStones.removeAll(crackyStonesToRemove);
                mirrorStones.removeAll(mirrorStonesToRemove);

                if (this.getX() < -sideLength || this.getY() < - sideLength ||
                        this.getX() > camera.getWidth()+sideLength || this.getY() > camera.getWidth()+sideLength) {
                    this.detachSelf();
                    this.dispose();
                }
            }
        };
        secondLayer.attachChild(cannonball);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, cannonball, BodyType.KinematicBody, FIXTURE_DEF);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(cannonball, body, true, false));
        ball = new Ball();
        ball.setDirection(direction);
        ball.setBody(body);
        cannonball.setUserData(ball);
    }

    private void createStones(int level) {
        float randomNumber = randomGenerator.nextFloat();
        double probabilityStone; //= 0.6
        float interval;
        float slowMotionFactor = (slowMotion) ? 2f : 1f;

        switch (level) {
            case 1:
                int direction = randomGenerator.nextInt(4) + 1;
                interval =  4500*slowMotionFactor;
                long age = (new Date()).getTime() - stoneTime;
                if (firstStonesInLevel) interval = 1800*slowMotionFactor;
                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    this.showStonesToScreen(direction, false);
                    stoneTime = new Date().getTime();
                }
                break;
            case 2:
                age = (new Date()).getTime() - stoneTime;
                interval = 4800*slowMotionFactor;
                if (firstStonesInLevel) interval = 1800*slowMotionFactor;
                if (!this.variantUsed && age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    variantUsed = true;
                    variant = randomGenerator.nextInt(3) + 1;
                    showStonesToScreen(variant, true);
                    stoneTime = new Date().getTime();
                } else if (variantUsed) {
                    switch (variant) {
                        case 1:
                            if (v1FirstStone != null && v1FirstStone.getX() <= (camera.getWidth()/2) + (camera.getHeight()/2) - (sideLength*3/4) && v1FirstStone.getX() != 0) {
                                showStonesToScreen(variant, false);
                                v1FirstStone = null;
                                variantUsed = false;
                            }
                            break;
                        case 2:
                            if (v2FirstStone != null && v2FirstStone.getX() <= (camera.getWidth()/2) + (camera.getHeight()/2) - (sideLength*3/4) && v2FirstStone.getX() != 0) {
                                showStonesToScreen(variant, false);
                                v2FirstStone = null;
                                variantUsed = false;
                            }
                            break;
                        case 3:
                            if (v3FirstStone != null && v3FirstStone.getX() <= (camera.getWidth()/2) + (camera.getHeight()/2) - (sideLength*3/4) && v3FirstStone.getX() != 0) {
                                showStonesToScreen(variant, false);
                                v3FirstStone = null;
                                variantUsed = false;
                            }
                            break;
                    }
                }
                break;
            case 3:
                variant = randomGenerator.nextInt(2) + 1;
                interval = 4500*slowMotionFactor;
                age = (new Date()).getTime() - stoneTime;
                if (firstStonesInLevel) interval = 1800*slowMotionFactor;
                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    this.showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                }
                break;
            case 4:
                age = (new Date()).getTime() - stoneTime;
                interval = 555*slowMotionFactor;


                if (firstStonesInLevel) interval = 1800*slowMotionFactor;
                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    this.showStonesToScreen(currentDirection, false);
                    currentRow++;
                    if(currentRow > 2) {
                        currentRow = 0;
                        currentDirection = (currentDirection < 3) ? currentDirection+2 : currentDirection-2;
                    }
                    stoneTime = new Date().getTime();
                }
                break;
        }


    }

    private void showStonesToScreen(int directionVariant, final boolean thornyFirst) {
        switch (level) {
            case 1:
                showLevel1(directionVariant);
                break;
            case 2:
                showLevel2(directionVariant, thornyFirst);
                break;
            case 3:
                showLevel3(directionVariant);
                break;
            case 4:
                showLevel4();
                break;
        }
    }

    private void showLevel1(int directionVariant) {
        int secondDirection = (directionVariant <= 2) ? directionVariant+2 : directionVariant-2;
        int randomRow = randomGenerator.nextInt(3); //values 0 to 2
        float speed = (directionVariant%2 == 0) ? 0.8f*ratio : 0.7f;

        addBall('M', directionVariant, randomRow%3, speed); //mirror stone
        addBall('T', directionVariant, (randomRow+1)%3, speed);
        addBall('T', directionVariant, (randomRow+2)%3, speed);

        addBall('M', secondDirection, randomRow%3, speed); //cracky stone
        addBall('T', secondDirection, (randomRow+1)%3, speed);
        addBall('T', secondDirection, (randomRow+2)%3, speed);
    }

    private void showLevel2(int variant, boolean thornyFirst) {
        switch (variant) {
            case 1:
                if (thornyFirst) {
                    v1FirstStone = addBall('T', 2, 2, 1);
                    addBall('M', 2, 0, 1);
                    addBall('M', 4, 1, 1);
                    addBall('C', 4, 0, 1);
                } else {
                    addBall('C', 1, 0, 1);
                    addBall('T', 3, 2, 1);
                    addBall('M', 3, 1, 1);
                }
                break;
            case 2:
                if (thornyFirst) {
                    v2FirstStone = addBall('T', 2, 0, 1);
                    addBall('C', 2, 1, 1);
                    addBall('M', 4, 2, 1);
                } else {
                    addBall('C', 1, 0, 1);
                    addBall('M', 1, 2, 0.8f);
                    addBall('M', 3, 1, 1);
                    addBall('T', 3, 0, 1);
                }
                break;
            case 3:
                if (thornyFirst) {
                    v3FirstStone = addBall('T', 2, 1, 1);
                    addBall('C', 2, 2, 1);
                    addBall('C', 4, 0, 1);
                    addBall('M', 4, 2, 1);
                } else {
                    addBall('M', 3, 0, 0.8f);
                    addBall('T', 1, 1, 1);
                    addBall('M', 1, 2, 1);
                }
                break;
        }
    }

    private void showLevel3(int variant) {
        int firstPos;
        switch (variant) {
            case 1:
                firstPos = randomGenerator.nextInt(3);

                v1FirstStone = addBall((0 == firstPos) ? 'M' : 'T', 2, 0, 0.7f * ratio);
                addBall((1 == firstPos) ? 'M' : 'T', 2, 1,  0.7f * ratio);
                addBall((2 == firstPos) ? 'M' : 'T', 2, 2,  0.7f * ratio);

                addBall(!(0 == firstPos) ? 'M' : 'T', 4, 0,  0.7f * ratio);
                addBall(!(1 == firstPos) ? 'M' : 'T', 4, 1,  0.7f * ratio);
                addBall(!(2 == firstPos) ? 'M' : 'T', 4, 2,  0.7f * ratio);
                break;
            case 2:
                firstPos = randomGenerator.nextInt(3);

                v1FirstStone = addBall(!(0 == firstPos) ? 'M' : 'T', 2, 0,  0.7f * ratio);
                addBall(!(1 == firstPos) ? 'M' : 'T', 2, 1, 0.7f * ratio);
                addBall(!(2 == firstPos) ? 'M' : 'T', 2, 2,  0.7f * ratio);

                addBall((0 == firstPos) ? 'M' : 'T', 4, 0, 0.7f* ratio);
                addBall((1 == firstPos) ? 'M' : 'T', 4, 1,  0.7f * ratio);
                addBall((2 == firstPos) ? 'M' : 'T', 4, 2,  0.7f * ratio);
                break;
        }
    }

    private void showLevel4() {
        addBall('M', currentDirection, currentRow, 0.8f);
    }

    private boolean allStonesGone() {
        boolean allStonesGone = false;
        int stonesOverHalf = 0;
        for (Sprite stone : stones) {
            Ball ball = (Ball) stone.getUserData();
            switch (ball.getDirection()) {
                case 1:
                    if (stone.getY() < camera.getCenterY()-2*sideLength) stonesOverHalf++;
                    break;
                case 2:
                    if (stone.getX() < camera.getCenterX()-2*sideLength) stonesOverHalf++;
                    break;
                case 3:
                    if (stone.getY() > camera.getCenterY()+2*sideLength) stonesOverHalf++;
                    break;
                case 4:
                    if (stone.getX() > camera.getCenterX()+2*sideLength) stonesOverHalf++;
                    break;
            }
        }
        if (stonesOverHalf == stones.size()) allStonesGone = true;
        return allStonesGone;
    }

    private void coinCheck() {
        if (xPosLume == xPosCoin && yPosLume == yPosCoin) {
            int randomBelch = randomGenerator.nextInt(3) + 1;
            switch (randomBelch) {
                case 1:
                    ResourcesManager.getInstance().belchSound1.play();
                    break;
                case 2:
                    ResourcesManager.getInstance().belchSound2.play();
                    break;
                case 3:
                    ResourcesManager.getInstance().belchSound3.play();
                    break;
            }
            addToScore(1);
        }
    }

    public Sprite addBall(final char type, int direction, int position, float speedFactor) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        float slowMotionFactor = (slowMotion) ? 0.5f : 1f;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/15 * speedFactor * slowMotionFactor;

        final Sprite stone;
        ITextureRegion textureRegion = null;
        switch (type) {
            case 'T':
                textureRegion = resourcesManager.thorny_stone_region;
                break;
            case 'C':
                textureRegion = resourcesManager.cracky_stone_region;
                break;
            case 'M':
                textureRegion = resourcesManager.cracky_mirror;
                break;
        }
        Ball ball;

        switch (direction) {
            case 1:
                x = camera.getCenterX() - sideLength + sideLength*position;
                y = camera.getHeight() - sideLength / 2;
                yVel = -speed;
                break;
            case 2:
                x = camera.getWidth() - sideLength / 2;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = -speed;
                break;
            case 3:
                x = camera.getCenterX()-sideLength + sideLength*position;
                y = sideLength / 2;
                yVel = speed;
                break;
            case 4:
                x = sideLength / 2;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = speed;
                break;
        }

//        stoneCircle = new Circle(x, y, sideLength*3/8);
        stone = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                final Circle lumeCircle, stoneCircle;
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
                stoneCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);

                if (stoneCircle.collision(lumeCircle) && !gameOverDisplayed) {
                    displayGameOverScene();
                    score = 0;
                }
//                if (this.getX() < -sideLength || this.getY() < -sideLength ||
//                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
//                    if (!thorny) crackyStones.remove(this);
//                    this.detachSelf();
//                    this.dispose();
//                }


                if (this.getX() < -sideLength || this.getY() < -sideLength ||
                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
                    stonesToRemove.add(this);
                    if (type == 'C') crackyStonesToRemove.add(this);
                    if (type == 'M') mirrorStonesToRemove.add(this);

                    engine.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Sprite sprite : stonesToRemove) {
                                sprite.detachSelf();
                                sprite.dispose();
                            }
                            crackyStonesToRemove.clear();
                            mirrorStonesToRemove.clear();
                            stonesToRemove.clear();
                        }

                    });
                }

                crackyStones.removeAll(crackyStonesToRemove);
                mirrorStones.removeAll(mirrorStonesToRemove);
                stones.removeAll(stonesToRemove);

            }
        };
        secondLayer.attachChild(stone);

        //animate cannon
        animateCannon(direction, position);

        final Body body = PhysicsFactory.createCircleBody(physicsWorld, stone, BodyType.KinematicBody, FIXTURE_DEF);
        if (type == 'C') {
            crackyStones.add(stone);
        } else if (type == 'M') mirrorStones.add(stone);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(stone, body, true, false));
        ball = new Ball(direction, null, body, stone, (type == 'T'), false, speedFactor);
        stone.setUserData(ball);
        stones.add(stone);
        return stone;
    }

    private void rebound(Sprite cannonBall) {
        int direction = 0;
        float slowMotionFactor = (slowMotion) ? 0.5f : 1f;
        float xVel = 0;
        float yVel = 0;
        float speed = (level == 4) ? resourcesManager.screenWidth*4/400*slowMotionFactor :
                resourcesManager.screenWidth*6/400*slowMotionFactor;
        Ball ball = (Ball) cannonBall.getUserData();
        Body body = ball.getBody();
        switch (ball.getDirection()) {
            case 1:
                yVel = speed;
                direction = 3;
                break;
            case 2:
                xVel = speed;
                direction = 4;
                break;
            case 3:
                yVel = -speed;
                direction = 1;
                break;
            case 4:
                xVel = -speed;
                direction = 2;
                break;
        }
        body.setLinearVelocity(xVel, yVel);
        ball.setDirection(direction);
        ball.setBody(body);
    }

    private void addToScore(int i) {
        score += i;
        scoreText.setText(String.valueOf(score));
        if (score%10 == 9) {
            scoreText.setColor(android.graphics.Color.parseColor("#ffc300"));
        }
        if (score%10 == 0) {
            if (cameFromLevelsScene) {
                ResourcesManager.getInstance().backgroundMusic.stop();
                disposeHUD();
                SceneManager.getInstance().loadWorlds1to4Scene(engine);
                return;
            }
            removeCoin();
            waitingForStonesToDisappear = true;
            this.registerUpdateHandler(new IUpdateHandler() {
                @Override
                public void onUpdate(float pSecondsElapsed) {
                    if (allStonesGone() && waitingForStonesToDisappear) {
                        level++;
                        waitingForStonesToDisappear = false;
                        if (level == 4) currentDirection = 2;
                        if (level == 5) {
                            worldFinished = true; //just to stop time
                            ResourcesManager.getInstance().backgroundMusic.stop();
                            ResourcesManager.getInstance().backgroundMusic.pause();
                            ResourcesManager.getInstance().easySound.play();
                            Text tooEasyText = new Text(camera.getCenterX(), sideLength*7.5f, resourcesManager.bigFont,
                                    "T O O  E A S Y !", vbom);
                            int color = android.graphics.Color.parseColor("#1eb1e1");
                            tooEasyText.setColor(color);
                            attachChild(tooEasyText);
                            tooEasyText.registerEntityModifier(new ScaleModifier(2f, 0.5f, 1.5f));
                            engine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
                                public void onTimePassed(final TimerHandler pTimerHandler) {
                                    engine.unregisterUpdateHandler(pTimerHandler);
                                    activity.unlockWorld(5);
                                    tooEasyText.detachSelf();
                                    tooEasyText.dispose();
                                    disposeHUD();
                                    SceneManager.getInstance().loadMenuScene(engine);
                                }
                            }));
                        } else {
                            levelText.setText("L" + String.valueOf(level));
                            resetData();
                            showLevelText();
                        }
                    }
                }
                @Override
                public void reset() {

                }
            });
        } else {
            createCoin();
        }
    }

    //moves the player in X or Y direction
    public void movePlayer(char direction) {
        switch (direction) {
            case 'R':
                if (xPosLume < 3) {
                    xPosLume++;
                    lumeSprite.setPosition(lumeSprite.getX() + sideLength, lumeSprite.getY());
                }
                break;
            case 'L':
                if (xPosLume > 1) {
                    xPosLume--;
                    lumeSprite.setPosition(lumeSprite.getX() - sideLength, lumeSprite.getY());
                }
                break;
            case 'D':
                if (yPosLume > 1) {
                    yPosLume--;
                    lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() - sideLength);
                }
                break;
            case 'U':
                if (yPosLume < 3) {
                    yPosLume++;
                    lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() + sideLength);
                }
                break;
        }
        coinCheck();
    }

    private void createPhysics() {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
        registerUpdateHandler(physicsWorld);
    }

    private void setSlowMotionMode() {
        for (Sprite stone : stones) {
            Ball ball = (Ball) stone.getUserData();
            Body body = ball.getBody();
            if (slowMotion) {
                body.setLinearVelocity(body.getLinearVelocity().x*0.5f,
                        body.getLinearVelocity().y*0.5f);
            } else {
                body.setLinearVelocity(body.getLinearVelocity().x/0.5f,
                        body.getLinearVelocity().y/0.5f);
            }
        }
    }

}