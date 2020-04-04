package org.andengine.scene;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.base.BaseScene;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class HighscoreScene extends BaseScene {

    final short CATEGORY_CANNONBALL = 0x0001;  // 0000000000000001 in binary
    final short CATEGORY_THORNY = 0x0002; // 0000000000000010 in binary
    final short CATEGORY_CRACKY = 0x0003;

    final short MASK_CANNONBALL = ~CATEGORY_THORNY;
    final short MASK_THORNY = ~(CATEGORY_CANNONBALL | CATEGORY_THORNY | CATEGORY_CRACKY);
    final short MASK_CRACKY = ~(CATEGORY_THORNY | CATEGORY_CRACKY);

    private boolean variantUsed = false;
    private boolean gameOverDisplayed = false;
    private boolean firstStonesInLevel = true;
    private boolean cameFromLevelsScene;
    private boolean waitingForStonesToDisappear = false;

    private boolean shootNormal = true;
    private boolean moveNormal = true;
    private boolean isGravity = false;
    private boolean rebound = false;
    private boolean isLamporghina = false;

    private static final int FIRST_LAYER = 0; //is used for ground, player and coin
    private static final int SECOND_LAYER = 1; //is used for  stones
    private int variant;
    private int variantStage;
    int timeBetweenStones = 0;
    int redArrow = 0;
    int yellowArrow = 0;
    private int level = 1;
    private int score = 0;
    private int time = 100*60; //100 seconds
    private int sideLength;
    private int xPosLume, yPosLume;
    private int xPosCoin, yPosCoin;

    private int[] playVariants;
    private int[] usedPlayVariants;

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
    private ArrayList<Sprite> crackyStones, crackyStonesToRemove, cannonBallsToRemove,
            mirrorStonesToRemove, mirrorStones;

    private Sprite luserSprite;

    private ArrayList<Sprite> elements, elementsToRemove;

    private HUD gameHUD;
    private Text scoreText, levelText, timeText;
    private Sprite shootNormalSign, moveNormalSign, shootDiagonalSign, moveDiagonalSign,
        mirrorSign, lamporghinaSign, helmetSign;
    private PhysicsWorld physicsWorld;


    private static final int SWIPE_MIN_DISTANCE = 10;

    private Text gameOverText;

    public HighscoreScene() { //default constructor
        this.level = 1;
        cameFromLevelsScene = false;
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
        elements = new ArrayList<Sprite>();
        elementsToRemove = new ArrayList<Sprite>();
        stoneTimes = new long[4];
        for (int i = 0; i < 4; i++) {
            stoneTimes[i] = 0;
        }
        level = 1;

        makePlayVariants();
        createLayers();
        createBackground();
        createPhysics();
        createBoard();
        createLume();
        createCannons();
        createHalves();
        createHUD();

        resetData();

        this.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (!waitingForStonesToDisappear) {
                    createStones(playVariants[((level-1)%5)]);
                    time--;
                    int subtrahend = (int)(time/(10*60));
                    int newLevel = 10 - subtrahend;
                    if (level != newLevel) {
                        if (newLevel == 0) newLevel = 1;
                        level = newLevel;
                        resetData();
                    }
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
        activity.toastOnUiThread(String.valueOf(playVariants[0]) + ", "
            + String.valueOf(playVariants[1]) + ", " + String.valueOf(playVariants[2]));
        stoneTime = new Date().getTime();
        for (int i = 0; i < 4; i++) {
            stoneTimes[i] = new Date().getTime();
        }
        makeSignsInvisible();
        setPlayVariant(playVariants[((level-1)%5)]);
        levelText.setText("L"+String.valueOf(level));
        firstStonesInLevel = true;
        variantUsed = false;
    }

    public void makeSignsInvisible() {
        shootNormalSign.setVisible(false);
        shootDiagonalSign.setVisible(false);
        mirrorSign.setVisible(false);
        lamporghinaSign.setVisible(false);
        moveNormalSign.setVisible(false);
        moveDiagonalSign.setVisible(false);
        helmetSign.setVisible(false);
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
        return SceneType.SCENE_HIGHSCORE;
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

        gameOverText = new Text(camera.getCenterX(), camera.getCenterY(), resourcesManager.smallFont, "Game Over!", vbom);
        gameOverText.setColor(Color.RED);
        gameOverScene.attachChild(gameOverText);

        //stop things
        unregisterUpdateHandler(physicsWorld);
        this.setIgnoreUpdate(true);
        this.setChildScene(gameOverScene, false, true, true); //set gameOverScene as a child scene - so game will be paused

        //show interstitial setAdVisibility
        if (!cameFromLevelsScene) {
            ResourcesManager.getInstance().activity.showMultiInterstitial();
        }

        gameOverScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
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
            }
        });
    }

    private void createLayers() {
        this.attachChild(new Entity()); // First Layer
        this.attachChild(new Entity()); // Second Layer
        firstLayer = this.getChildByIndex(FIRST_LAYER);
        secondLayer = this.getChildByIndex(SECOND_LAYER);
    }

    private void createHUD() {
        gameHUD = new HUD();

        scoreText = new Text(20, camera.getHeight() - 60, resourcesManager.smallFont, "0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
        scoreText.setAnchorCenter(0, 0);
        scoreText.setText("0");
        gameHUD.attachChild(scoreText);

        levelText = new Text(camera.getWidth()-200, camera.getHeight() - 60, resourcesManager.smallFont, "L0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
        levelText.setAnchorCenter(0, 0);
        levelText.setText("L" + String.valueOf(level));
        gameHUD.attachChild(levelText);

        timeText = new Text(camera.getWidth() - 65, camera.getHeight() - 60, resourcesManager.smallFont, "0123456789", new TextOptions(HorizontalAlign.RIGHT), vbom);
        timeText.setAnchorCenter(0, 0);
        timeText.setText("30");
        gameHUD.attachChild(timeText);

        //left signs
        shootNormalSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.shoot_normal_region, vbom);
        shootDiagonalSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.shoot_diagonal_region, vbom);
        mirrorSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.cracky_mirror_sign_region, vbom);
        lamporghinaSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.lamporghina_sign_region, vbom);
        //right signs
        moveNormalSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.move_normal_region, vbom);
        moveDiagonalSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.move_diagonal_region, vbom);
        helmetSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.helmet_region, vbom);

        gameHUD.attachChild(shootNormalSign);
        gameHUD.attachChild(shootDiagonalSign);
        gameHUD.attachChild(mirrorSign);
        gameHUD.attachChild(lamporghinaSign);
        gameHUD.attachChild(moveNormalSign);
        gameHUD.attachChild(moveDiagonalSign);
        gameHUD.attachChild(helmetSign);

        makeSignsInvisible();


        camera.setHUD(gameHUD);
    }

    private void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world0_region, vbom));
        this.setBackground(spriteBackground);
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
                        if (shootNormal) {
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
                        } else {
                            createCannonball(deltaX, deltaY);
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
                                if (2 * Math.abs(deltaY) > Math.abs(deltaX) && !moveNormal) {
                                    if (deltaY > 0) {
                                        movePlayer('1');
                                    } else {
                                        movePlayer('2');
                                    }
                                } else {
                                    movePlayer('R');
                                }
                            } else { //right to left
                                if (2 * Math.abs(deltaY) > Math.abs(deltaX) && !moveNormal) {
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
                                if (2 * Math.abs(deltaX) > Math.abs(deltaY) && !moveNormal) {
                                    if (deltaX > 0) {
                                        movePlayer('1');
                                    } else {
                                        movePlayer('4');
                                    }
                                } else {
                                    movePlayer('U');
                                }
                            } else {
                                if (2 * Math.abs(deltaX) > Math.abs(deltaY) && !moveNormal) {
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
        Ball ball;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
//        float speed = 1.4f*sideLength/5;
        float speed = resourcesManager.screenWidth*6/400;
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

                //if mirror stones are present, cannonball is deadly
                if (cannonCircle.collision(lumeCircle) && !gameOverDisplayed && rebound) {
                    luserSprite = new Sprite(lumeSprite.getX()-lumeSprite.getWidth()*4/10,
                            lumeSprite.getY() + lumeSprite.getHeight()*5/10,
                            lumeSprite.getWidth(), lumeSprite.getWidth(),
                            ResourcesManager.getInstance().finger_luser, vbom);
                    secondLayer.attachChild(luserSprite);
                    displayGameOverText();
                    score = 0;
                }

                for (Sprite crackyStone : crackyStones) { //loop for cracky stones
                    final Circle stoneCircle;
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
                                        sprite.detachSelf();
                                        sprite.dispose();
                                    }
                                    elementsToRemove.clear();
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
                        elementsToRemove.add(mirrorStone);

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
                                    elementsToRemove.clear();
                                    mirrorStonesToRemove.clear();
                                }
                            }
                        });
                    }
                }
                elements.removeAll(elementsToRemove);
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

    private void createCannonball(float deltaX, float deltaY) {
        ITextureRegion textureRegion = resourcesManager.cannonball_region;
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        FIXTURE_DEF.filter.categoryBits = CATEGORY_CANNONBALL;
        FIXTURE_DEF.filter.maskBits = MASK_CANNONBALL;
        final Sprite cannonball;
        Ball ball;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/5;

        float factor, vectorLength;
        float dirVectorX, dirVectorY;

        vectorLength = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        factor = (float) 0.6f*sideLength/vectorLength;
        dirVectorX = deltaX*factor;
        dirVectorY = deltaY*factor;

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
        body.setLinearVelocity(xVel, yVel);
        ball = new Ball();
        ball.setBody(body);
        cannonball.setUserData(ball);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(cannonball, body, true, false));
    }

    private void createStones(int mode) {
        char stoneType = 'C';
        float randomNumber = randomGenerator.nextFloat();
        double levelFactor = 1 + (level/10);
        long interval;

        switch (mode) {
            case 1:
                long age = (new Date()).getTime() - stoneTime;
                int direction = (randomGenerator.nextInt(4) + 1);
                if (level <= 5) {
                    interval = (long) (2500/levelFactor);
                    if (firstStonesInLevel) interval = 500;
                    if (age >= interval) {
                        if (firstStonesInLevel) createCoin();
                        firstStonesInLevel = false;
                        if (randomNumber < 0.4f) stoneType = 'T';
                        this.showStonesToScreen(direction, stoneType);
                        stoneTime = new Date().getTime();
                    }
                } else {
                    interval = (long) (2500/levelFactor);
                    if (firstStonesInLevel) interval = 500;
                    if (age >= interval) {
                        if (firstStonesInLevel) createCoin();
                        firstStonesInLevel = false;
                        if (randomNumber < 0.4f) stoneType = 'T';
                        this.showStonesToScreen(direction, stoneType);
                        stoneTime = new Date().getTime();
                    }
                }
                break;
            case 2:
                age = (new Date()).getTime() - stoneTime;
                direction = (randomGenerator.nextInt(4) + 1);
                if (level <= 5) {

                } else {

                }

                interval = (long) (2500/levelFactor);
                if (firstStonesInLevel) interval = 500;
                if (age >= interval) {
                    firstStonesInLevel = false;
                    if (randomNumber < 0.4f) stoneType = 'T';
                    this.showStonesToScreen(direction, stoneType);
                    stoneTime = new Date().getTime();
                }
                break;
            case 3:
                age = (new Date()).getTime() - stoneTime;
                direction = (randomGenerator.nextInt(4) + 1);
                if (level <= 5) {

                } else {

                }

                interval = (long) (2400/levelFactor);
                if (firstStonesInLevel) interval = 500;
                if (age >= interval) {
                    firstStonesInLevel = false;
                    if (randomNumber < 0.4f) stoneType = 'T';
                    this.showStonesToScreen(direction, stoneType);
                    stoneTime = new Date().getTime();
                }
                break;
            case 4:
                age = (new Date()).getTime() - stoneTime;
                direction = (randomGenerator.nextInt(4) + 1);
                if (level <= 5) {

                } else {

                }

                interval = (long) (2600/levelFactor);
                if (firstStonesInLevel) interval = 500;
                if (age >= interval) {
                    firstStonesInLevel = false;
                    if (randomNumber < 0.4f) stoneType = 'T';
                    this.showStonesToScreen(direction, stoneType);
                    stoneTime = new Date().getTime();
                }
                break;
            case 5:
                age = (new Date()).getTime() - stoneTime;
                direction = (randomGenerator.nextInt(4) + 1);
                if (level <= 5) {

                } else {

                }

                interval = (long) (3000/levelFactor);
                if (firstStonesInLevel) interval = 500;
                if (age >= interval) {
                    firstStonesInLevel = false;
                    if (randomNumber < 0.4f) stoneType = 'T';
                    this.showStonesToScreen(direction, stoneType);
                    stoneTime = new Date().getTime();
                }
                break;
        }
    }

    private void showStonesToScreen(int directionVariant, char showArrow) {
        float speed = 1f;
        boolean firstHalf = level <= 5;
        int randomPos = randomGenerator.nextInt(3);
        switch (playVariants[((level-1)%5)]) {
            case 1: //showing two stones next to each other
                showMode1(firstHalf, directionVariant, showArrow);
                break;
            case 2: //showing two stones next to each other
                showMode2(firstHalf, directionVariant, showArrow);
                break;
            case 3:
                showMode3(firstHalf, directionVariant, showArrow);

                float randomNum = randomGenerator.nextFloat();
                if (randomNum < 0.3f) {
                    speed = 2.5f;
                } else {
                    speed = 1.1f;
                }
                addBall(showArrow, directionVariant, randomPos, speed);
                addArrow(directionVariant, speed);
                break;
            case 4:
                showMode4(firstHalf, directionVariant, showArrow);
                break;
            case 5:
                showMode5(firstHalf, directionVariant, showArrow);
                break;
        }

    }

    private void showMode1(boolean firstHalf, int direction, char showArrow) {
        float levF = 1 + (level/10);
        float dirF = (direction%2 == 0) ? ratio : 1f;
        float speed = 1f;
        int randomPos = randomGenerator.nextInt(3);
        if (firstHalf) {
            int secondPos = randomPos+1;
            if (secondPos > 3) secondPos = 0;
            addBall('T', direction, randomPos, speed*levF*dirF); //thorny stone
            addBall('C', direction, secondPos, speed*levF*dirF); //cracky stone
        } else {
            addBall((randomPos == 0) ? 'C' : 'T', direction, 0, speed*levF*dirF);
            addBall((randomPos == 1) ? 'C' : 'T', direction, 1, speed*levF*dirF);
            addBall((randomPos == 2) ? 'C' : 'T', direction, 2, speed*levF*dirF);
        }
    }

    private void showMode2(boolean firstHalf, int direction, char showArrow) {
        float levF = 1 + (level/10);
        float dirF = (direction%2 == 0) ? ratio : 1f;
        float speed = 1f;
        int randomPos = randomGenerator.nextInt(3);
        if (firstHalf) {
            addBall((randomPos == 0) ? 'C' : 'T', direction, 0, speed*levF*dirF);
            addBall((randomPos == 1) ? 'C' : 'T', direction, 1, speed*levF*dirF);
            addBall((randomPos == 2) ? 'C' : 'T', direction, 2, speed*levF*dirF);
        } else {
            addBall((randomPos == 0) ? 'C' : 'T', direction, 0, speed*levF*dirF);
            addBall((randomPos == 1) ? 'C' : 'T', direction, 1, speed*levF*dirF);
            addBall((randomPos == 2) ? 'C' : 'T', direction, 2, speed*levF*dirF);
        }
    }

    private void showMode3(boolean firstHalf, int direction, char showArrow) {
        float levF = 1 + (level/10);
        float dirF = (direction%2 == 0) ? ratio : 1f;
        float speed = 1f;
        int randomPos = randomGenerator.nextInt(3);
        if (firstHalf) {
            addBall((randomPos == 0) ? 'C' : 'T', direction, 0, speed*levF*dirF);
            addBall((randomPos == 1) ? 'C' : 'T', direction, 1, speed*levF*dirF);
            addBall((randomPos == 2) ? 'C' : 'T', direction, 2, speed*levF*dirF);
        } else {
            addBall((randomPos == 0) ? 'C' : 'T', direction, 0, speed*levF*dirF);
            addBall((randomPos == 1) ? 'C' : 'T', direction, 1, speed*levF*dirF);
            addBall((randomPos == 2) ? 'C' : 'T', direction, 2, speed*levF*dirF);
        }
    }

    private void showMode4(boolean firstHalf, int direction, char showArrow) {
        float levF = 1 + (level/10);
        float dirF = (direction%2 == 0) ? ratio : 1f;
        float speed = 1f;
        int randomPos = randomGenerator.nextInt(3);
        if (firstHalf) {
            addBall((randomPos == 0) ? 'C' : 'T', direction, 0, speed*levF*dirF);
            addBall((randomPos == 1) ? 'C' : 'T', direction, 1, speed*levF*dirF);
            addBall((randomPos == 2) ? 'C' : 'T', direction, 2, speed*levF*dirF);
        } else {
            addBall((randomPos == 0) ? 'C' : 'T', direction, 0, speed*levF*dirF);
            addBall((randomPos == 1) ? 'C' : 'T', direction, 1, speed*levF*dirF);
            addBall((randomPos == 2) ? 'C' : 'T', direction, 2, speed*levF*dirF);
        }
    }

    private void showMode5(boolean firstHalf, int direction, char showArrow) {
        float levF = 1 + (level/10);
        float dirF = (direction%2 == 0) ? ratio : 1f;
        float speed = 1f;
        int randomPos = randomGenerator.nextInt(3);
        if (firstHalf) {
            addBall((randomPos == 0) ? 'C' : 'T', direction, 0, speed*levF*dirF);
            addBall((randomPos == 1) ? 'C' : 'T', direction, 1, speed*levF*dirF);
            addBall((randomPos == 2) ? 'C' : 'T', direction, 2, speed*levF*dirF);
        } else {
            addBall((randomPos == 0) ? 'C' : 'T', direction, 0, speed*levF*dirF);
            addBall((randomPos == 1) ? 'C' : 'T', direction, 1, speed*levF*dirF);
            addBall((randomPos == 2) ? 'C' : 'T', direction, 2, speed*levF*dirF);
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
            addToScore(1);
//            createCoin();
        }
    }

    private void makePlayVariants() {
        playVariants = new int[5];
        usedPlayVariants = new int[5];
        for (int i = 0; i < 5; i++) {
            int newNr;
            boolean doAgain = false;
            do {
                doAgain = false;
                newNr = randomGenerator.nextInt(5) + 1;
                for (int j = 0; j < usedPlayVariants.length; j++) {
                    if (newNr == usedPlayVariants[j]) {
                        doAgain = true;
                    }
                }
            } while (doAgain);
            playVariants[i] = newNr;
            usedPlayVariants[i] = newNr;
        }
    }

    private void setPlayVariant(int variant) {
        activity.toastOnUiThread(String.valueOf(variant), 0);
        switch (variant) {
            case 1:
                shootNormal = true;
                moveNormal = true;
                rebound = false;
                isGravity = false;
                isLamporghina = false;
                shootNormalSign.setVisible(true);
                moveNormalSign.setVisible(true);
                break;
            case 2:
                shootNormal = false;
                moveNormal = true;
                rebound = false;
                isGravity = true;
                isLamporghina = false;
                shootDiagonalSign.setVisible(true);
                moveNormalSign.setVisible(true);
                break;
            case 3:
                shootNormal = true;
                moveNormal = false;
                rebound = false;
                isGravity = false;
                isLamporghina = false;
                shootNormalSign.setVisible(true);
                moveDiagonalSign.setVisible(true);
                break;
            case 4:
                shootNormal = true;
                moveNormal = true;
                rebound = true;
                isGravity = false;
                isLamporghina = false;
                mirrorSign.setVisible(true);
                moveNormalSign.setVisible(true);
                break;
            case 5:
                shootNormal = true;
                moveNormal = true;
                rebound = false;
                isGravity = false;
                isLamporghina = true;
                lamporghinaSign.setVisible(true);
                helmetSign.setVisible(true);
                break;
            default:
                shootNormal = true;
                moveNormal = true;
                rebound = false;
                isGravity = false;
                isLamporghina = false;
                shootNormalSign.setVisible(true);
                moveNormalSign.setVisible(true);
        }
    }

    public Sprite addBall(final char type, int direction, int position, float speedFactor) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/15 * speedFactor;
        int gravityDirection = getGravityDirection(direction);

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
                xVel = (isGravity) ? (gravityDirection-3)*speed : 0;
                yVel = -speed;
                break;
            case 2:
                x = camera.getWidth() - sideLength / 2;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = -speed;
                yVel = (isGravity) ? (gravityDirection-2)*speed : 0;
                break;
            case 3:
                x = camera.getCenterX()-sideLength + sideLength*position;
                y = sideLength / 2;
                xVel = (isGravity) ? (gravityDirection-3)*speed : 0;
                yVel = speed;
                break;
            case 4:
                x = sideLength / 2;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = speed;
                yVel = (isGravity) ? (gravityDirection-2)*speed : 0;
                break;
        }

        stone = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                final Circle lumeCircle, stoneCircle;
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
                stoneCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);

                if ((stoneCircle.collision(lumeCircle) && !gameOverDisplayed && !isLamporghina) ||
                        (isLamporghina && type == 'T' && !gameOverDisplayed)) {
                    luserSprite = new Sprite(lumeSprite.getX()-lumeSprite.getWidth()*4/10,
                            lumeSprite.getY() + lumeSprite.getHeight()*5/10,
                            lumeSprite.getWidth(), lumeSprite.getWidth(),
                            ResourcesManager.getInstance().finger_luser, vbom);
                    secondLayer.attachChild(luserSprite);
                    displayGameOverText();
                    score = 0;
                } else if (stoneCircle.collision(lumeCircle) && isLamporghina && type == 'C') {
                    final Sprite crackyBallToRemove = this;

                    engine.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            crackyBallToRemove.detachSelf();
                            crackyBallToRemove.dispose();
                        }
                    });
                }


                if (this.getX() < -sideLength || this.getY() < -sideLength ||
                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
                    elementsToRemove.add(this);
                    if (type == 'C') crackyStonesToRemove.add(this);
                    if (type == 'M') mirrorStonesToRemove.add(this);

                    engine.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Sprite sprite : elementsToRemove) {
                                sprite.detachSelf();
                                sprite.dispose();
                            }
                            crackyStonesToRemove.clear();
                            mirrorStonesToRemove.clear();
                            elementsToRemove.clear();
                        }

                    });
                }

                crackyStones.removeAll(crackyStonesToRemove);
                mirrorStones.removeAll(mirrorStonesToRemove);
                elements.removeAll(elementsToRemove);

            }
        };
        secondLayer.attachChild(stone);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, stone, BodyType.KinematicBody, FIXTURE_DEF);
        if (type == 'C') {
            crackyStones.add(stone);
        } else if (type == 'M') mirrorStones.add(stone);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(stone, body, true, false));
        ball = new Ball(direction, null, body, stone, (type == 'T'), false, speedFactor);
        stone.setUserData(ball);
        elements.add(stone);
        return stone;
    }

    private void rebound(Sprite cannonBall) {
        int direction = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = (level == 4) ? resourcesManager.screenWidth*4/400 :
                resourcesManager.screenWidth*6/400;
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

    private void addArrow(final int direction, float speedFactor) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = speedFactor * sideLength / 15;
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
                    displayGameOverText();
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
        removeCoin();
        createCoin();
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

    private int getGravityDirection(int direction) {
        int gravityDirection;
        gravityDirection = ((direction%2+1) + randomGenerator.nextInt(2)*2);
        return gravityDirection;
    }

    private Vector2 getGravity(int gravityDirection) {
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

    private void createPhysics() {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
        registerUpdateHandler(physicsWorld);
    }

}