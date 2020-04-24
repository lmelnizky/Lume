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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class World8 extends BaseScene {

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
    private int currentPosHor = 0;
    private int currentPosVer = 0;
    private int level;
    private int score = 0;
    private float time = 30*60; //seconds
    private int sideLength;
    private int directionInLevel4 = 1;
    private int xPosLume, yPosLume;
    private int xPosGrume, yPosGrume;
    private int xPosCoin, yPosCoin;

    private long[] stoneTimes;
    private long stoneTime;

    private float shootX1, shootX2, shootY1, shootY2;
    private float swipeX1, swipeX2, swipeY1, swipeY2;

    private Random randomGenerator;

    private Sprite lumeSprite;
    private Sprite lamporghinaSprite;
    private Sprite coinSprite;
    private Sprite[] cannonN, cannonE, cannonS, cannonW;
    private Sprite[] cannonNS, cannonES, cannonSS, cannonWS;
    private Sprite[] cannonNU, cannonEU, cannonSU, cannonWU;
    private ArrayList<Sprite> crackyStones, crackyStonesToRemove, cannonBallsToRemove;
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

    public World8() { //default constructor
        this.level = 1;
        cameFromLevelsScene = false;
        createHUD();
    }

    public World8(int level) { //constructor used when selecting a level
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
        stones = new ArrayList<Sprite>();
        stonesToRemove = new ArrayList<Sprite>();
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
        createLamporghina();
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
        timeText.setColor(Color.WHITE);
        scoreText.setColor(Color.WHITE);
        stoneTime = new Date().getTime();
        for (int i = 0; i < 4; i++) {
            stoneTimes[i] = new Date().getTime();
        }
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
        return SceneType.SCENE_WORLD8;
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
                    if (cameFromLevelsScene) {
                        SceneManager.getInstance().loadWorld8Scene(engine, level);
                    } else {
                        SceneManager.getInstance().loadWorld8Scene(engine, 0);
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

        shootSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.lamporghina_sign_region, vbom);
        moveSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.helmet_region, vbom);
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
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world8_region, vbom));
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
        xPosLume = 3;
        yPosLume = 3;
        lumeSprite = new Sprite(camera.getCenterX() + sideLength, camera.getCenterY() + sideLength, sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.lume_region, vbom);
        secondLayer.attachChild(lumeSprite);
    }

    private void createLamporghina() {
        xPosGrume = 1;
        yPosGrume = 1;
        lamporghinaSprite = new Sprite(camera.getCenterX() - sideLength, camera.getCenterY() - sideLength, sideLength*3/4, sideLength*3/4, resourcesManager.lamporghina_region, vbom);
        secondLayer.attachChild(lamporghinaSprite);
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
                                moveGrume('R');
                            } else { //right to left
                                moveGrume('L');
                            }
                        } else { //vertical swipe
                            if (deltaY > 0) { //up to down
                                moveGrume('U');
                            } else { //down to up
                                moveGrume('D');
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
                char moveDir = 0;
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
                                moveDir = 'R';
                            } else { //right to left
                                moveDir = 'L';
                            }
                        } else { //vertical
                            if (deltaY > 0) { //up to down
                                moveDir = 'U';
                            } else { //down to up
                                moveDir = 'D';
                            }
                        }
                    } else { //TAP - show slowMotion
                        if (cameFromLevelsScene) {
                            slowMotion = !slowMotion;
                            setSlowMotionMode(); //sets values of current moving stones
                            snailSign.setVisible(slowMotion);
                            noSnailSign.setVisible(!slowMotion && cameFromLevelsScene);                        }
                    }

                    moveLume(moveDir);

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

    private void createStones(int level) {
        float randomNumber = randomGenerator.nextFloat();
        double probabilityStone; //= 0.6
        float interval;
        float slowMotionFactor = (slowMotion) ? 2f : 1f;

        switch (level) {
            case 1:
                int direction = randomGenerator.nextInt(4) + 1;
                boolean thorny = false;
                long age;
                age = (new Date()).getTime() - stoneTime;
                interval = 3800*slowMotionFactor;
                if (firstStonesInLevel) interval = 1000*slowMotionFactor;
                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    this.showStonesToScreen(direction, thorny);
                    stoneTime = new Date().getTime();
                }
                break;
            case 2:
                age = (new Date()).getTime() - stoneTime;
                interval = 3400*slowMotionFactor;
                float timeBetweenStones = 1000*slowMotionFactor;
                if (firstStonesInLevel) interval = 1500*slowMotionFactor;

                if (!this.variantUsed && age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    variantUsed = true;
                    variant = randomGenerator.nextInt(4) + 1;
                    variantStage = 1;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                } else if (variantUsed && variantStage == 1 && age >= timeBetweenStones) {
                    variantStage = 2;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                    variantUsed = false;
                }
                break;
            case 3:
                age = (new Date()).getTime() - stoneTime;
                interval = 5500*slowMotionFactor;
                if (firstStonesInLevel) interval = 700*slowMotionFactor;
                if (age >= interval) {
                    variant = randomGenerator.nextInt(4) + 1;
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    this.showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                }
                break;
            case 4:
                age = (new Date()).getTime() - stoneTime;
                interval = 1400*slowMotionFactor; //3000
                if (firstStonesInLevel) interval = 700*slowMotionFactor;
                if (!this.variantUsed && age >= interval && firstStonesInLevel) { //only brings the first stones here
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    currentPosHor = 0;
                    currentPosVer = 0;
                    showLevel4(2, false);
                    showLevel4(3, true);
                }
                break;
        }


    }

    private void showStonesToScreen(int directionVariant, final boolean thornyFirst) {
        int randomRow = randomGenerator.nextInt(3); //values 0 to 2
        float ratio = resourcesManager.screenRatio;

        switch (level) {
            case 1:
                showLevel1(directionVariant);
                break;
            case 2:
                showLevel2();
                break;
            case 3:
                showLevel3();
                break;
            case 4:
                //directly from createStones()
                showLevel4(directionVariant, false);
                break;
        }
    }

    private void showLevel1(int direction) {
        int direction1 = direction;
        int direction2 = (direction > 2) ? direction - 2 : direction + 2;
        int direction3 = direction + 1;
        if (direction3 > 4) direction3 = 1;
        int pos1 = randomGenerator.nextInt(3);
        int pos2 = pos1 + 1;
        float ratio = resourcesManager.screenRatio;
        float factor1And2 = (direction%2 == 0) ? ratio : 1f;
        float factor3 = (direction3%2 == 0) ? ratio : 1f;

        addBall(true, direction1, pos1, 1,1.1f*factor1And2);
        addBall(true, direction1, pos1, 2,1.1f*factor1And2);
        addBall(true, direction1, pos1, 3,1.1f*factor1And2);
        addBall(true, direction2, pos1, 1,1.1f*factor1And2);
        addBall(true, direction2, pos1, 2,1.1f*factor1And2);
        addBall(true, direction2, pos1, 3,1.1f*factor1And2);
        addBall(false, direction3, 0, 1,1.1f*factor3);
        addBall(false, direction3, 1, 1,1.1f*factor3);
        addBall(false, direction3, 2, 1,1.1f*factor3);
    }

    private void showLevel2() {
        float ratio = resourcesManager.screenRatio;
        switch (variant) {
            case 1:
                addBall(true, 1, 2, 1, 1.0f);
                addBall(true, 3, 2, 1, 1.0f);
                addBall(true, 2, 2, 1, 1.0f*ratio);
                addBall(true, 4, 2, 1, 1.0f*ratio);

                if (variantStage == 1) {
                    addBall(false, 2, 1, 1, 1.0f*ratio);
                    addBall(false, 4, 0, 1, 1.0f*ratio);
                } else if (variantStage == 2) {
                    addBall(false, 1, 0, 1, 1.0f);
                    addBall(false, 3, 1, 1, 1.0f);
                }
                break;
            case 2:
                addBall(true, 1, 2, 1, 1.0f);
                addBall(true, 3, 2, 1, 1.0f);
                addBall(true, 2, 0, 1, 1.0f*ratio);
                addBall(true, 4, 0, 1, 1.0f*ratio);

                if (variantStage == 1) {
                    addBall(false, 2, 1, 1, 1.0f*ratio);
                    addBall(false, 4, 2, 1, 1.0f*ratio);
                } else if (variantStage == 2) {
                    addBall(false, 1, 1, 1, 1.0f);
                    addBall(false, 3, 0, 1, 1.0f);
                }
                break;
            case 3:
                addBall(true, 1, 0, 1, 1.0f);
                addBall(true, 3, 0, 1, 1.0f);
                addBall(true, 2, 0, 1, 1.0f*ratio);
                addBall(true, 4, 0, 1, 1.0f*ratio);

                if (variantStage == 1) {
                    addBall(false, 2, 2, 1, 1.0f*ratio);
                    addBall(false, 4, 1, 1, 1.0f*ratio);
                } else if (variantStage == 2) {
                    addBall(false, 1, 1, 1, 1.0f);
                    addBall(false, 3, 2, 1, 1.0f);
                }
                break;
            case 4:
                addBall(true, 1, 0, 1, 1.0f);
                addBall(true, 3, 0, 1, 1.0f);
                addBall(true, 2, 2, 1, 1.0f*ratio);
                addBall(true, 4, 2, 1, 1.0f*ratio);

                if (variantStage == 1) {
                    addBall(false, 2, 0, 1, 1.0f*ratio);
                    addBall(false, 4, 1, 1, 1.0f*ratio);
                } else if (variantStage == 2) {
                    addBall(false, 1, 2, 1, 1.0f);
                    addBall(false, 3, 1, 1, 1.0f);
                }
                break;
        }
    }

    private void showLevel3() {
        float ratio = resourcesManager.screenRatio;
        switch (variant) {
            case 1:
                addBall(true, 1, 2, 1, 0.6f);
                addBall(true, 3, 2, 1, 0.6f);
                addBall(true, 2, 2, 1, 0.6f*ratio);
                addBall(true, 4, 2, 1, 0.6f*ratio);

                addBall(false, 1, 0, 1, 0.6f);
                addBall(false, 1, 1, 1, 0.6f);
                addBall(false, 2, 0, 1, 0.6f*ratio);
                addBall(false, 2, 1, 1, 0.6f*ratio);
                addBall(false, 4, 0, 1, 0.6f*ratio);
                addBall(false, 4, 1, 1, 0.6f*ratio);
                break;
            case 2:
                addBall(true, 1, 2, 1, 0.6f);
                addBall(true, 3, 2, 1, 0.6f);
                addBall(true, 2, 0, 1, 0.6f*ratio);
                addBall(true, 4, 0, 1, 0.6f*ratio);

                addBall(false, 1, 0, 1, 0.6f);
                addBall(false, 1, 1, 1, 0.6f);
                addBall(false, 3, 0, 1, 0.6f);
                addBall(false, 3, 1, 1, 0.6f);
                addBall(false, 2, 2, 1, 0.6f*ratio);
                addBall(false, 2, 1, 1, 0.6f*ratio);
                break;
            case 3:
                addBall(true, 1, 0, 1, 0.6f);
                addBall(true, 3, 0, 1, 0.6f);
                addBall(true, 2, 0, 1, 0.6f*ratio);
                addBall(true, 4, 0, 1, 0.6f*ratio);

                addBall(false, 3, 2, 1, 0.6f);
                addBall(false, 3, 1, 1, 0.6f);
                addBall(false, 2, 2, 1, 0.6f*ratio);
                addBall(false, 2, 1, 1, 0.6f*ratio);
                addBall(false, 4, 2, 1, 0.6f*ratio);
                addBall(false, 4, 1, 1, 0.6f*ratio);
                break;
            case 4:
                addBall(true, 1, 0, 1, 0.6f);
                addBall(true, 3, 0, 1, 0.6f);
                addBall(true, 2, 2, 1, 0.6f*ratio);
                addBall(true, 4, 2, 1, 0.6f*ratio);

                addBall(false, 1, 2, 1, 0.6f);
                addBall(false, 1, 1, 1, 0.6f);
                addBall(false, 3, 2, 1, 0.6f);
                addBall(false, 3, 1, 1, 0.6f);
                addBall(false, 4, 0, 1, 0.6f*ratio);
                addBall(false, 4, 1, 1, 0.6f*ratio);
                break;
        }
    }

    private void showLevel4(int direction, boolean littleBehind) {
        float ratio = resourcesManager.screenRatio;
        float factor = (direction%2==0) ? ratio : 1f;
        int thornyPos = (direction%2==0) ? currentPosHor : currentPosVer; //checks the currentPos of thorny stone.
        int row = (littleBehind) ? 3 : 1;
        addBall((0 == thornyPos), direction, 0, row, 0.6f*factor);
        addBall((1 == thornyPos), direction, 1, row, 0.6f*factor);
        addBall((2 == thornyPos), direction, 2, row, 0.6f*factor);
    }

    //checks if all stones are over half, so that the next level can be started
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
        }

