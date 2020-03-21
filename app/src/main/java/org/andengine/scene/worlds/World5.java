package org.andengine.scene.worlds;

import android.util.Log;

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
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnSceneTouchListener;
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
import org.andengine.util.adt.io.in.ResourceInputStreamOpener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class World5 extends BaseScene {

    private boolean slowMotion = false;
    private boolean variantUsed = false;
    private boolean gameOverDisplayed = false;
    private boolean firstStonesInLevel = true;
    private boolean cameFromLevelsScene;
    private boolean waitingForStonesToDisappear = false;
    private boolean worldFinished = false;

    private static final int FIRST_LAYER = 0; //is used for ground, player and coin
    private static final int SECOND_LAYER = 1; //is used for  stones
    private int variant = 1;
    private int variantStage;
    int timeBetweenStones = 0;
    int redArrow = 0;
    int yellowArrow = 0;
    private int level;
    private int score = 0;
    private float time = 30 * 60; //seconds
    private int sideLength;
    private int xPosLume, yPosLume;
    private int xPosCoin, yPosCoin;
    private int direction1 = 1; //just default values, are overwritten
    private int direction2 = 2;

    private long[] stoneTimes;
    private long stoneTime;

    private float ratio = resourcesManager.screenRatio;
    private float shootX1, shootX2, shootY1, shootY2;
    private float swipeX1, swipeX2, swipeY1, swipeY2;

    private Random randomGenerator;

    private IEntity firstLayer, secondLayer;

    private Sprite lumeSprite;
    private Sprite coinSprite;
    private Sprite cannonsN, cannonsE, cannonsS, cannonsW;
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
    private Sprite replaySprite;

    public World5() { //default constructor
        this.level = 1;
        cameFromLevelsScene = false;
        createHUD();
    }

    public World5(int level) { //constructor used when selecting a level
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
        createCannons();
//        createCoin();
        createHalves();
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
                        luserSprite = new Sprite(lumeSprite.getX()-lumeSprite.getWidth()*4/10,
                                lumeSprite.getY() + lumeSprite.getHeight()*5/10,
                                lumeSprite.getWidth(), lumeSprite.getWidth(),
                                ResourcesManager.getInstance().finger_luser, vbom);
                        secondLayer.attachChild(luserSprite);
                        displayGameOverText();
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
        time = 30 * 60;
        timeText.setColor(Color.WHITE);
        scoreText.setColor(Color.WHITE);
        stoneTime = new Date().getTime();
        for (int i = 0; i < 4; i++) {
            stoneTimes[i] = new Date().getTime();
        }
        firstStonesInLevel = true;
        variantUsed = false;
        variant = 1;
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
        ResourcesManager.getInstance().backgroundMusic.stop(); //TODO for all Worlds
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
        return SceneType.SCENE_WORLD5;
    }

    @Override
    public void disposeScene() {
        camera.setHUD(null);
        camera.setChaseEntity(null);
        camera.setCenter(camera.getCenterX(), camera.getCenterY());

        // TODO code responsible for disposing scene
        // removing all game scene objects.
    }

    private void displayGameOverText() {
        gameOverDisplayed = true;

        Scene gameOverScene = new CameraScene(camera);
        gameOverScene.setBackgroundEnabled(false);

        ResourcesManager.getInstance().backgroundMusic.stop();
        ResourcesManager.getInstance().backgroundMusic.pause();
        ResourcesManager.getInstance().luserSound.play();
        if (cameFromLevelsScene) activity.showSlowMoHintWorld();

        float textY = (yPosLume == 2) ? camera.getCenterY() + sideLength : camera.getCenterY();
        gameOverText = new Text(camera.getCenterX(), textY,
                resourcesManager.smallFont, "L u s e r !", vbom) {
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

        replaySprite = new Sprite(camera.getCenterX(), camera.getHeight()*2/9, sideLength, sideLength,
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
                        SceneManager.getInstance().loadWorld5Scene(engine, level);
                    } else {
                        SceneManager.getInstance().loadWorld5Scene(engine, 0);
                    }
                    disposeHUD();
                    return true;
                } else {
                    return false;
                }
            }
        };

        gameOverText.setColor(Color.RED);
        gameOverScene.registerTouchArea(gameOverText);
        gameOverScene.attachChild(gameOverText);

        gameOverScene.registerTouchArea(replaySprite);
        gameOverScene.attachChild(replaySprite);

        //stop things
        unregisterUpdateHandler(physicsWorld);
        this.setIgnoreUpdate(true);
        this.setChildScene(gameOverScene, false, true, true); //set gameOverScene as a child scene - so game will be paused
        ResourcesManager.getInstance().activity.showLevelHint();

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

    private void createLayers() {
        this.attachChild(new Entity()); // First Layer
        this.attachChild(new Entity()); // Second Layer
        firstLayer = this.getChildByIndex(FIRST_LAYER);
        secondLayer = this.getChildByIndex(SECOND_LAYER);
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

        shootSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.shoot_normal_region, vbom);
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
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world5_region, vbom));
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
        lumeSprite = new Sprite(camera.getCenterX() - sideLength, camera.getCenterY() - sideLength, sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.lume_region, vbom);
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
        cannonsN = new Sprite(camera.getCenterX(), camera.getHeight() - sideLength / 2, sideLength * 3, sideLength, resourcesManager.cannons_n_region, vbom);
        cannonsE = new Sprite(camera.getWidth() - sideLength / 2, camera.getCenterY(), sideLength, sideLength * 3, resourcesManager.cannons_e_region, vbom);
        cannonsS = new Sprite(camera.getCenterX(), sideLength / 2, sideLength * 3, sideLength, resourcesManager.cannons_s_region, vbom);
        cannonsW = new Sprite(sideLength / 2, camera.getCenterY(), sideLength, sideLength * 3, resourcesManager.cannons_w_region, vbom);
        secondLayer.attachChild(cannonsN);
        secondLayer.attachChild(cannonsE);
        secondLayer.attachChild(cannonsS);
        secondLayer.attachChild(cannonsW);
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
        float interval;
        float slowMotionFactor = (slowMotion) ? 2f : 1f;

        switch (level) {
            case 1:
                long age = (new Date()).getTime() - stoneTime;
                int direction = (randomGenerator.nextInt(4) + 1);
                boolean middle = randomGenerator.nextBoolean();
                interval =  2400*slowMotionFactor;
                if (firstStonesInLevel) interval = 50*slowMotionFactor;
                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    this.showStonesToScreen(direction, middle);
                    stoneTime = new Date().getTime();
                }
                break;
            case 2:
                age = (new Date()).getTime() - stoneTime;
                direction = (randomGenerator.nextInt(2) + 1);
                interval = (long) 6500*slowMotionFactor;
                if (firstStonesInLevel) interval = 300*slowMotionFactor;
                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    this.showStonesToScreen(direction, false);
                    stoneTime = new Date().getTime();
                }
                break;
            case 3:
                age = (new Date()).getTime() - stoneTime;
                direction = (randomGenerator.nextInt(4) + 1);
                interval = 4600*slowMotionFactor / variant;
                if (firstStonesInLevel) interval = 1000*slowMotionFactor;
                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    this.showStonesToScreen(direction, false);
                    variant++;
                    if (variant > 2) variant = 1;
                    stoneTime = new Date().getTime();
                }
                break;
            case 4:
                age = (new Date()).getTime() - stoneTime;
                interval = (long) 3500*slowMotionFactor;
                if (firstStonesInLevel) interval = 700*slowMotionFactor;
                //!this.variantUsed && randomNumber < probabilityStone &&

                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    variant = randomGenerator.nextInt(4) + 1;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                }
                break;
        }


    }

    private void showStonesToScreen(int directionVariant, final boolean middleFirst) {
        int direction1;
        int direction2;
        int direction3;
        float factor = (directionVariant%2 == 0) ? ratio : 1f;
        float antiFactor = (directionVariant%2 == 0) ? 1f : ratio;
        switch (level) {
            case 1:
                if (middleFirst) {
                    addBall(true, directionVariant, 1, 1, 1*factor);
                    addBall(false, directionVariant, 0, 1, 1*factor);
                    addBall(false, directionVariant, 2, 1, 1*factor);
                    addBall(true, directionVariant, 0, 2.3f, 1*factor);
                    addBall(true, directionVariant, 2, 2.3f, 1*factor);
                } else {
                    addBall(false, directionVariant, 1, 1, 1*factor);
                    addBall(true, directionVariant, 0, 1, 1*factor);
                    addBall(true, directionVariant, 2, 1, 1*factor);
                    addBall(true, directionVariant, 1, 2.3f, 1*factor);
                }
                break;
            case 2:
                direction1 = directionVariant*2;
                direction2 = directionVariant*2 - 1;

                addBall(true, direction1, 1, 0, 0.7f*ratio);
                addBall(true, direction1, 0, 1.5f, 0.7f*ratio);
                addBall(true, direction1, 2, 1.5f, 0.7f*ratio);
                addBall(true, direction1, 1, 3f, 0.7f*ratio);
                addBall(false, direction1, 1, 1.5f, 0.7f*ratio); //first balls

                if (!(score%10 == 9 || score == 20)) { //show 2nd balls not when almost finished
                    addBall(true, direction2, 1, 4.5f,1.0f);
                    addBall(true, direction2, 0, 6f,1.0f);
                    addBall(true, direction2, 2, 6f,1.0f);
                    addBall(true, direction2, 1, 7.5f,1.0f);
                    addBall(false, direction2, 1, 6f, 1.0f); //second balls
                }
                break;
            case 3:
                showLevel3(directionVariant);
                break;
            case 4:
                showLevel4(directionVariant);
                break;
        }
    }

    private void showLevel3(int direction) {
        float factor = (direction%2 == 0) ? ratio : 1f;
        float antiFactor = (direction%2 == 0) ? 1f : ratio;
        if (variant == 1) {
            direction1 = direction;
            direction2 = (direction+1 > 4) ? 1 : direction+1;
            boolean dir1Is2Or3 = (direction1 == 2 || direction1 == 3);
            boolean dir2Is2Or3 = (direction2 == 2 || direction2 == 3);

            addBall(true, direction1, 0, (dir1Is2Or3) ? 0.8f : 2f, 1.3f*factor);
            addBall(true, direction1, 1, 2f, 1.3f*factor);
            addBall(true, direction1, 2, (!dir1Is2Or3) ? 0.8f : 2f, 1.3f*factor);
            addBall(false, direction1, 1, 0.8f, 1.3f*factor);

            addBall(true, direction2, 0, (!dir2Is2Or3) ? 0.8f : 2f, 1.3f*antiFactor);
            addBall(true, direction2, 1, 2f, 1.3f*antiFactor);
            addBall(true, direction2, 2, (dir2Is2Or3) ? 0.8f : 2f, 1.3f*antiFactor);
            addBall(false, direction2, 1, 0.8f, 1.3f*antiFactor);
        } else if (variant == 2) {
            /**direction1 = direction;
            direction2 = (direction+1 > 4) ? 1 : direction+1;
             **/

            if (direction1+direction2 == 5) { //three stones from bottom left to up right
                if (direction1 == 2) {
                    addBall(true, direction1, 0, 0.8f, 0.9f*ratio);
                    addBall(true, direction1, 1, 2f, 0.9f*ratio);
                    addBall(true, direction1, 2, 3f, 0.9f*ratio);
                    addBall(false, direction1, 0, 2f, 0.9f*ratio);

                    addBall(true, direction2, 0, 3f, 0.9f);
                    addBall(true, direction2, 1, 2f, 0.9f);
                    addBall(true, direction2, 2, 0.8f, 0.9f);
                    addBall(false, direction2, 2, 2f, 0.9f);
                } else if (direction1 == 4) {
                    addBall(true, direction1, 0, 3f, 0.9f*ratio);
                    addBall(true, direction1, 1, 2f, 0.9f*ratio);
                    addBall(true, direction1, 2, 0.8f, 0.9f*ratio);
                    addBall(false, direction1, 2, 2f, 0.9f*ratio);

                    addBall(true, direction2, 0, 0.8f, 0.9f);
                    addBall(true, direction2, 1, 2f, 0.9f);
                    addBall(true, direction2, 2, 3f, 0.9f);
                    addBall(false, direction2, 0, 2f, 0.9f);
                }
            } else { //three stones from top left to bottom right
                if (direction1 == 1) {
                    addBall(true, direction1, 0, 3f, 0.9f);
                    addBall(true, direction1, 1, 2f, 0.9f);
                    addBall(true, direction1, 2, 0.8f, 0.9f);
                    addBall(false, direction1, 2, 2f, 0.9f);

                    addBall(true, direction2, 0, 3f, 0.9f*ratio);
                    addBall(true, direction2, 1, 2f, 0.9f*ratio);
                    addBall(true, direction2, 2, 0.8f, 0.9f*ratio);
                    addBall(false, direction2, 2, 2f, 0.9f*ratio);
                } else if (direction1 == 3) {
                    addBall(true, direction1, 0, 0.8f, 0.9f);
                    addBall(true, direction1, 1, 2f, 0.9f);
                    addBall(true, direction1, 2, 3f, 0.9f);
                    addBall(false, direction1, 0, 2f, 0.9f);

                    addBall(true, direction2, 0, 0.8f, 0.9f*ratio);
                    addBall(true, direction2, 1, 2f, 0.9f*ratio);
                    addBall(true, direction2, 2, 3f, 0.9f*ratio);
                    addBall(false, direction2, 0, 2f, 0.9f*ratio);
                }
            }
        }
    }

    private void showLevel4(int direction) {
        switch (variant) {
            case 1:
                addBall(true, 4, 2, 1, 1.8f * ratio);
                addBall(true, 4, 2, 2, 1.8f * ratio);
                addBall(true, 4, 1, 3, 1.8f * ratio);
                addBall(true, 4, 0, 1, 1.8f * ratio);
                addBall(true, 4, 0, 2, 1.8f * ratio);
                addBall(false, 4, 1,2, 1.8f * ratio);

                addBall(true, 1, 2, 3, 1.8f);
                addBall(true, 1, 2, 4, 1.8f);
                addBall(true, 1, 2, 5, 1.8f);
                addBall(true, 1, 1, 3, 1.8f);
                addBall(true, 1, 1, 4, 1.8f);
                addBall(true, 1, 1, 5, 1.8f);

                addBall(true, 3, 2, 3, 1.8f);
                addBall(true, 3, 2, 4, 1.8f);
                addBall(true, 3, 2, 5, 1.8f);
                addBall(true, 3, 1, 3, 1.8f);
                addBall(true, 3, 1, 4, 1.8f);
                addBall(true, 3, 1, 5, 1.8f);
                break;
            case 2:
                addBall(true, 2, 1, 1, 1.8f * ratio);
                addBall(true, 2, 2, 1, 1.8f * ratio);
                addBall(true, 2, 1, 2, 1.8f * ratio);
                addBall(true, 2, 0, 3, 1.8f * ratio);
                addBall(false, 2, 1, 3, 1.8f * ratio);

                addBall(true, 3, 0, 5, 1.8f);
                addBall(true, 3, 0, 6, 1.8f);
                addBall(true, 3, 0, 7, 1.8f);
                addBall(true, 3, 2, 5, 1.8f);
                addBall(true, 3, 2, 6, 1.8f);
                addBall(true, 3, 2, 7, 1.8f);

                addBall(true, 1, 0, 3, 1.8f);
                addBall(true, 1, 0, 4, 1.8f);
                addBall(true, 1, 0, 5, 1.8f);
                addBall(true, 1, 2, 3, 1.8f);
                addBall(true, 1, 2, 4, 1.8f);
                addBall(true, 1, 2, 5, 1.8f);
                break;
            case 3:
                addBall(true, 2, 2, 1, 1.8f * ratio);
                addBall(true, 2, 2, 2, 1.8f * ratio);
                addBall(true, 2, 1, 3, 1.8f * ratio);
                addBall(true, 2, 0, 1, 1.8f * ratio);
                addBall(true, 2, 0, 2, 1.8f * ratio);
                addBall(false, 2, 0,3, 1.8f * ratio);
                addBall(false, 2, 2,3, 1.8f * ratio);

                addBall(true, 1, 0, 3, 1.8f);
                addBall(true, 1, 0, 4, 1.8f);
                addBall(true, 1, 0, 5, 1.8f);
                addBall(true, 1, 1, 3, 1.8f);
                addBall(true, 1, 1, 4, 1.8f);
                addBall(true, 1, 1, 5, 1.8f);

                addBall(true, 3, 0, 3, 1.8f);
                addBall(true, 3, 0, 4, 1.8f);
                addBall(true, 3, 0, 5, 1.8f);
                addBall(true, 3, 1, 3, 1.8f);
                addBall(true, 3, 1, 4, 1.8f);
                addBall(true, 3, 1, 5, 1.8f);
                break;
            case 4:
                addBall(true, 4, 1, 1, 1.8f * ratio);
                addBall(true, 4, 0, 1, 1.8f * ratio);
                addBall(true, 4, 1, 2, 1.8f * ratio);
                addBall(true, 4, 2, 3, 1.8f * ratio);
                addBall(false, 4, 2, 2, 1.8f * ratio);

                addBall(true, 1, 0, 5, 1.8f);
                addBall(true, 1, 0, 6, 1.8f);
                addBall(true, 1, 0, 7, 1.8f);
                addBall(true, 1, 2, 5, 1.8f);
                addBall(true, 1, 2, 6, 1.8f);
                addBall(true, 1, 2, 7, 1.8f);

                addBall(true, 3, 0, 3, 1.8f);
                addBall(true, 3, 0, 4, 1.8f);
                addBall(true, 3, 0, 5, 1.8f);
                addBall(true, 3, 2, 3, 1.8f);
                addBall(true, 3, 2, 4, 1.8f);
                addBall(true, 3, 2, 5, 1.8f);
                break;
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
                final Circle lumeCircle, stoneCircle;
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
                stoneCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);

                if (stoneCircle.collision(lumeCircle) && !gameOverDisplayed) {
                    luserSprite = new Sprite(lumeSprite.getX()-lumeSprite.getWidth()*4/10,
                            lumeSprite.getY() + lumeSprite.getHeight()*5/10,
                            lumeSprite.getWidth(), lumeSprite.getWidth(),
                            ResourcesManager.getInstance().finger_luser, vbom);
                    secondLayer.attachChild(luserSprite);
                    displayGameOverText();
                }
//                if (this.getX() < -sideLength || this.getY() < -sideLength ||
//                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
//                    if (!thorny) crackyStones.remove(this);
//                    this.detachSelf();
//                    this.dispose();
//                }


                if (this.getX() < -9*sideLength || this.getY() < -9*sideLength ||
                        this.getX() > camera.getWidth() + 9*sideLength || this.getY() > camera.getHeight() + 9*sideLength) {
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

    private void addToScore(int i) {
        score += i;
        scoreText.setText(String.valueOf(score));
        if (score % 10 == 9) {
            scoreText.setColor(android.graphics.Color.parseColor("#ffc300"));
        }
        if (score % 10 == 0) {
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
                                    activity.unlockWorld(6);
                                    SceneManager.getInstance().loadMenuScene(engine);
                                }
                            }));
                        } else {
                            levelText.setText("L" + String.valueOf(level)); //TODO add else
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