package org.lume.scene;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.lume.base.BaseScene;
import org.lume.engine.camera.hud.HUD;
import org.lume.engine.handler.IUpdateHandler;
import org.lume.engine.handler.timer.ITimerCallback;
import org.lume.engine.handler.timer.TimerHandler;
import org.lume.entity.Entity;
import org.lume.entity.IEntity;
import org.lume.entity.modifier.ScaleModifier;
import org.lume.entity.primitive.Rectangle;
import org.lume.entity.scene.CameraScene;
import org.lume.entity.scene.Scene;
import org.lume.entity.scene.background.SpriteBackground;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.AutoWrap;
import org.lume.entity.text.Text;
import org.lume.entity.text.TickerText;
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
import org.lume.opengl.texture.region.ITextureRegion;
import org.lume.util.adt.align.HorizontalAlign;
import org.lume.util.adt.color.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MultiScene extends BaseScene {

    private boolean lumeCanBomb = false;
    private boolean grumeCanBomb = false;
    private boolean lumeIsBombing = false;
    private boolean grumeIsBombing = false;
    private boolean lumeCanShoot = false;
    private boolean grumeCanShoot = false;
    private boolean bombing = false;
    private boolean bombLaid = false;
    private boolean firstStonesInLevel = true;
    private boolean gameOverDisplayed = false;
    private boolean gameStarted = false;
    private boolean lumeIndestructible;
    private boolean grumeIndestructible;
    private boolean revenge = false;
    private boolean kimmelnitzVisible, tutorialShowing;

    private boolean cannonBallRemove = false;

    private static final int HALVES_LAYER = 0;
    private static final int FIRST_LAYER = 1; //is used for ground, player and coin
    private static final int SECOND_LAYER = 2; //is used for  stones
    private static final int THIRD_LAYER = 3; //is used for  stones

    private int touchCount = 0;

    private int time = 0; //seconds

    private int lumeScore = 0;
    private int grumeScore = 0;
    private int lumeLives = 3;
    private int grumeLives = 3;
    private int coinsTotal = 0;
    private int variant = 1;

    private int sideLength;
    private int xPosLume, yPosLume;
    private int xPosGrume, yPosGrume;
    private int xPosCoin, yPosCoin;
    private int xPosBomb, yPosBomb;

    private long[] stoneTimes;
    private long stoneTime;

    private float shootX1, shootX2, shootY1, shootY2;
    private float swipeX1, swipeX2, swipeY1, swipeY2;

    private float ratio = resourcesManager.screenRatio;

    private Random randomGenerator;

    private IEntity halvesLayer, firstLayer, secondLayer, thirdLayer;

    private Sprite kimmelnitzSprite = null;
    private Sprite kimmelnitzKOSprite = null;
    private Sprite punchSprite = null;
    private Sprite lumeSprite;
    private Sprite lumeSpriteGameOver;
    private Sprite grumeSprite;
    private Sprite grumeSpriteGameOver;
    private Sprite luserSprite;
    private Sprite fingerSprite;
    private Sprite coinSprite;
    private Sprite bombSprite;
    private Sprite redBombSprite;
    private Sprite fireBeamHorizontal, fireBeamVertical;
    private Sprite lumeBomb, grumeBomb;
    private Sprite lumeHeart1, lumeHeart2, lumeHeart3, lumeHeart4;
    private Sprite grumeHeart1, grumeHeart2, grumeHeart3, grumeHeart4;
    private Sprite[] cannonN, cannonE, cannonS, cannonW;
    private Sprite[] cannonNS, cannonES, cannonSS, cannonWS;
    private Sprite[] cannonNU, cannonEU, cannonSU, cannonWU;
    private ArrayList<Sprite> crackyStones, crackyStonesToRemove, cannonBallsToRemove;
    private ArrayList<Sprite> stones, stonesToRemove;

    private Rectangle shootLeft, swipeRight;

    private HUD gameHUD;
    private Text lumeScoreText, myLivesText, timeText, grumeScoreText, opponentLivesText;
    private Text lumeText, grumeText;
    private Text gameOverText, winnerText, luserText;
    private Text tutorialText;
    private Text startText;
    private PhysicsWorld physicsWorld;
    private static final int SWIPE_MIN_DISTANCE = 5;

    @Override
    public void createScene() {
        sideLength = (int) resourcesManager.screenHeight / 9;
        randomGenerator = new Random();
        xPosBomb = 0;
        yPosBomb = 0;
        crackyStones = new ArrayList<Sprite>();
        crackyStonesToRemove = new ArrayList<Sprite>();
        cannonBallsToRemove = new ArrayList<Sprite>();
        stoneTimes = new long[4];
        for (int i = 0; i < 4; i++) {
            stoneTimes[i] = 0;
        }


        createBackground();
        createLayers();
        createPhysics();
        createHalves();
        createBoard();
        createCannons();
        createPlayer();
        createHUD();
        if (activity.isMultiTutorialSeen()) {
            createStartText();
        } else {
            createKimmelnitz();
        }
    }

    private void createStartText() {
        startText = new Text(camera.getCenterX(), camera.getCenterY(),
                resourcesManager.bigFont, "Touch to start", vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    startGame();
                    gameStarted = true;
                    return true;
                } else {
                    return false;
                }
            }
        };
        startText.setColor(Color.WHITE);
        startText.setRotation(90);
        this.registerTouchArea(startText);
        secondLayer.attachChild(startText);
    }

    public void resetData() {
        if (revenge) {
            createLayers();
            createPhysics();
            createBoard();
            createPlayer();
            createHalves();
            disposeHUD();
            createHUD();
        }
        revenge = false;
        gameOverDisplayed = false;
        lumeLives = 3;
        grumeLives = 3;
        stoneTime = new Date().getTime();
    }

    private void createKimmelnitz() {
        this.unregisterTouchArea(shootLeft);
        this.unregisterTouchArea(swipeRight);
        kimmelnitzVisible = true;
        tutorialShowing = true;
        showTutorialText();
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
                    removeTutorialText();

                    unregisterTouchArea(sprite);
                    registerTouchArea(shootLeft);
                    registerTouchArea(swipeRight);
                    sprite.setVisible(kimmelnitzVisible);

                    createStartText();
                    sprite.detachSelf();
                    sprite.dispose();
                    kimmelnitzSprite = null;
                }
            }
        }));
    }

    private void showTutorialText() {
        String tvText1 = "Swipe to move. tap to take a BOMB, swipe to lay it where you wish. You have to collect 3 coins to BOMB." +
                "Take a cannonBall to shoot. Have fun!";
        if (tutorialText == null) {
            tutorialText = new TickerText(sideLength*6.6f, camera.getHeight() / 6, resourcesManager.smallFont, tvText1,
                    new TickerText.TickerTextOptions(AutoWrap.WORDS, sideLength*13.2f, HorizontalAlign.CENTER,60), resourcesManager.vbom);
            secondLayer.attachChild(tutorialText);
            tutorialText.setAlpha(0.7f);
            //activity.createTypingText(tvText1, tutorialText, false);
        }
    }

    private void removeTutorialText() {
        tutorialText.setText("");
        tutorialText.detachSelf();
        tutorialText.dispose();
    }

    private void startGame() {
        if (!startText.isDisposed()) {
            startText.detachSelf();
            startText.dispose();
        }

        resetData();

        createMusic();

        this.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                createStones();
            }

            @Override
            public void reset() {

            }
        });

        createCoin();
    }

    @Override
    public void onBackKeyPressed() {
        if (ResourcesManager.getInstance().backgroundMusic != null) ResourcesManager.getInstance().backgroundMusic.stop();
        if (ResourcesManager.getInstance().backgroundMusic != null) ResourcesManager.getInstance().backgroundMusic.pause();
        disposeHUD();
        SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_MULTI;
    }

    @Override
    public void disposeScene() {
        camera.setHUD(null);
        camera.setChaseEntity(null);
        camera.setCenter(camera.getCenterX(), camera.getCenterY());

        // TODO code responsible for disposing scene
        // removing all game scene objects.
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
        Sprite boardSprite = new Sprite(camera.getCenterX(), camera.getCenterY(), sideLength*3, sideLength*3,
                resourcesManager.board_region, vbom);
        firstLayer.attachChild(boardSprite);
    }

    private void createPlayer() {
        xPosLume = 1;
        yPosLume = 1;
        xPosGrume = 3;
        yPosGrume = 3;
        lumeSprite = new Sprite(camera.getCenterX() - sideLength, camera.getCenterY() - sideLength,
                sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.lume_region, vbom);
        secondLayer.attachChild(lumeSprite);
        lumeSprite.setRotation(90);
        grumeSprite = new Sprite(camera.getCenterX() + sideLength, camera.getCenterY() + sideLength,
                sideLength*3/4, sideLength*3/4, resourcesManager.grume_region, vbom);
        secondLayer.attachChild(grumeSprite);
        grumeSprite.setRotation(270);
    }

    private void createCoin() {
        do {
            xPosCoin = randomGenerator.nextInt(3) + 1;
            yPosCoin = randomGenerator.nextInt(3) + 1;
        } while ((xPosCoin == xPosLume && yPosCoin == yPosLume) || (xPosCoin == xPosGrume && yPosCoin == yPosGrume));
        if (coinSprite == null) {
            coinSprite = new Sprite(camera.getCenterX() - sideLength + ((xPosCoin - 1) * sideLength), camera.getCenterY() - sideLength + ((yPosCoin - 1) * sideLength),
                    sideLength * 7 / 8, sideLength * 7 / 8, resourcesManager.coin_region, vbom);
            firstLayer.attachChild(coinSprite);
        } else {
            coinSprite.registerEntityModifier(new ScaleModifier(0.2f,0.7f,1f));
            coinSprite.setPosition(camera.getCenterX() - sideLength + ((xPosCoin - 1) * sideLength), camera.getCenterY() - sideLength + ((yPosCoin - 1) * sideLength));
        }
        coinsTotal++;
    }

    private void createHalves() {

        //LUME
        shootLeft = new Rectangle(camera.getCenterX() - sideLength/2 - resourcesManager.screenWidth/4,
                camera.getCenterY(), resourcesManager.screenWidth/2 - sideLength, resourcesManager.screenHeight, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown() && gameStarted) {
                    shootX1 = x;
                    shootY1 = y;
                    return true;
                } else if (touchEvent.isActionUp() && gameStarted) {
                    shootX2 = x;
                    shootY2 = y;
                    float deltaX = shootX2 - shootX1;
                    float deltaY = shootY2 - shootY1;
                    //checks if it was a swipe
                    if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE || Math.abs(deltaY) > SWIPE_MIN_DISTANCE) {
                        //checks if it was horizontal or vertical
                        if (Math.abs(deltaX) > Math.abs(deltaY)) { //horizontal swipe
                            if (deltaX > 0) { //left to right
                                if ((lumeCanShoot == true)) {
                                    cannonBallShot('L', 4);
                                } else if (bombLaid == true && lumeIsBombing) {
                                    if (xPosLume < 3) createBomb(xPosLume+1, yPosLume);
                                } else {
                                    moveLume('R');
                                }
                            } else { //right to left
                                if ((lumeCanShoot == true)) {
                                    cannonBallShot('L', 2);
                                } else if (bombLaid == true && lumeIsBombing) {
                                    if (xPosLume > 1) createBomb(xPosLume-1, yPosLume);
                                } else {
                                    moveLume('L');
                                }
                            }
                        } else { //vertical swipe
                            if (deltaY > 0) { //up to down
                                if ((lumeCanShoot == true)) {
                                    cannonBallShot('L', 3);
                                } else if (bombLaid == true && lumeIsBombing) {
                                    if (yPosLume < 3) createBomb(xPosLume, yPosLume+1);
                                } else {
                                    moveLume('U');
                                }
                            } else { //down to up
                                if ((lumeCanShoot == true)) {
                                    cannonBallShot('L', 1);
                                } else if (bombLaid == true && lumeIsBombing) {
                                    if (yPosLume > 1) createBomb(xPosLume, yPosLume-1);
                                } else {
                                    moveLume('D');
                                }
                            }
                        }
                    } else { //TAP
                        if (lumeCanBomb && !bombing && !bombLaid) {
                            layBomb(xPosLume, yPosLume, 'L');
                        }
                    }


                    return true;
                } else {
                    return false;
                }
            }

            ;
        };

        //GRUME
        swipeRight = new Rectangle(camera.getCenterX() + sideLength/2 + resourcesManager.screenWidth/4,
                camera.getCenterY(), resourcesManager.screenWidth/2 - sideLength, resourcesManager.screenHeight, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown() && gameStarted) {
                    swipeX1 = x;
                    swipeY1 = y;
                    return true;
                } else if (touchEvent.isActionUp() && gameStarted) {
                    swipeX2 = x;
                    swipeY2 = y;
                    float deltaX = swipeX2 - swipeX1;
                    float deltaY = swipeY2 - swipeY1;
                    //checks if it was a swipe and not a tap
                    if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE || Math.abs(deltaY) > SWIPE_MIN_DISTANCE) {
                        //horizontal or vertical check
                        if (Math.abs(deltaX) > Math.abs(deltaY)) { //horizontal
                            if (deltaX > 0) { //left to right
                                if ((grumeCanShoot == true)) {
                                    cannonBallShot('G', 4);
                                } else if (bombLaid == true && grumeIsBombing) {
                                    if (xPosGrume < 3) createBomb(xPosGrume+1, yPosGrume);
                                } else {
                                    moveGrume('R');
                                }
                            } else { //right to left
                                if ((grumeCanShoot == true)) {
                                    cannonBallShot('G', 2);
                                } else if (bombLaid == true && grumeIsBombing) {
                                    if (xPosGrume > 1) createBomb(xPosGrume-1, yPosGrume);
                                } else {
                                    moveGrume('L');
                                }
                            }
                        } else { //vertical
                            if (deltaY > 0) { //up to down
                                if ((grumeCanShoot == true)) {
                                    cannonBallShot('G', 3);
                                } else if (bombLaid == true && grumeIsBombing) {
                                    if (yPosGrume < 3) createBomb(xPosGrume, yPosGrume+1);
                                } else {
                                    moveGrume('U');
                                }
                            } else { //down to up
                                if ((grumeCanShoot == true)) {
                                    cannonBallShot('G', 1);
                                } else if (bombLaid == true && grumeIsBombing) {
                                    if (yPosGrume > 1) createBomb(xPosGrume, yPosGrume-1);
                                } else {
                                    moveGrume('D');
                                }
                            }
                        }
                    } else { //TAP
                        if (grumeCanBomb && !bombing && !bombLaid) {
                            layBomb(xPosGrume, yPosGrume, 'G');
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }

            ;
        };

        shootLeft.setColor(Color.BLACK);
        swipeRight.setColor(Color.BLACK);
        shootLeft.setAlpha(0.05f);
        swipeRight.setAlpha(0.05f);
        this.registerTouchArea(shootLeft);
        this.registerTouchArea(swipeRight);
        halvesLayer.attachChild(shootLeft);
        halvesLayer.attachChild(swipeRight);
    }

    private void createCannonball(int direction, float x, float y) {
        ITextureRegion textureRegion = resourcesManager.cannonball_region;
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        final Sprite cannonball;
//        Ball ball;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/5;
        switch (direction) {
            case 1: //up to down
                yVel = -speed;
                y -= sideLength;
                break;
            case 2: //right to left
                xVel = -speed;
                x-= sideLength;
                break;
            case 3: //down to up
                yVel = speed;
                y += sideLength;
                break;
            case 4: //left to right
                xVel = speed;
                x += sideLength;
                break;
        }
        cannonball = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                final Circle lumeCircle, grumeCircle, stoneCircle;
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
                grumeCircle = new Circle(grumeSprite.getX(), grumeSprite.getY(), grumeSprite.getWidth() / 2);
                stoneCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);
                if (stoneCircle.collision(lumeCircle) && !lumeIndestructible && !gameOverDisplayed) {
                    lumeKill();
                }

                if (stoneCircle.collision(grumeCircle) && !grumeIndestructible && !gameOverDisplayed) {
                    grumeKill();
                }

                if (this.getX() < -sideLength || this.getY() < - sideLength ||
                        this.getX() > camera.getWidth()+sideLength || this.getY() > camera.getWidth()+sideLength) {
                    this.detachSelf();
                    this.dispose();
                }
            }
        };
        secondLayer.attachChild(cannonball);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, cannonball, BodyDef.BodyType.KinematicBody, FIXTURE_DEF);
