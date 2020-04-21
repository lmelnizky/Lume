package org.andengine.base;

import com.badlogic.gdx.math.Vector2;

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
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public abstract class SkillScene extends BaseScene {

    //manager attributes

    /*game attributes*/

    //constants
    protected static final int FIRST_LAYER = 0; //is used for ground, player and coin
    protected static final int SECOND_LAYER = 1; //is used for  stones
    protected static final int SWIPE_MIN_DISTANCE = 20;

    //primitive data types
    protected boolean tutorialShowing = true;
    protected boolean waitingForStonesToDisappear = false;
    protected boolean firstStonesInLevel = true;
    protected boolean gameOverDisplayed = false;
    protected int level, xPosLume, yPosLume, xPosCoin, yPosCoin;
    protected int time = 30*60;
    protected int score = 0;
    protected float sideLength;
    protected float shootX1, shootX2, shootY1, shootY2;
    protected float swipeX1, swipeX2, swipeY1, swipeY2;
    protected long[] stoneTimes;
    protected long stoneTime;

    //general attributes
    protected Random randomGenerator;

    //andengine attributes
    protected ArrayList<Sprite> crackyStones, stones;
    protected ArrayList<Sprite> crackyStonesToRemove, stonesToRemove, cannonBallsToRemove;
    protected IEntity firstLayer, secondLayer;
    protected PhysicsWorld physicsWorld;
    protected Sprite lumeSprite, kimmelnitzSprite, kimmelnitzKOSprite, punchSprite, luserSprite, coinSprite;
    protected Sprite cannonsN, cannonsE, cannonsS, cannonsW;
    protected HUD gameHUD;
    protected Text scoreText, levelText, timeText, kimmelnitzText;
    protected Text gameOverText;
    protected Sprite shootSign, moveSign;
    protected Rectangle shootLeft, swipeRight;

    //constructor
    public SkillScene() {
        //manager attributes do all in basescene
//        this.resourcesManager = ResourcesManager.getInstance();
//        this.engine = resourcesManager.engine;
//        this.activity = resourcesManager.activity;
//        this.vbom = resourcesManager.vbom;
//        this.camera = resourcesManager.camera;
//
//        createScene();
    }



    //inherited
    public void onBackKeyPressed() {
        SceneManager.getInstance().loadMenuScene(engine);
        disposeHUD();
    }

    public SceneType getSceneType () {
        return SceneType.SCENE_SKILLGAME;
    }

    @Override
    public void disposeScene() {
        camera.setHUD(null);
        camera.setChaseEntity(null);
        camera.setCenter(camera.getCenterX(), camera.getCenterY());
    }

    public void createScene() {
        level = 0;
        sideLength = resourcesManager.screenHeight / 9;
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
        createBoard();

        createHUD();
        resetData();

        createHalves();
        createKimmelnitz();
    }

    public void startGame() {
        this.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (!waitingForStonesToDisappear) {
                    createStones();
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

    public void createLayers() {
        this.attachChild(new Entity()); // First Layer
        this.attachChild(new Entity()); // Second Layer
        firstLayer = this.getChildByIndex(FIRST_LAYER);
        secondLayer = this.getChildByIndex(SECOND_LAYER);
    }

    public void createMusic() {
        ResourcesManager.getInstance().backgroundMusic.play();
    }

    public void createPhysics() {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
        registerUpdateHandler(physicsWorld);
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

    public void createKimmelnitz() {
        this.unregisterTouchArea(shootLeft);
        this.unregisterTouchArea(swipeRight);
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

    public void removeTutorialOnTime(Sprite sprite) {
        engine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                engine.unregisterUpdateHandler(pTimerHandler);

                unregisterTouchArea(sprite);
                sprite.detachSelf();
                sprite.dispose();
                kimmelnitzSprite = null;
                kimmelnitzKOSprite.detachSelf();
                kimmelnitzKOSprite.dispose();
                punchSprite.detachSelf();
                punchSprite.dispose();
                removeText();
                registerTouchArea(shootLeft);
                registerTouchArea(swipeRight);
                resetData();
                startGame();
            }
        }));
    }

    public void removeText() {
        kimmelnitzText.setText("");
        kimmelnitzText.detachSelf();
        kimmelnitzText.dispose();
    }

    public void createBoard() {
        Sprite boardSprite = new Sprite(camera.getCenterX(), camera.getCenterY(), sideLength * 3, sideLength * 3, resourcesManager.board_region, vbom);
        firstLayer.attachChild(boardSprite);
    }

    public void createLume() {
        xPosLume = 1;
        yPosLume = 1;
        lumeSprite = new Sprite(camera.getCenterX() - sideLength, camera.getCenterY() - sideLength, sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.lume_region, vbom);
        secondLayer.attachChild(lumeSprite);
    }

    public void createCannons() {
        cannonsN = new Sprite(camera.getCenterX(), camera.getHeight() - sideLength/2, sideLength*3, sideLength, resourcesManager.cannons_n_region, vbom);
        cannonsE = new Sprite(camera.getWidth() - sideLength/2, camera.getCenterY(), sideLength, sideLength*3, resourcesManager.cannons_e_region, vbom);
        cannonsS = new Sprite(camera.getCenterX(), sideLength/2, sideLength*3, sideLength, resourcesManager.cannons_s_region, vbom);
        cannonsW = new Sprite(sideLength/2, camera.getCenterY(), sideLength, sideLength*3, resourcesManager.cannons_w_region, vbom);
        secondLayer.attachChild(cannonsN);
        secondLayer.attachChild(cannonsE);
        secondLayer.attachChild(cannonsS);
        secondLayer.attachChild(cannonsW);
    }

    public void coinCheck() {
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

    public void addToScore(int i) {
        score += i;
        scoreText.setText(String.valueOf(score));
        if (score%10 == 9) {
            scoreText.setColor(android.graphics.Color.parseColor("#ffc300"));
        }
        if (score%10 == 0) {
            ResourcesManager.getInstance().backgroundMusic.stop();
            ResourcesManager.getInstance().backgroundMusic.pause();
            ResourcesManager.getInstance().easySound.play();
            engine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
                public void onTimePassed(final TimerHandler pTimerHandler) {
                    engine.unregisterUpdateHandler(pTimerHandler);
                    SceneManager.getInstance().loadMenuScene(engine);
                }
            }));
            removeCoin();
        } else {
            createCoin();
        }
    }

    public void createHUD() {
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

        //add shoot and move sign
        addShootandMoveSign();
        gameHUD.attachChild(shootSign);
        gameHUD.attachChild(moveSign);

        camera.setHUD(gameHUD);
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

    public void createCoin() {
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

    public void removeCoin() {
        xPosCoin = 0;
        yPosCoin = 0;
        coinSprite.detachSelf();
        coinSprite.dispose();
        coinSprite = null;
    }

    public void displayGameOverText() {
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
                    SceneManager.getInstance().loadMenuScene(engine);
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
                ResourcesManager.getInstance().activity.showSingleInterstitial();
            }
        }));
    }

    //given methods
    public abstract void createBackground();
    public abstract void addShootandMoveSign();
    public abstract void createHalves(); //because symbols can be different
    public abstract void showText(); //because all texts are different
    public abstract void createStones(); //because all texts are different
}
