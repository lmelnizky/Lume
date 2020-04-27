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
import org.andengine.object.Line;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class World7 extends BaseScene {

    final short CATEGORY_CANNONBALL = 0x0001;  // 0000000000000001 in binary
    final short CATEGORY_THORNY = 0x0002; // 0000000000000010 in binary
    final short CATEGORY_CRACKY = 0x0003;

    final short MASK_CANNONBALL = ~CATEGORY_THORNY;
    final short MASK_THORNY = ~(CATEGORY_CANNONBALL | CATEGORY_THORNY | CATEGORY_CRACKY);
    final short MASK_CRACKY = ~(CATEGORY_THORNY | CATEGORY_CRACKY);

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
    private int direction = 0;
    private int variantStage;
    private int level = 1;
    private int score = 0;
    private float time = 30*60; //seconds
    private int sideLength;
    private int xPosLume, yPosLume;
    private int xPosCoin, yPosCoin;

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


    public World7() { //default constructor
        this.level = 1;
        cameFromLevelsScene = false;
        createHUD();
    }

    public World7(int level) { //constructor used when selecting a level
        this.level = level;
        cameFromLevelsScene = true;
        createHUD();
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
        createLume();
        createHalves();
        createCannons();
        createHUD();

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
    }

    public void resetData() {
        time = 30*60;
        for (int i = 0; i < 4; i++) {
            stoneTimes[i] = 0;
        }
        timeText.setColor(Color.WHITE);
        scoreText.setColor(Color.WHITE);
        stoneTime = new Date().getTime();
        firstStonesInLevel = true;
        variantUsed = false;
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
            SceneManager.getInstance().loadWorlds5to8Scene(engine);
            disposeHUD();
        } else {
            SceneManager.getInstance().loadMenuScene(engine);
            disposeHUD();
        }
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_WORLD7;
    }

    @Override
    public void disposeScene() {
        camera.setHUD(null);
        camera.setChaseEntity(null);
        camera.setCenter(camera.getCenterX(), camera.getCenterY());
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
                    if (cameFromLevelsScene) {
                        SceneManager.getInstance().loadWorld7Scene(engine, level);
                    } else {
                        SceneManager.getInstance().loadWorld7Scene(engine, 0);
                    }
                    disposeHUD();
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
                    if (cameFromLevelsScene) {
                        SceneManager.getInstance().loadWorlds5to8Scene(engine);
                    } else {
                        SceneManager.getInstance().loadMenuScene(engine);
                    }
                    disposeHUD();
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

        shootSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.shoot_diagonal_region, vbom);
        moveSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.move_diagonal_region, vbom);
        snailSign = new Sprite(camera.getCenterX() + 4*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.snail_sign_region, vbom);
        noSnailSign = new Sprite(camera.getCenterX() + 4*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.no_snail_sign_region, vbom);
        gameHUD.attachChild(shootSign);
        gameHUD.attachChild(moveSign);
        gameHUD.attachChild(snailSign);
        gameHUD.attachChild(noSnailSign);

        snailSign.setVisible(slowMotion);
        noSnailSign.setVisible(!slowMotion && cameFromLevelsScene);
        camera.setHUD(gameHUD);
    }

    private void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world7_region, vbom));
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
            coinSprite.setPosition(camera.getCenterX() - sideLength + ((xPosCoin - 1) * sideLength),
                    camera.getCenterY() - sideLength + ((yPosCoin - 1) * sideLength));
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
                    if (deltaX > SWIPE_MIN_DISTANCE || deltaY > SWIPE_MIN_DISTANCE
                            || deltaX < -SWIPE_MIN_DISTANCE || deltaY < -SWIPE_MIN_DISTANCE) {
                        createCannonball(deltaX, deltaY);
                    }


                    return true;
                } else {
                    return false;
                }
            }

            ;
        };

        final Rectangle swipeRight = new Rectangle
                (camera.getCenterX() + resourcesManager.screenWidth / 4, camera.getCenterY(),
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

    private void createCannonball(float deltaX, float deltaY) {
        ITextureRegion textureRegion = resourcesManager.cannonball_region;
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        FIXTURE_DEF.filter.categoryBits = CATEGORY_CANNONBALL;
        FIXTURE_DEF.filter.maskBits = MASK_CANNONBALL;
        final Sprite cannonball;
        Ball ball;
        float slowMotionFactor = (slowMotion) ? 0.5f : 1f;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/5 * slowMotionFactor;

        float factor, vectorLength;
        float dirVectorX, dirVectorY;

        vectorLength = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        factor = (float)  0.7f*sideLength/vectorLength;
        dirVectorX = deltaX*factor*slowMotionFactor;
        dirVectorY = deltaY*factor*slowMotionFactor;

        x = lumeSprite.getX();
        y = lumeSprite.getY();
        xVel = dirVectorX;
        yVel = dirVectorY;

        cannonball = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                for (Sprite crackyStone : crackyStones) {
                    final Circle cannonCircle, stoneCircle;
                    cannonCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);
                    stoneCircle = new Circle(crackyStone.getX(), crackyStone.getY(), crackyStone.getWidth() / 2);

                    if (cannonCircle.collision(stoneCircle) && stoneOnField(crackyStone)) {
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

                if (this.getX() < - 20*sideLength || this.getY() < - 20*sideLength ||
                        this.getX() > camera.getWidth()+20*sideLength || this.getY() > camera.getWidth()+ 20*sideLength) {
                    this.detachSelf();
                    this.dispose();
                }
            }
        };

        secondLayer.attachChild(cannonball);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, cannonball, BodyType.KinematicBody, FIXTURE_DEF);
        body.setLinearVelocity(xVel, yVel);
        ball = new Ball();
        ball.setBody(body);
        cannonball.setUserData(ball);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(cannonball, body, true, false));
    }

    private void createStones(int level) {
        float randomNumber = randomGenerator.nextFloat();
        double probabilityStone; //= 0.6
        float interval;
        float slowMotionFactor = (slowMotion) ? 2f : 1f;

        switch (level) {
            case 1:
                long age;
                age = (new Date()).getTime() - stoneTime;
                interval = 6200*slowMotionFactor;
                if (firstStonesInLevel) interval = 1500*slowMotionFactor;
                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    variant = randomGenerator.nextInt(4) + 1; //numbers 1 to 4
                    this.showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                }
                break;
            case 2:
                age = (new Date()).getTime() - stoneTime;
                interval = 5200*slowMotionFactor;
                if (firstStonesInLevel) interval = 1500*slowMotionFactor;
                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    direction = randomGenerator.nextInt(4) + 5; //numbers 5 to 8
                    this.showStonesToScreen(direction, false);
                    stoneTime = new Date().getTime();
                }
                break;
            case 3:
                age = (new Date()).getTime() - stoneTime;
                interval = 4500*slowMotionFactor;
                float timeBetweenStones = 1000*slowMotionFactor;
                if (firstStonesInLevel) interval = 1500*slowMotionFactor;

                if (!this.variantUsed && age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    variantUsed = true;
                    variant = randomGenerator.nextInt(2) + 1;
                    direction = randomGenerator.nextInt(4) + 1;
                    variantStage = 1;
                    showStonesToScreen(direction, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 1 && age >= timeBetweenStones) {
                    variantStage = 2;
                    direction = randomGenerator.nextInt(4) + 1;
                    showStonesToScreen(direction, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 2 && age >= timeBetweenStones) {
                    variantStage = 3;
                    direction = randomGenerator.nextInt(4) + 1;
                    showStonesToScreen(direction, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 3 && age >= timeBetweenStones) {
                    variantStage = 4;
                    direction = randomGenerator.nextInt(4) + 1;
                    showStonesToScreen(direction, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 4 && age >= timeBetweenStones) {
                    variantStage = 5;
                    direction = randomGenerator.nextInt(4) + 1;
                    showStonesToScreen(direction, false);
                    stoneTime = new Date().getTime();
                    variantUsed = false;
                }
                break;
            case 4:
                age = (new Date()).getTime() - stoneTime;
                interval = 8500*slowMotionFactor;
                direction = randomGenerator.nextInt(2)*2 + 2;
                if (firstStonesInLevel) interval = 200*slowMotionFactor;

                if (!this.variantUsed && age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    showStonesToScreen(direction, false);
                    stoneTime = new Date().getTime();
                }
                break;
        }
    }

    private void showStonesToScreen(int directionVariant, final boolean thornyFirst) {
        int randomRow = randomGenerator.nextInt(3); //values 0 to 2
        int direction1, direction2;

        switch (level) {
            case 1:
                showLevel1(directionVariant);
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

    private void showLevel1(int variant) {
        switch (variant){
            case 1:
                addBall(true, 2, 1, 2, 0.9f*ratio);
                addBall(true, 2, 2, 2, 0.9f*ratio);
                addBall(true, 3, 0, 0.5f, 0.9f);
                addBall(true, 3, 1, 2, 0.9f);
                addBall(true, 3, 2, 2, 0.9f);


                if (score < 9) {
                    addBall(true, 4, 0, 7.5f, 0.9f*ratio);
                    addBall(true, 4, 1, 7.5f, 0.9f*ratio);
                    addBall(true, 4, 2, 6f, 0.9f*ratio);
                    addBall(true, 1, 0, 3.5f, 0.9f); //was 0.9f
                    addBall(true, 1, 1, 3.5f, 0.9f);
                }

                addBall(false, 5, 0, 2, 0.9f);
                addBall(false, 6, -1, 5, 0.9f);
                addBall(false, 7, 0, 3, 0.9f);
                addBall(false, 8, 0, 4, 0.9f);
                break;
            case 2:
                addBall(true, 2, 0, 0.5f, 0.9f*ratio);
                addBall(true, 2, 1, 2, 0.9f*ratio);
                addBall(true, 2, 2, 2, 0.9f*ratio);
                addBall(true, 1, 0, 2f, 0.9f);
                addBall(true, 1, 1, 2f, 0.9f);


                if (score < 9) {
                    addBall(true, 3, 0, 3.5f, 0.9f);
                    addBall(true, 3, 1, 5f, 0.9f);
                    addBall(true, 3, 2, 5f, 0.9f);
                    addBall(true, 4, 0, 3.5f, 0.9f*ratio);
                    addBall(true, 4, 1, 3.5f, 0.9f*ratio);
                }


                addBall(false, 5, -3, 5, 0.9f);
                addBall(false, 6, 0, 3, 0.9f);
                addBall(false, 7, -2, 4, 0.9f);
                addBall(false, 8, 3, 2, 0.9f);
                break;
            case 3:
                addBall(true, 4, 0, 2f, 0.9f*ratio);
                addBall(true, 4, 1, 2f, 0.9f*ratio);
                addBall(true, 1, 0, 2f, 0.9f);
                addBall(true, 1, 1, 2f, 0.9f);
                addBall(true, 1, 2, 0.5f, 0.9f);


                if (score < 9) {
                    addBall(true, 2, 0, 6f, 0.9f*ratio);
                    addBall(true, 2, 1, 7.5f, 0.9f*ratio);
                    addBall(true, 2, 2, 7.5f, 0.9f*ratio);
                    addBall(true, 3, 1, 3.5f, 0.9f); //was 0.9f
                    addBall(true, 3, 2, 3.5f, 0.9f);
                }

                addBall(false, 5, 0, 3, 0.9f);
                addBall(false, 6, 0, 4, 0.9f);
                addBall(false, 7, 0, 2, 0.9f);
                addBall(false, 8, -1, 5f, 0.9f);
                break;
            case 4:
                addBall(true, 3, 1, 2f, 0.9f);
                addBall(true, 3, 2, 2f, 0.9f);
                addBall(true, 4, 0, 2f, 0.9f*ratio);
                addBall(true, 4, 1, 2f, 0.9f*ratio);
                addBall(true, 4, 2, 0.5f, 0.9f*ratio);


                if (score < 9) {
                    addBall(true, 2, 1, 3.5f, 0.9f*ratio);
                    addBall(true, 2, 2, 3.5f, 0.9f*ratio);
                    addBall(true, 1, 0, 5f, 0.9f);
                    addBall(true, 1, 1, 5f, 0.9f);
                    addBall(true, 1, 2, 3.5f, 0.9f);
                }


                addBall(false, 5, -2, 4, 0.9f);
                addBall(false, 6, 3, 2, 0.9f);
                addBall(false, 7, -3, 5, 0.9f);
                addBall(false, 8, 0, 3, 0.9f);
                break;
        }
    }

    //two rows of stones from straight and two from dia
    private void showLevel2(int direction) {
        int upOrDownSummand = randomGenerator.nextInt(2)*2 - 1; //-1 or 1
        int direction2 = direction + upOrDownSummand;
        int direction3 = (direction > 6) ? direction-2 : direction+2;
        if (direction2 < 5) direction2 = 8;
        if (direction2 > 8) direction2 = 5;

        addBall(true, direction, 0, 1, 1f);
        addBall(true, direction, 1, 2, 1f);
        addBall(true, direction, 2, 3, 1f);
        addBall(true, direction2, -1, 2, 1f);
        addBall(true, direction2, 0, 3, 1f);
        addBall(true, direction2, 1, 4, 1f);

        addBall(false, direction3, 0, 1, 1f);
        addBall(false, direction3, 1, 2, 1f);
        addBall(false, direction3, 2, 3, 1f);
    }

    private void showLevel3(int direction) {
        int arrowSummand = (variantStage%2 == 0) ? 2 : 0;
        boolean isHorizontal = (direction%2 == 0);
        float factor = (isHorizontal) ? ratio : 1f;
        boolean vFirst = randomGenerator.nextBoolean();

        if (variant == 1) {
            if (variantStage == 1) {
                addArrow(1, 1f);
                addArrow(2, 1f);
                addArrow(3, 1f);
                addArrow(4, 1f);
            } else {
                addArrow(1 + arrowSummand, 1f);
                addArrow(2 + arrowSummand, 1f);
            }
        } else {
            if (variantStage == 1) {
                addArrow(5, 1f);
                addArrow(6, 1f);
                addArrow(7, 1f);
                addArrow(8, 1f);
            } else {
                addArrow(5 + arrowSummand, 1f);
                addArrow(6 + arrowSummand, 1f);
            }
        }

        if (variantStage == 1) {
            if (vFirst) {
                addBall(true, direction, 2, 1, 1f*factor);
                addBall(true, direction, 0, 1, 1f*factor);
                addBall(true, direction, 1, 2, 1f*factor);
                addBall(false, direction, 0, 2, 1f*factor);
                addBall(false, direction, 2, 2, 1f*factor);
            } else {
                addBall(true, direction, 1, 1, 1f*factor);
                addBall(true, direction, 0, 2, 1f*factor);
                addBall(true, direction, 2, 2, 1f*factor);
                addBall(false, direction, 1, 2, 1f*factor);
            }
        } else if (variantStage == 3) {
            if (vFirst) {
                addBall(true, direction, 1, 1, 1f*factor);
                addBall(true, direction, 0, 2, 1f*factor);
                addBall(true, direction, 2, 2, 1f*factor);
                addBall(false, direction, 1, 2, 1f*factor);
            } else {
                addBall(true, direction, 2, 1, 1f*factor);
                addBall(true, direction, 0, 1, 1f*factor);
                addBall(true, direction, 1, 2, 1f*factor);
                addBall(false, direction, 0, 2, 1f*factor);
                addBall(false, direction, 2, 2, 1f*factor);
            }
        } else if (variantStage == 5){
            if (vFirst) {
                addBall(true, direction, 2, 1, 1f*factor);
                addBall(true, direction, 0, 1, 1f*factor);
                addBall(true, direction, 1, 2, 1f*factor);
                addBall(false, direction, 0, 2, 1f*factor);
                addBall(false, direction, 2, 2, 1f*factor);
            } else {
                addBall(true, direction, 1, 1, 1f*factor);
                addBall(true, direction, 0, 2, 1f*factor);
                addBall(true, direction, 2, 2, 1f*factor);
                addBall(false, direction, 1, 2, 1f*factor);
            }
        }
    }

    private void showLevel4(int direction) {
        float factor = 1.6f;
        addBall(true, direction, 0, 1*factor, 0.6f*ratio);
        addBall(false, direction, 1, 1*factor, 0.6f*ratio);
        addBall(true, direction, 2, 1*factor, 0.6f*ratio);
        addBall(false, direction, 0, 2*factor, 0.6f*ratio);
        addBall(true, direction, 1, 2*factor, 0.6f*ratio);
        addBall(false, direction, 2, 2*factor, 0.6f*ratio);
        addBall(true, direction, 0, 3*factor, 0.6f*ratio);
        addBall(false, direction, 1, 3*factor, 0.6f*ratio);
        addBall(true, direction, 2, 3*factor, 0.6f*ratio);
        addBall(false, direction, 0, 4*factor, 0.6f*ratio);
        addBall(true, direction, 1, 4*factor, 0.6f*ratio);
        addBall(false, direction, 2, 4*factor, 0.6f*ratio);
        addBall(true, direction, 0, 5*factor, 0.6f*ratio);
        addBall(false, direction, 1, 5*factor, 0.6f*ratio);
        addBall(true, direction, 2, 5*factor, 0.6f*ratio);
        addBall(false, direction, 0, 6*factor, 0.6f*ratio);
        addBall(true, direction, 1, 6*factor, 0.6f*ratio);
        addBall(false, direction, 2, 6*factor, 0.6f*ratio);
        addBall(true, direction, 0, 7*factor, 0.6f*ratio);
        addBall(false, direction, 1, 7*factor, 0.6f*ratio);
        addBall(true, direction, 2, 7*factor, 0.6f*ratio);

        int randomDirection = randomGenerator.nextInt(2)*2 + 1;int randomPosition = randomGenerator.nextInt(2)*2;
        int secondPosition = (randomPosition == 0) ? 2 : 0;
        addBall(true, randomDirection, randomPosition, 1*factor, 0.6f);
        randomDirection = randomGenerator.nextInt(2)*2 + 1;
        addBall(true, randomDirection, secondPosition, 3*factor, 0.6f);
        randomDirection = randomGenerator.nextInt(2)*2 + 1;
        addBall(true, randomDirection, randomPosition, 5*factor, 0.6f);
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

    private boolean stoneOnField(Sprite stone) {
        boolean onField = false;
        if (stone.getX() <= camera.getWidth() && stone.getX() >= 0 &&
            stone.getY() >= 0 && stone.getY() <= camera.getHeight()) onField = true;
        return onField;
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

    public Sprite addBall(final boolean thorny, int direction, int position, float row, float speedFactor) {
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
                y = camera.getHeight() - sideLength / 2 + sideLength*row;
                yVel = -speed;
                break;
            case 2:
                x = camera.getWidth() - sideLength / 2 + sideLength*row;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = -speed;
                break;
            case 3:
                x = camera.getCenterX()-sideLength + sideLength*position;
                y = sideLength / 2 - sideLength*row;
                yVel = speed;
                break;
            case 4:
                x = sideLength / 2 - sideLength*row;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = speed;
                break;
            case 5:
                x = camera.getCenterX() - camera.getCenterY() +
                        sideLength/2 - sideLength*3 + sideLength*position;
                y = camera.getHeight() - sideLength/2 + sideLength*row;
                xVel = speed;
                yVel = -speed;
                break;
            case 6:
                x = camera.getCenterX() + camera.getCenterY() -
                        sideLength/2 + sideLength*3 - sideLength*position;
                y = camera.getHeight() - sideLength/2 + sideLength*row;
                xVel = -speed;
                yVel = -speed;
                break;
            case 7:
                x = camera.getCenterX() + camera.getCenterY() -
                        sideLength/2 + sideLength*3 - sideLength*position;
                y = sideLength/2 - sideLength*row;
                xVel = -speed;
                yVel = speed;
                break;
            case 8:
                x = camera.getCenterX() - camera.getCenterY() +
                        sideLength/2 - sideLength*3 + sideLength*position;
                y = sideLength/2 - sideLength*row;
                xVel = speed;
                yVel = speed;
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
                }
//                if (this.getX() < -sideLength || this.getY() < -sideLength ||
//                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
//                    if (!thorny) crackyStones.remove(this);
//                    this.detachSelf();
//                    this.dispose();
//                }


                if (this.getX() < -19*sideLength || this.getY() < -19*sideLength ||
                        this.getX() > camera.getWidth() + 19*sideLength || this.getY() > camera.getHeight() + 19*sideLength) {
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

        //animate cannons
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
            case 1:
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
            case 5:
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
        if (score%10 == 9) {
            scoreText.setColor(android.graphics.Color.parseColor("#ffc300"));
        }
        if (score%10 == 0) {
            if (cameFromLevelsScene) {
                ResourcesManager.getInstance().backgroundMusic.stop();
                SceneManager.getInstance().loadWorlds5to8Scene(engine);
                disposeHUD();
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
                            engine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
                                public void onTimePassed(final TimerHandler pTimerHandler) {
                                    engine.unregisterUpdateHandler(pTimerHandler);
                                    activity.unlockWorld(8);
                                    SceneManager.getInstance().loadMenuScene(engine);
                                }
                            }));
                        } else {
                            levelText.setText("L" + String.valueOf(level));
                            resetData();
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