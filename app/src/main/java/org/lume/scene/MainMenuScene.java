package org.lume.scene;

import android.content.Intent;
import android.net.Uri;

import org.lume.OnlineUsers.User;
import org.lume.base.BaseScene;
import org.lume.entity.modifier.LoopEntityModifier;
import org.lume.entity.modifier.RotationModifier;
import org.lume.entity.scene.background.AutoParallaxBackground;
import org.lume.entity.scene.background.ParallaxBackground;
import org.lume.entity.scene.menu.MenuScene;
import org.lume.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.lume.entity.scene.menu.item.IMenuItem;
import org.lume.entity.scene.menu.item.SpriteMenuItem;
import org.lume.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.lume.entity.sprite.AnimatedSprite;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.Text;
import org.lume.entity.text.TextOptions;
import org.lume.input.touch.TouchEvent;
import org.lume.manager.SceneManager;
import org.lume.manager.SceneType;
import org.lume.scene.OnlineScenes.UploadUserScene;
import org.lume.util.adt.align.HorizontalAlign;

import java.util.Random;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------

    private MenuScene menuChildScene;

    private boolean loudVisible;
    //private boolean helpVisible;
    private boolean isOnline;
    private boolean leftRun, rightRun;

    private float sideLength;

    IMenuItem loudMenuItem;
    IMenuItem psstMenuItem;
    IMenuItem helpMultiMenuItem;
    IMenuItem knowMultiMenuItem;