//        ball = new Ball(cannonball, "cannonball");
//        body.setUserData(ball);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(cannonball, body, true, false));
    }

    private void cannonBallShot(char player, int direction) {
        switch (player) {
            case 'L':
                createCannonball(direction, lumeSprite.getX(), lumeSprite.getY());
                lumeCanShoot = false;
                break;
            case 'G':
                createCannonball(direction, grumeSprite.getX(), grumeSprite.getY());
                grumeCanShoot = false;
                break;
        }
    }

    private void layBomb(int xPos, int yPos, char player) {
        bombLaid = true;
        if (player == 'L') {
            lumeCanBomb = false;
            lumeIsBombing = true;
            lumeScore = 0;
            lumeBomb = new Sprite(camera.getCenterX()-sideLength*3, camera.getHeight() - sideLength/2,
                    sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign00_region, vbom);
            engine.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    removeLumeBomb();
                    gameHUD.attachChild(lumeBomb);
                }
            });
        } else {
            grumeScore = 0;
            grumeCanBomb = false;
            grumeIsBombing = true;
            grumeBomb = new Sprite(camera.getCenterX() + sideLength*3, camera.getHeight() - sideLength/2,
                    sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign00_region, vbom);
            engine.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    removeGrumeBomb();
                    gameHUD.attachChild(grumeBomb);
                }
            });
        }
        bombSprite = new Sprite(camera.getCenterX() - sideLength + ((xPos - 1) * sideLength), camera.getCenterY() - sideLength + ((yPos - 1) * sideLength),
                sideLength * 3/4, sideLength * 3/4, resourcesManager.bomb_normal_region, vbom);
        this.attachChild(bombSprite);
        xPosBomb = xPos;
        yPosBomb = yPos;
    }

    private void createBomb(int xPos, int yPos) {
        bombLaid = false;
        bombing = true;
        lumeIsBombing = false;
        grumeIsBombing = false;
        bombSprite.setPosition(camera.getCenterX() - sideLength + ((xPos - 1) * sideLength),
                camera.getCenterY() - sideLength + ((yPos - 1) * sideLength));

        registerUpdateHandler(new TimerHandler(0.5f, false, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                bombSprite.detachSelf();
                bombSprite.dispose();
                bombSprite = null;
                createRedBomb(xPosBomb, yPosBomb);
            }
        }));
        xPosBomb = xPos;
        yPosBomb = yPos;
    }

    private void createRedBomb(final int xPos, final int yPos) {
        redBombSprite = new Sprite(camera.getCenterX() - sideLength + ((xPos - 1) * sideLength), camera.getCenterY() - sideLength + ((yPos - 1) * sideLength),
                sideLength * 3/4, sideLength * 3/4, resourcesManager.bomb_red_region, vbom);
        this.attachChild(redBombSprite);
        registerUpdateHandler(new TimerHandler(0.5f, false, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                redBombSprite.detachSelf();
                redBombSprite.dispose();
                redBombSprite = null;
                explode(xPos, yPos);
            }
        }));
    }

    private void explode(int xPos, int yPos) {
        fireBeamHorizontal = new Sprite(camera.getCenterX(), camera.getCenterY() - sideLength + (yPos-1)*sideLength,
                sideLength*3, sideLength*3/4, resourcesManager.firebeam_horizontal, vbom);
        fireBeamVertical = new Sprite(camera.getCenterX() - sideLength + (xPos-1)*sideLength, camera.getCenterY(),
                sideLength*3/4, sideLength*3, resourcesManager.firebeam_vertical, vbom);
        this.attachChild(fireBeamHorizontal);
        this.attachChild(fireBeamVertical);
        killCheck();
        registerUpdateHandler(new TimerHandler(0.2f, false, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                killCheck();
                fireBeamHorizontal.detachSelf();
                fireBeamHorizontal.dispose();
                fireBeamHorizontal = null;
                fireBeamVertical.detachSelf();
                fireBeamVertical.dispose();
                fireBeamVertical = null;
                bombing = false;
                xPosBomb = 0;
                yPosBomb = 0;
            }
        }));
    }

    private void killCheck() {
        if ((xPosLume == xPosBomb || yPosLume == yPosBomb) && !lumeIndestructible && !gameOverDisplayed) {
            lumeKill();
        }
        if ((xPosGrume == xPosBomb || yPosGrume == yPosBomb) && !grumeIndestructible && !gameOverDisplayed) {
            grumeKill();
        }
    }

    private void lumeKill() {
        lumeLives--;

        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(50);
        }

        if (lumeLives == 0) {
            lumeHeart3.detachSelf();
            lumeHeart3.dispose();
            displayGameOverText(1);
        } else if (lumeLives == 1){ //lume has one life left
            lumeIndestructible = true;
            lumeSprite.setAlpha(0.3f);
            registerUpdateHandler(new TimerHandler(2f, false, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    lumeSprite.setAlpha(1f);
                    lumeIndestructible = false;
                }
            }));
            lumeHeart2.detachSelf();
            lumeHeart2.dispose();
        } else if (lumeLives == 2) { //lume has two lives left
            lumeIndestructible = true;
            lumeSprite.setAlpha(0.3f);
            registerUpdateHandler(new TimerHandler(2f, false, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    lumeSprite.setAlpha(1f);
                    lumeIndestructible = false;
                }
            }));
            lumeHeart1.detachSelf();
            lumeHeart1.dispose();
        }
    }

    private void grumeKill() {
        grumeLives--;

        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(50);
        }

        if (grumeLives == 0) {
            grumeHeart3.detachSelf();
            grumeHeart3.dispose();
            displayGameOverText(2);
        } else if (grumeLives == 1){ //grume has one life left
            grumeIndestructible = true;
            grumeSprite.setAlpha(0.3f);
            registerUpdateHandler(new TimerHandler(2f, false, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    grumeSprite.setAlpha(1f);
                    grumeIndestructible = false;
                }
            }));
            grumeHeart2.detachSelf();
            grumeHeart2.dispose();
        } else if (grumeLives == 2) { //grume has two lives left
            grumeIndestructible = true;
            grumeSprite.setAlpha(0.3f);
            registerUpdateHandler(new TimerHandler(2f, false, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    grumeSprite.setAlpha(1f);
                    grumeIndestructible = false;
                }
            }));
            grumeHeart1.detachSelf();
            grumeHeart1.dispose();
        }
    }

    private void createPhysics() {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
        registerUpdateHandler(physicsWorld);
    }

    private void createLayers() {
        this.attachChild(new Entity()); //Halves Layer
        this.attachChild(new Entity()); // First Layer
        this.attachChild(new Entity()); // Second Layer
        this.attachChild(new Entity()); // Thrid Layer
        halvesLayer = this.getChildByIndex(HALVES_LAYER);
        firstLayer = this.getChildByIndex(FIRST_LAYER);
        secondLayer = this.getChildByIndex(SECOND_LAYER);
        thirdLayer = this.getChildByIndex(THIRD_LAYER);
    }

    private void createHUD() {
        gameHUD = new HUD();

        lumeBomb = new Sprite(camera.getCenterX()-sideLength*3, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign00_region, vbom);
        gameHUD.attachChild(lumeBomb);

        grumeBomb = new Sprite(camera.getCenterX() + sideLength*3, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign00_region, vbom);
        gameHUD.attachChild(grumeBomb);

        grumeHeart1 = new Sprite(camera.getWidth()-sideLength*5/2, camera.getHeight()-sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //innerst heart
        grumeHeart2 = new Sprite(camera.getWidth()-sideLength*3/2, camera.getHeight()-sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //middle heart
        grumeHeart3 = new Sprite(camera.getWidth()-sideLength/2, camera.getHeight()-sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //outer heart
        gameHUD.attachChild(grumeHeart1);
        gameHUD.attachChild(grumeHeart2);
        gameHUD.attachChild(grumeHeart3);

        lumeHeart1 = new Sprite(sideLength*5/2, camera.getHeight()-sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //innerst heart
        lumeHeart2 = new Sprite(sideLength*3/2, camera.getHeight()-sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //middle inner heart
        lumeHeart3 = new Sprite(sideLength/2, camera.getHeight()-sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //middle outer heart
        gameHUD.attachChild(lumeHeart1);
        gameHUD.attachChild(lumeHeart2);
        gameHUD.attachChild(lumeHeart3);

        camera.setHUD(gameHUD);
    }

    private void disposeHUD() {
        if (!lumeHeart1.isDisposed()) lumeHeart1.detachSelf();
        if (!lumeHeart1.isDisposed()) lumeHeart1.dispose();
        if (!lumeHeart2.isDisposed()) lumeHeart2.detachSelf();
        if (!lumeHeart2.isDisposed()) lumeHeart2.dispose();
        if (!lumeHeart3.isDisposed()) lumeHeart3.detachSelf();
        if (!lumeHeart3.isDisposed()) lumeHeart3.dispose();

        if (!grumeHeart1.isDisposed()) grumeHeart1.detachSelf();
        if (!grumeHeart1.isDisposed()) grumeHeart1.dispose();
        if (!grumeHeart2.isDisposed()) grumeHeart2.detachSelf();
        if (!grumeHeart2.isDisposed()) grumeHeart2.dispose();
        if (!grumeHeart3.isDisposed()) grumeHeart3.detachSelf();
        if (!grumeHeart3.isDisposed()) grumeHeart3.dispose();

        if (!lumeBomb.isDisposed()) lumeBomb.detachSelf();
        if (!lumeBomb.isDisposed()) lumeBomb.dispose();
        if (!grumeBomb.isDisposed()) grumeBomb.detachSelf();
        if (!grumeBomb.isDisposed()) grumeBomb.dispose();

        gameHUD.detachChildren();
        gameHUD.detachSelf();
        gameHUD.dispose();
        gameHUD = null;
    }

    //updates HUD bombs
    private void updateHUD(final char player) {
        switch (lumeScore) {
            case 1:
                lumeBomb = new Sprite(camera.getCenterX()-sideLength*3, camera.getHeight() - sideLength/2,
                        sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign13_region, vbom);
                break;
            case 2:
                lumeBomb = new Sprite(camera.getCenterX()-sideLength*3, camera.getHeight() - sideLength/2,
                        sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign23_region, vbom);
                break;
            case 3:
                lumeBomb = new Sprite(camera.getCenterX()-sideLength*3, camera.getHeight() - sideLength/2,
                        sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign_red_region, vbom);
                lumeCanBomb = true;
                break;
        }

        switch (grumeScore) {
            case 1:
                grumeBomb = new Sprite(camera.getCenterX() + sideLength*3, camera.getHeight() - sideLength/2,
                        sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign13_region, vbom);
                break;
            case 2:
                grumeBomb = new Sprite(camera.getCenterX() + sideLength*3, camera.getHeight() - sideLength/2,
                        sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign23_region, vbom);
                break;
            case 3:
                grumeBomb = new Sprite(camera.getCenterX() + sideLength*3, camera.getHeight() - sideLength/2,
                        sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign_red_region, vbom);
                grumeCanBomb = true;
                break;
        }
        engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                if (player == 'L') {
                    removeLumeBomb();
                    gameHUD.attachChild(lumeBomb);
                } else if (player == 'G') {
                    removeGrumeBomb();
                    gameHUD.attachChild(grumeBomb);
                }
            }
        });

    }

    private void removeLumeBomb() {
        lumeBomb.detachSelf();
        gameHUD.detachChild(lumeBomb);
        if (!grumeBomb.isDisposed()) grumeBomb.dispose();
    }

    private void removeGrumeBomb() {
        grumeBomb.detachSelf();
        gameHUD.detachChild(grumeBomb);
        if (!grumeBomb.isDisposed()) grumeBomb.dispose();
    }

    //moves the player in X or Y direction
    public void moveLume(char direction) {
        switch (direction) {
            case 'R':
                if (xPosLume < 3) {
                    if ((xPosLume+1 != xPosGrume || yPosLume != yPosGrume) &&
                            (xPosLume+1 != xPosBomb || yPosLume != yPosBomb)) {
                        xPosLume++;
                        lumeSprite.setPosition(lumeSprite.getX() + sideLength, lumeSprite.getY());
                    }
                }
                break;
            case 'L':
                if (xPosLume > 1) {
                    if ((xPosLume-1 != xPosGrume || yPosLume != yPosGrume) &&
                            (xPosLume-1 != xPosBomb || yPosLume != yPosBomb)) {
                        xPosLume--;
                        lumeSprite.setPosition(lumeSprite.getX() - sideLength, lumeSprite.getY());
                    }
                }
                break;
            case 'D':
                if (yPosLume > 1) {
                    if ((xPosLume != xPosGrume || yPosLume-1 != yPosGrume) &&
                            (xPosLume != xPosBomb || yPosLume-1 != yPosBomb)) {
                        yPosLume--;
                        lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() - sideLength);
                    }
                }
                break;
            case 'U':
                if (yPosLume < 3) {
                    if ((xPosLume != xPosGrume || yPosLume+1 != yPosGrume) &&
                            (xPosLume != xPosBomb || yPosLume+1 != yPosBomb)) {
                        yPosLume++;
                        lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() + sideLength);
                    }
                }
                break;
        }
        coinCheck();
    }

    //moves the player in X or Y direction
    public void moveGrume(char direction) {
        switch (direction) {
            case 'R':
                if (xPosGrume < 3) {
                    if ((xPosLume != xPosGrume+1 || yPosLume != yPosGrume) &&
                            (xPosBomb != xPosGrume+1 || yPosBomb != yPosGrume)) {
                        xPosGrume++;
                        grumeSprite.setPosition(grumeSprite.getX() + sideLength, grumeSprite.getY());
                    }
                }
                break;
            case 'L':
                if (xPosGrume > 1) {
                    if ((xPosLume != xPosGrume-1 || yPosLume != yPosGrume) &&
                            (xPosBomb != xPosGrume-1 || yPosBomb != yPosGrume)) {
                        xPosGrume--;
                        grumeSprite.setPosition(grumeSprite.getX() - sideLength, grumeSprite.getY());
                    }
                }
                break;
            case 'D':
                if (yPosGrume > 1) {
                    if ((xPosLume != xPosGrume || yPosLume != yPosGrume-1) &&
                            (xPosBomb != xPosGrume || yPosBomb != yPosGrume-1)) {
                        yPosGrume--;
                        grumeSprite.setPosition(grumeSprite.getX(), grumeSprite.getY() - sideLength);
                    }
                }
                break;
            case 'U':
                if (yPosGrume < 3) {
                    if ((xPosLume != xPosGrume || yPosLume != yPosGrume+1) &&
                            (xPosBomb != xPosGrume || yPosBomb != yPosGrume+1)) {
                        yPosGrume++;
                        grumeSprite.setPosition(grumeSprite.getX(), grumeSprite.getY() + sideLength);
                    }
                }
                break;
        }
        coinCheck();
    }

    private void coinCheck() {
        if (xPosLume == xPosCoin && yPosLume == yPosCoin) {
            addMyScore(1, 'L');
            createCoin();
        } else if (xPosGrume == xPosCoin && yPosGrume == yPosCoin) {
            addMyScore(1, 'G');
            createCoin();
        }
    }

    private void addMyScore(int i, char player) {
        switch(player) {
            case 'L':
                lumeScore += i;
                updateHUD(player);
                break;
            case 'G':
                grumeScore += i;
                updateHUD(player);
                break;
        }
    }

    private void createStones() {
        boolean thorny = true; //else cannonball
        float randomNumber = randomGenerator.nextFloat();
        float randomThornyNumber = randomGenerator.nextFloat();
        double probabilityCannon = 0.3;
        int direction = randomGenerator.nextInt(4) + 1;
        int randomRow = randomGenerator.nextInt(3) + 1; //values 0 to 2
        //long[] age = new long[4]; //is used to prevent screen from showing too many stones
        long interval = (long) 5000;
        if (coinsTotal > 10) interval = 4500;
        if (firstStonesInLevel) interval = 1500;
        long age = new Date().getTime() - stoneTime;
        //age[direction - 1] = (new Date()).getTime() - stoneTimes[direction - 1];
        if (age >= interval) {
            if (firstStonesInLevel) createCoin();
            firstStonesInLevel = false;
            variant = randomGenerator.nextInt(3) + 1;
            boolean isCannon = randomNumber<probabilityCannon;
            this.showStones(variant, isCannon);
            //this.addBall(direction, 0, thorny);
            //if (randomThornyNumber < 0.15f) thorny = false;
            stoneTime = new Date().getTime();
        }
    }

    private void showStones(int variant, boolean addCannonball) {
        int randomDir1 = randomGenerator.nextInt(4) + 1;
        int randomDir2 = randomDir1+1;
        if (randomDir2 > 4) randomDir2 = 1;
        int dir1Opp = (randomDir1 <= 2) ? randomDir1+2 : randomDir1-2;
        int dir2Opp = (randomDir2 <= 2) ? randomDir2+2 : randomDir2-2;
        int pos0or2_1 = randomGenerator.nextInt(2)*2;
        int pos0or2_2 = randomGenerator.nextInt(2)*2;

        switch (variant) {
            case 1://cross
                addBall(randomDir1, 1, true);
                addBall(dir1Opp, 1, true);
                addBall(randomDir2, 1, true);
                addBall(dir2Opp, 1, true);
                break;
            case 2://T

                break;
            case 3://L

                break;
        }
    }

    private void addBall(int direction, int position, final boolean thorny) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        final Sprite stone;
        Ball ball;

//        int randomRow = randomGenerator.nextInt(3); //values 0 to 2
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = (coinsTotal > 10) ? camera.getWidth()/370 : camera.getWidth()/500;

        switch (direction) {
            case 1:
                x = camera.getCenterX()-sideLength + ((position-1)*sideLength);
                y = camera.getHeight() + sideLength/2;
                yVel = -speed;
                break;
            case 2:
                x = camera.getWidth() + sideLength/2;
                y = camera.getCenterY()-sideLength + ((position-1)*sideLength);
                xVel = -speed;
                break;
            case 3:
                x = camera.getCenterX()-sideLength + ((position-1)*sideLength);
                y = -sideLength/2;
                yVel = speed;
                break;
            case 4:
                x = -sideLength/2;
                y = camera.getCenterY()-sideLength + ((position-1)*sideLength);
                xVel = speed;
                break;
        }
        ITextureRegion textureRegion = (thorny) ? resourcesManager.thorny_stone_region : resourcesManager.cannonball_region;
        stone = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                final Circle lumeCircle, grumeCircle, stoneCircle;
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
                grumeCircle = new Circle(grumeSprite.getX(), grumeSprite.getY(), grumeSprite.getWidth() / 2);
                stoneCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);
                if (stoneCircle.collision(lumeCircle) && !lumeIndestructible && thorny && !gameOverDisplayed) {
                    lumeKill();
                } else if (stoneCircle.collision(lumeCircle) && !thorny) { //cannonballshot
                    this.detachSelf();
                    this.dispose();
                    lumeCanShoot = true;
                }
                if (stoneCircle.collision(grumeCircle) && !grumeIndestructible && thorny && !gameOverDisplayed) {
                    grumeKill();
                } else if (stoneCircle.collision(grumeCircle) && !thorny) { //cannonballshot
                    this.detachSelf();
                    this.dispose();
                    grumeCanShoot = true;
                }
                if (this.getX() < -sideLength*3 || this.getY() < -sideLength*3 ||
                        this.getX() > camera.getWidth() + sideLength*3 || this.getY() > camera.getWidth() + sideLength*3) {
                    if (!thorny) crackyStones.remove(this);
                    this.detachSelf();
                    this.dispose();
                }
            }
        };
        secondLayer.attachChild(stone);

        //animate cannon
        animateCannon(direction, position-1);

        final Body body = PhysicsFactory.createCircleBody(physicsWorld, stone, BodyDef.BodyType.KinematicBody, FIXTURE_DEF);
        if (thorny) {
//            ball = new Ball(stone, "thorny");
//            body.setUserData(ball);
        } else {
            crackyStones.add(stone);
//            ball = new Ball(stone, "cracky");
//            body.setUserData(ball);
        }
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(stone, body, true, false));
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

    private void removeItems() {
        secondLayer.detachChildren();
        firstLayer.detachChildren();
    }

    private void deleteLayers() {
        secondLayer.detachSelf();
        firstLayer.detachSelf();
        secondLayer.dispose();
        firstLayer.dispose();
        secondLayer = null;
        firstLayer = null;
    }

    private void displayGameOverText(int player) {
        gameOverDisplayed = true;

        //removeItems();
        halvesLayer.detachChildren();

        final Scene gameOverScene = new CameraScene(camera);
        gameOverScene.setBackgroundEnabled(false);

        ResourcesManager.getInstance().backgroundMusic.stop();
        ResourcesManager.getInstance().backgroundMusic.pause();
        ResourcesManager.getInstance().luserSound.play();

        lumeSpriteGameOver = new Sprite(camera.getCenterX() - 4*sideLength, resourcesManager.screenHeight/2,
                sideLength*3, sideLength*3, resourcesManager.lume_region, vbom);
        secondLayer.attachChild(lumeSpriteGameOver);
        grumeSpriteGameOver = new Sprite(camera.getCenterX() + 4*sideLength, resourcesManager.screenHeight/2,
                sideLength*3, sideLength*3, resourcesManager.grume_region, vbom);
        secondLayer.attachChild(grumeSpriteGameOver);

        if (player == 2) {
            winnerText = new Text(camera.getCenterX() - 4*sideLength, camera.getCenterY() - 2*sideLength, resourcesManager.bigFont, "Winner", vbom);
            winnerText.setColor(Color.BLUE);
            luserText = new Text(camera.getCenterX() + 4*sideLength, camera.getCenterY() - 2*sideLength,
                    resourcesManager.smallFont, "Luser", vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
//                    if (touchEvent.isActionDown()) {
//                        clearChildScene();
//                        gameOverScene.detachSelf();
//                        gameOverScene.dispose();
//                        removeItems();
//                        deleteLayers();
//                        revenge = true;
//                        resetData();
//                    }
                    return false;
                }
            };
            luserText.setColor(Color.RED);
            luserSprite = new Sprite(lumeSpriteGameOver.getX() - lumeSpriteGameOver.getWidth()/2 + lumeSpriteGameOver.getWidth()*3/10,
                    lumeSpriteGameOver.getY() + lumeSpriteGameOver.getHeight()*4/10,
                    lumeSpriteGameOver.getWidth()*6/10, lumeSpriteGameOver.getWidth()*6/10,
                    resourcesManager.finger_luser, vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
//                    if (touchEvent.isActionDown()) {
//                        clearChildScene();
//                        gameOverScene.detachSelf();
//                        gameOverScene.dispose();
//                        removeItems();
//                        deleteLayers();
//                        revenge = true;
//                        resetData();
//                    }
                    return false;
                }
            };
            grumeSpriteGameOver.setSize(sideLength*2, sideLength*2);
            fingerSprite = new Sprite(grumeSpriteGameOver.getX() - grumeSpriteGameOver.getWidth()/2, grumeSpriteGameOver.getY() - grumeSpriteGameOver.getHeight()/2 + grumeSpriteGameOver.getWidth()*45/100,
                    grumeSpriteGameOver.getWidth()*4.8f/10, grumeSpriteGameOver.getWidth()*7f/10, resourcesManager.finger_middle, vbom);
        } else if (player == 1) {
            winnerText = new Text(camera.getCenterX() + 4*sideLength, camera.getCenterY() - 2*sideLength, resourcesManager.bigFont, "Winner", vbom);
            winnerText.setColor(Color.GREEN);
            luserText = new Text(camera.getCenterX() - 4*sideLength, camera.getCenterY() - 2*sideLength,
                    resourcesManager.smallFont, "Luser", vbom) {
               public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
//                    if (touchEvent.isActionDown()) {
//                        clearChildScene();
//                        gameOverScene.detachSelf();
//                        gameOverScene.dispose();
//                        removeItems();
//                        deleteLayers();
//                        revenge = true;
//                        resetData();
//                    }
                    return false;
                }
            };
            luserText.setColor(Color.RED);
            luserSprite = new Sprite(grumeSpriteGameOver.getX() - grumeSpriteGameOver.getWidth()/2 + grumeSpriteGameOver.getWidth()*3/10,
                    grumeSpriteGameOver.getY() + grumeSpriteGameOver.getHeight()*4/10,
                    grumeSpriteGameOver.getWidth()*6/10, grumeSpriteGameOver.getWidth()*6/10,
                    resourcesManager.finger_luser, vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
//                    if (touchEvent.isActionDown()) {
//                        clearChildScene();
//                        gameOverScene.detachSelf();
//                        gameOverScene.dispose();
//                        removeItems();
//                        deleteLayers();
//                        revenge = true;
//                        resetData();
//                    }
                    return false;
                }
            };
            lumeSpriteGameOver.setSize(sideLength*2, sideLength*2);
            fingerSprite = new Sprite(lumeSpriteGameOver.getX() + lumeSpriteGameOver.getWidth()/2, lumeSpriteGameOver.getY() - lumeSpriteGameOver.getHeight()/2 + lumeSpriteGameOver.getWidth()*45/100,
                    lumeSpriteGameOver.getWidth()*4.8f/10, lumeSpriteGameOver.getWidth()*7f/10, resourcesManager.finger_middle, vbom);
        }
