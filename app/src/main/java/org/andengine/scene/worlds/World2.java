package org.andengine.scene.worlds;

import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

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
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class World2 extends BaseScene {

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

    private int variant;
    private int variantStage;
    private int level = 1;
    private int score = 0;
    private int time = 30*60; //seconds
    private int sideLength;
    private int xPosLume, yPosLume;
    private int xPosCoin, yPosCoin;

    private long[] stoneTimes;
    private long stoneTime = 0;

    private float shootX1, shootX2, shootY1, shootY2;
    private float swipeX1, swipeX2, swipeY1, swipeY2;

    private Random randomGenerator;

    private IEntity firstLayer, secondLayer;

    private Sprite lumeSprite;
    private Sprite coinSprite;
    private Sprite cannonsN, cannonsE, cannonsS, cannonsW;
    private ArrayList<Sprite> crackyStones, crackyStonesToRemove, cannonBallsToRemove;

    private Sprite v1FirstStone, v2FirstStone, v3FirstStone;

    private ArrayList<Sprite> stones, stonesToRemove;

    private HUD gameHUD;
    private Text scoreText, levelText, timeText;
    private Sprite shootSign, moveSign, snailSign,noSnailSign;
    private PhysicsWorld physicsWorld;

    private Sprite luserSprite;

    private Scene gameOverScene;

    private static final int SWIPE_MIN_DISTANCE = 10;

    private Text gameOverText;
    private Sprite replaySprite;


    public World2() { //default constructor
        this.level = 1;
        cameFromLevelsScene = false;
        createHUD();
    }

    public World2(int level) { //constructor used when selecting a level
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
                    time -= (slowMotion) ? 0.3f : 1f;
                    int displayTime = (int) Math.round(time/60);
                    timeText.setText(String.valueOf(displayTime));
                    if (time <= 0 && !gameOverDisplayed) {
                        luserSprite = new Sprite(lumeSprite.getX()-lumeSprite.getWidth()*2/10,
                                lumeSprite.getY() + lumeSprite.getHeight()*4/10,
                                lumeSprite.getWidth()*6/10, lumeSprite.getWidth()*6/10,
                                ResourcesManager.getInstance().finger_luser, vbom);
                        secondLayer.attachChild(luserSprite);
                        displayGameOverText();
                    }
                    if (displayTime <= 5) {
                        timeText.setColor(Color.RED);
                    }
                }
                moveStones();
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
            SceneManager.getInstance().loadWorlds1to4Scene(engine);
            disposeHUD();
        } else {
            SceneManager.getInstance().loadMenuScene(engine);
            disposeHUD();
        }
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_WORLD2;
    }

    @Override
    public void disposeScene() {
        camera.setHUD(null);
        camera.setChaseEntity(null);
        camera.setCenter(camera.getCenterX(), camera.getCenterY());
    }

    private void displayGameOverText() {
        gameOverDisplayed = true;

        gameOverScene = new CameraScene(camera);
        gameOverScene.setBackgroundEnabled(false);

        ResourcesManager.getInstance().backgroundMusic.stop();
        ResourcesManager.getInstance().backgroundMusic.pause();
        ResourcesManager.getInstance().luserSound.play();

        float textY = (yPosLume == 2) ? camera.getCenterY() + sideLength : camera.getCenterY();
        gameOverText = new Text(camera.getCenterX(), textY,
                resourcesManager.smallFont, "L u s e r !", vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    //clear child scenes - game will be resumed
                    finishWorld();
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
                        SceneManager.getInstance().loadWorld2Scene(engine, level);
                    } else {
                        SceneManager.getInstance().loadWorld2Scene(engine, 0);
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

    private void finishWorld() {
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

        shootSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.shoot_diagonal_region, vbom);
        moveSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.move_normal_region, vbom);
        snailSign = new Sprite(camera.getCenterX() + 4*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.snail_sign_region, vbom);
        noSnailSign = new Sprite(camera.getCenterX() + 4*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.no_snail_sign_region, vbom);
        gameHUD.attachChild(shootSign);
        gameHUD.attachChild(moveSign);
        gameHUD.attachChild(snailSign);

        snailSign.setVisible(slowMotion);


        camera.setHUD(gameHUD);
    }

    private void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world2_region, vbom));
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
//                    if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE || Math.abs(deltaY) > SWIPE_MIN_DISTANCE) {
//                        //checks if it was horizontal or vertical
//                        if (Math.abs(deltaX) > Math.abs(deltaY)) { //horizontal swipe
//                            if (deltaX > 0) { //left to right
//                                createCannonball(4);
//                            } else { //right to left
//                                createCannonball(2);
//                            }
//                        } else { //vertical swipe
//                            if (deltaY > 0) { //up to down
//                                createCannonball(3);
//                            } else { //down to up
//                                createCannonball(1);
//                            }
//                        }
//                    }
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
//                        if (cameFromLevelsScene) {
//                            slowMotion = !slowMotion;
//                            setSlowMotionMode(); //sets values of current moving stones
//                            snailSign.setVisible(slowMotion);
//                        }
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
                        stonesToRemove.add(crackyStone);
                        cannonBallsToRemove.add(this);
                        final Sprite cannonBalltoRemove = this;

                        engine.runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                if (cannonBallsToRemove.size()+crackyStonesToRemove.size() > 0) {
                                    if (cannonBallsToRemove.size() > 0) {
                                        final PhysicsConnector physicsConnector =
                                                physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(cannonBalltoRemove);
                                        if (physicsConnector != null) {
                                            physicsWorld.unregisterPhysicsConnector(physicsConnector);
                                            Ball ball = (Ball) cannonBalltoRemove.getUserData();
                                            Body body = ball.getBody();
                                            body.setActive(false);
                                            physicsWorld.destroyBody(body);
                                            cannonBalltoRemove.detachSelf();
                                            cannonBalltoRemove.dispose();
                                        }
                                    }
                                    cannonBallsToRemove.clear();
                                    for (Sprite sprite : crackyStonesToRemove) {
                                        final PhysicsConnector physicsConnector =
                                                physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(sprite);
                                        if (physicsConnector != null) {
                                            physicsWorld.unregisterPhysicsConnector(physicsConnector);
                                            Ball ball = (Ball) sprite.getUserData();
                                            Body body = ball.getBody();
                                            body.setActive(false);
                                            physicsWorld.destroyBody(body);
                                            sprite.detachSelf();
                                            sprite.dispose();
                                        }
                                    }
                                    stonesToRemove.clear();
                                    crackyStonesToRemove.clear();
                                }
                            }
                        });
                    }
                }
                crackyStones.removeAll(crackyStonesToRemove);
                stones.removeAll(stonesToRemove);