//        stoneCircle = new Circle(x, y, sideLength*3/8);
        stone = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                final Circle lumeCircle, lamporghinaCircle, stoneCircle;
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
                lamporghinaCircle = new Circle(lamporghinaSprite.getX(), lamporghinaSprite.getY(), lamporghinaSprite.getWidth()/2);
                stoneCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);

                if (stoneCircle.collision(lumeCircle) && !gameOverDisplayed  && thorny) {
                    displayGameOverScene();
                } else if (stoneCircle.collision(lumeCircle) && !thorny) {
                    final Sprite crackyBallToRemove = this;

                    engine.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            crackyBallToRemove.detachSelf();
                            crackyBallToRemove.dispose();
                        }
                    });

                    if((score%10 != 0 || score == 30 || score == 0) && level == 4) {
                        Ball ball = (Ball) this.getUserData();
                        int dir = ball.getDirection();
                        dir = (dir > 2) ? dir-2 : dir+2;
                        if (dir%2==0) { //horizontal shot
                            currentPosHor = yPosLume-1;
                        } else { //vertical shot
                            currentPosVer = xPosLume-1;
                        }
                        showStonesToScreen(dir, false); //show stones when the player shoots
                    }
                }
                if (stoneCircle.collision(lamporghinaCircle) && !gameOverDisplayed) {
                    displayGameOverScene();
                }
