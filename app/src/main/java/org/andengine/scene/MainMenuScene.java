package org.andengine.scene;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.andengine.OnlineUsers.User;
import org.andengine.base.BaseScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.scene.OnlineScenes.UploadUserScene;
import org.andengine.util.adt.align.HorizontalAlign;

import java.util.Random;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------

    private MenuScene menuChildScene;

    private boolean loudVisible;
    private boolean helpVisible;
    private boolean isOnline;

    IMenuItem loudMenuItem;
    IMenuItem psstMenuItem;
    IMenuItem helpMultiMenuItem;
    IMenuItem knowMultiMenuItem;
    IMenuItem ballFallMenuItem;
    IMenuItem militaryMenuItem;

    private int adNumber;

    private final int MENU_PLAY = 0;
    private final int MENU_MULTI = 1;
    private final int MENU_LEVELS = 2;
    private final int MENU_SOUND = 3;
    private final int MENU_HELP = 4;
    private final int MENU_AD_PARTNER = 5;
    private final int MENU_MILITARY = 6;
    private final int MENU_HIGH = 7;

    private Text worldText;

    //CONSTRUCTOR
    public MainMenuScene() {
        updateWorldText();
    }


    //---------------------------------------------
    // METHODS FROM SUPERCLASS
    //---------------------------------------------

    @Override
    public void createScene() {
        Log.i("MainMennuScene", "CreateScene()");
        createBackground();
        createWorldText();
        createMenuChildScene();
        showRandomAd();
        activity.showSlowMoHintMenu();
        if (!activity.isNameOnline()) {
            this.setChildScene(new UploadUserScene());
        } else {
            User.setUserData(activity.getCurrentWorld(), (activity.getCurrentWorld()-1)*40); //TODO update User data
        }
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
        // TODO Auto-generated method stub
    }

    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
        switch (pMenuItem.getID()) {
            case MENU_PLAY:
                //Load Game Scene!
                switch (activity.getCurrentWorld()) {
                    case 0:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(0, 0);
                        } else {
                            SceneManager.getInstance().loadWorld0Scene(engine, 0);
                        }
                        break;
                    case 1:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(1, 0);
                        } else {
                            SceneManager.getInstance().loadWorld1Scene(engine, 0);
                        }
                        break;
                    case 2:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(2, 0);
                        } else {
                            SceneManager.getInstance().loadWorld2Scene(engine, 0);
                        }
                        break;
                    case 3:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(3, 0);
                        } else {
                            SceneManager.getInstance().loadWorld3Scene(engine, 0);
                        }
                        break;
                    case 4:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(4, 0);
                        } else {
                            SceneManager.getInstance().loadWorld4Scene(engine, 0);
                        }
                        break;
                    case 5:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(5, 0);
                        } else {
                            SceneManager.getInstance().loadWorld5Scene(engine, 0);
                        }
                        break;
                    case 6:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(6, 0);
                        } else {
                            SceneManager.getInstance().loadWorld6Scene(engine, 0);
                        }
                        break;
                    case 7:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(7, 0);
                        } else {
                            SceneManager.getInstance().loadWorld7Scene(engine, 0);
                        }
                        break;
                    case 8:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(8, 0);
                        } else {
                            SceneManager.getInstance().loadWorld8Scene(engine, 0);
                        }
                        break;
                    case 9:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(8, 0);
                        } else {
                            SceneManager.getInstance().loadWorld8Scene(engine, 0);
                        }
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
                helpVisible = !helpVisible;

                helpMultiMenuItem.setVisible(helpVisible);
                knowMultiMenuItem.setVisible(!helpVisible);

                activity.setMultiTutorialSeen(!helpVisible);
                //SceneManager.getInstance().loadHelpScene(engine);
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
            default:
                return false;
        }
    }

    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

    private void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(), camera.getWidth(), camera.getHeight(), resourcesManager.menu_background_region, vbom));
        this.setBackground(spriteBackground);
    }

    private void createWorldText() {
        if (worldText == null) {
            worldText = new Text(55, camera.getHeight()-45, resourcesManager.smallFont, "WLUME0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
            worldText.setText("W" + String.valueOf(activity.getCurrentWorld()));
            if (activity.getCurrentWorld() == 9) {
                worldText.setText("LUME");
                worldText.setPosition(camera.getHeight()/8, worldText.getY());
            }
            int color = android.graphics.Color.parseColor("#808080");
            worldText.setColor(color);
            this.attachChild(worldText);
        } else {
            this.updateWorldText();
        }
    }

    private void showRandomAd() {
        Random random = new Random();
        int partners = 2;
        adNumber = random.nextInt(partners);
        militaryMenuItem.setVisible(adNumber == 0);
        ballFallMenuItem.setVisible(adNumber == 1);
    }

    public void updateWorldText() {
        if (worldText != null) {
            worldText.setText("W" + String.valueOf(activity.getCurrentWorld()));
            if (activity.getCurrentWorld() == 9) {
                worldText.setText("LUME");
                worldText.setPosition(camera.getHeight()/8, worldText.getY());
            }
        }
    }

    private void createMenuChildScene() {
        loudVisible = activity.isLoudVisible();
        helpVisible = !activity.isMultiTutorialSeen();

        menuChildScene = new MenuScene(camera);
        menuChildScene.setPosition(0, 0);

        final IMenuItem highMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HIGH, 60, 60, resourcesManager.play_coin_region, vbom), 1.2f, 1);
        final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, 60, 60, resourcesManager.play_region, vbom), 1.2f, 1);
        final IMenuItem levelsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_LEVELS, 60, 60, resourcesManager.world_region, vbom), 1.2f, 1);
        final IMenuItem multiMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MULTI, 60, 60, resourcesManager.play_multi_region, vbom), 1.2f, 1);
        ballFallMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_AD_PARTNER, 110, 110, resourcesManager.ball_fall, vbom), 1.2f, 1);
        militaryMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_AD_PARTNER, 110, 110, resourcesManager.military, vbom), 1.2f, 1);
        loudMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SOUND, 65, 30, resourcesManager.loud_region, vbom), 1.2f, 1);
        psstMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SOUND, 63, 42, resourcesManager.psst_region, vbom), 1.2f, 1);
        helpMultiMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HELP, 60, 60, resourcesManager.help_region, vbom), 1.2f, 1);
        knowMultiMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HELP, 60, 60, resourcesManager.know_region, vbom), 1.2f, 1);
        final IMenuItem singleTextMenuItem = new TextMenuItem(0, resourcesManager.smallFont, "Single Player", vbom);
        final IMenuItem multiTextMenuItem = new TextMenuItem(1, resourcesManager.smallFont, "Multi Player", vbom);
        final IMenuItem soundTextMenuItem = new TextMenuItem(MENU_SOUND, resourcesManager.smallFont, "Sound", vbom);
        final IMenuItem ballFallTextMenuItem = new TextMenuItem(MENU_AD_PARTNER, resourcesManager.smallFont, "Install", vbom);

        menuChildScene.addMenuItem(highMenuItem);
        menuChildScene.addMenuItem(playMenuItem);
        menuChildScene.addMenuItem(levelsMenuItem);
        menuChildScene.addMenuItem(multiMenuItem);
        menuChildScene.addMenuItem(ballFallMenuItem);
        menuChildScene.addMenuItem(militaryMenuItem);
        menuChildScene.addMenuItem(loudMenuItem);
        menuChildScene.addMenuItem(psstMenuItem);
        menuChildScene.addMenuItem(helpMultiMenuItem);
        menuChildScene.addMenuItem(knowMultiMenuItem);
        menuChildScene.addMenuItem(singleTextMenuItem);
        menuChildScene.addMenuItem(multiTextMenuItem);
        menuChildScene.addMenuItem(soundTextMenuItem);
        menuChildScene.addMenuItem(ballFallTextMenuItem);

        menuChildScene.buildAnimations();
        menuChildScene.setBackgroundEnabled(false);

