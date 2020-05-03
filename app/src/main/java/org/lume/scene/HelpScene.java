package org.lume.scene;

import android.content.Intent;
import android.net.Uri;

import org.lume.base.BaseScene;
import org.lume.engine.handler.timer.ITimerCallback;
import org.lume.engine.handler.timer.TimerHandler;
import org.lume.entity.Entity;
import org.lume.entity.IEntity;
import org.lume.entity.primitive.Rectangle;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.AutoWrap;
import org.lume.entity.text.Text;
import org.lume.entity.text.TextOptions;
import org.lume.input.touch.TouchEvent;
import org.lume.manager.SceneManager;
import org.lume.manager.SceneType;
import org.lume.util.adt.align.HorizontalAlign;
import org.lume.util.adt.color.Color;

public class HelpScene extends BaseScene {
    float sideLength = resourcesManager.sideLength;

    private static final int FIRST_LAYER = 0;
    private static final int SECOND_LAYER = 1;
    private static final int THIRD_LAYER = 2;

    private IEntity firstLayer, secondLayer, thirdLayer;

    private boolean helpVisible = true;

    private Sprite help, info;

    //help
    private Sprite kimmelnitzSprite, moveNormalSprite, moveDiagonalSprite,  shootNormalSprite, shootDiagonalSprite;
    private Sprite mirrorSprite, lamporghinaSprite, helmetSprite;
    private Sprite chosen;
    private Rectangle backRect;
    private Text helpText;

    //info
    private Text worldText, highscoreText, coinText, soundText, playerText;
    private Text installText;
    private Sprite ballFall, military;

