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
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
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

public class World0 extends BaseScene {

    private boolean kimmelnitzVisible = true;
    private boolean tutorialShowing = true;
    private boolean gameOverDisplayed = false;
    private boolean firstStonesInLevel = true;
    private boolean cameFromLevelsScene;
    private boolean waitingForStonesToDisappear = false;

    private static final int FIRST_LAYER = 0; //is used for ground, player and coin
    private static final int SECOND_LAYER = 1; //is used for  stones

    private int touchCount = 0;
    private int currentPosHor = 0;
    private int currentPosVer = 0;
    private int level;
    private int score = 0;
    private int time = 30*60; //seconds
    private int sideLength;
    private int xPosLume, yPosLume;
    private int xPosCoin, yPosCoin;

    private long[] stoneTimes;
    private long stoneTime;

    private float shootX1, shootX2, shootY1, shootY2;
    private float swipeX1, swipeX2, swipeY1, swipeY2;

    private Random randomGenerator;

    private Sprite lumeSprite;
    private Sprite kimmelnitzSprite = null;
    private Sprite kimmelnitzKOSprite = null;
    private Sprite punchSprite = null;
    private Sprite coinSprite;
    private Sprite cannonsN, cannonsE, cannonsS, cannonsW;
    private ArrayList<Sprite> crackyStones, crackyStonesToRemove, cannonBallsToRemove;
    private ArrayList<Sprite> stones, stonesToRemove;

    private Rectangle shootLeft, swipeRight;

    private IEntity firstLayer, secondLayer;

    private Sprite v1FirstStone, v2FirstStone, v3FirstStone;
    private Sprite luserSprite;

    private HUD gameHUD;
    private Text scoreText, levelText, timeText;
    private Sprite shootSign, moveSign;
    private Text text0, text1, text2, text3, text4;
    private PhysicsWorld physicsWorld;

    private static final int SWIPE_MIN_DISTANCE = 20;

    private Text gameOverText;

    public World0() { //default constructor
        level = 0;
        cameFromLevelsScene = false;
        createHUD();
    }

    public World0(int level) { //constructor used when selecting a level
        this.level = level;
        cameFromLevelsScene = true;
        createHUD();
    }

    @Override
    public void createScene() {

        level = 0;
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
        createCannons();

        createHUD();
        resetData();

        createHalves();
        createKimmelnitz();

    }

    private void startGame() {
        this.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (!waitingForStonesToDisappear) {
                    createStones(level);
                    if (!tutorialShowing) time--;
                    int displayTime = (int) Math.round(time/60);
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
        time = 30*60;
        timeText.setColor(Color.WHITE);
        scoreText.setColor(Color.WHITE);
        stoneTime = new Date().getTime();
        for (int i = 0; i < 4; i++) {
            stoneTimes[i] = new Date().getTime();
        }
        firstStonesInLevel = true;
    }

    public void disposeHUD() {
        scoreText.detachSelf();
        scoreText.dispose();
        levelText.detachSelf();
        levelText.dispose();
        timeText.detachSelf();
        timeText.dispose();

        gameHUD.detachSelf();
        gameHUD.dispose();
    }

    @Override
    public void onBackKeyPressed() {
        ResourcesManager.getInstance().backgroundMusic.stop();
        if (cameFromLevelsScene) {
            SceneManager.getInstance().loadWorlds1to4Scene(engine);
            disposeHUD();
        } else {
            SceneManager.getInstance().loadMenuScene(engine);
            disposeHUD();
        }

    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_WORLD0;
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
                        SceneManager.getInstance().loadWorlds1to4Scene(engine);
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

        gameOverText.setColor(Color.RED);
        gameOverScene.registerTouchArea(gameOverText);
        gameOverScene.attachChild(gameOverText);

        //stop things
        unregisterUpdateHandler(physicsWorld);
        this.setIgnoreUpdate(true);
        this.setChildScene(gameOverScene, false, true, true); //set gameOverScene as a child scene - so game will be paused

        ResourcesManager.getInstance().backgroundMusic.stop();
        ResourcesManager.getInstance().backgroundMusic.pause();
        ResourcesManager.getInstance().luserSound.play();

        engine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
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
        moveSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.move_normal_region, vbom);
        gameHUD.attachChild(shootSign);
        gameHUD.attachChild(moveSign);

        camera.setHUD(gameHUD);
    }

