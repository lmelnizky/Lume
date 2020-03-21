package org.andengine.scene;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.base.BaseScene;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class MultiBTScene extends BaseScene {

    private boolean master;
    private boolean canBomb = true;
    private boolean lumeIndestructible;
    private boolean grumeIndestructible;
    private boolean gameOverDisplayed = false;

    private boolean cannonBallRemove = false;

    private int finishTouch = 0;

    private int lumeScore = 0;
    private int grumeScore = 0;
    private int lumeLives = 2;
    private int grumeLives = 2;
    private int time = 0; //seconds
    private int pointsToLose;
    private int moveDirection = 0;
    private int shootDirection = 0;

    private int sideLength;
    private int xPositions, yPositions;
    private int xPosLume, yPosLume;
    private int xPosGrume, yPosGrume;
    private int xPosCoin, yPosCoin;
    private int xPosBomb, yPosBomb;

    private long[] stoneTimes;

    private float shootX1, shootX2, shootY1, shootY2;
    private float swipeX1, swipeX2, swipeY1, swipeY2;

    private Random randomGenerator;

    private Sprite lumeSprite;
    private Sprite grumeSprite;
    private Sprite coinSprite;
    private Sprite bombSprite;
    private Sprite redBombSprite;
    private Sprite fireBeamHorizontal, fireBeamVertical;
    private Sprite lumeHeart1, lumeHeart2, grumeHeart1, grumeHeart2;
    private ArrayList<Sprite> crackyStones, crackyStonesToRemove, cannonBallsToRemove;

    private HUD gameHUD;
    private Text myScoreText, myLivesText, timeText, opponentScoreText, opponentLivesText;
    private Text gameOverText, lumeResultText, grumeResultText, winnerText;
    private PhysicsWorld physicsWorld;


    private static final int SWIPE_MIN_DISTANCE = 10;
    private static final String LUME = "LUME";
    private static final String UUID = "41feb2a0-fb56-11e6-bc64-92361f002671";

    private String player;

    private Text enable, startGame, output;

    private interface MessageConstants {
        public static final int MESSAGE_READ=0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    private Handler handler;
    BluetoothAdapter bluetoothAdapter;

    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    @Override
    public void createScene() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            activity.toastOnUiThread("Bluetooth is not available!",
                    Toast.LENGTH_SHORT);
        }

        player = ResourcesManager.getInstance().player;
        if (player.equals("Lume")) {
            if (!ResourcesManager.getInstance().bluetoothSocket.isConnected()) {
                try {
                    ResourcesManager.getInstance().bluetoothSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (player.equals("Grume")) {
            if (!ResourcesManager.getInstance().bluetoothSocket.isConnected()) {
                try {
                    ResourcesManager.getInstance().bluetoothSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        sideLength = (int) resourcesManager.screenHeight / 9;
        xPositions = 4;
        yPositions = 4;
        randomGenerator = new Random();
        crackyStones = new ArrayList<Sprite>();
        crackyStonesToRemove = new ArrayList<Sprite>();
        cannonBallsToRemove = new ArrayList<Sprite>();
        stoneTimes = new long[4];
        for (int i = 0; i < 4; i++) {
            stoneTimes[i] = 0;
        }


        createBackground();
        createTexts();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                byte[] result;
                String readMessage;
                switch (inputMessage.what) {
                    case MessageConstants.MESSAGE_READ:
                        if (inputMessage.arg1 > 0) {
                            result = (byte[]) inputMessage.obj;
                            decodeMessage(result);
                            readMessage = new String(result, 0, inputMessage.arg1);
//                            ResourcesManager.getInstance().activity.toastOnUiThread(readMessage, Toast.LENGTH_SHORT);

//                            startGame();
                        }
                        break;
                    case MessageConstants.MESSAGE_WRITE:
//                        if (inputMessage.arg1 > 0) {
//                            result = (byte[]) inputMessage.obj;
//                            readMessage = new String(result, 0, inputMessage.arg1);
//                            ResourcesManager.getInstance().activity.toastOnUiThread(readMessage, Toast.LENGTH_SHORT);
//
////                            startGame();
//                        }
                        break;
                    default:
                        if (inputMessage.arg1 > 0) {
                            result = (byte[]) inputMessage.obj;
                            readMessage = new String(result, 0, inputMessage.arg1);
                        }
                }
            }
        };


        registerUpdateHandler(new TimerHandler(2f, false, new ITimerCallback()
        {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
                connectedThread = new ConnectedThread(ResourcesManager.getInstance().bluetoothSocket);
                connectedThread.start();
            }
        }));


    }

    public void startGame() {
        setBackground();
        deleteTexts();
        createPhysics();
        createBoard();
        createPlayer();
        createHalves();
        createHUD();

        this.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (master) createStones();
                time++;
                pointsToLose = (int) Math.round(time/300); //every 5 sec with 6o fps
                timeText.setText(String.valueOf(pointsToLose));
            }

            @Override
            public void reset() {

            }
        });

        createCoin(true);
    }

    public void decodeMessage(byte[] result){
        switch (result[0]) {
            case 1: //start Game
                if (result[1] == 1) { // got request
                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    activity.toastOnUiThread("RequestReceive: " + currentDateandTime, Toast.LENGTH_LONG);
                    byte[] g = new byte[]{1, 0, 1}; //send response
                    connectedThread.write(g);

                    startGame();
                } else if (result[2] == 1) { // got response
                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    activity.toastOnUiThread("ResponseReceive: " + currentDateandTime, Toast.LENGTH_LONG);

                    startGame();
                }
                break;
            case 2: //LumeMove
                if (result[3] == 1) { //requested move
                    byte[] g = new byte[]{2, result[1], result[2], 2}; //send response
                    connectedThread.write(g);

//                    this.moveLume(result[1]); //actually moves lume
                    this.setLumePosition(result[1], result[2]);
//                    this.coinCheck();
                } else if (result[3] == 2) { //response to my move

//                    this.moveLume(result[1]); //actually moves lume
                    this.coinCheck(); //only checkCoin once!!!
                }
                break;
            case 3: //GrumeMove
                if (result[3] == 1) { //requested move
                    byte[] g = new byte[]{3, result[1], result[2], 2}; //send response
                    connectedThread.write(g);

//                    this.moveGrume(result[1]); //actually moves grume
                    this.setGrumePosition(result[1], result[2]);
//                    this.coinCheck();
                } else if (result[3] == 2) { //response to my move

//                    this.moveGrume(result[1]); //actually moves grume
                    this.coinCheck();
                }
                break;
            case 4: //CoinPosition
                if (result[3] == 1) { //requested coin change from other player
                    byte[] g = new byte[]{4, result[1], result[2], 2}; //send response
                    connectedThread.write(g);

                    this.setCoinPosition(result[1], result[2]);

//                    byte[] g = null;
//                    if (this.player.equals("Lume")) {
//                        g = new byte[]{5, 1, (byte) lumeScore, 1}; //send request
//                    } else if (this.player.equals("Grume")) {
//                        g = new byte[]{5, 2, (byte) grumeScore, 1}; //send request
//                    }
//                    connectedThread.write(g);
                } else if (result[3] == 2) { //response from other device

                    this.setCoinPosition(result[1], result[2]);
//
                    byte[] g = null;
                    if (this.player.equals("Lume")) {
                        g = new byte[]{5, 1, (byte) lumeScore, 1}; //send request
                    } else if (this.player.equals("Grume")) {
                        g = new byte[]{5, 2, (byte) grumeScore, 1}; //send request
                    }

                    connectedThread.write(g);
                }
                break;
            case 5: //ScoreUpdate
                if (result[1] == 1) {
                    if (result[3] == 1) { //i am grume and got request from lume
                        lumeScore = result[2];
                        byte[] g = new byte[]{5, result[1], result[2], 2}; //send response
                        connectedThread.write(g);
                        setScoreText();
                    } else if (result[3] == 2) { //i am lume and got a response back
                        setScoreText();
                    }
                } else if (result[1] == 2) {
                    if (result[3] == 1) { //i am lume and got a request from grume
                        grumeScore = result[2];
                        byte[] g = new byte[]{5, result[1], result[2], 2}; //send response
                        connectedThread.write(g);
                        setScoreText();
                    } else if (result[3] == 2) { //i am grume and got a response back
                        setScoreText();
                    }
                }
                break;
            case 6: //Stones
                if (result[3] == 1) {
                    showStonesToScreen(result[1], result[2], false);
                    byte[] g = new byte[]{6, result[1], result[2], 2}; //send response
                    connectedThread.write(g);
                } else if (result[3] == 2) {
                    showStonesToScreen(result[1], result[2], false);
                }
                break;
            case 7: //Cannonball
                if (result[3] == 1) {
                    createCannonball(result[1], result[2]);
                }
                break;
            case 8: //Bomb
                if (result[3] == 1) {
                    createBomb(result[1], result[2]);
                }
                break;
            case 9: //LoseLife
                if (result[1] == 1) { //Lume
                    if (result[3] == 1) { //request
                        lumeKill();
                    }
                } else if (result[1] == 2) { //Grume
                    if (result[3] == 1) { //request
                        grumeKill();
                    }
                }
                break;
            case 10: //GameOver
                if (result[3] == 1) { //request
                    displayGameOverText(result[1]);
                }
                break;
        }
    }

    @Override
    public void onBackKeyPressed() {
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
        setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
    }

    private void setBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world1_region, vbom));
        this.setBackground(spriteBackground);
    }

    private void createTexts() {
        startGame = new Text(camera.getWidth()/2, camera.getCenterY(), resourcesManager.smallFont, "Start Game\n(wait 2 Seconds)", new TextOptions(HorizontalAlign.CENTER), vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
//                    Toast.makeText(activity.getApplicationContext(), "Message", Toast.LENGTH_SHORT).show();
                    if (ResourcesManager.getInstance().bluetoothSocket.isConnected()) {
                        activity.toastOnUiThread("Message",
                                Toast.LENGTH_SHORT);
                        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        master = true;
                        byte[] g = new byte[]{1, 1, 0};
                        connectedThread.write(g);
                    } else {
                        activity.toastOnUiThread("No Connection",
                                Toast.LENGTH_SHORT);
                    }
                    return  true;
                } else {
                    return false;
                }

            }
        };

        this.registerTouchArea(startGame);
        this.attachChild(startGame);
    }

    private void deleteTexts() {
        this.unregisterTouchArea(startGame);
        this.detachChild(startGame);
        startGame.dispose();
    }

    private void createBoard() {
        Sprite boardSprite = new Sprite(camera.getCenterX(), camera.getCenterY(), sideLength * 4, sideLength * 4, resourcesManager.board_region4, vbom);
        this.attachChild(boardSprite);
    }

    private void createPlayer() {
        xPosLume = 1;
        yPosLume = 1;
        xPosGrume = 4;
        yPosGrume = 4;
        lumeSprite = new Sprite(camera.getCenterX() - sideLength*3/2, camera.getCenterY() - sideLength*3/2,
                sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.lume_region, vbom);
        this.attachChild(lumeSprite);
        grumeSprite = new Sprite(camera.getCenterX() + sideLength*3/2, camera.getCenterY() + sideLength*3/2,
                sideLength*3/4, sideLength*3/4, resourcesManager.grume_region, vbom);
        this.attachChild(grumeSprite);
    }

    private void createCoin(boolean firstTime) {
        if (firstTime) {
            if (master) {
                do {
                    xPosCoin = randomGenerator.nextInt(xPositions) + 1;
                    yPosCoin = randomGenerator.nextInt(yPositions) + 1;
                } while ((xPosCoin == xPosLume && yPosCoin == yPosLume) || (xPosCoin == xPosGrume && yPosCoin == yPosGrume));
                byte xPos, yPos;
                xPos = (byte) xPosCoin;
                yPos = (byte) yPosCoin;
                byte[] g = new byte[]{4, xPos, yPos, 1}; //send request
                connectedThread.write(g);
            }
        } else {
            do {
                xPosCoin = randomGenerator.nextInt(xPositions) + 1;
                yPosCoin = randomGenerator.nextInt(yPositions) + 1;
            } while ((xPosCoin == xPosLume && yPosCoin == yPosLume) || (xPosCoin == xPosGrume && yPosCoin == yPosGrume));
            byte xPos, yPos;
            xPos = (byte) xPosCoin;
            yPos = (byte) yPosCoin;
            byte[] g = new byte[]{4, xPos, yPos, 1}; //send request
            connectedThread.write(g);

//            this.setCoinPosition(xPos, yPos);
        }

    }

    private void setCoinPosition(int xPos, int yPos) {
        if (coinSprite == null) {
            coinSprite = new Sprite(camera.getCenterX() - sideLength*3/2 + ((xPos - 1) * sideLength), camera.getCenterY() - sideLength*3/2 + ((yPos - 1) * sideLength),
                    sideLength * 7/8, sideLength * 7/8, resourcesManager.coin_region, vbom);
            this.attachChild(coinSprite);
        } else {
            coinSprite.setPosition(camera.getCenterX() - sideLength*3/2 + ((xPos - 1) * sideLength), camera.getCenterY() - sideLength*3/2 + ((yPos - 1) * sideLength));
        }
        xPosCoin = xPos;
        yPosCoin = yPos;
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
//                                createCannonball(4);
                                shootDirection = 4;
                            } else { //right to left
//                                createCannonball(2);
                                shootDirection = 2;
                            }
                        } else { //vertical swipe
                            if (deltaY > 0) { //up to down
//                                createCannonball(3);
                                shootDirection = 3;
                            } else { //down to up
//                                createCannonball(1);
                                shootDirection = 1;
                            }
                        }

                        if (player.equals("Lume")) {
                            byte direction = (byte) shootDirection;
                            byte[] g = new byte[]{7, direction, 1, 1}; //send request
                            connectedThread.write(g);

                            createCannonball(shootDirection, 1);
                        } else if (player.equals("Grume")) {
                            byte direction = (byte) shootDirection;
                            byte[] g = new byte[]{7, direction, 2, 1}; //send request
                            connectedThread.write(g);

                            createCannonball(shootDirection, 2);
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
//                                movePlayer(2);
                                moveDirection = 2;
                            } else { //right to left
//                                movePlayer(4);
                                moveDirection = 4;
                            }
                        } else { //vertical
                            if (deltaY > 0) { //up to down
//                                movePlayer(1);
                                moveDirection = 1;
                            } else { //down to up
//                                movePlayer(3);
                                moveDirection = 3;
                            }
                        }

                        if (player.equals("Lume")) {
                            int[] moveXY = moveLume(moveDirection); //sets xy coordinates
                            byte xMove = (byte) moveXY[0];
                            byte yMove = (byte) moveXY[1];
                            if (xMove > 0 && yMove > 0) {
                                byte[] g = new byte[]{2, xMove, yMove, 1}; //send request
                                connectedThread.write(g);
                                setLumePosition(xMove, yMove);
                            }
                        } else if (player.equals("Grume")) {
                            int[] moveXY = moveGrume(moveDirection); //sets xy coordinates
                            byte xMove = (byte) moveXY[0];
                            byte yMove = (byte) moveXY[1];

                            if (xMove > 0 && yMove > 0) {
                                byte[] g = new byte[]{3, xMove, yMove, 1}; //send request
                                connectedThread.write(g);
                                setGrumePosition(xMove, yMove);
                            }
                        }
                    } else { //TAP
                        if (player.equals("Lume") && canBomb) {
                            byte[] g = new byte[]{8, (byte) xPosLume, (byte) yPosLume, 1}; //send request
                            connectedThread.write(g);

                            createBomb(xPosLume, yPosLume);
                        } else if (player.equals("Grume") && canBomb) {
                            byte[] g = new byte[]{8, (byte) xPosGrume, (byte) yPosGrume, 1}; //send request
                            connectedThread.write(g);

                            createBomb(xPosGrume, yPosGrume);
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
        this.attachChild(shootLeft);
        this.attachChild(swipeRight);
    }

    private void createCannonball(int direction, int player) {
        ITextureRegion textureRegion = resourcesManager.cannonball_region;
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        final Sprite cannonball;
        Sprite playerSprite = null;
        Ball ball;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = 8;
        if (player == 1) {
            playerSprite = lumeSprite;
        } else if (player == 2) {
            playerSprite = grumeSprite;
        }
        switch (direction) {
            case 1: //up to down
                x = playerSprite.getX();
                y = playerSprite.getY() - playerSprite.getHeight();
                yVel = -speed;
                break;
            case 2: //right to left
                x = playerSprite.getX() - playerSprite.getWidth();
                y = playerSprite.getY();
                xVel = -speed;
                break;
            case 3: //down to up
                x = playerSprite.getX();
                y = playerSprite.getY() + playerSprite.getHeight();
                yVel = speed;
                break;
            case 4: //left to right
                x = playerSprite.getX() + playerSprite.getWidth();
                y = playerSprite.getY();
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

//                        cannonBallsToRemove.add(this);
                        cannonBallRemove = true;
                        final Sprite cannonBalltoRemove = this;

                        engine.runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                if (crackyStonesToRemove.size() > 0 && cannonBallRemove) {
//                                    for (Sprite sprite : cannonBallsToRemove) {
//                                        sprite.detachSelf();
//                                        sprite.dispose();
//                                    }
                                    if (cannonBallRemove) {
                                        cannonBalltoRemove.detachSelf();
                                        cannonBalltoRemove.dispose();
                                        cannonBallRemove = false;
                                    }
//                                    cannonBallsToRemove.clear();

                                }
                            }
                        });
                    }
                }

                crackyStones.removeAll(crackyStonesToRemove);

                for (Sprite sprite : crackyStonesToRemove) {
                    sprite.detachSelf();
                    sprite.dispose();
                }
                crackyStonesToRemove.clear();

                if (this.getX() < -sideLength || this.getY() < - sideLength ||
                        this.getX() > camera.getWidth()+sideLength || this.getY() > camera.getWidth()+sideLength) {
                    this.detachSelf();
                    this.dispose();
                }
            }
        };
        this.attachChild(cannonball);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, cannonball, BodyDef.BodyType.KinematicBody, FIXTURE_DEF);