//    IMenuItem ballFallMenuItem;
//    IMenuItem militaryMenuItem;

    private int adNumber;

    private final int MENU_PLAY = 0;
    private final int MENU_MULTI = 1;
    private final int MENU_LEVELS = 2;
    private final int MENU_SOUND = 3;
    private final int MENU_HELP = 4;
    private final int MENU_AD_PARTNER = 5;
    private final int MENU_MILITARY = 6;
    private final int MENU_HIGH = 7;
    private final int MENU_SHOPPING = 8;
    private final int MENU_SKILL = 9;
    private final int MENU_INFO = 10;
    private final int MENU_ONLINE_MULTI = 11;

    private Text worldText, coinText, hsText;
    private AnimatedSprite finger;

    //CONSTRUCTOR
    public MainMenuScene() {
        updateWorldText();
        updateCoinText();
        updateHSText();
    }


    //---------------------------------------------
    // METHODS FROM SUPERCLASS
    //---------------------------------------------

    @Override
    public void createScene() {
//        Log.w("MainMenuScene", "start connection with server");
//        new Server(new LumeGameActions(), new LumeUserActions()); // only called for tests!!!
//        Log.w("MainMenuScene", "done with connection");
        sideLength = resourcesManager.sideLength;

        if (!activity.isNameOnline() && activity.isWantOnline()) {
            UploadUserScene uus = new UploadUserScene();
            uus.registerParentScene(this);
            this.setChildScene(uus);
        } else {
            if (activity.isWantOnline()) User.setUserData(activity.getCurrentWorld(), activity.getCurrentHighscore());
            createMenuScene();
        }
    }

    public void createMenuScene() {
        createBackground();
        createWorldText();
        createCoinText();
        createHSText();
        createMenuChildScene();
        showRandomAd();
        activity.showSlowMoHintMenu();
    }

    @Override
    public void onBackKeyPressed() {
        System.exit(0);
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_MENU;
    }


    @Override
    public void disposeScene() {

    }

    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
        switch (pMenuItem.getID()) {
            case MENU_PLAY:
                //Load Game Scene!
                switch (activity.getCurrentWorld()) {
                    case 0:
                        SceneManager.getInstance().loadWorld0Scene(engine, 0);
                        break;
                    case 1:
                        SceneManager.getInstance().loadWorld1Scene(engine, 0);
                        break;
                    case 2:
                        SceneManager.getInstance().loadWorld2Scene(engine, 0);
                        break;
                    case 3:
                        SceneManager.getInstance().loadWorld3Scene(engine, 0);
                        break;
                    case 4:
                        SceneManager.getInstance().loadWorld4Scene(engine, 0);
                        break;
                    case 5:
                        SceneManager.getInstance().loadWorld5Scene(engine, 0);
                        break;
                    case 6:
                        SceneManager.getInstance().loadWorld6Scene(engine, 0);
                        break;
                    case 7:
                        SceneManager.getInstance().loadWorld7Scene(engine, 0);
                        break;
                    case 8:
                        SceneManager.getInstance().loadWorld8Scene(engine, 0);
                        break;
                    case 9:
                        SceneManager.getInstance().loadWorld8Scene(engine, 0);
                        break;
                }
//                disposeWorldText();
                return true;
            case MENU_MULTI:
//                Intent intent = new Intent(activity.getApplicationContext(), MultiActivity.class);
//                activity.startActivity(intent);

//                Intent intent2 = new Intent(activity.getApplicationContext(), MultiActivity.class);
//                Intent intent2 = new Intent(activity.getApplicationContext(), MultiplayerExample.class);
//                activity.startActivity(intent2);
//                disposeWorldText();
                SceneManager.getInstance().loadMultiScene(engine);
                return true;
            case MENU_LEVELS:
                if (activity.getCurrentWorld() <= 4) {
                    SceneManager.getInstance().loadWorlds1to4Scene(engine);
                } else {
                    SceneManager.getInstance().loadWorlds5to8Scene(engine);
                }
                return true;
            case MENU_SOUND:
                loudVisible = !loudVisible;

                loudMenuItem.setVisible(loudVisible);
                psstMenuItem.setVisible(!loudVisible);

                activity.setLoudVisible(loudVisible);
                return true;
            case MENU_HELP:
//                helpVisible = !helpVisible;
//
//                helpMultiMenuItem.setVisible(helpVisible);
//                knowMultiMenuItem.setVisible(!helpVisible);
//
//                activity.setMultiTutorialSeen(!helpVisible);
//                //SceneManager.getInstance().loadHelpScene(engine);
                return true;
            case MENU_AD_PARTNER:
                switch (adNumber) {
                    case 0:
                        final String militaryId = "com.IliasInc.MilitaryWars"; // getPackageName() from Context or Activity object
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + militaryId)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + militaryId)));
                        }
                        break;
                    case 1:
                        final String ballBallId = "com.trishader.ballFall"; // getPackageName() from Context or Activity object
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ballBallId)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + ballBallId)));
                        }
                        break;
                }
                return true;
            case MENU_HIGH:
                //activity.showHighHint();
                SceneManager.getInstance().loadHighscoreScene(engine);
                return true;
            case MENU_SHOPPING:
                //activity.toastOnUiThread("Making a photo", 0);
                SceneManager.getInstance().loadShopScene(engine);
                //TODO camera
                return true;
            case MENU_INFO:
                SceneManager.getInstance().loadInfoScene(engine);
                return true;
            case MENU_SKILL:
                SceneManager.getInstance().loadSkillMenuScene(engine);
                return true;
            case MENU_ONLINE_MULTI:
                if (!activity.isNameOnline()) {
                    UploadUserScene uus = new UploadUserScene();
                    uus.registerParentScene(this);
                    this.setChildScene(uus);
                } else {
                    SceneManager.getInstance().loadOnlineUsersScene(engine);
                }
                //activity.toastOnUiThread("Available soon!", 0);
                return true;
            default:
                return false;
        }
    }

    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

    private void createBackground() {
        final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 3);

        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0f,
                new Sprite(camera.getCenterX(), camera.getCenterY(), camera.getWidth(), camera.getHeight(),
                        resourcesManager.menu_background_region, vbom)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(+5f,
                new Sprite(resourcesManager.sideLength*9f, camera.getHeight()-resourcesManager.sideLength*1.125f,
                        resourcesManager.sideLength*19, resourcesManager.sideLength*2.5f, resourcesManager.clouds_region, vbom)));
        this.setBackground(autoParallaxBackground);


        //attach Lumetext sprite
        Sprite lumeTextSprite = new Sprite(camera.getCenterX(), camera.getHeight()-resourcesManager.sideLength*1f,
                resourcesManager.sideLength*5, resourcesManager.sideLength*1.5f, resourcesManager.lume_text_region, vbom);
        this.attachChild(lumeTextSprite);
        Sprite darkCloudSprite = new Sprite(sideLength*1.9f, camera.getHeight()-sideLength*1f, sideLength*6, sideLength*2.5f,
                resourcesManager.cloud_dark_region, vbom);
        this.attachChild(darkCloudSprite);

        //attach finger
        finger = new AnimatedSprite(sideLength*12.4f, sideLength*1.1f, sideLength, sideLength*1.25f,
                resourcesManager.finger_tiled, vbom);
        attachChild(finger);

        //attach zahnraeder
        Sprite[] redWheelsL = new Sprite[3];
        Sprite[] redWheelsR = new Sprite[3];
        Sprite[] blueWheelsL = new Sprite[3];
        Sprite[] blueWheelsR = new Sprite[3];
        Sprite blueWheelLume, stopWheelL, stopWheelR, runWheelL, runWheelR;
        float leftX = resourcesManager.sideLength/2;
        float rightX = camera.getWidth()-resourcesManager.sideLength/2;
        float distance = resourcesManager.sideLength*2;
        float lowestYRed = resourcesManager.sideLength/2;
        float lowestYBlue = resourcesManager.sideLength/2 + resourcesManager.sideLength;
        float highestY = lowestYRed+6*sideLength;
        for (int i = 0; i < redWheelsL.length; i++) {
            redWheelsL[i] = new Sprite(leftX, lowestYRed+i*distance, sideLength, 1.06f*sideLength,
                    resourcesManager.zahnrad_red_region, vbom);
            this.attachChild(redWheelsL[i]);
            redWheelsR[i] = new Sprite(rightX, lowestYRed+i*distance, sideLength, 1.06f*sideLength,
                    resourcesManager.zahnrad_red_region, vbom);
            this.attachChild(redWheelsR[i]);
            redWheelsL[i].registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, -360)));
        }
        for (int i = 0; i < blueWheelsL.length; i++) {
            blueWheelsL[i] = new Sprite(leftX, lowestYBlue+i*distance, sideLength*1.06f, sideLength,
                    resourcesManager.zahnrad_blue_region, vbom);
            this.attachChild(blueWheelsL[i]);
            blueWheelsR[i] = new Sprite(rightX, lowestYBlue+i*distance, sideLength*1.06f, sideLength,
                    resourcesManager.zahnrad_blue_region, vbom);
            this.attachChild(blueWheelsR[i]);
            //add rotation
            blueWheelsL[i].registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, 360)));
            //blueWheelsR[i].registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, 360)));
        }
        blueWheelLume = new Sprite(rightX-resourcesManager.sideLength, lowestYRed, resourcesManager.sideLength*1.06f, resourcesManager.sideLength,
                resourcesManager.zahnrad_blue_region, vbom);
        this.attachChild(blueWheelLume);
        //blueWheelLume.registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, 360)));

        //add touch wheels
        rightRun = false;
        leftRun = true;
        stopWheelL = new Sprite(leftX, highestY, sideLength, sideLength*1.06f,
                resourcesManager.zahnrad_stop_region, vbom);
        runWheelL = new Sprite(leftX, highestY, sideLength, sideLength*1.06f,
                resourcesManager.zahnrad_run_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    leftRun = !leftRun;
                    if (leftRun) {
                        this.setVisible(true);
                        stopWheelL.setVisible(false);
                        for (int i = 0; i < blueWheelsL.length; i++) {
                            blueWheelsL[i].registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, 360)));
                            redWheelsL[i].registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, -360)));
                        }
                        this.registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, -360)));
                    } else {
                        this.setVisible(false);
                        stopWheelL.setVisible(true);
                        for (int i = 0; i < blueWheelsL.length; i++) {
                            blueWheelsL[i].clearEntityModifiers();
                            redWheelsL[i].clearEntityModifiers();
                        }
                        this.clearEntityModifiers();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
        runWheelL.registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, -360)));
        stopWheelR = new Sprite(rightX, highestY, sideLength, sideLength*1.06f,
                resourcesManager.zahnrad_stop_region, vbom);
        runWheelR = new Sprite(rightX, highestY, sideLength, sideLength*1.06f,
                resourcesManager.zahnrad_run_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    rightRun = !rightRun;
                    if (rightRun) {
                        finger.animate(200);
                        this.setVisible(true);
                        stopWheelR.setVisible(false);
                        for (int i = 0; i < blueWheelsR.length; i++) {
                            blueWheelsR[i].registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, 360)));
                            redWheelsR[i].registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, -360)));
                        }
                        blueWheelLume.registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, 360)));
                        this.registerEntityModifier(new LoopEntityModifier(new RotationModifier(4f, 0, -360)));
                    } else {
                        finger.stopAnimation();
                        this.setVisible(false);
                        stopWheelR.setVisible(true);
                        for (int i = 0; i < blueWheelsL.length; i++) {
                            blueWheelsR[i].clearEntityModifiers();
                            redWheelsR[i].clearEntityModifiers();
                        }
                        blueWheelLume.clearEntityModifiers();
                        this.clearEntityModifiers();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.attachChild(stopWheelL);
        this.attachChild(stopWheelR);
        this.attachChild(runWheelL);
        this.attachChild(runWheelR);
        this.registerTouchArea(runWheelL);
        this.registerTouchArea(runWheelR);
        stopWheelL.setVisible(false);
        runWheelR.setVisible(false);
    }

    public void clearChildScene() {
        this.clearChildScene();
    }

    private void createWorldText() {
        if (worldText == null) {
            worldText = new Text(20, camera.getHeight()-resourcesManager.sideLength*0.8f,
                    resourcesManager.smallFont, "W: L0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
            worldText.setText("W: " + String.valueOf(activity.getCurrentWorld()));
            worldText.setPosition(20+worldText.getWidth()/2, worldText.getY());
            if (activity.getCurrentWorld() == 9) {
                worldText.setText("L");
                worldText.setPosition(20+worldText.getWidth()/2, worldText.getY());
            }
            int color = android.graphics.Color.parseColor("#808080");
            worldText.setColor(color);
            this.attachChild(worldText);
        } else {
            this.updateWorldText();
        }
    }

    private void createCoinText() {
        if (coinText == null) {
            coinText = new Text(20, camera.getHeight()-resourcesManager.sideLength*1.4f,
                    resourcesManager.smallFont, "C: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
            coinText.setText("C: " + String.valueOf(activity.getCurrentBeersos()));
            int color = android.graphics.Color.parseColor("#666666");
            coinText.setColor(color);
            this.attachChild(coinText);
            coinText.setPosition(20+coinText.getWidth()/2, coinText.getY());
//            coinSprite = new Sprite(coinText.getX()-coinText.getWidth()/2-camera.getHeight()/18, coinText.getY(),
//                    camera.getHeight()/16, camera.getHeight()/16, resourcesManager.coin_region, vbom);
//            this.attachChild(coinSprite);
        } else {
            this.updateCoinText();
        }
    }

    private void createHSText() {
        if (hsText == null) {
            hsText = new Text(20+worldText.getWidth()+sideLength/2, camera.getHeight()-resourcesManager.sideLength*0.8f,
                    resourcesManager.smallFont, "HS: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
            hsText.setText("HS: " + String.valueOf(activity.getCurrentBeersos()));
            int color = android.graphics.Color.parseColor("#4d4d4d");
            hsText.setColor(color);
            this.attachChild(hsText);
            hsText.setPosition(20+worldText.getWidth()+sideLength/2+hsText.getWidth()/2, hsText.getY());
        } else {
            this.updateHSText();
        }
    }

    private void showRandomAd() {
        Random random = new Random();
        int partners = 2;
        adNumber = random.nextInt(partners);
//        militaryMenuItem.setVisible(adNumber == 0);
//        ballFallMenuItem.setVisible(adNumber == 1);
    }

    public void updateWorldText() {
        if (worldText != null) {
            worldText.setText("W: " + String.valueOf(activity.getCurrentWorld()));
            if (activity.getCurrentWorld() == 9) {
                worldText.setText("L");
                worldText.setPosition(20+worldText.getWidth()/2, worldText.getY());
            }
        }
    }

    public void updateCoinText() {
        if (coinText != null) coinText.setText("C: " + String.valueOf(activity.getCurrentBeersos()));
        if (coinText != null) coinText.setPosition(20+coinText.getWidth()/2, coinText.getY());
    }
    public void updateHSText() {
        if (hsText != null) hsText.setText("HS: " + String.valueOf(activity.getCurrentHighscore()));
        if (hsText != null) hsText.setPosition(20+worldText.getWidth()+sideLength/2+hsText.getWidth()/2, hsText.getY());
    }

    private void createMenuChildScene() {
        float factorLeft = 1.5f;
        float factorRight = 1.4f;
        loudVisible = activity.isLoudVisible();
        //helpVisible = !activity.isMultiTutorialSeen();

        menuChildScene = new MenuScene(camera);
        menuChildScene.setPosition(0, 0);

        //create buttons
        final IMenuItem highMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HIGH, sideLength*factorLeft, sideLength*factorLeft, resourcesManager.play_coin_region, vbom), 1.2f, 1);
        final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, sideLength*factorLeft, sideLength*factorLeft, resourcesManager.play_region, vbom), 1.2f, 1);
        final IMenuItem levelsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_LEVELS, sideLength*factorLeft, sideLength*factorLeft, resourcesManager.world_region, vbom), 1.2f, 1);
        final IMenuItem shoppingMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SHOPPING, sideLength*factorRight, sideLength*0.92f*factorRight, resourcesManager.shopping_region, vbom), 1.2f, 1);
        final IMenuItem infoMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_INFO, sideLength*factorRight*0.56f, sideLength*factorRight, resourcesManager.info_region, vbom), 1.2f, 1);
        final IMenuItem skillMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SKILL, sideLength*factorLeft, sideLength*factorLeft, resourcesManager.skill_gym_region, vbom), 1.2f, 1);
