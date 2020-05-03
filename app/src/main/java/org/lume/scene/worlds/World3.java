package org.lume.scene.worlds;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.lume.base.BaseScene;
import org.lume.engine.camera.hud.HUD;
import org.lume.engine.handler.IUpdateHandler;
import org.lume.engine.handler.timer.ITimerCallback;
import org.lume.engine.handler.timer.TimerHandler;
import org.lume.entity.Entity;
import org.lume.entity.IEntity;
import org.lume.entity.modifier.AlphaModifier;
import org.lume.entity.modifier.RotationModifier;
import org.lume.entity.modifier.ScaleModifier;
import org.lume.entity.primitive.Rectangle;
import org.lume.entity.scene.CameraScene;
import org.lume.entity.scene.Scene;
import org.lume.entity.scene.background.SpriteBackground;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.Text;
import org.lume.entity.text.TextOptions;
import org.lume.extension.physics.box2d.FixedStepPhysicsWorld;
import org.lume.extension.physics.box2d.PhysicsConnector;
import org.lume.extension.physics.box2d.PhysicsFactory;
import org.lume.extension.physics.box2d.PhysicsWorld;
import org.lume.input.touch.TouchEvent;
import org.lume.manager.ResourcesManager;
import org.lume.manager.SceneManager;
import org.lume.manager.SceneType;
import org.lume.object.Ball;
import org.lume.object.Circle;
import org.lume.object.Line;
import org.lume.opengl.texture.region.ITextureRegion;
import org.lume.util.adt.align.HorizontalAlign;
import org.lume.util.adt.color.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class World3 extends BaseScene {

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

    private int variant;
    private int variantStage;
    int timeBetweenStones = 0;
    int redArrow = 0;
    int yellowArrow = 0;
    int firstYellowArrow = 0;
    private int level;
    private int score = 0;
    private float time = 30 * 60; //seconds
    private int sideLength;
    private int xPosLume, yPosLume;
    private int xPosCoin, yPosCoin;

    private int firstStonePosL3 = 0;
    private int thirdStonePosL3 = 0;

    private long[] stoneTimes;
    private long stoneTime;

    private float ratio = resourcesManager.screenRatio;
    private float shootX1, shootX2, shootY1, shootY2;
    private float swipeX1, swipeX2, swipeY1, swipeY2;

    private Random randomGenerator;

    private Scene gameOverScene;
    private IEntity firstLayer, secondLayer, thirdLayer;

    private Sprite lumeSprite;
    private Sprite coinSprite;
    private Sprite[] cannonN, cannonE, cannonS, cannonW;
    private Sprite[] cannonNS, cannonES, cannonSS, cannonWS;
    private Sprite[] cannonNU, cannonEU, cannonSU, cannonWU;
    private ArrayList<Sprite> crackyStones, crackyStonesToRemove, cannonBallsToRemove;

    private Sprite v1FirstStone, v2FirstStone, v3FirstStone;
    private Sprite luserSprite;

    private ArrayList<Sprite> elements, elementsToRemove;

    private HUD gameHUD;
    private Text scoreText, levelText, timeText;
    private Sprite shootSign, moveSign, snailSign, noSnailSign;
    private PhysicsWorld physicsWorld;

    private static final int SWIPE_MIN_DISTANCE = 10;

    private Text gameOverText;
    private Sprite replaySprite, finishSprite;

    public World3() { //default constructor
        this.level = 1;
        cameFromLevelsScene = false;
        createHUD();
        showLevelText();
    }

    public World3(int level) { //constructor used when selecting a level
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
        cannonBallsToRemove = new ArrayList<Sprite>();
        elements = new ArrayList<Sprite>();
        elementsToRemove = new ArrayList<Sprite>();
        stoneTimes = new long[4];
        for (int i = 0; i < 4; i++) {
            stoneTimes[i] = 0;
        }

        createLayers();
        createBackground();
        createMusic();
        createPhysics();
        createBoard();
        createHalves();
        createCannons();
        createLume();
        createHUD();

        resetData();

        this.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (!waitingForStonesToDisappear && !worldFinished) {
                    createStones(level);
                    time -= (slowMotion) ? 0.5f : 1f;
                    int displayTime = (int) Math.round(time / 60);
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
        time = 30 * 60;
        timeText.setColor(Color.WHITE);
        scoreText.setColor(Color.WHITE);
        stoneTime = new Date().getTime();
        for (int i = 0; i < 4; i++) {
            stoneTimes[i] = new Date().getTime();
        }
        firstStonesInLevel = true;
        variantUsed = false;
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
        return SceneType.SCENE_WORLD3;
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
                        SceneManager.getInstance().loadWorld3Scene(engine, level);
                    } else {
                        SceneManager.getInstance().loadWorld3Scene(engine, 0);
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

        scoreText = new Text(20, camera.getHeight() - 70, resourcesManager.smallFont, "0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
        scoreText.setAnchorCenter(0, 0);
        scoreText.setText("0");
        gameHUD.attachChild(scoreText);

        levelText = new Text(camera.getWidth()-200, camera.getHeight()-70, resourcesManager.smallFont, "L0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
        levelText.setAnchorCenter(0, 0);
        levelText.setText("L" + String.valueOf(level));
        gameHUD.attachChild(levelText);

        timeText = new Text(camera.getWidth() - 65, camera.getHeight() - 70, resourcesManager.smallFont, "0123456789", new TextOptions(HorizontalAlign.RIGHT), vbom);
        timeText.setAnchorCenter(0, 0);
        timeText.setText("30");
        gameHUD.attachChild(timeText);

        shootSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.shoot_normal_region, vbom);
        moveSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.move_diagonal_sign_region, vbom);
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
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world3_region, vbom));
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

                        //checks if it was horizontal or vertical
                        if (Math.abs(deltaX) > Math.abs(deltaY)) { //horizontal swipe
                            if (deltaX > 0) { //left to right
                                if (2 * Math.abs(deltaY) > Math.abs(deltaX)) {
                                    if (deltaY > 0) {
                                        movePlayer('1');
                                    } else {
                                        movePlayer('2');
                                    }
                                } else {
                                    movePlayer('R');
                                }
                            } else { //right to left
                                if (2 * Math.abs(deltaY) > Math.abs(deltaX)) {
                                    if (deltaY > 0) {
                                        movePlayer('4');
                                    } else {
                                        movePlayer('3');
                                    }
                                } else {
                                    movePlayer('L');
                                }
                            }
                        } else { //vertical swipe
                            if (deltaY > 0) {
                                if (2 * Math.abs(deltaX) > Math.abs(deltaY)) {
                                    if (deltaX > 0) {
                                        movePlayer('1');
                                    } else {
                                        movePlayer('4');
                                    }
                                } else {
                                    movePlayer('U');
                                }
                            } else {
                                if (2 * Math.abs(deltaX) > Math.abs(deltaY)) {
                                    if (deltaX > 0) {
                                        movePlayer('2');
                                    } else {
                                        movePlayer('3');
                                    }
                                } else {
                                    movePlayer('D');
                                }
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
//        Ball ball;
        float slowMotionFactor = (slowMotion) ? 0.5f : 1f;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength / 5 * slowMotionFactor;
        switch (direction) {
            case 1: //up to down
                x = lumeSprite.getX();
                y = lumeSprite.getY() - lumeSprite.getHeight();
                yVel = -speed;
                break;
            case 2: //right to left
                x = lumeSprite.getX() - lumeSprite.getWidth();
                y = lumeSprite.getY();
                xVel = -speed;
                break;
            case 3: //down to up
                x = lumeSprite.getX();
                y = lumeSprite.getY() + lumeSprite.getHeight();
                yVel = speed;
                break;
            case 4: //left to right
                x = lumeSprite.getX() + lumeSprite.getWidth();
                y = lumeSprite.getY();
                xVel = speed;
                break;
        }
        cannonball = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                for (Sprite crackyStone : crackyStones) {
                    final Circle cannonCircle, stoneCircle;
                    cannonCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);
                    stoneCircle = new Circle(crackyStone.getX(), crackyStone.getY(), crackyStone.getWidth() / 2);

                    if (cannonCircle.collision(stoneCircle)) {
                        crackyStonesToRemove.add(crackyStone);
                        elementsToRemove.add(crackyStone);
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
                                        if (!sprite.isDisposed()) { //sprite can be disposed in addArrow or addBall
                                            sprite.detachSelf();
                                            sprite.dispose();
                                        }
                                    }
                                    elementsToRemove.clear();
                                    crackyStonesToRemove.clear();
                                }
                            }
                        });
                    }
                }
                elements.removeAll(elementsToRemove);
                crackyStones.removeAll(crackyStonesToRemove);

                if (this.getX() < -sideLength || this.getY() < - sideLength ||
                        this.getX() > camera.getWidth()+sideLength || this.getY() > camera.getWidth()+sideLength) {
                    this.detachSelf();
                    this.dispose();
                }
            }
        };
        secondLayer.attachChild(cannonball);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, cannonball, BodyType.KinematicBody, FIXTURE_DEF);
