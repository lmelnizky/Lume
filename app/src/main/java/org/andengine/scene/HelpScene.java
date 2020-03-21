package org.andengine.scene;

import org.andengine.base.BaseScene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.util.adt.color.Color;

public class HelpScene extends BaseScene {
    @Override
    public void createScene() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.help_background_region, vbom));
        this.setBackground(spriteBackground);
        attachChild(new Text(camera.getCenterX(), camera.getHeight()*2/3, resourcesManager.smallFont, "Hahaha", vbom));
        attachChild(new Text(camera.getCenterX(), camera.getHeight()/3, resourcesManager.smallFont, "Help yourself!", vbom));
    }

    @Override
    public void onBackKeyPressed() {
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