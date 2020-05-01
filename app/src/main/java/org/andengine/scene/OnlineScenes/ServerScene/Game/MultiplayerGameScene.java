package org.andengine.scene.OnlineScenes.ServerScene.Game;

import com.badlogic.gdx.math.Vector2;

import org.andengine.base.BaseScene;
import org.andengine.engine.camera.hud.HUD;
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
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.BallCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Multiplayer;
import org.andengine.scene.OnlineScenes.ServerScene.Player;
import org.andengine.scene.OnlineScenes.ServerScene.Server;
import org.andengine.util.adt.color.Color;

import java.util.ArrayList;
import java.util.LinkedList;

//this is a singleton because only one instance should be initialized in runtime
//this class should not extend from the BaseScene class.. It should extend from PlayScene or stuff like that
public class MultiplayerGameScene extends BaseScene {
    //INSTANCE
    private static MultiplayerGameScene INSTANCE;

    //constants
    private static final int FIRST_LAYER = 0; //is used for ground, player and coin
    private static final int SECOND_LAYER = 1; //is used for  stones
    private static final int THIRD_LAYER = 2; //is used for cannons
    private static final int SWIPE_MIN_DISTANCE = 10;

    //primitives
    private boolean gameOverDisplayed;
    private float sideLength;
    private float shootX1, shootX2, shootY1, shootY2;
    private float swipeX1, swipeX2, swipeY1, swipeY2;
    private int xPositions, yPositions;
    private int xPosLume, yPosLume;
    private int xPosGrume, yPosGrume;
    private int xPosCoin, yPosCoin;
    private int xPosBomb, yPosBomb;

    //andengine variables
    public Scene gameOverScene;
    public IEntity firstLayer, secondLayer, thirdLayer;
    public Sprite lumeSprite, grumeSprite;
    public HUD gameHUD;
    public Sprite lumeBomb, grumeBomb;
    public Sprite lumeHeart1, lumeHeart2, lumeHeart3;
    public Sprite grumeHeart1, grumeHeart2, grumeHeart3;
    public Sprite coinSprite;
    public Sprite[] cannonN, cannonE, cannonS, cannonW;
    public Sprite[] cannonNS, cannonES, cannonSS, cannonWS;
    public Sprite[] cannonNU, cannonEU, cannonSU, cannonWU;
    public Sprite luserSprite, finishSprite, replaySprite;
    public Text gameOverText;


    //objects
    private Multiplayer multiplayer;

    //public attributes
    public ArrayList<Sprite> crackyStones, crackyStonesToRemove, cannonBallsToRemove;
    public ArrayList<Sprite> stones, stonesToRemove;
    public PhysicsWorld physicsWorld;

    //createScene method
    @Override
    public void createScene() {/*don't write code here, because the method is calling in the super constructor.. so in other classes, the getInstance() method will not work!!!(NullPointerException!)*/}
    //static methods
    public static void createInstance(LinkedList<Player> players, Server server){ INSTANCE = new MultiplayerGameScene(players, server); INSTANCE.create();}
    public static MultiplayerGameScene getInstance(){
        if(INSTANCE == null) throw new RuntimeException("you have to call create Instance before getInstance!!!");
        return INSTANCE;
    }
    public static void destroyInstance(){
        INSTANCE.disposeScene();
        INSTANCE = null;
    }
    //methods
    private void create(){
        //initialization
        sideLength = resourcesManager.sideLength;
        multiplayer = new Multiplayer(null);
        xPositions = 3;
        yPositions = 3;

        createLayers();
        createBackground();
        createMusic();
        createPhysics();
        createBoard();
        createPlayer();
        createHalves();
        createCannons();
        createHUD();
    }

    public void createGame(){
        //TODO should called in LumeGameActions, if all players connect to the server(startGame())
        //TODO create Game
        //TODO don't write code here, delete this method!
    }
    //constructor
    private MultiplayerGameScene(LinkedList<Player> players){multiplayer = new Multiplayer(players);}
    private MultiplayerGameScene(LinkedList<Player> players, Server server){multiplayer = new Multiplayer(server,players);}
    //override methods from superclass
    @Override
    public void onBackKeyPressed() {

    }
    @Override
    public SceneType getSceneType() {return SceneType.SCENE_ONLINEMULTI;}
    @Override
    public void disposeScene() {
        //don't call the static destroy method, it will be recursive :)
    }

    private void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world0_region, vbom));
        this.setBackground(spriteBackground);
    }

    private void createLayers() {
        this.attachChild(new Entity()); // First Layer
        this.attachChild(new Entity()); // Second Layer
        this.attachChild(new Entity()); // Third Layer
        firstLayer = this.getChildByIndex(FIRST_LAYER);
        secondLayer = this.getChildByIndex(SECOND_LAYER);
        thirdLayer = this.getChildByIndex(THIRD_LAYER);
    }

    private void createPhysics() {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
        registerUpdateHandler(physicsWorld);
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
                                //createCannonball(4);
                            } else { //right to left
                                //createCannonball(2);
                            }
                        } else { //vertical swipe
                            if (deltaY > 0) { //up to down
                                //createCannonball(3);
                            } else { //down to up
                                //createCannonball(1);
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
                        //horizontal or vertical check
                        if (Math.abs(deltaX) > Math.abs(deltaY)) { //horizontal
                            if (deltaX > 0) { //left to right
                                //movePlayer('R');
                            } else { //right to left
                                //movePlayer('L');
                            }
                        } else { //vertical
                            if (deltaY > 0) { //up to down
                                //movePlayer('U');
                            } else { //down to up
                                //movePlayer('D');
                            }
                        }
                    } else { //TAP - show slowMotion

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

    private void createMusic() {
        ResourcesManager.getInstance().backgroundMusic.play();
    }

    private void createBoard() {
        Sprite boardSprite = new Sprite(camera.getCenterX(), camera.getCenterY(), sideLength * 3, sideLength * 3, resourcesManager.board_region, vbom);
        firstLayer.attachChild(boardSprite);
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

    private void createPlayer() {
        xPosLume = 1;
        yPosLume = 1;
        xPosGrume = 3;
        yPosGrume = 3;
        lumeSprite = new Sprite(camera.getCenterX() - sideLength, camera.getCenterY() - sideLength,
                sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.player_region, vbom);
        secondLayer.attachChild(lumeSprite);
        lumeSprite.setRotation(90);
        grumeSprite = new Sprite(camera.getCenterX() + sideLength, camera.getCenterY() + sideLength,
                sideLength*3/4, sideLength*3/4, resourcesManager.grume_region, vbom);
        secondLayer.attachChild(grumeSprite);
        grumeSprite.setRotation(270);
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
        gameHUD.detachChildren();
        gameHUD.detachSelf();
        gameHUD.dispose();
        gameHUD = null;
    }

    private void displayGameOverScene() {
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
                    SceneManager.getInstance().loadOnlineUsersScene(engine);
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
                    SceneManager.getInstance().loadMenuScene(engine);
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

    //getter
    public Multiplayer getMultiplayer() {return multiplayer;}
}