//        removeItems();
//        deleteLayers();

        gameOverText = new Text(camera.getCenterX(), camera.getCenterY()+2*sideLength, resourcesManager.bigFont, "Game Over!", vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    clearChildScene();
                    setIgnoreUpdate(false);
                    gameOverDisplayed = false;
                    registerUpdateHandler(physicsWorld);
                    disposeHUD();
                    SceneManager.getInstance().loadMenuScene(engine);
                }
                return true;
            }
        };
        gameOverText.setColor(Color.RED);
//        revengeText = new Text(revengeX, revengeY, resourcesManager.smallFont, "Revenge!", vbom) {
//            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
//                if (touchEvent.isActionDown()) {
//                    gameOverDisplayed = false;
//                    revenge = true;
//                    gameOverScene.detachSelf();
//                    gameOverScene.dispose();
//                    startGame();
//                }
//                return true;
//            }
//        };
//        revengeText.setColor(revengeColor);

        gameOverText.detachSelf();
        //revengeText.detachSelf();
        gameOverScene.registerTouchArea(gameOverText);
        gameOverScene.registerTouchArea(luserText);
        gameOverScene.registerTouchArea(luserSprite);

        //gameOverScene.registerTouchArea(revengeText);
        gameOverScene.attachChild(gameOverText);
        //gameOverScene.attachChild(revengeText);
        gameOverScene.attachChild(winnerText);
        gameOverScene.attachChild(luserText);
        gameOverScene.attachChild(luserSprite);
        gameOverScene.attachChild(fingerSprite);

        //stop things
        unregisterUpdateHandler(physicsWorld);
        this.setIgnoreUpdate(true);
        this.setChildScene(gameOverScene, false, true, true); //set gameOverScene as a child scene - so game will be paused

        engine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                engine.unregisterUpdateHandler(pTimerHandler);
                //show interstitial setAdVisibility
                ResourcesManager.getInstance().activity.showMultiInterstitial();

            }
        }));
    }
}