//                if (this.getX() < -sideLength || this.getY() < -sideLength ||
//                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
//                    if (!thorny) crackyStones.remove(this);
//                    this.detachSelf();
//                    this.dispose();
//                }


                if (this.getX() < -9*sideLength || this.getY() < -9*sideLength ||
                        this.getX() > camera.getWidth() + 9*sideLength || this.getY() > camera.getHeight() + 9*sideLength) {
                    stonesToRemove.add(this);
                    if (!thorny) crackyStonesToRemove.add(this);

                    engine.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Sprite sprite : stonesToRemove) {
                                sprite.detachSelf();
                                sprite.dispose();
                            }
                            crackyStonesToRemove.clear();
                            stonesToRemove.clear();
                        }

                    });
                }

                crackyStones.removeAll(crackyStonesToRemove);
                stones.removeAll(stonesToRemove);

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
        stones.add(stone);
        return stone;
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
                                    activity.unlockWorld(9);
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
    public void moveLume(char direction) {
        switch (direction) {
            case 'R':
                if (xPosLume < 3 && !(xPosLume+1 == xPosGrume && yPosLume == yPosGrume)) {
                    xPosLume++;
                    lumeSprite.setPosition(lumeSprite.getX() + sideLength, lumeSprite.getY());
                }
                break;
            case 'L':
                if (xPosLume > 1 && !(xPosLume-1 == xPosGrume && yPosLume == yPosGrume)) {
                    xPosLume--;
                    lumeSprite.setPosition(lumeSprite.getX() - sideLength, lumeSprite.getY());
                }
                break;
            case 'D':
                if (yPosLume > 1 && !(xPosLume == xPosGrume && yPosLume-1 == yPosGrume)) {
                    yPosLume--;
                    lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() - sideLength);
                }
                break;
            case 'U':
                if (yPosLume < 3 && !(xPosLume == xPosGrume && yPosLume+1 == yPosGrume)) {
                    yPosLume++;
                    lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() + sideLength);
                }
                break;
        }
        coinCheck();
    }

    //moves the player in X or Y direction
    public void moveGrume(char direction) {
        switch (direction) {
            case 'R':
                if (xPosGrume < 3 && !(xPosLume == xPosGrume+1 && yPosLume == yPosGrume)) {
                    xPosGrume++;
                    lamporghinaSprite.setPosition(lamporghinaSprite.getX() + sideLength, lamporghinaSprite.getY());
                }
                break;
            case 'L':
                if (xPosGrume > 1 && !(xPosLume == xPosGrume-1 && yPosLume == yPosGrume)) {
                    xPosGrume--;
                    lamporghinaSprite.setPosition(lamporghinaSprite.getX() - sideLength, lamporghinaSprite.getY());
                }
                break;
            case 'D':
                if (yPosGrume > 1 && !(xPosLume == xPosGrume && yPosLume == yPosGrume-1)) {
                    yPosGrume--;
                    lamporghinaSprite.setPosition(lamporghinaSprite.getX(), lamporghinaSprite.getY() - sideLength);
                }
                break;
            case 'U':
                if (yPosGrume < 3 && !(xPosLume == xPosGrume && yPosLume == yPosGrume+1)) {
                    yPosGrume++;
                    lamporghinaSprite.setPosition(lamporghinaSprite.getX(), lamporghinaSprite.getY() + sideLength);
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