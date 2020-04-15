package org.andengine.scene;

import org.andengine.base.BaseScene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;

public class ShopScene extends BaseScene {

    private float sideLength;
    private Sprite lumeSprite, lamporghinaSprite, grumeSprite, personalSprite, coinSprite;
    private Text title;
    private Text lumeText, lamporghinaText, grumeText, personalText;
    private Text lumePrice, lamporghinaPrice, grumePrice, personalPrice;

    @Override
    public void createScene() {
        sideLength = (float) resourcesManager.sideLength;

        RepeatingSpriteBackground shopBackground = new RepeatingSpriteBackground(camera.getWidth(), camera.getHeight(),
                resourcesManager.background_shop_region, vbom);
        this.setBackground(shopBackground);

        //attachTitle
        title = new Text(camera.getCenterX(), (float)(camera.getHeight()-resourcesManager.sideLength),
                resourcesManager.bigFont, "SHOP", vbom);
        attachChild(title);

        //attachTexts
        lumeText = new Text(camera.getCenterX()/4, (float)(camera.getHeight()-2.5*resourcesManager.sideLength),
                resourcesManager.smallFont, "Lume", vbom);
        attachChild(lumeText);

        //attachSprites
        lumeSprite = new Sprite(camera.getCenterX()/4, (float)resourcesManager.sideLength*4,
                sideLength*4, sideLength*4, resourcesManager.lume_region, vbom);
        attachChild(lumeSprite);
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