//        ballFallMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_AD_PARTNER, 110, 110, resourcesManager.ball_fall, vbom), 1.2f, 1);
//        militaryMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_AD_PARTNER, 110, 110, resourcesManager.military, vbom), 1.2f, 1);
        loudMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SOUND, 65, 30, resourcesManager.loud_region, vbom), 1.2f, 1);
        psstMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SOUND, 63, 42, resourcesManager.psst_region, vbom), 1.2f, 1);
        //multi
//        helpMultiMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HELP, 60, 60, resourcesManager.help_region, vbom), 1.2f, 1);
//        knowMultiMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HELP, 60, 60, resourcesManager.know_region, vbom), 1.2f, 1);
        final IMenuItem multiMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MULTI, sideLength*factorRight, sideLength*factorRight, resourcesManager.play_multi_region, vbom), 1.2f, 1);
        final IMenuItem testMultiItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_ONLINE_MULTI, sideLength*factorRight*0.9f, sideLength*factorRight, resourcesManager.test_multi_region, vbom), 1.2f, 1);

        menuChildScene.addMenuItem(highMenuItem);
        menuChildScene.addMenuItem(playMenuItem);
        menuChildScene.addMenuItem(levelsMenuItem);
        menuChildScene.addMenuItem(shoppingMenuItem);
        menuChildScene.addMenuItem(infoMenuItem);
        menuChildScene.addMenuItem(skillMenuItem);
        menuChildScene.addMenuItem(multiMenuItem);
        menuChildScene.addMenuItem(testMultiItem);
        /*menuChildScene.addMenuItem(ballFallMenuItem);
        menuChildScene.addMenuItem(militaryMenuItem);*/
        menuChildScene.addMenuItem(loudMenuItem);
        menuChildScene.addMenuItem(psstMenuItem);
