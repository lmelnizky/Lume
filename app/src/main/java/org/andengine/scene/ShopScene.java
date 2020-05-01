package org.andengine.scene;

import org.andengine.base.BaseScene;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.util.adt.color.Color;

public class ShopScene extends BaseScene {

    private float sideLength;
    private float firstXPosition;
    private int columns = 5;
    private Sprite lumeSprite, lamporghinaSprite, grumeSprite, personalSprite, overlaySprite, moreCoinsSprite, chosen;
    private AnimatedSprite[] lowerCoins, upperCoins;
    private Text title;
    private Text lumeText, lamporghinaText, grumeText, personalText, moreCoinsText;
    private Text lumePrice, lamporghinaPrice, grumePrice, personalPrice;

    @Override
    public void createScene() {
        sideLength = (float) resourcesManager.sideLength;
        firstXPosition = camera.getCenterX()/5;

        SpriteBackground shopBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.background_shop_region,vbom));
        this.setBackground(shopBackground);

        //attach animated coins
        lowerCoins = new AnimatedSprite[8];
        upperCoins = new AnimatedSprite[8];
        float coinStartX = sideLength*1.5f;
        float coinDistance = sideLength*2;
        float coinYLow = sideLength*0.5f;
        float coinYHigh = camera.getHeight()-0.5f*sideLength;
        for (int i = 0; i < lowerCoins.length; i++) {
            lowerCoins[i] = new AnimatedSprite(coinStartX+i*coinDistance, coinYLow, sideLength, sideLength,
                    resourcesManager.coin_tiled_region, vbom);
            lowerCoins[i].animate(100);
            attachChild(lowerCoins[i]);

            upperCoins[i] = new AnimatedSprite(coinStartX+i*coinDistance, coinYHigh, sideLength, sideLength,
                    resourcesManager.coin_tiled_region, vbom);
            upperCoins[i].animate(100);
            attachChild(upperCoins[i]);
        }

        //attachOverlay
        overlaySprite = new Sprite(camera.getCenterX(), (float)resourcesManager.sideLength*4,
                camera.getWidth(), resourcesManager.sideLength*6, resourcesManager.shop_overlay_region, vbom);
        attachChild(overlaySprite);

        //attachTitle
        title = new Text(camera.getCenterX(), (float)(camera.getHeight()-resourcesManager.sideLength),
                resourcesManager.bigFont, "SHOP", vbom);
        attachChild(title);

        //attachTexts
        lumeText = new Text(firstXPosition, (float)(camera.getHeight()-2.5*resourcesManager.sideLength),
                resourcesManager.standardFont, "Lume", vbom);
        attachChild(lumeText);
        lamporghinaText = new Text(firstXPosition + camera.getWidth()/5, (float)(camera.getHeight()-2.5*resourcesManager.sideLength),
                resourcesManager.standardFont, "Lamporghina", vbom);
        attachChild(lamporghinaText);
        grumeText = new Text(camera.getCenterX(), (float)(camera.getHeight()-2.5*resourcesManager.sideLength),
                resourcesManager.standardFont, "Grume", vbom);
        attachChild(grumeText);
        personalText = new Text(firstXPosition+3*camera.getWidth()/5, (float)(camera.getHeight()-2.5*resourcesManager.sideLength),
                resourcesManager.standardFont, "Available soon", vbom);
        attachChild(personalText);
        moreCoinsText = new Text(firstXPosition+4*camera.getWidth()/5, (float)(camera.getHeight()-2.5*resourcesManager.sideLength),
                resourcesManager.standardFont, "Get more coins!", vbom);
        attachChild(moreCoinsText);
        lumeText.setColor(Color.WHITE_ARGB_PACKED_INT);
        lamporghinaText.setColor(Color.WHITE_ARGB_PACKED_INT);
        grumeText.setColor(Color.WHITE_ARGB_PACKED_INT);
        personalText.setColor(Color.WHITE_ARGB_PACKED_INT);

        //attachSprites
        lumeSprite = new Sprite(firstXPosition, (float)resourcesManager.sideLength*4,
                sideLength*2.8f, sideLength*2.8f, resourcesManager.lume_big_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    activity.showCoinHint(0, 0, ShopScene.this);
                    return true;
                } else {
                    return false;
                }
            }
        };
        attachChild(lumeSprite);
        lamporghinaSprite = new Sprite(firstXPosition + camera.getWidth()/5, (float)resourcesManager.sideLength*4,
                sideLength*2.8f, sideLength*2.8f, resourcesManager.lamporghina_big_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if (activity.getCurrentBeersos() >= 100) {
                        activity.showCoinHint(1, 100, ShopScene.this);
                    } else {
                        activity.toastOnUiThread("Not enough coins, man!", 0);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
        attachChild(lamporghinaSprite);
        grumeSprite = new Sprite(camera.getCenterX(), (float)resourcesManager.sideLength*4,
                sideLength*2.8f, sideLength*2.8f, resourcesManager.grume_big_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if (activity.getCurrentBeersos() >= 1000) {
                        activity.showCoinHint(0, 1000, ShopScene.this);
                    } else {
                        activity.toastOnUiThread("Not enough coins, man!");
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
        attachChild(grumeSprite);
        personalSprite = new Sprite(firstXPosition+3*camera.getWidth()/5, (float)resourcesManager.sideLength*4,
                sideLength*2.8f, sideLength*2.8f, resourcesManager.personal_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    activity.toastOnUiThread("Available soon!", 0);
                    updateChosenRect();
                    return true;
                } else {
                    return false;
                }
            }
        };
        attachChild(personalSprite);
        moreCoinsSprite = new Sprite(firstXPosition+4*camera.getWidth()/5, (float)resourcesManager.sideLength*4,
                sideLength*2.8f, sideLength*2.8f, resourcesManager.more_coins_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    activity.showRewarded(1);
                    return true;
                } else {
                    return false;
                }
            }
        };
        attachChild(moreCoinsSprite);
        registerTouchArea(lumeSprite);
        registerTouchArea(lamporghinaSprite);
        registerTouchArea(grumeSprite);
        registerTouchArea(personalSprite);
        registerTouchArea(moreCoinsSprite);

        //attach price
        lumePrice = new Text(firstXPosition, (float)(float)(1.5*resourcesManager.sideLength),
                resourcesManager.smallFont, "0", vbom);
        attachChild(lumePrice);
        lamporghinaPrice = new Text(firstXPosition+camera.getWidth()/5, (float)(1.5*resourcesManager.sideLength),
                resourcesManager.smallFont, "100", vbom);
        attachChild(lamporghinaPrice);
        grumePrice = new Text(camera.getCenterX(), (float)(1.5*resourcesManager.sideLength),
                resourcesManager.smallFont, "1000", vbom);
        attachChild(grumePrice);
        personalPrice = new Text(firstXPosition+3*camera.getWidth()/5, (float)(1.5*resourcesManager.sideLength),
                resourcesManager.smallFont, "?", vbom);
        attachChild(personalPrice);

        //add chosen rect
        chosen = new Sprite(lumeSprite.getX()+camera.getWidth()/5*activity.getCurrentPlayer(), lumeSprite.getY(),
                camera.getWidth()/5, sideLength*6, resourcesManager.chosen_region, vbom);
        attachChild(chosen);
    }

    public void updateChosenRect() {
        chosen.setPosition(lumeSprite.getX()+camera.getWidth()/5*activity.getCurrentPlayer(), lumeSprite.getY());
    }

    @Override
    public void onBackKeyPressed() {
        SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_SHOP;
    }

    @Override
    public void disposeScene() {

    }
}
