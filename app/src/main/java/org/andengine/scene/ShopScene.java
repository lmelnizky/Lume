package org.andengine.scene;

import org.andengine.base.BaseScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.util.adt.color.Color;

public class ShopScene extends BaseScene {

    private float sideLength;
    private Sprite lumeSprite, lamporghinaSprite, grumeSprite, personalSprite, overlaySprite;
    private AnimatedSprite[] lowerCoins, upperCoins;
    private Text title;
    private Text lumeText, lamporghinaText, grumeText, personalText;
    private Text lumePrice, lamporghinaPrice, grumePrice, personalPrice;

    @Override
    public void createScene() {
        sideLength = (float) resourcesManager.sideLength;

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
        lumeText = new Text(camera.getCenterX()/4, (float)(camera.getHeight()-2.5*resourcesManager.sideLength),
                resourcesManager.standardFont, "Lume", vbom);
        attachChild(lumeText);
        lamporghinaText = new Text(camera.getCenterX()/4 + camera.getWidth()/4, (float)(camera.getHeight()-2.5*resourcesManager.sideLength),
                resourcesManager.standardFont, "Lamporghina", vbom);
        attachChild(lamporghinaText);
        grumeText = new Text(camera.getCenterX()/4 + camera.getWidth()/2, (float)(camera.getHeight()-2.5*resourcesManager.sideLength),
                resourcesManager.standardFont, "Grume", vbom);
        attachChild(grumeText);
        personalText = new Text(camera.getWidth()-camera.getCenterX()/4, (float)(camera.getHeight()-2.5*resourcesManager.sideLength),
                resourcesManager.standardFont, "Available soon", vbom);
        attachChild(personalText);
        lumeText.setColor(Color.WHITE_ARGB_PACKED_INT);
        lamporghinaText.setColor(Color.WHITE_ARGB_PACKED_INT);
        grumeText.setColor(Color.WHITE_ARGB_PACKED_INT);
        personalText.setColor(Color.WHITE_ARGB_PACKED_INT);

        //attachSprites
        lumeSprite = new Sprite(camera.getCenterX()/4, (float)resourcesManager.sideLength*4,
                sideLength*3.5f, sideLength*3.5f, resourcesManager.lume_big_region, vbom);
        attachChild(lumeSprite);
        lamporghinaSprite = new Sprite(camera.getCenterX()/4 + camera.getWidth()/4, (float)resourcesManager.sideLength*4,
                sideLength*3.5f, sideLength*3.5f, resourcesManager.lamporghina_region, vbom);
        attachChild(lamporghinaSprite);
        grumeSprite = new Sprite(camera.getCenterX()/4 + camera.getWidth()/2, (float)resourcesManager.sideLength*4,
                sideLength*3.5f, sideLength*3.5f, resourcesManager.grume_big_region, vbom);
        attachChild(grumeSprite);
        personalSprite = new Sprite(camera.getWidth()-camera.getCenterX()/4, (float)resourcesManager.sideLength*4,
                sideLength*3.5f, sideLength*3.5f, resourcesManager.personal_region, vbom);
        attachChild(personalSprite);

        //attach price
        lumePrice = new Text(camera.getCenterX()/4, (float)(float)(1.5*resourcesManager.sideLength),
                resourcesManager.smallFont, "0", vbom);
        attachChild(lumePrice);
        lamporghinaPrice = new Text(camera.getCenterX()/4 + camera.getWidth()/4, (float)(1.5*resourcesManager.sideLength),
                resourcesManager.smallFont, "100", vbom);
        attachChild(lamporghinaPrice);
        grumePrice = new Text(camera.getCenterX()/4 + camera.getWidth()/2, (float)(1.5*resourcesManager.sideLength),
                resourcesManager.smallFont, "1000", vbom);
        attachChild(grumePrice);
        personalPrice = new Text(camera.getWidth()-camera.getCenterX()/4, (float)(1.5*resourcesManager.sideLength),
                resourcesManager.smallFont, "?", vbom);
        attachChild(personalPrice);
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