//                crackyStonesToRemove.clear();

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

    private void createStones(int level) {
        float randomNumber = randomGenerator.nextFloat();
        double probabilityStone; //= 0.6
        long interval;

        switch (level) {
            case 1:
                probabilityStone = 0.7;
                int direction = (randomGenerator.nextInt(2) + 1)*2;
                boolean thorny = false;
                long[] ages = new long[4]; //is used to prevent screen from showing too many stones
                interval = (long) 1400;
                ages[direction - 1] = (new Date()).getTime() - stoneTimes[direction - 1];
                if (firstStonesInLevel) interval = 1000;
                if (ages[direction - 1] >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    if (randomNumber < probabilityStone) {
                        if (randomNumber < 0.25) {
                            thorny = true;
                        }
                        this.showStonesToScreen(direction, thorny);
                    }
                    stoneTimes[direction - 1] = new Date().getTime();
                }
                break;
            case 2:
                probabilityStone = 0.7;
                direction = (randomGenerator.nextInt(2) + 1)*2;
                thorny = false;
                ages = new long[4]; //is used to prevent screen from showing too many stones
                interval = (long) 1300;
                ages[direction - 1] = (new Date()).getTime() - stoneTimes[direction - 1];
                if (firstStonesInLevel) interval = 500;
                if (ages[direction - 1] >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    if (randomNumber < probabilityStone) {
                        if (randomNumber < 0.2) {
                            thorny = true;
                        }
                        this.showStonesToScreen(direction, thorny);
                    }
                    stoneTimes[direction - 1] = new Date().getTime();
                }
                break;
            case 3:
                long age = (new Date()).getTime() - stoneTime;
                probabilityStone = 0.8;
                direction = (randomGenerator.nextInt(4) + 1);
                interval = (long) 700;
                if (firstStonesInLevel) interval = 1000;
                if (age >= interval) {
                    if (firstStonesInLevel) createCoin();
                    firstStonesInLevel = false;
                    if (randomNumber < probabilityStone) {
                        this.showStonesToScreen(direction, false);
                    }
                    stoneTime = new Date().getTime();
                }
                break;
            case 4:
                randomNumber = randomGenerator.nextFloat();
                probabilityStone = 0.2;
                age = (new Date()).getTime() - stoneTime;
                interval = (long) 3300;
                if (firstStonesInLevel) interval = 500;
                int timeBetweenStones = 950;

                if (!this.variantUsed && randomNumber < probabilityStone && age >= interval) {
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
                } else if (variantUsed && variantStage == 2 && age >= timeBetweenStones) {
                    variantStage = 3;
                    showStonesToScreen(variant, false);
                    stoneTime = new Date().getTime();
                    variantUsed = false;
                }
                break;
        }
    }

    private void showStonesToScreen(int directionVariant, final boolean thornyFirst) {
        int randomRow = randomGenerator.nextInt(3); //values 0 to 2
        int gravityDirection;

        switch (level) {
            case 1:
                gravityDirection = 3;
                addBall(thornyFirst, directionVariant, gravityDirection, randomRow, 1.1f);
                break;
            case 2:
                gravityDirection = this.getGravityDirection(directionVariant); //randomly flies left or right
                addBall(thornyFirst, directionVariant, gravityDirection, randomRow, 1.1f);
                break;
            case 3:
                float factor = 1.2f;
                gravityDirection = this.getGravityDirection(directionVariant); //randomly flies left or right
                if (directionVariant == 1 || directionVariant == 3) {
                    if (randomRow == 2) gravityDirection = 4;
                    if (randomRow == 0) gravityDirection = 2;
                    factor = 0.8f;
                }
                addBall(thornyFirst, directionVariant, gravityDirection, randomRow, factor);
                break;
            case 4:
                switch (directionVariant) {
                    case 1:
                        if (variantStage == 1) {
                            addBall(true, 1, 1, 0, 2.1f);
                            addBall(false, 2, 1, 2, 1.1f);
                            addBall(false, 4, 3, 0, 1.1f);
                        } else if (variantStage == 2) {
                            addBall(true, 1, 1, 2, 2.1f);
                            addBall(false, 1, 2, 2, 0.8f);
                            addBall(false, 4, 1, 0, 1.1f);
                        } else if (variantStage == 3) {
                            addBall(true, 1, 1, 1, 2.1f);
                            addBall(false, 2, 3, 1, 1.1f);
                        }
                        break;
                    case 2:
                        if (variantStage == 1) {
                            addBall(true, 1, 1, 1, 2.1f);
                            addBall(false, 2, 3, 0, 1.1f);
                            addBall(false, 4, 1, 1, 1.1f);
                        } else if (variantStage == 2) {
                            addBall(true, 1, 1, 0, 2.1f);
                            addBall(false, 3, 4, 1, 0.8f);
                            addBall(false, 2, 1, 1, 1.1f);
                        } else if (variantStage == 3) {
                            addBall(true, 1, 1, 2, 2.1f);
                            addBall(false, 4, 3, 0, 1.1f);
                        }
                        break;
                    case 3:
                        if (variantStage == 1) {
                            addBall(true, 3, 3, 2, 2.1f);
                            addBall(false, 2, 3, 0, 1.1f);
                            addBall(false, 4, 1, 2, 1.1f);
                        } else if (variantStage == 2) {
                            addBall(true, 3, 3, 1, 2.1f);
                            addBall(false, 3, 4, 1, 0.8f);
                            addBall(false, 2, 1, 1, 1.1f);
                        } else if (variantStage == 3) {
                            addBall(true, 3, 3, 0, 2.1f);
                            addBall(false, 4, 3, 0, 1.1f);
                        }
                        break;
                    case 4:
                        if (variantStage == 1) {
                            addBall(true, 1, 1, 1, 2.1f);
                            addBall(false, 2, 1, 2, 1.1f);
                            addBall(false, 4, 3, 0, 1.1f);
                        } else if (variantStage == 2) {
                            addBall(true, 1, 1, 2, 2.1f);
                            addBall(false, 1, 2, 2, 0.8f);
                            addBall(false, 4, 1, 0, 1.1f);
                        } else if (variantStage == 3) {
                            addBall(true, 1, 1, 0, 2.1f);
                            addBall(false, 2, 3, 1, 1.1f);
                        }
                        break;
                }
                break;
        }
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

    public Sprite addBall(final boolean thorny, int direction, int gravityDirection, int position, float speedFactor) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        float slowMotionFactor = (slowMotion) ? 0.3f : 1f;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float xSpeed = camera.getWidth()/16/10 * speedFactor * slowMotionFactor;
        float ySpeed = sideLength/10 * speedFactor * slowMotionFactor;

        final Sprite stone;
        ITextureRegion textureRegion = (thorny) ? resourcesManager.thorny_stone_region : resourcesManager.cracky_stone_region;
        Ball ball;

        if (thorny) {
            FIXTURE_DEF.filter.categoryBits = CATEGORY_THORNY;
            FIXTURE_DEF.filter.maskBits = MASK_THORNY;
        } else { //cracky
            FIXTURE_DEF.filter.categoryBits = CATEGORY_CRACKY;
            FIXTURE_DEF.filter.maskBits = MASK_CRACKY;
        }

        switch (direction) {
            case 1:
                x = camera.getCenterX() - sideLength + sideLength*position;
                y = camera.getHeight() - sideLength / 2;
                xVel = (level == 4) ? 0 : (gravityDirection-3)*xSpeed; //2 v 4
                yVel = -ySpeed;
                break;
            case 2:
                x = camera.getWidth() - sideLength / 2;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = -xSpeed;
                yVel = (gravityDirection-2)*ySpeed;
                break;
            case 3:
                x = camera.getCenterX()-sideLength + sideLength*position;
                y = sideLength / 2;
                xVel = (level == 4) ? 0 : (gravityDirection-3)*xSpeed; //2 v 4
                yVel = ySpeed;
                break;
            case 4:
                x = sideLength / 2;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = xSpeed;
                yVel = (gravityDirection-2)*ySpeed;
                break;
        }

        final Vector2 gravity = getGravity(gravityDirection);

        stone = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom);
        Body body = PhysicsFactory.createCircleBody(physicsWorld, stone, BodyType.DynamicBody, FIXTURE_DEF);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(stone, body, true, false));
        ball = new Ball(direction, gravity, body, stone, thorny, false, speedFactor);
        stone.setUserData(ball);
        stones.add(stone);