//        ball = new Ball(cannonball, "cannonball");
//        body.setUserData(ball);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(cannonball, body, true, false));
    }

    private void createStones(int level) {
        float randomNumber = randomGenerator.nextFloat();
        double probabilityStone; //= 0.6
        long interval;
        float slowMotionFactor = (slowMotion) ? 2f : 1f;

        switch (level) {
            case 1:
                long age = (new Date()).getTime() - stoneTime;
                interval = (long) (3300*slowMotionFactor);
                if (firstStonesInLevel) interval = (long)(1800*slowMotionFactor);
                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    this.showStonesToScreen(0, false);
                    stoneTime = new Date().getTime();                }
                break;
            case 2:
                randomNumber = randomGenerator.nextFloat();
                probabilityStone = 0.2;
                age = (new Date()).getTime() - stoneTime;
                interval = (long) (3000*slowMotionFactor);
                timeBetweenStones = (int)(600*slowMotionFactor);
                if (firstStonesInLevel) interval = (long)(1800*slowMotionFactor);

                if (!this.variantUsed && randomNumber < probabilityStone && age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    variantUsed = true;
                    variant = randomGenerator.nextInt(2) + 1;
                    variantStage = 1;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 1 && age >= timeBetweenStones) {
                    variantStage = 2;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 2 && age >= timeBetweenStones) {
                    variantStage = 3;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 3 && age >= timeBetweenStones) {
                    variantStage = 4;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 4 && age >= timeBetweenStones) {
                    variantStage = 5;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                    variantUsed = false;
                }
                break;
            case 3:
                randomNumber = randomGenerator.nextFloat();
                probabilityStone = 0.2;
                age = (new Date()).getTime() - stoneTime;
                interval = (long) (3000*slowMotionFactor);
                timeBetweenStones = (int)(800*slowMotionFactor);
                if (firstStonesInLevel) interval = (long)(1800*slowMotionFactor);

                if (!this.variantUsed && randomNumber < probabilityStone && age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    variantUsed = true;
                    variant = randomGenerator.nextInt(2) + 1;
                    variantStage = 1;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 1 && age >= timeBetweenStones) {
                    variantStage = 2;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 2 && age >= timeBetweenStones) {
                    variantStage = 3;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 3 && age >= timeBetweenStones) {
                    variantStage = 4;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 4 && age >= timeBetweenStones) {
                    variantStage = 5;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                    variantUsed = false;
                }
                break;
            case 4:
                randomNumber = randomGenerator.nextFloat();
                probabilityStone = 0.2;
                age = (new Date()).getTime() - stoneTime;
                interval = (long) (3000*slowMotionFactor);
                if (firstStonesInLevel) interval = (long)(1800*slowMotionFactor);

                if (!this.variantUsed && randomNumber < probabilityStone && age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    variantUsed = true;
                    variant = randomGenerator.nextInt(4) + 1;
                    variantStage = 1;
                    timeBetweenStones = (int)(900*slowMotionFactor);
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 1 && age >= timeBetweenStones) {
                    variantStage = 2;
                    timeBetweenStones = (int)(600*slowMotionFactor);
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 2 && age >= timeBetweenStones) {
                    variantStage = 3;
                    timeBetweenStones = (int)(600*slowMotionFactor);
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 3 && age >= timeBetweenStones) {
                    variantStage = 4;
                    timeBetweenStones = (int)(1500*slowMotionFactor);
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                    variantUsed = false;
                }
                break;
        }


    }

    private void showStonesToScreen(int directionVariant, final boolean showArrow) {
        int randomRow = randomGenerator.nextInt(3); //values 0 to 2
        boolean thorny = randomGenerator.nextBoolean();

        switch (level) {
            case 1:
                //random directions of arrows and stones
                int randomRed = randomGenerator.nextInt(4) + 1;
                int randomYellow = randomGenerator.nextInt(4) + 5;
                int randomThorny = randomGenerator.nextInt(4) + 1;
                int randomCracky;
                do {
                    randomCracky = randomGenerator.nextInt(4) + 1;
                } while (randomThorny == randomCracky);


                addArrow(randomRed, 1f);
                addArrow(randomYellow, 1f);
                addBall(true, randomThorny, randomRow, 1);
                randomRow = randomGenerator.nextInt(3); //values 0 to 2
                addBall(false, randomCracky, randomRow, 1);
                break;
            case 2:
                showLevel2(directionVariant);
                break;
            case 3:
                showLevel3(directionVariant);
                break;
            case 4:
                showLevel4(directionVariant);
                break;
        }
    }

    private void showLevel2(int variant) {
        int randomRow = randomGenerator.nextInt(3); //values 0 to 2

        switch (variant) {
            case 1:
                if (variantStage == 1) {
                    addArrow(1, 1f);
                    addArrow(2, 1f);
                    addBall(true, 2, 1, 1.5f*ratio);
                    addBall(true, 4, 1, 1.5f*ratio);
                } else if (variantStage == 3) {
                    addArrow(3, 1f);
                    addArrow(4, 1f);
                    addBall(true, 1, 1, 1.5f);
                    addBall(true, 3, 1, 1.5f);
                } else if (variantStage == 5) {
                    addArrow(1, 1f);
                    addArrow(2, 1f);
                    addBall(true, 2, 1, 1.5f*ratio);
                    addBall(true, 4, 1, 1.5f*ratio);
                } else if (variantStage%2 == 0) { // when 2 and 4
                    randomRow = randomGenerator.nextInt(4) + 1;
                    addArrow(randomRow, 1f);
                }
                break;
            case 2:
                if (variantStage == 1) {
                    addArrow(3, 1f);
                    addArrow(4, 1f);
                    addBall(true, 1, 1, 1.5f);
                    addBall(true, 3, 1, 1.5f);
                } else if (variantStage == 3) {
                    addArrow(1, 1f);
                    addArrow(2, 1f);
                    addBall(true, 2, 1, 1.5f*ratio);
                    addBall(true, 4, 1, 1.5f*ratio);
                } else if (variantStage == 5) {
                    addArrow(3, 1f);
                    addArrow(4, 1f);
                    addBall(true, 1, 1, 1.5f);
                    addBall(true, 3, 1, 1.5f);
                } else if (variantStage%2 == 0) { // when 2 and 4
                    randomRow = randomGenerator.nextInt(4) + 1;
                    addArrow(randomRow, 1f);
                }
                break;
        }
    }

    private void showLevel3(int variant) {
        int randomRow24 = randomGenerator.nextInt(3); //values 0 to 2

        switch (variant) {
            case 1:
                if (variantStage == 1) {
                    firstStonePosL3 = randomGenerator.nextInt(2); //0 or 1
                    addArrow(5, 1f);
                    addArrow(6, 1f);
                    addBall(true, 2, firstStonePosL3, 1.5f*ratio);
//                    addBall(true, 4, firstStonePosL3, 1.5f*ratio);
//                    addBall(true, 2, firstStonePosL3+1, 1.5f*ratio);
                    addBall(true, 4, firstStonePosL3+1, 1.5f*ratio);
                } else if (variantStage == 3) {
                    addArrow(7, 1f);
                    addArrow(8, 1f);
                    addBall(true, 2, 0, 1.3f*ratio);
                    addBall(true, 4, 0, 1.3f*ratio);
                    addBall(false, 2, 1, 1.3f*ratio);
                    addBall(false, 4, 1, 1.3f*ratio);
                    addBall(true, 2, 2, 1.3f*ratio);
                    addBall(true, 4, 2, 1.3f*ratio);
                } else if (variantStage == 5) {
                    thirdStonePosL3 = Math.abs(firstStonePosL3-1);
                    addArrow(5, 1f);
                    addArrow(6, 1f);
                    addBall(true, 2, thirdStonePosL3, 1.5f*ratio);
//                    addBall(true, 4, thirdStonePosL3, 1.5f*ratio);
//                    addBall(true, 2, thirdStonePosL3 + 1, 1.5f*ratio);
                    addBall(true, 4, thirdStonePosL3 + 1, 1.5f*ratio);
                } else if (variantStage%2 == 0) { // when 2 and 4
                    randomRow24 = randomGenerator.nextInt(4) + 5;
                    addArrow(randomRow24, 1f);
                    int secondArrowDirection = (randomRow24+1 > 8) ? 5 : randomRow24+1;
                    addArrow(secondArrowDirection, 1f);
                }
                break;
            case 2:
                if (variantStage == 1) {
                    firstStonePosL3 = randomGenerator.nextInt(2); //0 or 1
                    addArrow(7, 1f);
                    addArrow(8, 1f);
                    addBall(true, 1, firstStonePosL3, 1.5f);
//                    addBall(true, 3, firstStonePosL3, 1.5f);
//                    addBall(true, 1, firstStonePosL3+1, 1.5f);
                    addBall(true, 3, firstStonePosL3+1, 1.5f);
                } else if (variantStage == 3) {
                    addArrow(5, 1f);
                    addArrow(6, 1f);
                    addBall(true, 1, 0, 1.3f);
                    addBall(true, 3, 0, 1.3f);
                    addBall(false, 1, 1, 1.3f);
                    addBall(false, 3, 1, 1.3f);
                    addBall(true, 1, 2, 1.3f);
                    addBall(true, 3, 2, 1.3f);
                } else if (variantStage == 5) {
                    thirdStonePosL3 = Math.abs(firstStonePosL3-1);
                    addArrow(7, 1f);
                    addArrow(8, 1f);
                    addBall(true, 1, thirdStonePosL3, 1.5f);
//                    addBall(true, 3, thirdStonePosL3, 1.5f);
//                    addBall(true, 1, thirdStonePosL3 + 1, 1.5f);
                    addBall(true, 3, thirdStonePosL3 + 1, 1.5f);
                } else if (variantStage%2 == 0) { // when 2 and 4
                    randomRow24 = randomGenerator.nextInt(4) + 5;
                    addArrow(randomRow24, 1f);
                    int secondArrowDirection = (randomRow24+1 > 8) ? 5 : randomRow24+1;
                    addArrow(secondArrowDirection, 1f);
                }
                break;
        }
    }

    private void showLevel4(int direction) {
        boolean clockWise = (direction%2==0);
        float speedFactor = 1f;
//                int redArrow = (clockWise) ? direction+(variantStage-1) : direction-(variantStage-1);
//                redArrow = redArrow%4;
//                if (redArrow == 0) redArrow = 4;
//                if (redArrow == -1) redArrow = 3;
//                int yellowArrow = (redArrow%2 == 0) ? redArrow+3 : redArrow+5;

        if (variantStage <= 4) {
            if (variantStage == 1) {
                redArrow = direction;
                speedFactor = 1.4f;
            } else {
                redArrow = (clockWise) ? redArrow+1 : redArrow-1;
                redArrow = redArrow%4;
                if (redArrow == 0) redArrow = 4;
                speedFactor = 1.7f;
            }
            yellowArrow = (redArrow%2 == 0) ? redArrow+3 : redArrow+5;
            if (variantStage == 1) firstYellowArrow = yellowArrow;
            addArrow(redArrow, speedFactor);
            if (redArrow <= 2) {
                addArrow(redArrow+2, speedFactor);
            } else {
                addArrow(redArrow-2, speedFactor);
            }
            addArrow(yellowArrow, speedFactor);


            int ballDirection = (firstYellowArrow%2 == 0) ? yellowArrow-4 : yellowArrow-5;
            if (ballDirection < 1) ballDirection = 4;
            if (ballDirection > 4) ballDirection = 1;
            addBall(false, ballDirection, 1, 0.9f);
        }
    }

    private boolean allStonesGone() {
        boolean allStonesGone = false;
        int stonesOverHalf = 0;
        for (Sprite stone : elements) {
            Ball ball = (Ball) stone.getUserData();
            int direction = ball.getDirection()%4;
            if (direction == 0) direction = 4;

            switch (direction) {
                case 1:
                    if (stone.getY() < camera.getCenterY()-sideLength) stonesOverHalf++;
                    break;
                case 2:
                    if (stone.getX() < camera.getCenterX()-sideLength) stonesOverHalf++;
                    break;
                case 3:
                    if (stone.getY() > camera.getCenterY()+sideLength) stonesOverHalf++;
                    break;
                case 4:
                    if (stone.getX() > camera.getCenterX()+sideLength) stonesOverHalf++;
                    break;
            }
        }
        if (stonesOverHalf == elements.size()) allStonesGone = true;
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

    public Sprite addBall(final boolean thorny, int direction, int position, float speedFactor) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        float slowMotionFactor = (slowMotion) ? 0.5f : 1f;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/15 * speedFactor * slowMotionFactor;

        final Sprite stone;
        ITextureRegion textureRegion = (thorny) ? resourcesManager.thorny_stone_region : resourcesManager.cracky_stone_region;
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
                    elementsToRemove.add(this);
                    if (!thorny) crackyStonesToRemove.add(this);

                    engine.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Sprite sprite : elementsToRemove) {
                                sprite.detachSelf();
                                sprite.dispose();
                            }
                            crackyStonesToRemove.clear();
                            elementsToRemove.clear();
                        }

                    });
                }

                crackyStones.removeAll(crackyStonesToRemove);
                elements.removeAll(elementsToRemove);

            }
        };
        secondLayer.attachChild(stone);
        //animate cannon
        animateCannon(direction, position);

        final Body body = PhysicsFactory.createCircleBody(physicsWorld, stone, BodyType.KinematicBody, FIXTURE_DEF);
        if (thorny) {
        } else {
            crackyStones.add(stone);
        }
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(stone, body, true, false));
        ball = new Ball(direction, null, body, stone, thorny, false, speedFactor);
        stone.setUserData(ball);
        elements.add(stone);
        return stone;
    }

    private void addArrow(final int direction, float speedFactor) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        float slowMotionFactor = (slowMotion) ? 0.5f : 1f;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = speedFactor * sideLength / 15 * slowMotionFactor;
        boolean yellow = false;

        final Sprite arrow;
        Ball ball;

        switch (direction) {
            case 1: //1-4: red arrows
                x = camera.getCenterX() - camera.getCenterY() + sideLength / 5;
                y = camera.getHeight() - sideLength / 5;
                xVel = speed;
                yVel = -speed;
                break;
            case 2:
                x = camera.getCenterX() + camera.getCenterY() - sideLength / 5;
                y = camera.getHeight() - sideLength / 5;
                xVel = -speed;
                yVel = -speed;
                break;
            case 3:
                x = camera.getCenterX() + camera.getCenterY() - sideLength / 5;
                y = sideLength / 5;
                xVel = -speed;
                yVel = speed;
                break;
            case 4:
                x = camera.getCenterX() - camera.getCenterY() + sideLength / 5;
                y = sideLength / 5;
                xVel = speed;
                yVel = speed;
                break;
            case 5: //5 - 8: yellow arrows
                yellow = true;
                x = camera.getCenterX() - camera.getCenterY() + sideLength / 5 + sideLength;
                y = camera.getHeight() - sideLength / 5;
                xVel = speed;
                yVel = -speed;
                break;
            case 6:
                yellow = true;
                x = camera.getCenterX() + camera.getCenterY() - sideLength / 5 - sideLength;
                y = camera.getHeight() - sideLength / 5;
                xVel = -speed;
                yVel = -speed;
                break;
            case 7:
                yellow = true;
                x = camera.getCenterX() + camera.getCenterY() - sideLength / 5 - sideLength;
                y = sideLength / 5;
                xVel = -speed;
                yVel = speed;
                break;
            case 8:
                yellow = true;
                x = camera.getCenterX() - camera.getCenterY() + sideLength / 5 + sideLength;
                y = sideLength / 5;
                xVel = speed;
                yVel = speed;
                break;
        }

        ITextureRegion textureRegion = (yellow) ? resourcesManager.arrow_yellow_region : resourcesManager.arrow_region;

        arrow = new Sprite(x, y, sideLength * 2, sideLength * 2 / 5, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                final Circle lumeCircle;
                final Line line;
                float peakX = 0;
                float peakY = 0;
                float arrowLength = 2 * sideLength;
                double radOf45 = Math.PI / 4;

                switch (direction) {
                    case 1:
                        peakX = (float) (this.getX() + Math.sin(radOf45) * arrowLength / 2);
                        peakY = (float) (this.getY() - Math.sin(radOf45) * arrowLength / 2);
                        break;
                    case 2:
                        peakX = (float) (this.getX() - Math.sin(radOf45) * arrowLength / 2);
                        peakY = (float) (this.getY() - Math.sin(radOf45) * arrowLength / 2);
                        break;
                    case 3:
                        peakX = (float) (this.getX() - Math.sin(radOf45) * arrowLength / 2);
                        peakY = (float) (this.getY() + Math.sin(radOf45) * arrowLength / 2);
                        break;
                    case 4:
                        peakX = (float) (this.getX() + Math.sin(radOf45) * arrowLength / 2);
                        peakY = (float) (this.getY() + Math.sin(radOf45) * arrowLength / 2);
                        break;
                    case 5:
                        peakX = (float) (this.getX() + Math.sin(radOf45) * arrowLength / 2);
                        peakY = (float) (this.getY() - Math.sin(radOf45) * arrowLength / 2);
                        break;
                    case 6:
                        peakX = (float) (this.getX() - Math.sin(radOf45) * arrowLength / 2);
                        peakY = (float) (this.getY() - Math.sin(radOf45) * arrowLength / 2);
                        break;
                    case 7:
                        peakX = (float) (this.getX() - Math.sin(radOf45) * arrowLength / 2);
                        peakY = (float) (this.getY() + Math.sin(radOf45) * arrowLength / 2);
                        break;
                    case 8:
                        peakX = (float) (this.getX() + Math.sin(radOf45) * arrowLength / 2);
                        peakY = (float) (this.getY() + Math.sin(radOf45) * arrowLength / 2);
                        break;
                }
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
                line = new Line(this.getX(), this.getY(), peakX, peakY);

                if (line.collision(lumeCircle)) {
                    displayGameOverScene();
                    score = 0;
                }

                if (this.getX() < -sideLength || this.getY() < -sideLength ||
                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
//                    this.detachSelf();
//                    this.dispose();

                    elementsToRemove.add(this);

                    engine.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Sprite sprite : elementsToRemove) {
                                sprite.detachSelf();
                                sprite.dispose();
                            }
                            elementsToRemove.clear();
                        }

                    });
                }
                elements.removeAll(elementsToRemove);
            }
        };
        secondLayer.attachChild(arrow);
        arrow.setRotation(((direction - 1) % 4) * 90 + 45);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, arrow, BodyType.KinematicBody, FIXTURE_DEF);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(arrow, body, true, false));
        ball = new Ball(direction, null, body, arrow, false, false, speedFactor);
        arrow.setUserData(ball);
        elements.add(arrow);
    }

    private void addToScore(int i) {
        score += i;
        scoreText.setText(String.valueOf(score));
        if (score % 10 == 9) {
            scoreText.setColor(android.graphics.Color.parseColor("#ffc300"));
        }
        if (score % 10 == 0) {
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
                        if (level == 5) {
                            worldFinished = true; //just to stop time
                            ResourcesManager.getInstance().backgroundMusic.stop();
                            ResourcesManager.getInstance().backgroundMusic.pause();
                            ResourcesManager.getInstance().easySound.play();
                            Text tooEasyText = new Text(camera.getCenterX(), sideLength*7.0f, resourcesManager.bigFont,
                                    "TOO EASY!", vbom);
                            int color = android.graphics.Color.parseColor("#00ff00");
                            tooEasyText.setColor(color);
                            attachChild(tooEasyText);
                            tooEasyText.registerEntityModifier(new ScaleModifier(2f, 0.5f, 1.5f));
                            engine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
                                public void onTimePassed(final TimerHandler pTimerHandler) {
                                    engine.unregisterUpdateHandler(pTimerHandler);
                                    activity.unlockWorld(4);
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
                }
                break;
            case 'L':
                if (xPosLume > 1) {
                    xPosLume--;
                }
                break;
            case 'D':
                if (yPosLume > 1) {
                    yPosLume--;
                }
                break;
            case 'U':
                if (yPosLume < 3) {
                    yPosLume++;
                }
                break;
            case '1':
                if (xPosLume < 3) {
                    xPosLume++;
                }
                if (yPosLume < 3) {
                    yPosLume++;
                }
                break;
            case '2':
                if (xPosLume < 3) {
                    xPosLume++;
                }
                if (yPosLume > 1) {
                    yPosLume--;
                }
                break;
            case '3':
                if (xPosLume > 1) {
                    xPosLume--;
                }
                if (yPosLume > 1) {
                    yPosLume--;
                }
                break;
            case '4':
                if (xPosLume > 1) {
                    xPosLume--;
                }
                if (yPosLume < 3) {
                    yPosLume++;
                }
                break;
        }
        lumeSprite.setPosition(camera.getCenterX() - sideLength + (xPosLume-1)*sideLength,
                camera.getCenterY() - sideLength + (yPosLume-1)*sideLength);
        coinCheck();
    }

    private void createPhysics() {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
        registerUpdateHandler(physicsWorld);
    }

    private void setSlowMotionMode() {
        for (Sprite stone : elements) {
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