    @Override
    public void createScene() {
        createLayers();
        createBackground();
        createTouchRectsRight();
        createTouchRectLeft();
        engine.registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                engine.unregisterUpdateHandler(pTimerHandler);
                addHelpSigns();
            }
        }));
    }

    private void createLayers() {
        this.attachChild(new Entity()); // First Layer
        this.attachChild(new Entity()); // Second Layer
        this.attachChild(new Entity()); // Third Layer
        firstLayer = this.getChildByIndex(FIRST_LAYER);
        secondLayer = this.getChildByIndex(SECOND_LAYER);
        thirdLayer = this.getChildByIndex(THIRD_LAYER);
    }

    private void createBackground() {
        help = new Sprite(camera.getCenterX(), camera.getCenterY(), camera.getWidth(), camera.getHeight(),
                resourcesManager.help_shop_region, vbom);
        firstLayer.attachChild(help);
        info = new Sprite(camera.getCenterX(), camera.getCenterY(), camera.getWidth(), camera.getHeight(),
                resourcesManager.info_shop_region, vbom);
        firstLayer.attachChild(info);
        help.setVisible(true);
        info.setVisible(false);
    }

    private void addHelpSigns() {
        float firstX;
        float lowerY, upperY;
        float distance = camera.getWidth()/4;
        firstX = sideLength*2;
        lowerY = sideLength*2;
        upperY = sideLength*6;
        backRect = new Rectangle(camera.getCenterX(), sideLength*2, camera.getWidth(), sideLength*4, vbom);
        backRect.setColor(0.8f, 0.8f, 0.8f, 0.9f);
        backRect.setVisible(false);
        thirdLayer.attachChild(backRect);
        chosen = new Sprite(camera.getWidth()/4, sideLength*6, sideLength*4, sideLength*4,
                resourcesManager.chosen_region, vbom);
        chosen.setVisible(false);
        thirdLayer.attachChild(chosen);
        helpText = new Text(camera.getCenterX(), sideLength*2, resourcesManager.smallFont,
                " ABCDEFGHIJKLMNOPQRSTUVWXYZ:,.!  ABCDEFGHIJKLMNOPQRSTUVWXYZ:,.!  ABCDEFGHIJKLMNOPQRSTUVWXYZ:,.!  ABCDEFGHIJKLMNOPQRSTUVWXYZ:,.!  ABCDEFGHIJKLMNOPQRSTUVWXYZ:,.!" +
                        " ABCDEFGHIJKLMNOPQRSTUVWXYZ:,.!  ABCDEFGHIJKLMNOPQRSTUVWXYZ:,.!  ABCDEFGHIJKLMNOPQRSTUVWXYZ:,.!  ABCDEFGHIJKLMNOPQRSTUVWXYZ:,.!",
                new TextOptions(AutoWrap.WORDS, sideLength*15f, HorizontalAlign.CENTER), vbom);
        thirdLayer.attachChild(helpText);
        helpText.setColor(Color.BLACK);
        helpText.setText("");
        helpText.setVisible(false);
        kimmelnitzSprite = new Sprite(firstX, upperY, sideLength*3, sideLength*3, resourcesManager.kimmelnitz_region, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    backRect.setVisible(true);
                    backRect.setPosition(camera.getCenterX(), sideLength*2);
                    helpText.setVisible(true);
                    helpText.setPosition(camera.getCenterX(), sideLength*2);
                    helpText.setText("THIS IS DR. KIMMELNITZ. HE IS VERY INTELLIGENT, SO BETTER LISTEN TO HIM!");
                    chosen.setPosition(firstX, upperY);
                    chosen.setVisible(true);
                    return true;
                } else {
                    return false;
                }
            }
        };
        moveNormalSprite = new Sprite(firstX+distance, upperY, sideLength*3, sideLength*3, resourcesManager.move_normal_sign_region, vbom){
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    backRect.setVisible(true);
                    backRect.setPosition(camera.getCenterX(), sideLength*2);
                    helpText.setVisible(true);
                    helpText.setPosition(camera.getCenterX(), sideLength*2);
                    helpText.setText("THIS SIGN MEANS THAT YOU CAN MOVE IN ONE OF THE FOUR DIRECTIONS BY SWIPING ON THE RIGHT HALF OF THE SCREEN IN ANY GAME.");
                    chosen.setPosition(firstX+distance, upperY);
                    chosen.setVisible(true);
                    return true;
                } else {
                    return false;
                }
            }
        };
        shootNormalSprite = new Sprite(firstX+2*distance, upperY, sideLength*3, sideLength*3, resourcesManager.shoot_normal_region, vbom){
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    backRect.setVisible(true);
                    backRect.setPosition(camera.getCenterX(), sideLength*2);
                    helpText.setVisible(true);
                    helpText.setPosition(camera.getCenterX(), sideLength*2);
                    helpText.setText("THIS SIGN MEANS THAT YOU CAN SHOOT IN ONE OF THE FOUR DIRECTIONS BY SWIPING ON THE LEFT HALF OF THE SCREEN IN ANY GAME.");
                    chosen.setPosition(firstX+2*distance, upperY);
                    chosen.setVisible(true);
                    return true;
                } else {
                    return false;
                }
            }
        };
        moveDiagonalSprite = new Sprite(firstX+3*distance, upperY, sideLength*3, sideLength*3, resourcesManager.move_diagonal_sign_region, vbom){
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    backRect.setVisible(true);
                    backRect.setPosition(camera.getCenterX(), sideLength*2);
                    helpText.setVisible(true);
                    helpText.setPosition(camera.getCenterX(), sideLength*2);
                    helpText.setText("THIS SIGN MEANS THAT YOU CAN MOVE IN ONE OF EIGHT DIRECTIONS, SO DIAGONAL MOVING IS ALSO POSSIBLE.");
                    chosen.setPosition(firstX+3*distance, upperY);
                    chosen.setVisible(true);
                    return true;
                } else {
                    return false;
                }
            }
        };
        shootDiagonalSprite = new Sprite(firstX, lowerY, sideLength*3, sideLength*3, resourcesManager.shoot_diagonal_sign_region, vbom){
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    backRect.setVisible(true);
                    backRect.setPosition(camera.getCenterX(), upperY);
                    helpText.setVisible(true);
                    helpText.setPosition(camera.getCenterX(), upperY);
                    helpText.setText("THIS SIGN MEANS THAT YOU CAN SHOOT IN ALL DIRECTIONS, SO BETTER AIM ACCURATELY.");
                    chosen.setPosition(firstX, lowerY);
                    chosen.setVisible(true);
                    return true;
                } else {
                    return false;
                }
            }
        };
        mirrorSprite = new Sprite(firstX+distance, lowerY, sideLength*3, sideLength*2.6f, resourcesManager.cracky_mirror_sign_region, vbom){
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    backRect.setVisible(true);
                    backRect.setPosition(camera.getCenterX(), upperY);
                    helpText.setVisible(true);
                    helpText.setPosition(camera.getCenterX(), upperY);
                    helpText.setText("THIS SIGN SHOWS THAT THERE ARE MIRROR STONES IN THIS LEVEL AND THAT IT IS POSSIBLE TO KILL YOURSELF WITH A CANNONBALL.");
                    chosen.setPosition(firstX+distance, lowerY);
                    chosen.setVisible(true);
                    return true;
                } else {
                    return false;
                }
            }
        };
        helmetSprite = new Sprite(firstX+2*distance, lowerY, sideLength*3, sideLength*3, resourcesManager.helmet_sign_region, vbom){
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    backRect.setVisible(true);
                    backRect.setPosition(camera.getCenterX(), upperY);
                    helpText.setVisible(true);
                    helpText.setPosition(camera.getCenterX(), upperY);
                    helpText.setText("THIS SIGN MEANS THAT YOU CAN DESTROY CRACKY STONES WITH YOUR PLAYER.");
                    chosen.setPosition(firstX+2*distance, lowerY);
                    chosen.setVisible(true);
                    return true;
                } else {
                    return false;
                }
            }
        };
        lamporghinaSprite = new Sprite(firstX+3*distance, lowerY, sideLength*3, sideLength*3, resourcesManager.lamporghina_sign_region, vbom){
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    backRect.setVisible(true);
                    backRect.setPosition(camera.getCenterX(), upperY);
                    helpText.setVisible(true);
                    helpText.setPosition(camera.getCenterX(), upperY);
                    helpText.setText("THIS SIGN MEANS THAT YOU CONTROL LAMPORGHINA WITH THE LEFT SCREEN HALF.");
                    chosen.setPosition(firstX+3*distance, lowerY);
                    chosen.setVisible(true);
                    return true;
                } else {
                    return false;
                }
            }
        };

        secondLayer.attachChild(kimmelnitzSprite);
        secondLayer.attachChild(moveNormalSprite);
        secondLayer.attachChild(shootNormalSprite);
        secondLayer.attachChild(moveDiagonalSprite);
        secondLayer.attachChild(shootDiagonalSprite);
        secondLayer.attachChild(mirrorSprite);
        secondLayer.attachChild(lamporghinaSprite);
        secondLayer.attachChild(helmetSprite);

        registerTouchArea(kimmelnitzSprite);
        registerTouchArea(moveNormalSprite);
        registerTouchArea(shootNormalSprite);
        registerTouchArea(moveDiagonalSprite);
        registerTouchArea(shootDiagonalSprite);
        registerTouchArea(mirrorSprite);
        registerTouchArea(lamporghinaSprite);
        registerTouchArea(helmetSprite);
    }

    private void removeHelpSigns() {
        unregisterTouchArea(kimmelnitzSprite);
        kimmelnitzSprite.detachSelf();
        kimmelnitzSprite.dispose();

        unregisterTouchArea(moveNormalSprite);
        moveNormalSprite.detachSelf();
        moveNormalSprite.dispose();

        unregisterTouchArea(shootNormalSprite);
        shootNormalSprite.detachSelf();
        shootNormalSprite.dispose();

        unregisterTouchArea(moveDiagonalSprite);
        moveDiagonalSprite.detachSelf();
        moveDiagonalSprite.dispose();

        unregisterTouchArea(shootDiagonalSprite);
        shootDiagonalSprite.detachSelf();
        shootDiagonalSprite.dispose();

        unregisterTouchArea(mirrorSprite);
        mirrorSprite.detachSelf();
        mirrorSprite.dispose();

        unregisterTouchArea(lamporghinaSprite);
        lamporghinaSprite.detachSelf();
        lamporghinaSprite.dispose();

        unregisterTouchArea(helmetSprite);
        helmetSprite.detachSelf();
        helmetSprite.dispose();

        backRect.detachSelf();
        backRect.dispose();
        helpText.detachSelf();
        helpText.dispose();
        chosen.detachSelf();
        chosen.dispose();
    }

    private void addInfo() {
        float infoX = sideLength*4.5f;
        worldText = new Text(infoX, sideLength*6, resourcesManager.smallFont, "CURRENT WORLD: 1234567890", vbom);
        worldText.setText("CURRENT WORLD: " + String.valueOf(activity.getCurrentWorld()));
        worldText.setColor(Color.BLACK);
        secondLayer.attachChild(worldText);

        highscoreText = new Text(infoX, sideLength*5, resourcesManager.smallFont, "HIGHSCORE: 1234567890", vbom);
        highscoreText.setText("HIGHSCORE: " + String.valueOf(activity.getCurrentHighscore()));
        highscoreText.setColor(Color.BLACK);
        secondLayer.attachChild(highscoreText);

        soundText = new Text(infoX, sideLength*4, resourcesManager.smallFont, "SOUND: ONOFF", vbom);
        soundText.setText("SOUND: " + (activity.isLoudVisible() ? "ON" : "OFF"));
        soundText.setColor(Color.BLACK);
        secondLayer.attachChild(soundText);

        coinText = new Text(infoX, sideLength*3, resourcesManager.smallFont, "COINS: 1234567890", vbom);
        coinText.setText("COINS: " + String.valueOf(activity.getCurrentBeersos()));
        coinText.setColor(Color.BLACK);
        secondLayer.attachChild(coinText);

        playerText = new Text(infoX, sideLength*2, resourcesManager.smallFont, "PLAYER: LUMELAMPORGHINAGRUME", vbom);
        String player;
        switch (activity.getCurrentPlayer()) {
            case 0:
                player = "LUME";
                break;
            case 1:
                player = "LAMPORGHINA";
                break;
            case 2:
                player = "GRUME";
                break;
            default: player = "LUME";
        }
        playerText.setText("PLAYER: " + player);
        playerText.setColor(Color.BLACK);
        secondLayer.attachChild(playerText);

        float installX = sideLength*13;
        installText = new Text(installX, camera.getHeight()-2*sideLength, resourcesManager.smallFont,
                "INSTALL ALSO", vbom);
        secondLayer.attachChild(installText);
        ballFall = new Sprite(installX, sideLength*4.5f, sideLength*2f, sideLength*2f,
                resourcesManager.ball_fall, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    final String ballBallId = "com.trishader.ballFall"; // getPackageName() from Context or Activity object
                    try {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ballBallId)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + ballBallId)));
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
        military = new Sprite(installX, sideLength*1.5f, sideLength*2f, sideLength*2f,
                resourcesManager.military, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    final String militaryId = "com.IliasInc.MilitaryWars"; // getPackageName() from Context or Activity object
                    try {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + militaryId)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + militaryId)));
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };

        secondLayer.attachChild(ballFall);
        secondLayer.attachChild(military);
        registerTouchArea(ballFall);
        registerTouchArea(military);
    }

    private void removeInfo() {
        worldText.detachSelf();
        worldText.dispose();

        highscoreText.detachSelf();
        highscoreText.dispose();

        playerText.detachSelf();
        playerText.dispose();

        coinText.detachSelf();
        coinText.dispose();

        soundText.detachSelf();
        soundText.dispose();

        installText.detachSelf();
        installText.dispose();
        ballFall.detachSelf();
        ballFall.dispose();
        military.detachSelf();
        military.dispose();
    }

    private void createTouchRectsRight() {
        final Rectangle infoTouch = new Rectangle(resourcesManager.sideLength*12, camera.getHeight()-resourcesManager.sideLength/2,
                resourcesManager.sideLength*6, resourcesManager.sideLength, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    helpVisible = false;
                    removeHelpSigns();
                    setHelpVisible(helpVisible);
                    addInfo();
                    return true;
                } else {
                    return false;
                }
            }
        };

        firstLayer.attachChild(infoTouch);
        registerTouchArea(infoTouch);
        infoTouch.setAlpha(0f);
    }

    private void createTouchRectLeft() {
        final Rectangle helpTouch = new Rectangle(resourcesManager.sideLength*4, camera.getHeight()-resourcesManager.sideLength/2,
                resourcesManager.sideLength*6, resourcesManager.sideLength, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    helpVisible = true;
                    removeInfo();
                    setHelpVisible(helpVisible);
                    addHelpSigns();
                    return true;
                } else {
                    return false;
                }
            }
        };

        firstLayer.attachChild(helpTouch);
        registerTouchArea(helpTouch);
        helpTouch.setAlpha(0f);
    }

    private void setHelpVisible(boolean visible) {
        helpVisible = visible;
        help.setVisible(helpVisible);
        info.setVisible(!helpVisible);
    }

    @Override
    public void onBackKeyPressed() {
        if (helpVisible) {
            removeHelpSigns();
        } else {
            removeInfo();
        }
        SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_HELP;
    }

    @Override
    public void disposeScene() {

    }
}