//        stoneCircle = new Circle(x, y, sideLength*3/8);
//        stone = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
//
//            @Override
//            protected void onManagedUpdate(float pSecondsElapsed) {
//                Body body = PhysicsFactory.createCircleBody(physicsWorld, this, BodyType.DynamicBody, FIXTURE_DEF);
//
//                this.setUserData(body);
//                final Circle lumeCircle, stoneCircle;
//                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
//                stoneCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);
//
//                body.applyForce(gravity, body.getWorldCenter());
//
//                if (stoneCircle.collision(lumeCircle) && !gameOverDisplayed) {
//                    displayGameOverText();
//                    score = 0;
//                    scoreText.createTypingText("0");
//                }
//                if (this.getX() < -sideLength || this.getY() < -sideLength ||
//                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
//                    if (!thorny) crackyStones.remove(this);
//                    this.detachSelf();
//                    this.dispose();
//                }
//                super.onManagedUpdate(pSecondsElapsed);
//            }
//        };
        secondLayer.attachChild(stone);

        if (!thorny) {
            crackyStones.add(stone);
        }

//        body.setLinearVelocity(xVel, yVel);
//        physicsWorld.registerPhysicsConnector(new PhysicsConnector(stone, body, true, false));

//        stones.get(stones.size()-1) = PhysicsFactory.createCircleBody(physicsWorld, stone, BodyType.DynamicBody, FIXTURE_DEF);

        return stone;
    }

    private void moveStones() {
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
                luserSprite = new Sprite(lumeSprite.getX()-lumeSprite.getWidth()*4/10,
                        lumeSprite.getY() + lumeSprite.getHeight()*5/10,
                        lumeSprite.getWidth(), lumeSprite.getWidth(),
                        ResourcesManager.getInstance().finger_luser, vbom);
                secondLayer.attachChild(luserSprite);
                displayGameOverText();
                score = 0;
            }
            if (stone.getX() < -sideLength || stone.getY() < -sideLength ||
                    stone.getX() > camera.getWidth() + sideLength || stone.getY() > camera.getWidth() + sideLength) {
                stonesToRemove.add(stone);
                if (!thorny) crackyStonesToRemove.add(stone);

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
                        waitingForStonesToDisappear = false;
                        if (level == 5) {
                            worldFinished = true; //just to stop time
                            ResourcesManager.getInstance().backgroundMusic.stop();
                            ResourcesManager.getInstance().backgroundMusic.pause();
                            ResourcesManager.getInstance().easySound.play();
                            engine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
                                public void onTimePassed(final TimerHandler pTimerHandler) {
                                    engine.unregisterUpdateHandler(pTimerHandler);
                                    activity.unlockWorld(3);
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
//        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -SensorManager.GRAVITY_EARTH), false);
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
        registerUpdateHandler(physicsWorld);
    }

    private int getGravityDirection(int direction) {
        int gravityDirection;
        gravityDirection = (level == 1) ? 3 : ((direction%2+1) + randomGenerator.nextInt(2)*2);
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

    private void setSlowMotionMode() {
        for (Sprite stone : stones) {
            Ball ball = (Ball) stone.getUserData();
            Body body = ball.getBody();
            Vector2 gravity = ball.getGravity();
            if (slowMotion) {
                body.setLinearVelocity(body.getLinearVelocity().x*0.3f,
                        body.getLinearVelocity().y*0.3f);
                Vector2 newGravity = new Vector2(ball.getGravity().x*0.3f,
                        ball.getGravity().y*0.3f);
                ball.setGravity(newGravity);
            } else {
                body.setLinearVelocity(body.getLinearVelocity().x/0.3f,
                        body.getLinearVelocity().y/0.3f);
                Vector2 newGravity = new Vector2(ball.getGravity().x/0.3f,
                        ball.getGravity().y/0.3f);
                ball.setGravity(newGravity);
            }
        }
    }

}