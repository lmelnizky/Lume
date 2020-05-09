package org.lume.base;

import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import org.lume.engine.camera.hud.HUD;
import org.lume.engine.handler.IUpdateHandler;
import org.lume.engine.handler.timer.ITimerCallback;
import org.lume.engine.handler.timer.TimerHandler;
import org.lume.entity.Entity;
import org.lume.entity.IEntity;
import org.lume.entity.modifier.RotationModifier;
import org.lume.entity.modifier.ScaleModifier;
import org.lume.entity.primitive.Rectangle;
import org.lume.entity.scene.CameraScene;
import org.lume.entity.scene.Scene;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.Text;
import org.lume.entity.text.TextOptions;
import org.lume.extension.physics.box2d.FixedStepPhysicsWorld;
import org.lume.extension.physics.box2d.PhysicsWorld;
import org.lume.input.touch.TouchEvent;
import org.lume.manager.ResourcesManager;
import org.lume.manager.SceneManager;
import org.lume.manager.SceneType;
import org.lume.object.Ball;
import org.lume.object.Circle;
import org.lume.util.adt.align.HorizontalAlign;
import org.lume.util.adt.color.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public abstract class SkillScene extends BaseScene {

    //manager attributes

    /*game attributes*/

    //constants
    protected static final int FIRST_LAYER = 0; //is used for ground, player and coin
    protected static final int SECOND_LAYER = 1; //is used for  stones
    protected static final int THIRD_LAYER = 2; //is used for cannons

    protected final short CATEGORY_CANNONBALL = 0x0001;  // 0000000000000001 in binary
    protected final short CATEGORY_THORNY = 0x0002; // 0000000000000010 in binary
    protected final short CATEGORY_CRACKY = 0x0003;

    protected final short MASK_CANNONBALL = ~CATEGORY_THORNY;
    protected final short MASK_THORNY = ~(CATEGORY_CANNONBALL | CATEGORY_THORNY | CATEGORY_CRACKY);
    protected final short MASK_CRACKY = ~(CATEGORY_THORNY | CATEGORY_CRACKY);

    protected static final int SWIPE_MIN_DISTANCE = 20;

    //primitive data types
    protected boolean tutorialShowing = true;
    protected boolean worldFinished = false;
    protected boolean waitingForStonesToDisappear;
    protected boolean firstStonesInLevel = true;
    protected boolean variantUsed = false;
    protected boolean gameOverDisplayed = false;
    protected int level, xPosLume, yPosLume, xPosCoin, yPosCoin, xPosGrume, yPosGrume;
    protected int variantStage, variant, stoneDirection, variantRow;
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
    protected ArrayList<Sprite> mirrorStones, mirrorStonesToRemove, crackyStonesToRemove, stonesToRemove, cannonBallsToRemove;
    protected Scene gameOverScene;
    protected IEntity firstLayer, secondLayer, thirdLayer;
    protected PhysicsWorld physicsWorld;
    protected Sprite lumeSprite, lamporghinaSprite, kimmelnitzSprite, kimmelnitzKOSprite, punchSprite, luserSprite, coinSprite;
    protected Sprite[] cannonN, cannonE, cannonS, cannonW;
    protected Sprite[] cannonNS, cannonES, cannonSS, cannonWS;
    protected Sprite[] cannonNU, cannonEU, cannonSU, cannonWU;
    protected HUD gameHUD;
    protected Text scoreText, levelText, timeText, kimmelnitzText;
    protected Text gameOverText;
    protected Sprite replaySprite, finishSprite;
    protected Sprite shootSign, moveSign;
    protected Rectangle shootLeft, swipeRight;

    //constructor
    public SkillScene() {
        //manager attributes do all in basescene
    }

    public SkillScene(int level) {
        this.level = level;
        createHUD();
    }



    //inherited
    public void onBackKeyPressed() {
        if (ResourcesManager.getInstance().backgroundMusic != null) ResourcesManager.getInstance().backgroundMusic.stop();
        if (ResourcesManager.getInstance().backgroundMusic != null) ResourcesManager.getInstance().backgroundMusic.pause();
        disposeHUD();
        SceneManager.getInstance().loadSkillMenuScene(engine);
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
        sideLength = resourcesManager.screenHeight / 9;
        randomGenerator = new Random();
        crackyStones = new ArrayList<Sprite>();
        crackyStonesToRemove = new ArrayList<Sprite>();
        mirrorStones = new ArrayList<Sprite>();
        mirrorStonesToRemove = new ArrayList<Sprite>();
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
        createHalves();
        createLume();
        createCannons();
        createBoard();
        createHUD();
        resetData();

        createKimmelnitz();
    }

    public void startGame() {
        this.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (!waitingForStonesToDisappear && !worldFinished) {
                    createStones();
                    if (!tutorialShowing) time--;
                    int displayTime = (int) Math.round(time/60);
                    timeText.setText(String.valueOf(displayTime));
                    if (time <= 0 && !gameOverDisplayed) {
                        displayGameOverScene();
                    }
                    if (displayTime <= 5) {
                        timeText.setColor(Color.RED);
                    }
                }

                if (level == 8) moveStones();
            }
            @Override
            public void reset() {
            }
        });
    }

    protected void createLayers() {
        this.attachChild(new Entity()); // First Layer
        this.attachChild(new Entity()); // Second Layer
        this.attachChild(new Entity()); // Third Layer
        firstLayer = this.getChildByIndex(FIRST_LAYER);
        secondLayer = this.getChildByIndex(SECOND_LAYER);
        thirdLayer = this.getChildByIndex(THIRD_LAYER);
    }

    public void createMusic() {
        ResourcesManager.getInstance().backgroundMusic.play();
    }

    public void createPhysics() {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false){
            @Override
            public void onUpdate(float pSecondsElapsed) {
                super.onUpdate(pSecondsElapsed);
                Log.i("UPDATE", "UPDATE");
            }
        };
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
        variantUsed = false;
        variant = 0;
        variantStage = 0;
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
                    if (touchEvent.isActionDown() && tutorialShowing) {
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
            registerKimmelnitzTouchOnTime();
            secondLayer.attachChild(kimmelnitzSprite);
        } else {
            kimmelnitzSprite.setVisible(true);
            this.registerTouchArea(kimmelnitzSprite);
        }
    }

    public void registerKimmelnitzTouchOnTime() {
        engine.registerUpdateHandler(new TimerHandler(0.8f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                engine.unregisterUpdateHandler(pTimerHandler);
                registerTouchArea(kimmelnitzSprite);
            }
        }));
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
        lumeSprite = new Sprite(camera.getCenterX() - sideLength, camera.getCenterY() - sideLength, sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.player_region, vbom);
        secondLayer.attachChild(lumeSprite);
    }

    public void createCannons() {
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

    public void animateCannon(int direction, int position) {
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

    public void coinCheck() {
        if ((xPosLume == xPosCoin && yPosLume == yPosCoin) ||
                (level == 0 && xPosGrume == xPosCoin && yPosGrume == yPosCoin)) {
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
        if (score == 10 || score > 9) {
            removeCoin();
            waitingForStonesToDisappear = true;
            worldFinished = true;
            this.registerUpdateHandler(new IUpdateHandler() {
                @Override
                public void onUpdate(float pSecondsElapsed) {
                    if (allStonesGone() && waitingForStonesToDisappear) {
                        waitingForStonesToDisappear = false;
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
                                tooEasyText.detachSelf();
                                tooEasyText.dispose();
                                disposeHUD();
                                SceneManager.getInstance().loadSkillMenuScene(engine);
                            }
                        }));
                    }
                }

                @Override
                public void reset() {
                }
            });
        } else {
            if (level != 8) createCoin();
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
        shootSign.detachSelf();
        shootSign.dispose();
        moveSign.detachSelf();
        moveSign.dispose();

        gameHUD.detachSelf();
        gameHUD.dispose();
    }

    public void createCoin() {
        do {
            xPosCoin = randomGenerator.nextInt(3) + 1;
            yPosCoin = randomGenerator.nextInt(3) + 1;
        } while ((xPosCoin == xPosLume && yPosCoin == yPosLume)
            || (level == 6 && xPosCoin == xPosGrume && yPosCoin == yPosGrume));
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
        if (coinSprite != null) {
            xPosCoin = 0;
            yPosCoin = 0;
            coinSprite.detachSelf();
            coinSprite.dispose();
            coinSprite = null;
        }
    }

    protected void displayGameOverScene() {
        gameOverDisplayed = true;

        gameOverScene = new CameraScene(camera);
        gameOverScene.setBackgroundEnabled(false);

        ResourcesManager.getInstance().backgroundMusic.stop();
        ResourcesManager.getInstance().backgroundMusic.pause();
        ResourcesManager.getInstance().luserSound.play();

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
                ResourcesManager.getInstance().activity.showSingleInterstitial();
            }
        }));
    }

    protected boolean allStonesGone() {
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

    protected void displayGameOverButtons() {
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
                    SceneManager.getInstance().loadSkillGameScene(engine, level);
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
                    SceneManager.getInstance().loadSkillMenuScene(engine);
                    return true;
                } else {
                    return false;
                }
            }
        };
        gameOverScene.registerTouchArea(finishSprite);
        gameOverScene.attachChild(finishSprite);
    }

    public void moveStones() {
        for (Sprite stone : stones) {
            final Circle lumeCircle, stoneCircle;
            lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
            stoneCircle = new Circle(stone.getX(), stone.getY(), stone.getWidth() / 2);
            Ball ball = (Ball) stone.getUserData();

            Vector2 gravity = ball.getGravity();
            Body body = ball.getBody();
            boolean thorny = ball.isThorny();

            body.applyForce(gravity, body.getWorldCenter());

            if (stoneCircle.collision(lumeCircle) && !gameOverDisplayed) {
                displayGameOverScene();
                score = 0;
            }
            if (stone.getX() < -sideLength || stone.getY() < -sideLength ||
                    stone.getX() > camera.getWidth() + sideLength || stone.getY() > camera.getWidth() + sideLength) {
                stonesToRemove.add(stone);
                if (!thorny) crackyStonesToRemove.add(stone);
                if (score > 0) addToScore(-1);

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
//                 crackyStones.remove(stone);
//                this.detachSelf();
//                this.dispose();
            }
        }
        crackyStones.removeAll(crackyStonesToRemove);
        stones.removeAll(stonesToRemove);
    }

    public int getGravityDirection(int direction) {
        int gravityDirection;
        gravityDirection = (level == 1) ? 3 : ((direction%2+1) + randomGenerator.nextInt(2)*2);
        return gravityDirection;
    }

    public Vector2 getGravity(int gravityDirection) {
        Vector2 gravity = null;

        switch(gravityDirection) {
            case 1:
                gravity = new Vector2(0, SensorManager.GRAVITY_EARTH); //everything goes down
                break;
            case 2:
                gravity = new Vector2(SensorManager.GRAVITY_EARTH, 0); //everything goes right
                break;
            case 3:
                gravity = new Vector2(0, -SensorManager.GRAVITY_EARTH); //everything goes up
                break;
            case 4:
                gravity = new Vector2(-SensorManager.GRAVITY_EARTH, 0); //everything goes left
                break;
        }
        return gravity;
    }

    //given methods
    public abstract void createBackground();
    public abstract void addShootandMoveSign();
    public abstract void createHalves(); //because symbols can be different
    public abstract void showText(); //because all texts are different
    public abstract void createStones(); //because all texts are different
}