//        menuChildScene.addMenuItem(helpMultiMenuItem);
//        menuChildScene.addMenuItem(knowMultiMenuItem);

        menuChildScene.buildAnimations();
        menuChildScene.setBackgroundEnabled(false);

//        playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() + 10);
        highMenuItem.setPosition(sideLength*4, sideLength*5);
        playMenuItem.setPosition(sideLength*6, sideLength*5);
        levelsMenuItem.setPosition(sideLength*6, sideLength*3);
        shoppingMenuItem.setPosition(sideLength*10.7f, sideLength*2.5f);
        infoMenuItem.setPosition(sideLength*13.4f, sideLength*2.5f);
        skillMenuItem.setPosition(sideLength*4, sideLength*3);
        multiMenuItem.setPosition(sideLength*11, sideLength*5.5f);
        testMultiItem.setPosition(sideLength*13, sideLength*5.5f);
        /*ballFallMenuItem.setPosition(resourcesManager.screenWidth*6.8f/32, camera.getCenterY()-110);
        militaryMenuItem.setPosition(resourcesManager.screenWidth*6.8f/32, camera.getCenterY()-110);*/
//        helpMultiMenuItem.setPosition(camera.getCenterX()+50, playMenuItem.getY() - 170);
//        knowMultiMenuItem.setPosition(camera.getCenterX()+50, playMenuItem.getY() - 170);

        loudMenuItem.setPosition(sideLength*12.02f, sideLength*2.48f);
        psstMenuItem.setPosition(sideLength*12.02f, sideLength*2.48f);

        if (loudVisible) {
            loudMenuItem.setVisible(true);
            psstMenuItem.setVisible(false);
        } else {
            loudMenuItem.setVisible(false);
            psstMenuItem.setVisible(true);
        }

        /*if (helpVisible) {
            helpMultiMenuItem.setVisible(true);
            knowMultiMenuItem.setVisible(false);
        } else {
            helpMultiMenuItem.setVisible(false);
            knowMultiMenuItem.setVisible(true);
        }*/

//        ballFallMenuItem.setVisible(true);
//        militaryMenuItem.setVisible(false);


        menuChildScene.setOnMenuItemClickListener(this);

        setChildScene(menuChildScene);
    }
}