//        playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() + 10);
        highMenuItem.setPosition(camera.getCenterX()-100, camera.getCenterY());
        playMenuItem.setPosition(camera.getCenterX(), camera.getCenterY());
        levelsMenuItem.setPosition(camera.getCenterX()+100, camera.getCenterY());
        multiMenuItem.setPosition(camera.getCenterX()-50, playMenuItem.getY() - 170);
        ballFallMenuItem.setPosition(resourcesManager.screenWidth*6.8f/32, camera.getCenterY()-110);
        militaryMenuItem.setPosition(resourcesManager.screenWidth*6.8f/32, camera.getCenterY()-110);
        helpMultiMenuItem.setPosition(camera.getCenterX()+50, playMenuItem.getY() - 170);
        knowMultiMenuItem.setPosition(camera.getCenterX()+50, playMenuItem.getY() - 170);
        singleTextMenuItem.setPosition(camera.getCenterX(), playMenuItem.getY() + 70);
        multiTextMenuItem.setPosition(camera.getCenterX(), multiMenuItem.getY() + 70);
        soundTextMenuItem.setPosition(resourcesManager.screenWidth*25/32, camera.getCenterY()-20);
        ballFallTextMenuItem.setPosition(resourcesManager.screenWidth*7f/32, camera.getCenterY()-20);

        loudMenuItem.setPosition(resourcesManager.screenWidth*24.6f/32, camera.getCenterY()-70);
        psstMenuItem.setPosition(resourcesManager.screenWidth*24.6f/32, camera.getCenterY()-70);

        if (loudVisible) {
            loudMenuItem.setVisible(true);
            psstMenuItem.setVisible(false);
        } else {
            loudMenuItem.setVisible(false);
            psstMenuItem.setVisible(true);
        }

        if (helpVisible) {
            helpMultiMenuItem.setVisible(true);
            knowMultiMenuItem.setVisible(false);
        } else {
            helpMultiMenuItem.setVisible(false);
            knowMultiMenuItem.setVisible(true);
        }

        ballFallMenuItem.setVisible(true);
        militaryMenuItem.setVisible(false);


        menuChildScene.setOnMenuItemClickListener(this);

        setChildScene(menuChildScene);
    }
}