//        ball = new Ball(cannonball, "cannonball");
//        body.setUserData(ball);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(cannonball, body, true, false));
    }

    private void createBomb(int xPos, int yPos) {
        canBomb = false;
        bombSprite = new Sprite(camera.getCenterX() - sideLength*3/2 + ((xPos - 1) * sideLength), camera.getCenterY() - sideLength*3/2 + ((yPos - 1) * sideLength),
                sideLength * 3/4, sideLength * 3/4, resourcesManager.bomb_normal_region, vbom);
        this.attachChild(bombSprite);
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
        redBombSprite = new Sprite(camera.getCenterX() - sideLength*3/2 + ((xPos - 1) * sideLength), camera.getCenterY() - sideLength*3/2 + ((yPos - 1) * sideLength),
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
        fireBeamHorizontal = new Sprite(camera.getCenterX(), camera.getCenterY() - sideLength*3/2 + (yPos-1)*sideLength,
                sideLength*4, sideLength*3/4, resourcesManager.firebeam_horizontal, vbom);
        fireBeamVertical = new Sprite(camera.getCenterX() - sideLength*3/2 + (xPos-1)*sideLength, camera.getCenterY(),
                sideLength*3/4, sideLength*4, resourcesManager.firebeam_vertical, vbom);
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
                canBomb = true;
            }
        }));
    }

    private void killCheck() {
        if ((xPosLume == xPosBomb || yPosLume == yPosBomb) && !lumeIndestructible) {
            lumeKill();

            byte[] g = new byte[]{9, 1, 0, 1}; //send request
            connectedThread.write(g);
        }
        if ((xPosGrume == xPosBomb || yPosGrume == yPosBomb) && !grumeIndestructible) {
            grumeKill();

            byte[] g = new byte[]{9, 2, 0, 1}; //send request
            connectedThread.write(g);
        }
    }

    private void lumeKill() {
        lumeLives--;
        if (lumeLives == 0) {
            byte[] g = new byte[]{10, 1, 0, 1}; //send request
            connectedThread.write(g);

            lumeHeart2.detachSelf();
            lumeHeart2.dispose();
            displayGameOverText(1);
        } else if (lumeLives == 1){ //lume has one life left

            lumeIndestructible = true;
            lumeSprite.setAlpha(0.6f);
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
        if (grumeLives == 0) {
            byte[] g = new byte[]{10, 2, 0, 1}; //send request
            connectedThread.write(g);

            grumeHeart2.detachSelf();
            grumeHeart2.dispose();
            displayGameOverText(2);
        } else if (grumeLives == 1){ //grume has one life left

            grumeIndestructible = true;
            grumeSprite.setAlpha(0.6f);
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
//        physicsWorld.setContactListener(contactListener());
        registerUpdateHandler(physicsWorld);
    }

    private void createHUD() {
        gameHUD = new HUD();

        if (player.equals("Lume")) {
            //my Points
            myScoreText = new Text(camera.getCenterX() + sideLength*3, camera.getHeight()-70,
                    resourcesManager.smallFont, "0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
            myScoreText.setAnchorCenter(0, 0);
            myScoreText.setText("0");
            myScoreText.setColor(Color.BLUE);
            gameHUD.attachChild(myScoreText);

            //opponent`s Points
            opponentScoreText = new Text(camera.getCenterX() - sideLength*3, camera.getHeight()-70,
                    resourcesManager.smallFont, "0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
            opponentScoreText.setAnchorCenter(0, 0);
            opponentScoreText.setText("0");
            opponentScoreText.setColor(Color.GREEN);
            gameHUD.attachChild(opponentScoreText);

            //my Lives
            lumeHeart1 = new Sprite(camera.getWidth()-sideLength*3/2, camera.getHeight() - sideLength/2,
                    sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //inner heart
            lumeHeart2 = new Sprite(camera.getWidth()-sideLength/2, camera.getHeight() - sideLength/2,
                    sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //outer heart
            gameHUD.attachChild(lumeHeart1);
            gameHUD.attachChild(lumeHeart2);

            //opponent`s Lives
            grumeHeart1 = new Sprite(sideLength*3/2, camera.getHeight() - sideLength/2,
                    sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //inner heart
            grumeHeart2 = new Sprite(sideLength/2, camera.getHeight() - sideLength/2,
                    sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //outer heart
            gameHUD.attachChild(grumeHeart1);
            gameHUD.attachChild(grumeHeart2);

        } else if (player.equals("Grume")) {
            //my Points
            myScoreText = new Text(camera.getCenterX() + sideLength*3, camera.getHeight()-70,
                    resourcesManager.smallFont, "0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
            myScoreText.setAnchorCenter(0, 0);
            myScoreText.setText("0");
            myScoreText.setColor(Color.GREEN);
            gameHUD.attachChild(myScoreText);

            //opponent`s Points
            opponentScoreText = new Text(camera.getCenterX() - sideLength*3, camera.getHeight()-70,
                    resourcesManager.smallFont, "0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
            opponentScoreText.setAnchorCenter(0, 0);
            opponentScoreText.setText("0");
            opponentScoreText.setColor(Color.BLUE);
            gameHUD.attachChild(opponentScoreText);

            //my Lives
            grumeHeart1 = new Sprite(camera.getWidth()-sideLength*3/2, camera.getHeight()-sideLength/2,
                    sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //inner heart
            grumeHeart2 = new Sprite(camera.getWidth()-sideLength/2, camera.getHeight()-sideLength/2,
                    sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //outer heart
            gameHUD.attachChild(grumeHeart1);
            gameHUD.attachChild(grumeHeart2);

            //opponent`s Lives
            lumeHeart1 = new Sprite(sideLength*3/2, camera.getHeight()-sideLength/2,
                    sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //inner heart
            lumeHeart2 = new Sprite(sideLength/2, camera.getHeight()-sideLength/2,
                    sideLength*3/4, sideLength*3/4, resourcesManager.heart_region, vbom); //outer heart
            gameHUD.attachChild(lumeHeart1);
            gameHUD.attachChild(lumeHeart2);

        }



        timeText = new Text(camera.getCenterX(), camera.getHeight()-70,
                resourcesManager.smallFont, "0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
        timeText.setAnchorCenter(0, 0);
        timeText.setText("0");
        timeText.setColor(Color.RED);
        timeText.setAlpha(0.7f);
        gameHUD.attachChild(timeText);

        camera.setHUD(gameHUD);
    }

    //moves the player in X or Y direction
    public int[] moveLume(int direction) {
        int[] xyArr = new int[2];
        xyArr[0] = 0;
        xyArr[1] = 0;
        switch (direction) {
            case 2:
                if (xPosLume < xPositions) {
//                    xPosLume++;
//                    lumeSprite.setPosition(lumeSprite.getX() + sideLength, lumeSprite.getY());
                    xyArr[0] = xPosLume + 1;
                    xyArr[1] = yPosLume;
                }
                break;
            case 4:
                if (xPosLume > 1) {
//                    xPosLume--;
//                    lumeSprite.setPosition(lumeSprite.getX() - sideLength, lumeSprite.getY());
                    xyArr[0] = xPosLume - 1;
                    xyArr[1] = yPosLume;
                }
                break;
            case 3:
                if (yPosLume > 1) {
//                    yPosLume--;
//                    lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() - sideLength);
                    xyArr[0] = xPosLume;
                    xyArr[1] = yPosLume - 1;
                }
                break;
            case 1:
                if (yPosLume < yPositions) {
//                    yPosLume++;
//                    lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() + sideLength);
                    xyArr[0] = xPosLume;
                    xyArr[1] = yPosLume + 1;
                }
                break;
        }

        return xyArr;
    }

    //moves the player in X or Y direction
    public int[] moveGrume(int direction) {
        int xyArr[] = new int[2];
        xyArr[0] = 0;
        xyArr[1] = 0;
        switch (direction) {
            case 2:
                if (xPosGrume < xPositions) {
//                    xPosGrume++;
//                    grumeSprite.setPosition(grumeSprite.getX() + sideLength, grumeSprite.getY());
                    xyArr[0] = xPosGrume + 1;
                    xyArr[1] = yPosGrume;
                }
                break;
            case 4:
                if (xPosGrume > 1) {
//                    xPosGrume--;
//                    grumeSprite.setPosition(grumeSprite.getX() - sideLength, grumeSprite.getY());
                    xyArr[0] = xPosGrume - 1;
                    xyArr[1] = yPosGrume;
                }
                break;
            case 3:
                if (yPosGrume > 1) {
//                    yPosGrume--;
//                    grumeSprite.setPosition(grumeSprite.getX(), grumeSprite.getY() - sideLength);
                    xyArr[0] = xPosGrume;
                    xyArr[1] = yPosGrume - 1;
                }
                break;
            case 1:
                if (yPosGrume < yPositions) {
//                    yPosGrume++;
//                    grumeSprite.setPosition(grumeSprite.getX(), grumeSprite.getY() + sideLength);
                    xyArr[0] = xPosGrume;
                    xyArr[1] = yPosGrume + 1;
                }
                break;
        }

        return xyArr;
    }

    private void setLumePosition(int x, int y) {
        xPosLume = x;
        yPosLume = y;
        lumeSprite.setPosition(camera.getCenterX() - sideLength*3/2 + ((xPosLume-1)*sideLength),
                camera.getCenterY() - sideLength*3/2 + ((yPosLume-1)*sideLength));
    }

    private void setGrumePosition(int x, int y) {
        xPosGrume = x;
        yPosGrume = y;
        grumeSprite.setPosition(camera.getCenterX() - sideLength*3/2 + ((xPosGrume-1)*sideLength),
                camera.getCenterY() - sideLength*3/2 + ((yPosGrume-1)*sideLength));
    }

    private void coinCheck() {
        if (xPosLume == xPosCoin && yPosLume == yPosCoin) {
            addMyScore(1, 'L');
            createCoin(false);
        } else if (xPosGrume == xPosCoin && yPosGrume == yPosCoin) {
            addMyScore(1, 'G');
            createCoin(false);
        }
    }

    private void addMyScore(int i, char player) {
        switch(player) {
            case 'L':
                lumeScore += i;
//                if (this.player.equals("Lume")) {
                    myScoreText.setText(String.valueOf(lumeScore));
//                } else if (this.player.equals("Grume")) {
//                    opponentScoreText.createTypingText(String.valueOf(lumeScore));
//                }
//                byte[] g = new byte[]{5, 1, (byte) lumeScore, 1}; //send request
//                connectedThread.write(g);
                break;
            case 'G':
                grumeScore += i;
//                if (this.player.equals("Grume")) {
                    myScoreText.setText(String.valueOf(grumeScore));
//                } else if (this.player.equals("Lume")) {
//                    opponentScoreText.createTypingText(String.valueOf(grumeScore));
//                }
//                byte[] h = new byte[]{5, 2, (byte) grumeScore, 1}; //send request
//                connectedThread.write(h);
                break;
        }
    }

    private void setScoreText() {
        if (this.player.equals("Lume")) {
            myScoreText.setText(String.valueOf(lumeScore));
            opponentScoreText.setText(String.valueOf(grumeScore));
        } else if (this.player.equals("Grume")) {
            myScoreText.setText(String.valueOf(grumeScore));
            opponentScoreText.setText(String.valueOf(lumeScore));
        }
    }

    private void createStones() {
        float randomNumber = randomGenerator.nextFloat();
        double probabilityStone = 0.6;
        int direction = randomGenerator.nextInt(4) + 1;
        int randomRow = randomGenerator.nextInt(4) + 1; //values 0 to 2
        boolean thorny = false;
        long[] age = new long[4]; //is used to prevent screen from showing too many stones
        long interval = (long) 2500;
        age[direction - 1] = (new Date()).getTime() - stoneTimes[direction - 1];
        if (age[direction - 1] >= interval) {
            if (randomNumber < probabilityStone) {
                if (randomNumber < 0.2) {
                    thorny = true;
                }
                byte[] g = new byte[]{6, (byte) direction, (byte) randomRow, 1}; //send request
                connectedThread.write(g);

//                this.showStonesToScreen(direction, randomRow, false); //TODO thorny stones
            }
            stoneTimes[direction - 1] = new Date().getTime();
        }
    }

    private void showStonesToScreen(int direction, int randomRow, final boolean thorny) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        final Sprite stone;
        Ball ball;

//        int randomRow = randomGenerator.nextInt(3); //values 0 to 2
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = camera.getWidth()/250;
        switch (direction) {
            case 1:
                x = camera.getCenterX()-sideLength*3/2 + ((randomRow-1)*sideLength);
                y = camera.getHeight() - sideLength / 2;
                yVel = -speed;
                break;
            case 2:
                x = camera.getWidth() - sideLength / 2;
                y = camera.getCenterY()-sideLength*3/2 + ((randomRow-1)*sideLength);
                xVel = -speed;
                break;
            case 3:
                x = camera.getCenterX()-sideLength*3/2 + ((randomRow-1)*sideLength);
                y = sideLength / 2;
                yVel = speed;
                break;
            case 4:
                x = sideLength / 2;
                y = camera.getCenterY()-sideLength*3/2 + ((randomRow-1)*sideLength);
                xVel = speed;
                break;
        }
        ITextureRegion textureRegion = (thorny) ? resourcesManager.thorny_stone_region : resourcesManager.cracky_stone_region;
        stone = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                final Circle lumeCircle, grumeCircle, stoneCircle;
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
                grumeCircle = new Circle(grumeSprite.getX(), grumeSprite.getY(), grumeSprite.getWidth() / 2);
                stoneCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);
                if (stoneCircle.collision(lumeCircle) && !lumeIndestructible && player.equals("Lume")) {
                    byte[] g = new byte[]{9, 1, 0, 1}; //send request
                    connectedThread.write(g);

                    lumeKill();
                }
                if (stoneCircle.collision(grumeCircle) && !grumeIndestructible && player.equals("Grume")) {
                    byte[] g = new byte[]{9, 2, 0, 1}; //send request
                    connectedThread.write(g);

                    grumeKill();
                }
                if (this.getX() < -sideLength || this.getY() < -sideLength ||
                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
                    if (!thorny) crackyStones.remove(this);
                    this.detachSelf();
                    this.dispose();
                }
            }
        };
        this.attachChild(stone);
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

    private void displayGameOverText(int player) {
        gameOverDisplayed = true;

        Scene gameOverScene = new CameraScene(camera);
        gameOverScene.setBackgroundEnabled(false);

        gameOverText = new Text(camera.getCenterX(), camera.getCenterY() + sideLength*2, resourcesManager.smallFont, "Game Over - Touch 3 x to finish", vbom);
        gameOverText.setColor(Color.RED);

        int lumeResult = lumeScore;
        int grumeResult = grumeScore;
        if (player == 1) { //lume died
            lumeResult = lumeScore - pointsToLose;
            if (lumeResult < 0) lumeResult = 0;

            lumeResultText = new Text(camera.getCenterX(), camera.getCenterY(), resourcesManager.smallFont, "Lume: "
                     + String.valueOf(lumeScore) + " - " + String.valueOf(pointsToLose) + " = " + String.valueOf(lumeResult), vbom);

            grumeResultText = new Text(camera.getCenterX(), camera.getCenterY() - sideLength, resourcesManager.smallFont, "Grume: "
                     + String.valueOf(grumeScore), vbom);
        } else if (player == 2) { //grume died
            grumeResult = grumeScore - pointsToLose;
            if (grumeResult < 0) grumeResult = 0;

            grumeResultText = new Text(camera.getCenterX(), camera.getCenterY(), resourcesManager.smallFont, "Grume: "
                    + String.valueOf(grumeScore) + " - " + String.valueOf(pointsToLose) + " = " + String.valueOf(grumeResult), vbom);

            lumeResultText = new Text(camera.getCenterX(), camera.getCenterY() - sideLength, resourcesManager.smallFont, "Lume: "
                    + String.valueOf(lumeScore), vbom);
        }
        if (lumeResult > grumeResult) {
            winnerText = new Text(camera.getCenterX(), camera.getCenterY() - 3*sideLength, resourcesManager.smallFont, "Winner: "
                    + "Lume", vbom);
        } else if (lumeResult == grumeResult) {
            winnerText = new Text(camera.getCenterX(), camera.getCenterY() - 3*sideLength, resourcesManager.smallFont, "Draw", vbom);
        } else if (lumeResult < grumeResult) {
            winnerText = new Text(camera.getCenterX(), camera.getCenterY() - 3*sideLength, resourcesManager.smallFont, "Winner: "
                    + "Grume", vbom);
        }

        lumeResultText.setColor(Color.BLUE);
        grumeResultText.setColor(Color.GREEN);
        winnerText.setColor(Color.WHITE);


        gameOverText.detachSelf();
        gameOverScene.attachChild(gameOverText);
        gameOverScene.attachChild(lumeResultText);
        gameOverScene.attachChild(grumeResultText);
        gameOverScene.attachChild(winnerText);

        //stop things
        unregisterUpdateHandler(physicsWorld);
        this.setIgnoreUpdate(true);
        this.setChildScene(gameOverScene, false, true, true); //set gameOverScene as a child scene - so game will be paused

        gameOverScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

                if (pSceneTouchEvent.isActionDown()) {
                    finishTouch++;
                    if (finishTouch == 3) {
                        //clear child scenes - game will be resumed
                        clearChildScene();
                        setIgnoreUpdate(false);
                        gameOverDisplayed = false;
                        registerUpdateHandler(physicsWorld);
                        SceneManager.getInstance().loadMenuScene(engine);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                UUID uuid = java.util.UUID.fromString(UUID);
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(LUME, uuid);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
//                    manageMyConnectedSocket(socket);

                    ResourcesManager.getInstance().bluetoothSocket = socket;
//                    app.setBluetoothSocket(socket);
                    this.cancel();
                    //TODO start game, putExtra boolean, cancel
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
//            manageMyConnectedSocket(mmSocket);

            ResourcesManager.getInstance().bluetoothSocket = mmSocket;
            ResourcesManager.getInstance().bluetoothDevice = mmDevice;

//            app.setBluetoothSocket(mmSocket);
//            app.setBluetoothDevice(mmDevice);
            this.cancel();
            //TODO start game, putExtra boolean, cancel
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                ResourcesManager.getInstance().bluetoothSocket = mmSocket;
                ResourcesManager.getInstance().bluetoothDevice = mmDevice;

//                app.setBluetoothSocket(mmSocket);
//                app.setBluetoothDevice(mmDevice);
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }




    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; //store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; //bytes returned from read

//            if (!mmSocket.isConnected()) {
//                try {
//                    mmSocket.connect();
//                    socketConnected = true;
//                    activity.toastOnUiThread("connecting successfully");
//                } catch (IOException e) {
//
////                    try {
////                        mmSocket =(BluetoothSocket) ResourcesManager.getInstance().bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(ResourcesManager.getInstance().bluetoothDevice,1);
////                    } catch (IllegalAccessException e1) {
////                        e1.printStackTrace();
////                    } catch (InvocationTargetException e1) {
////                        e1.printStackTrace();
////                    } catch (NoSuchMethodException e1) {
////                        e1.printStackTrace();
////                    }
//                    e.printStackTrace();
//                }
//            } else {
//            }

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
//                    handler.startGame(readMsg); //TODO ADDED new LINE
                } catch (IOException e) {
                    break;
                }
            }

        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }


    }

}