    private void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world0_region, vbom));
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

    private void createKimmelnitz() {
        this.unregisterTouchArea(shootLeft);
        this.unregisterTouchArea(swipeRight);
        kimmelnitzVisible = true;
        tutorialShowing = true;
        showText();
        if (kimmelnitzSprite == null) {
            kimmelnitzSprite = new Sprite(14.5f*sideLength, 1.5f*sideLength,
                    3*sideLength, 3*sideLength, resourcesManager.kimmelnitz_region, vbom) {
                @Override
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown() && !activity.isShowText() && tutorialShowing) {
                        tutorialShowing = false;
                        kimmelnitzKOSprite = new Sprite(14.5f*sideLength, 1.5f*sideLength,
                                3*sideLength, 3*sideLength, resourcesManager.kimmelnitz_ko_region, vbom);
                        secondLayer.attachChild(kimmelnitzKOSprite);
                        punchSprite = new Sprite(13*sideLength + x, y,
                                1.2f*sideLength, 1.2f*sideLength, resourcesManager.punch_region, vbom);
                        secondLayer.attachChild(punchSprite);
                        removeTutorialOnTime(this);


                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(kimmelnitzSprite);
            secondLayer.attachChild(kimmelnitzSprite);
        } else {
            kimmelnitzSprite.setVisible(true);
            this.registerTouchArea(kimmelnitzSprite);
        }
    }

    private void removeTutorialOnTime(Sprite sprite) {
        engine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                engine.unregisterUpdateHandler(pTimerHandler);

                kimmelnitzKOSprite.detachSelf();
                kimmelnitzKOSprite.dispose();
                punchSprite.detachSelf();
                punchSprite.dispose();
                if (kimmelnitzVisible) {
                    kimmelnitzVisible = false;
                    removeText();

                    unregisterTouchArea(sprite);
                    registerTouchArea(shootLeft);
                    registerTouchArea(swipeRight);
                    sprite.setVisible(kimmelnitzVisible);

                    //start moving
                    if (level == 1) {
                        startGame();
                    } else if (level == 0) {
                        level++;
                        levelText.setText("L" + String.valueOf(level));
                        resetData();
                        createKimmelnitz();
                    } else {
                        resetData();
                    }
                    if (level == 4) {
                        sprite.detachSelf();
                        sprite.dispose();
                        kimmelnitzSprite = null;
                        ResourcesManager.getInstance().backgroundMusic.stop();
                        ResourcesManager.getInstance().backgroundMusic.pause();
                        ResourcesManager.getInstance().easySound.play();
                        engine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
                            public void onTimePassed(final TimerHandler pTimerHandler) {
                                engine.unregisterUpdateHandler(pTimerHandler);
                                activity.unlockWorld(1);
                                SceneManager.getInstance().loadMenuScene(engine);
                            }
                        }));
                    }
                }
            }
        }));
    }

    private void showText() {
        switch (level) {
            case 0:
                showText0();
                break;
            case 1:
                showText1();
                break;
            case 2:
                showText2();
                break;
            case 3:
                showText3();
                break;
            case 4:
                showText4();
                break;
        }
    }

    private void showText0() {
        String tvText0 = "Desperados have taken your Lamporghina. " +
                "In order to save her you have to collect money and get Beer for them.";
//        +
//                "You also have a gun to destroy the cracky stones. You cannot destroy the red ones. " +
//                "Shoot on the right half of the screen. " +
//                "Look on the signs on the top! They will guide you in each world. Complete all four " +
//                "Levels in a row to complete one world. Good luck man!";
        if (text0 == null) {
            text0 = new Text(sideLength * 6.6f, camera.getHeight() / 6, ResourcesManager.getInstance().smallFont,
                    tvText0, new TextOptions(AutoWrap.WORDS, 13 * sideLength, HorizontalAlign.CENTER),
                    ResourcesManager.getInstance().vbom);
            secondLayer.attachChild(text0);
            text0.setAlpha(0.7f);
            activity.createTypingText(tvText0, text0, false);
        }
    }

    private void showText1() {
        String tvText1 = "Move on the right half of the screen to collect 10 coins. " +
                "BE Brave, clever and fast, LUME!";
//        +
//                "You also have a gun to destroy the cracky stones. You cannot destroy the red ones. " +
//                "Shoot on the right half of the screen. " +
//                "Look on the signs on the top! They will guide you in each world. Complete all four " +
//                "Levels in a row to complete one world. Good luck man!";
        if (text1 == null) {
            text1 = new Text(sideLength * 6.6f, camera.getHeight() / 6, ResourcesManager.getInstance().smallFont,
                    tvText1, new TextOptions(AutoWrap.WORDS, 13 * sideLength, HorizontalAlign.CENTER),
                    ResourcesManager.getInstance().vbom);
            secondLayer.attachChild(text1);
            text1.setAlpha(0.7f);
            activity.createTypingText(tvText1, text1, false);
        }
    }

    private void showText2() {
        String tvText2 = "Time is running! " +
                "You have 30 seconds per level to collect the coins. " +
                "Be also aware of the stones!";
//        +
//                "You also have a gun to destroy the cracky stones. You cannot destroy the red ones. " +
//                "Shoot on the right half of the screen. " +
//                "Look on the signs on the top! They will guide you in each world. Complete all four " +
//                "Levels in a row to complete one world. Good luck man!";
        if (text2 == null) {
            text2 = new Text(sideLength*6.6f, camera.getHeight()/6, ResourcesManager.getInstance().smallFont,
                    tvText2, new TextOptions(AutoWrap.WORDS, 13*sideLength, HorizontalAlign.CENTER),
                    ResourcesManager.getInstance().vbom);
            secondLayer.attachChild(text2);
            text2.setAlpha(0.7f);
            activity.createTypingText(tvText2, text2, false);
        }
    }

    private void showText3() {
        String tvText3 = "Destroy cracky stones By shooting on the left half of the screen. " +
                "You cannot destroy the red ones. Look at the signs!";
//        +
//                "You also have a gun to destroy the cracky stones. You cannot destroy the red ones. " +
//                "Shoot on the right half of the screen. " +
//                "Look on the signs on the top! They will guide you in each world. Complete all four " +
//                "Levels in a row to complete one world. Good luck man!";
        if (text3 == null) {
            text3 = new Text(sideLength*6.6f, camera.getHeight()/6, ResourcesManager.getInstance().smallFont,
                    tvText3, new TextOptions(AutoWrap.WORDS, 13*sideLength, HorizontalAlign.CENTER),
                    ResourcesManager.getInstance().vbom);
            secondLayer.attachChild(text3);
            text3.setAlpha(0.7f);
            activity.createTypingText(tvText3, text3, false);
        }
    }

    private void showText4() {
        String tvText4 = "Collect 10 coins to finish one level and finish 4 levels to complete one world, " +
                "But train hard in the Levels Menu before!";
//        +
//                "You also have a gun to destroy the cracky stones. You cannot destroy the red ones. " +
//                "Shoot on the right half of the screen. " +
//                "Look on the signs on the top! They will guide you in each world. Complete all four " +
//                "Levels in a row to complete one world. Good luck man!";

        if (text4 == null) {
            text4 = new Text(sideLength*6.6f, camera.getHeight()/6, ResourcesManager.getInstance().smallFont,
                    tvText4, new TextOptions(AutoWrap.WORDS, 13*sideLength, HorizontalAlign.CENTER),
                    ResourcesManager.getInstance().vbom);
            secondLayer.attachChild(text4);
            text4.setAlpha(0.7f);
            activity.createTypingText(tvText4, text4, false);
        }
    }

    private void removeText() {
        switch (level) {
            case 0:
                text0.setText("");
                text0.detachSelf();
                text0.dispose();
                break;
            case 1:
                text1.setText("");
                text1.detachSelf();
                text1.dispose();
                break;
            case 2:
                text2.setText("");
                text2.detachSelf();
                text2.dispose();
                break;
            case 3:
                text3.setText("");
                text3.detachSelf();
                text3.dispose();
                break;
            case 4:
                text4.setText("");
                text4.detachSelf();
                text4.dispose();
                break;
        }
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

        shootLeft = new Rectangle(camera.getCenterX() - resourcesManager.screenWidth / 4, camera.getCenterY(),
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

        swipeRight = new Rectangle(camera.getCenterX() + resourcesManager.screenWidth / 4, camera.getCenterY(),
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
        cannonsN = new Sprite(camera.getCenterX(), camera.getHeight() - sideLength/2, sideLength*3, sideLength, resourcesManager.cannons_n_region, vbom);
        cannonsE = new Sprite(camera.getWidth() - sideLength/2, camera.getCenterY(), sideLength, sideLength*3, resourcesManager.cannons_e_region, vbom);
        cannonsS = new Sprite(camera.getCenterX(), sideLength/2, sideLength*3, sideLength, resourcesManager.cannons_s_region, vbom);
        cannonsW = new Sprite(sideLength/2, camera.getCenterY(), sideLength, sideLength*3, resourcesManager.cannons_w_region, vbom);
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
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/5;
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
                stones.removeAll(stonesToRemove);
                crackyStones.removeAll(crackyStonesToRemove);

                if (this.getX() < -sideLength || this.getY() < - sideLength ||
                        this.getX() > camera.getWidth()+sideLength || this.getY() > camera.getWidth()+sideLength) {
                    this.detachSelf();
                    this.dispose();
                }
            }
        };
        if((score%10 != 0 || score == 30 || score == 0) && level == 4) {
            if (direction%2==0) { //horizontal shot
                currentPosHor = yPosLume-1;
            } else { //vertical shot
                currentPosVer = xPosLume-1;
            }
            showStonesToScreen(direction, false); //show stones when the player shoots
        }
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

        switch (level) {
            case 1:
                if (firstStonesInLevel) createCoin();
                firstStonesInLevel = false;
                break;
            case 2:
                int direction = randomGenerator.nextInt(4) + 1;
                interval = (long) 4000;
                long age = (new Date()).getTime() - stoneTime;
                if (firstStonesInLevel) interval = 1000;
                if (age >= interval && !kimmelnitzVisible) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    this.showStonesToScreen(direction, false);
                    stoneTime = new Date().getTime();
                }
                break;
            case 3:
                direction = randomGenerator.nextInt(4) + 1;
                interval = (long) 5000;
                age = (new Date()).getTime() - stoneTime;
                if (firstStonesInLevel) interval = 1000;
                if (age >= interval && !kimmelnitzVisible) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    this.showStonesToScreen(direction, false);
                    stoneTime = new Date().getTime();
                }
                break;
        }


    }

    private void showStonesToScreen(int directionVariant, final boolean thornyFirst) {
        int randomRow = randomGenerator.nextInt(3); //values 0 to 2
        float ratio = resourcesManager.screenRatio;

        switch (level) {
            case 1:
                //only coins here
                break;
            case 2:
                addBall(true, directionVariant, randomGenerator.nextInt(3), 0.9f);
                break;
            case 3:
                addBall(false, directionVariant, 0, 0.8f);
                addBall(false, directionVariant, 1, 0.8f);
                addBall(false, directionVariant, 2, 0.8f);
                break;
        }
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

    public Sprite addBall(final boolean thorny, int direction, int position, float speedFactor) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/15 * speedFactor;

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


                if (this.getX() < -sideLength || this.getY() < -sideLength ||
                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getHeight() + sideLength) {
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
                SceneManager.getInstance().loadWorlds1to4Scene(engine);
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
                        levelText.setText("L" + String.valueOf(level));
                        resetData();
                        waitingForStonesToDisappear = false;
                        createKimmelnitz();
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

}