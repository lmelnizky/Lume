package org.lume.scene.OnlineScenes.ServerScene.Game;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;

import org.lume.base.BaseScene;
import org.lume.engine.camera.hud.HUD;
import org.lume.engine.handler.timer.ITimerCallback;
import org.lume.engine.handler.timer.TimerHandler;
import org.lume.entity.Entity;
import org.lume.entity.IEntity;
import org.lume.entity.modifier.RotationModifier;
import org.lume.entity.modifier.ScaleModifier;
import org.lume.entity.primitive.Rectangle;
import org.lume.entity.scene.CameraScene;
import org.lume.entity.scene.Scene;
import org.lume.entity.scene.background.SpriteBackground;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.Text;
import org.lume.extension.physics.box2d.FixedStepPhysicsWorld;
import org.lume.extension.physics.box2d.PhysicsWorld;
import org.lume.input.touch.TouchEvent;
import org.lume.manager.ResourcesManager;
import org.lume.manager.SceneManager;
import org.lume.manager.SceneType;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.CannonCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.LoseLifeCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.MoveCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.PutBombCreator;
import org.lume.scene.OnlineScenes.ServerScene.Multiplayer;
import org.lume.scene.OnlineScenes.ServerScene.Player;
import org.lume.scene.OnlineScenes.ServerScene.Server;
import org.lume.util.adt.color.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

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
    public boolean gameOverDisplayed = false, waitingForStonesToDisappear, gameFinished, firstStonesInLevel = true;
    public boolean localCanShoot = true;
    public boolean lumeIndestructible;
    public boolean grumeIndestructible;
    public boolean lumeCanBomb = false, grumeCanBomb = false;
    public boolean lumeCanStone, grumeCanStone;
    public boolean bombing = false;
    public boolean bombLaid = false;
    public boolean iLaidBomb = false;
    public boolean stoneLaid = false;
    public float sideLength;
    public float shootX1, shootX2, shootY1, shootY2;
    public float swipeX1, swipeX2, swipeY1, swipeY2;
    public long stoneTime;
    public int xPositions, yPositions;
    public int xPosLocal, yPosLocal;
    public int xPosOpponent, yPosOpponent;
    public int xPosCoin, yPosCoin;
    public int xPosBomb, yPosBomb;
    public int xPosStone, yPosStone;
    public int lumeLives = 3;
    public int grumeLives = 3;
    public int stoneScoreLume, stoneScoreGrume;
    public int bombScoreLume, bombScoreGrume;
    public int time;
    public int variant;

    //andengine variables
    public Scene gameOverScene;
    public IEntity firstLayer, secondLayer, thirdLayer;
    public Sprite lumeSprite, grumeSprite;
    public HUD gameHUD;
    public Sprite lumeBomb03, lumeBomb13, lumeBomb23, lumeBomb33;
    public Sprite grumeBomb03, grumeBomb13, grumeBomb23, grumeBomb33;
    public Sprite lumeStone03, lumeStone13, lumeStone23, lumeStone33;
    public Sprite grumeStone03, grumeStone13, grumeStone23, grumeStone33;
    public Sprite lumeHeart1, lumeHeart2, lumeHeart3;
    public Sprite grumeHeart1, grumeHeart2, grumeHeart3;
    public Text lumeText, grumeText;
    public Sprite coinSprite;
    public Sprite bombSprite, stoneSprite, redBombSprite;
    public Sprite fireBeamHorizontal, fireBeamVertical;
    public Sprite[] cannonN, cannonE, cannonS, cannonW;
    public Sprite[] cannonNS, cannonES, cannonSS, cannonWS;
    public Sprite[] cannonNU, cannonEU, cannonSU, cannonWU;
    public Sprite luserSprite, finishSprite, replaySprite;
    public Text gameOverText;
    public Player localPlayer, opponentPlayer;
    private boolean debug = false;

    public Referee referee;


    //objects
    public Multiplayer multiplayer;
    public Random randomGenerator = new Random();

    //public attributes
    public ArrayList<Sprite> crackyStones = new ArrayList<>(), crackyStonesToRemove = new ArrayList<>(), cannonBallsToRemove = new ArrayList<>();
    public ArrayList<Sprite> stones = new ArrayList<Sprite>(), stonesToRemove = new ArrayList<Sprite>();
    public PhysicsWorld physicsWorld;

    //createScene method
    @Override
    public void createScene() {/*don't write code here, because the method is calling in the super constructor.. so in other classes, the getInstance() method will not work!!!(NullPointerException!)*/}
    //static methods
    public static void createInstance(LinkedList<Player> players, Server server, String room){ INSTANCE = new MultiplayerGameScene(players, server, room);}
    public static MultiplayerGameScene getInstance(){
        if(INSTANCE == null) throw new RuntimeException("you have to call create Instance before getInstance!!!");
        return INSTANCE;
    }
    public static void destroyInstance(){
        INSTANCE.disposeScene();
        INSTANCE = null;
    }
    //methods
    public void create() {
        //initialization
        sideLength = resourcesManager.sideLength;
        xPositions = 3;
        yPositions = 3;
        stoneTime = new Date().getTime();

        createLayers();
        createBackground();
        //createMusic();
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
    private MultiplayerGameScene(LinkedList<Player> players, Server server, String room){multiplayer = new Multiplayer(server,players, room);}
    //override methods from superclass
    @Override
    public void onBackKeyPressed() {
        disposeHUD();
        resourcesManager.server.deleteMe();
        resourcesManager.entities.removeAll(resourcesManager.entities);
        resourcesManager.playerEntities.removeAll(resourcesManager.playerEntities);
        resourcesManager.players.removeAll(resourcesManager.players);
        SceneManager.getInstance().loadMenuScene(engine);
    }
    @Override
    public SceneType getSceneType() {return SceneType.SCENE_ONLINEMULTI;}
    @Override
    public void disposeScene() {
        //don't call the static destroy method, it will be recursive :)
    }

    private void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.background_multi_region, vbom));
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
                System.out.println("tap on the left side!");
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
                                if (localCanShoot) {
                                    disableShootOnTime();
                                    multiplayer.getServer().emit(new CannonCreator(multiplayer.getRoom(), 4, multiplayer.getServer().id));
                                }
                            } else { //right to left
                                //createCannonball(2);
                                if (localCanShoot) {
                                    disableShootOnTime();
                                    multiplayer.getServer().emit(new CannonCreator(multiplayer.getRoom(), 2, multiplayer.getServer().id));
                                }
                            }
                        } else { //vertical swipe
                            if (deltaY > 0) { //up to down
                                //createCannonball(3);
                                if (localCanShoot) {
                                    disableShootOnTime();
                                    multiplayer.getServer().emit(new CannonCreator(multiplayer.getRoom(), 3, multiplayer.getServer().id));
                                }
                            } else { //down to up
                                if (localCanShoot) {
                                    disableShootOnTime();
                                    multiplayer.getServer().emit(new CannonCreator(multiplayer.getRoom(), 1, multiplayer.getServer().id));
                                }
                                //createCannonball(1);
                            }
                        }
                    } else { //TAP
                        debug = true;
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
                                Log.i("MultiPlayerGameScene", "iLaaidBomb: " + iLaidBomb);

                                if (bombLaid && lumeCanBomb && iLaidBomb) {
                                    System.out.println("lege bomb nach rechts");
                                    if (xPosLocal < 3) {
                                        iLaidBomb = false;
                                        multiplayer.getServer().emit(new PutBombCreator(multiplayer.getRoom(), (int) localPlayer.getCurrentPosition().x+1, (int) localPlayer.getCurrentPosition().y, localPlayer.getId()));
                                    }
                                } else {
                                    if (localPlayer.getCurrentPosition().x < 3 &&
                                            (localPlayer.getCurrentPosition().x + 1 != opponentPlayer.getCurrentPosition().x || localPlayer.getCurrentPosition().y != opponentPlayer.getCurrentPosition().y) &&
                                            (localPlayer.getCurrentPosition().x + 1 != xPosBomb || localPlayer.getCurrentPosition().y != yPosBomb)) {
                                        multiplayer.getServer().emit(new MoveCreator(multiplayer.getRoom(), 'R', getMultiplayer().getServer().id));
                                    }
                                }
                            } else { //right to left
                                //movePlayer('L');
                                Log.i("MultiPlayerGameScene", "iLaaidBomb: " + iLaidBomb);
                                if (bombLaid && lumeCanBomb && iLaidBomb) {
                                    System.out.println("lege bomb nach links");
                                    if (xPosLocal > 1) {
                                        iLaidBomb = false;
                                        multiplayer.getServer().emit(new PutBombCreator(multiplayer.getRoom(), (int) localPlayer.getCurrentPosition().x-1, (int) localPlayer.getCurrentPosition().y, localPlayer.getId()));
                                    }
                                } else {
                                    if (localPlayer.getCurrentPosition().x > 1 &&
                                            (localPlayer.getCurrentPosition().x - 1 != opponentPlayer.getCurrentPosition().x || localPlayer.getCurrentPosition().y != opponentPlayer.getCurrentPosition().y) &&
                                            (localPlayer.getCurrentPosition().x - 1 != xPosBomb || localPlayer.getCurrentPosition().y != yPosBomb)) {
                                        multiplayer.getServer().emit(new MoveCreator(multiplayer.getRoom(), 'L', getMultiplayer().getServer().id));
                                    }
                                }
                            }
                        } else { //vertical
                            if (deltaY > 0) { //up to down
                                //movePlayer('U');
                                Log.i("MultiPlayerGameScene", "iLaaidBomb: " + iLaidBomb);
                                if (bombLaid && lumeCanBomb && iLaidBomb) {
                                    System.out.println("lege bombe nach oben");
                                    if (yPosLocal < 3) {
                                        iLaidBomb = false;
                                        multiplayer.getServer().emit(new PutBombCreator(multiplayer.getRoom(), (int) localPlayer.getCurrentPosition().x, (int) localPlayer.getCurrentPosition().y+1, localPlayer.getId()));
                                    }
                                } else {
                                    if (localPlayer.getCurrentPosition().y < 3 &&
                                            (localPlayer.getCurrentPosition().x != opponentPlayer.getCurrentPosition().x || localPlayer.getCurrentPosition().y + 1 != opponentPlayer.getCurrentPosition().y) &&
                                            (localPlayer.getCurrentPosition().x != xPosBomb || localPlayer.getCurrentPosition().y + 1 != yPosBomb)) {
                                        multiplayer.getServer().emit(new MoveCreator(multiplayer.getRoom(), 'U', getMultiplayer().getServer().id));
                                    }
                                }
                            } else { //down to up
                                //movePlayer('D');
                                Log.i("MultiPlayerGameScene", "iLaaidBomb: " + iLaidBomb);
                                if (bombLaid && lumeCanBomb && iLaidBomb) {
                                    System.out.println("lege bombe nach unten");
                                    if (yPosLocal > 1) {
                                        iLaidBomb = false;
                                        multiplayer.getServer().emit(new PutBombCreator(multiplayer.getRoom(), (int) localPlayer.getCurrentPosition().x, (int) localPlayer.getCurrentPosition().y-1, localPlayer.getId()));
                                    }
                                } else {
                                    if (localPlayer.getCurrentPosition().y > 1 &&
                                            (localPlayer.getCurrentPosition().x != opponentPlayer.getCurrentPosition().x || localPlayer.getCurrentPosition().y - 1 != opponentPlayer.getCurrentPosition().y) &&
                                            (localPlayer.getCurrentPosition().x != xPosBomb || localPlayer.getCurrentPosition().y - 1 != yPosBomb)) {
                                        multiplayer.getServer().emit(new MoveCreator(multiplayer.getRoom(), 'D', getMultiplayer().getServer().id));
                                    }
                                }
                            }
                        }
                    } else { //TAP
                        System.out.println("Ich tippe gerade und versuche eine Bombe zu senden und habe so viele Muenzen: " + String.valueOf(bombScoreLume));
                        System.out.println("lumecanBomb: " + lumeCanBomb);
                        System.out.println("bombing: " + !bombing);
                        System.out.println("bomblaid: " + !bombLaid);
                        if (lumeCanBomb && !bombing && !bombLaid) {
                            System.out.println("Ich lege gerade eine Bombe!");
                            multiplayer.getServer().emit(new PutBombCreator(multiplayer.getRoom(),(int) localPlayer.getCurrentPosition().x,(int) localPlayer.getCurrentPosition().y, localPlayer.getId()));
                            iLaidBomb = true;
                            System.out.println("laidbomb: " + iLaidBomb);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
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


    private void playerMoved() {
        for (Player p : this.getMultiplayer().getPlayers()) {
            if  (p.getCurrentPosition().x == this.xPosCoin && p.getCurrentPosition().y == this.yPosCoin) {
//                int randomBelch = randomGenerator.nextInt(3) + 1;
//                switch (randomBelch) {
//                    case 1:
//                        ResourcesManager.getInstance().belchSound1.play();
//                        break;
//                    case 2:
//                        ResourcesManager.getInstance().belchSound2.play();
//                        break;
//                    case 3:
//                        ResourcesManager.getInstance().belchSound3.play();
//                        break;
//                }
                p.setScore(p.getScore()+1);
            }
        }
    }

    private void createMusic() {
        //ResourcesManager.getInstance().backgroundMusic.play();
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

    public void createRedBomb(final int xPos, final int yPos) {
        if (redBombSprite == null) {
            redBombSprite = new Sprite(camera.getCenterX() - sideLength + ((xPos - 1) * sideLength), camera.getCenterY() - sideLength + ((yPos - 1) * sideLength),
                    sideLength * 3/4, sideLength * 3/4, resourcesManager.bomb_red_region, vbom);
            this.attachChild(redBombSprite);
        } else {
            redBombSprite.setPosition(camera.getCenterX() - sideLength + ((xPos - 1) * sideLength), camera.getCenterY() - sideLength + ((yPos - 1) * sideLength));
            redBombSprite.setVisible(true);
        }

        registerUpdateHandler(new TimerHandler(0.5f, false, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
//                redBombSprite.detachSelf();
//                redBombSprite.dispose();
//                redBombSprite = null;
                redBombSprite.setVisible(false);
                explode(xPos, yPos);
            }
        }));
    }

    public void explode(int xPos, int yPos) {
        if (fireBeamHorizontal == null) {
            fireBeamHorizontal = new Sprite(camera.getCenterX(), camera.getCenterY() - sideLength + (yPos-1)*sideLength,
                    sideLength*3, sideLength*3/4, resourcesManager.firebeam_horizontal, vbom);
            this.attachChild(fireBeamHorizontal);
        } else {
            fireBeamHorizontal.setPosition(camera.getCenterX(), camera.getCenterY() - sideLength + (yPos-1)*sideLength);
            fireBeamHorizontal.setVisible(true);
        }

        if (fireBeamVertical == null) {
            fireBeamVertical = new Sprite(camera.getCenterX() - sideLength + (xPos-1)*sideLength, camera.getCenterY(),
                    sideLength*3/4, sideLength*3, resourcesManager.firebeam_vertical, vbom);
            this.attachChild(fireBeamVertical);
        } else {
            fireBeamVertical.setPosition(camera.getCenterX() - sideLength + (xPos-1)*sideLength, camera.getCenterY());
            fireBeamVertical.setVisible(true);
        }


        if (referee != null) killCheck();
        registerUpdateHandler(new TimerHandler(0.2f, false, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                if (referee != null) killCheck();
//                fireBeamHorizontal.detachSelf();
//                fireBeamHorizontal.dispose();
//                fireBeamHorizontal = null;
//                fireBeamVertical.detachSelf();
//                fireBeamVertical.dispose();
//                fireBeamVertical = null;
                fireBeamHorizontal.setVisible(false);
                fireBeamVertical.setVisible(false);
                bombing = false;
                xPosBomb = 0;
                yPosBomb = 0;
            }
        }));
    }

    public void killCheck() {
        if ((localPlayer.getCurrentPosition().x == xPosBomb || localPlayer.getCurrentPosition().y == yPosBomb) &&
                !lumeIndestructible && !gameOverDisplayed) {
            lumeIndestructible = true; //after emit would take too long!!!
            multiplayer.getServer().emit(new LoseLifeCreator(multiplayer.getRoom(), localPlayer.getId()));
        }
        if ((opponentPlayer.getCurrentPosition().x == xPosBomb || opponentPlayer.getCurrentPosition().y == yPosBomb) &&
                !grumeIndestructible && !gameOverDisplayed) {
            grumeIndestructible = true; //after emit would take too long!!!
            multiplayer.getServer().emit(new LoseLifeCreator(multiplayer.getRoom(), opponentPlayer.getId()));
        }
    }

    public void coinCheck() {
        Log.i("MultiplayerGameScene", "start coincheck");
        if (localPlayer.getCurrentPosition().x == xPosCoin && localPlayer.getCurrentPosition().y == yPosCoin) {
            Log.i("MultiPlayerGameScene", "localcurpos.x: " + String.valueOf(localPlayer.getCurrentPosition().x) +
                    "localcurpos.y: " + String.valueOf(localPlayer.getCurrentPosition().y));
            addBombScore(localPlayer);
            createCoin();
        } else if (opponentPlayer.getCurrentPosition().x == xPosCoin && opponentPlayer.getCurrentPosition().y == yPosCoin) {
            Log.i("MultiPlayerGameScene", "opponentcurpos.x: " + String.valueOf(opponentPlayer.getCurrentPosition().x) +
                    "opponentcurpos.y: " + String.valueOf(opponentPlayer.getCurrentPosition().y));
            addBombScore(opponentPlayer);
            createCoin();
        }
        Log.i("MultiplayerGamescene", "finishCoincheck");
    }

    public void createCoin() {
        Log.i("Multiplayergamescene", "before sending createCoin emit");

        if (referee != null){
        multiplayer.getServer().emit(referee.createCoin());
        }
        Log.i("MultiplayerGameScene", "Referee created new CoinPosition");
    }

    public void addBombScore(Player player) {
        System.out.println("");
        Log.i("MultiPlayerGameScene", "addBombscore start");
        if (player == localPlayer) {
            bombScoreLume++;
            Log.i("MultiplayerGameScene", "Added bombScoreLume, now: " + String.valueOf(bombScoreLume));
            if (bombScoreLume >= 3) lumeCanBomb = true;
        } else if (player == opponentPlayer) {
            bombScoreGrume++;
            Log.i("MultiplayerGameScene", "Added bombScoreGrume, now: " + String.valueOf(bombScoreGrume));
            if (bombScoreGrume >= 3) grumeCanBomb = true;
        }
        updateBombsHUD();
    }

    public void disableShootOnTime() {
        localCanShoot = false;
        engine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                localCanShoot = true;
            }
        }));
    }

    private void createPlayer() {
        String[] array = new String[2];
        array[0] = multiplayer.getPlayers().get(0).getId();
        array[1] = multiplayer.getPlayers().get(1).getId();
        Arrays.sort(array);
        xPosLocal = multiplayer.getServer().id.equals(array[0]) ? 1 : 3;
        yPosLocal = multiplayer.getServer().id.equals(array[0]) ? 1 : 3;
        xPosOpponent = xPosLocal == 1 ? 3 : 1;
        yPosOpponent = xPosLocal == 1 ? 3 : 1;
        lumeSprite = new Sprite(camera.getCenterX() - sideLength + (xPosLocal-1)*sideLength, camera.getCenterY() - sideLength + (yPosLocal-1)*sideLength,
                sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.lume_region, vbom);
        secondLayer.attachChild(lumeSprite);
        lumeSprite.setRotation(90);
        grumeSprite = new Sprite(camera.getCenterX() - sideLength+ (xPosOpponent-1)*sideLength, camera.getCenterY() - sideLength+ (yPosOpponent-1)*sideLength,
                sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.grume_region, vbom);
        secondLayer.attachChild(grumeSprite);
        grumeSprite.setRotation(270);

        if (multiplayer.getPlayers().get(0).getId().equals(multiplayer.getServer().id)){

            localPlayer = multiplayer.getPlayers().get(0);
            opponentPlayer = multiplayer.getPlayers().get(1);
            multiplayer.getPlayers().get(0).setSprite(lumeSprite);
            multiplayer.getPlayers().get(0).updatePosition(new Vector2(xPosLocal, yPosLocal));
            multiplayer.getPlayers().get(1).setSprite(grumeSprite);
            multiplayer.getPlayers().get(1).updatePosition(new Vector2(xPosOpponent, yPosOpponent));
        } else {
            localPlayer = multiplayer.getPlayers().get(1);
            opponentPlayer = multiplayer.getPlayers().get(0);
            multiplayer.getPlayers().get(0).setSprite(grumeSprite);
            multiplayer.getPlayers().get(0).updatePosition(new Vector2(xPosOpponent, yPosOpponent));
            multiplayer.getPlayers().get(1).setSprite(lumeSprite);
            multiplayer.getPlayers().get(1).updatePosition(new Vector2(xPosLocal, yPosLocal));
        }
    }

    private void createHUD() {
        gameHUD = new HUD();

        lumeBomb03 = new Sprite(camera.getCenterX()-sideLength*3, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign00_region, vbom);
        gameHUD.attachChild(lumeBomb03);

        lumeBomb13 = new Sprite(camera.getCenterX()-sideLength*3, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign13_region, vbom);
        lumeBomb13.setVisible(false);
        gameHUD.attachChild(lumeBomb13);

        lumeBomb23 = new Sprite(camera.getCenterX()-sideLength*3, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign23_region, vbom);
        lumeBomb23.setVisible(false);
        gameHUD.attachChild(lumeBomb23);

        lumeBomb33 = new Sprite(camera.getCenterX()-sideLength*3, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign_red_region, vbom);
        lumeBomb33.setVisible(false);
        gameHUD.attachChild(lumeBomb33);

        grumeBomb03 = new Sprite(camera.getCenterX() + sideLength*3, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign00_region, vbom);
        gameHUD.attachChild(grumeBomb03);

        grumeBomb13 = new Sprite(camera.getCenterX()+sideLength*3, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign13_region, vbom);
        grumeBomb13.setVisible(false);
        gameHUD.attachChild(grumeBomb13);

        grumeBomb23 = new Sprite(camera.getCenterX()+sideLength*3, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign23_region, vbom);
        grumeBomb23.setVisible(false);
        gameHUD.attachChild(grumeBomb23);

        grumeBomb33 = new Sprite(camera.getCenterX()+sideLength*3, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.bomb_sign_red_region, vbom);
        grumeBomb33.setVisible(false);
        gameHUD.attachChild(grumeBomb33);



        lumeStone03 = new Sprite(camera.getCenterX()-sideLength*4, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.stone_03_region, vbom);
        gameHUD.attachChild(lumeStone03);
        lumeStone03.setVisible(false);

        lumeStone13 = new Sprite(camera.getCenterX()-sideLength*4, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.stone_13_region, vbom);
        lumeStone13.setVisible(false);
        gameHUD.attachChild(lumeStone13);

        lumeStone23 = new Sprite(camera.getCenterX()-sideLength*4, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.stone_23_region, vbom);
        lumeStone23.setVisible(false);
        gameHUD.attachChild(lumeStone23);

        lumeStone33 = new Sprite(camera.getCenterX()-sideLength*4, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.stone_33_region, vbom);
        lumeStone33.setVisible(false);
        gameHUD.attachChild(lumeStone33);

        grumeStone03 = new Sprite(camera.getCenterX() + sideLength*4, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.stone_03_region, vbom);
        gameHUD.attachChild(grumeStone03);
        grumeStone03.setVisible(false);

        grumeStone13 = new Sprite(camera.getCenterX()+sideLength*4, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.stone_13_region, vbom);
        grumeStone13.setVisible(false);
        gameHUD.attachChild(grumeStone13);

        grumeStone23 = new Sprite(camera.getCenterX()+sideLength*4, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.stone_23_region, vbom);
        grumeStone23.setVisible(false);
        gameHUD.attachChild(grumeStone23);

        grumeStone33 = new Sprite(camera.getCenterX()+sideLength*4, camera.getHeight() - sideLength/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.stone_33_region, vbom);
        grumeStone33.setVisible(false);
        gameHUD.attachChild(grumeStone33);

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

        lumeText = new Text(sideLength*3, sideLength*0.5f, resourcesManager.standardFont, localPlayer.getUsername(), vbom);
        lumeText.setColor(0.8f, 0.8f, 0.8f);
        gameHUD.attachChild(lumeText);

        grumeText = new Text(camera.getWidth()-sideLength*3, sideLength*0.5f, resourcesManager.standardFont, opponentPlayer.getUsername(), vbom);
        grumeText.setColor(0.8f, 0.8f, 0.8f);
        gameHUD.attachChild(grumeText);

        camera.setHUD(gameHUD);
    }

    public void updateBombsHUD() {
        switch (bombScoreLume) {
            case 0:
                lumeBomb03.setVisible(true);
                lumeBomb13.setVisible(false);
                lumeBomb23.setVisible(false);
                lumeBomb33.setVisible(false);
                break;
            case 1:
                lumeBomb03.setVisible(false);
                lumeBomb13.setVisible(true);
                lumeBomb23.setVisible(false);
                lumeBomb33.setVisible(false);
                break;
            case 2:
                lumeBomb03.setVisible(false);
                lumeBomb13.setVisible(false);
                lumeBomb23.setVisible(true);
                lumeBomb33.setVisible(false);
                break;
            case 3:
                lumeBomb03.setVisible(false);
                lumeBomb13.setVisible(false);
                lumeBomb23.setVisible(false);
                lumeBomb33.setVisible(true);
            break;
        }

        switch (bombScoreGrume) {
            case 0:
                grumeBomb03.setVisible(true);
                grumeBomb13.setVisible(false);
                grumeBomb23.setVisible(false);
                grumeBomb33.setVisible(false);
                break;
            case 1:
                grumeBomb03.setVisible(false);
                grumeBomb13.setVisible(true);
                grumeBomb23.setVisible(false);
                grumeBomb33.setVisible(false);
                break;
            case 2:
                grumeBomb03.setVisible(false);
                grumeBomb13.setVisible(false);
                grumeBomb23.setVisible(true);
                grumeBomb33.setVisible(false);
                break;
            case 3:
                grumeBomb03.setVisible(false);
                grumeBomb13.setVisible(false);
                grumeBomb23.setVisible(false);
                grumeBomb33.setVisible(true);
                break;
        }
    }

    public void updateStonesHUD() {
        switch (stoneScoreLume) {
            case 0:
                lumeStone03.setVisible(true);
                lumeStone13.setVisible(false);
                lumeStone23.setVisible(false);
                lumeStone33.setVisible(false);
                break;
            case 1:
                lumeStone03.setVisible(false);
                lumeStone13.setVisible(true);
                lumeStone23.setVisible(false);
                lumeStone33.setVisible(false);
                break;
            case 2:
                lumeStone03.setVisible(false);
                lumeStone13.setVisible(false);
                lumeStone23.setVisible(true);
                lumeStone33.setVisible(false);
                break;
            case 3:
                lumeStone03.setVisible(false);
                lumeStone13.setVisible(false);
                lumeStone23.setVisible(false);
                lumeStone33.setVisible(true);
                break;
        }

        switch (stoneScoreGrume) {
            case 0:
                grumeStone03.setVisible(true);
                grumeStone13.setVisible(false);
                grumeStone23.setVisible(false);
                grumeStone33.setVisible(false);
                break;
            case 1:
                grumeStone03.setVisible(false);
                grumeStone13.setVisible(true);
                grumeStone23.setVisible(false);
                grumeStone33.setVisible(false);
                break;
            case 2:
                grumeStone03.setVisible(false);
                grumeStone13.setVisible(false);
                grumeStone23.setVisible(true);
                grumeStone33.setVisible(false);
                break;
            case 3:
                grumeStone03.setVisible(false);
                grumeStone13.setVisible(false);
                grumeStone23.setVisible(false);
                grumeStone33.setVisible(true);
                break;
        }
    }

    private void disposeHUD() {
        if (gameHUD != null) {
            gameHUD.detachChildren();
            gameHUD.detachSelf();
            gameHUD.dispose();
            gameHUD = null;
        }
    }

    public void displayGameOverScene(boolean winner) {
        multiplayer.getServer().deleteMe();
        gameOverDisplayed = true;

        gameOverScene = new CameraScene(camera);
        gameOverScene.setBackgroundEnabled(false);

//        ResourcesManager.getInstance().backgroundMusic.stop();
//        ResourcesManager.getInstance().backgroundMusic.pause();
//        ResourcesManager.getInstance().luserSound.play();

        float textY = (yPosLocal == 2) ? camera.getCenterY() + sideLength : camera.getCenterY();
        gameOverText = new Text(camera.getCenterX(), textY,
                resourcesManager.smallFont, "L u s e r ! W i n n e r !", vbom);
        if (winner) {
            gameOverText.setText("W i n n e r !");
            gameOverText.setColor(Color.GREEN);
        } else {
            gameOverText.setText("L u s e r !");
            gameOverText.setColor(Color.RED);

            luserSprite = new Sprite(lumeSprite.getX()-lumeSprite.getWidth()*4/10,
                    lumeSprite.getY() + lumeSprite.getHeight()*5/10,
                    lumeSprite.getWidth(), lumeSprite.getWidth(),
                    ResourcesManager.getInstance().finger_luser, vbom);
            secondLayer.attachChild(luserSprite);
            luserSprite.setVisible(false);
        }

        displayGameOverButtons();

        gameOverScene.registerTouchArea(gameOverText);
        gameOverScene.attachChild(gameOverText);

        RotationModifier rotMod = new RotationModifier(0.7f, 180, 720) {
            @Override
            protected void onModifierFinished(IEntity item) {
                //stop things
                //unregisterUpdateHandler(physicsWorld);
                setIgnoreUpdate(true);
                if (luserSprite != null) luserSprite.setVisible(true);
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
//        replaySprite = new Sprite(camera.getCenterX() + sideLength,
//                camera.getHeight()*2/9, sideLength, sideLength,
//                ResourcesManager.getInstance().replay_region, vbom) {
//            @Override
//            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
//                                         final float pTouchAreaLocalY) {
//                if (pSceneTouchEvent.isActionDown()) {
//                    //clear child scenes - game will be resumed
//                    clearChildScene();
//                    setIgnoreUpdate(false);
//                    gameOverDisplayed = false;
//                    registerUpdateHandler(physicsWorld);
//                    SceneManager.getInstance().loadOnlineUsersScene(engine);
//                    disposeHUD();
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        };
//        gameOverScene.registerTouchArea(replaySprite);
//        gameOverScene.attachChild(replaySprite);

        //add finish Sprite
        finishSprite = new Sprite(camera.getCenterX(),
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
                    resourcesManager.server.deleteMe();
                    resourcesManager.entities.removeAll(resourcesManager.entities);
                    resourcesManager.playerEntities.removeAll(resourcesManager.playerEntities);
                    resourcesManager.players.removeAll(resourcesManager.players);
                    SceneManager.getInstance().loadMenuScene(engine);
                    return true;
                } else {
                    return false;
                }
            }
        };
        gameOverScene.registerTouchArea(finishSprite);
        gameOverScene.attachChild(finishSprite);
    }

    public void loseLife(String playerId) {
        if (playerId.equals(localPlayer.getId())) {
            lumeLives--;

            Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds on local device
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                //deprecated in API 26
                v.vibrate(50);
            }

            if (lumeLives == 0) {
//                lumeHeart3.detachSelf();
//                lumeHeart3.dispose();
                lumeHeart3.setVisible(false);
                displayGameOverScene(false);
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
//                lumeHeart2.detachSelf();
//                lumeHeart2.dispose();
                lumeHeart2.setVisible(false);
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
//                lumeHeart1.detachSelf();
//                lumeHeart1.dispose();
                lumeHeart1.setVisible(false);
            }
        } else {
            grumeLives--;

            if (grumeLives == 0) {
//                grumeHeart3.detachSelf();
//                grumeHeart3.dispose();
                grumeHeart3.setVisible(false);
                displayGameOverScene(true);
            } else if (grumeLives == 1){ //lume has one life left
                grumeIndestructible = true;
                grumeSprite.setAlpha(0.3f);
                registerUpdateHandler(new TimerHandler(2f, false, new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        grumeSprite.setAlpha(1f);
                        grumeIndestructible = false;
                    }
                }));
//                grumeHeart2.detachSelf();
//                grumeHeart2.dispose();
                grumeHeart2.setVisible(false);
            } else if (grumeLives == 2) { //lume has two lives left
                grumeIndestructible = true;
                grumeSprite.setAlpha(0.3f);
                registerUpdateHandler(new TimerHandler(2f, false, new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        grumeSprite.setAlpha(1f);
                        grumeIndestructible = false;
                    }
                }));
//                grumeHeart1.detachSelf();
//                grumeHeart1.dispose();
                grumeHeart1.setVisible(false);
            }
        }
    }

    //getter
    public Multiplayer getMultiplayer() {return multiplayer;}
}