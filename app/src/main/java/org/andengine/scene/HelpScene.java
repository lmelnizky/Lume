package org.andengine.scene;

import org.andengine.base.BaseScene;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.util.adt.color.Color;

public class HelpScene extends BaseScene {
    private boolean helpVisible = true;

    private Sprite help, info;

    @Override
    public void createScene() {
        createBackground();
        createTouchRects();
    }

    private void createBackground() {
        help = new Sprite(camera.getCenterX(), camera.getCenterY(), camera.getWidth(), camera.getHeight(),
                resourcesManager.help_shop_region, vbom);
        this.attachChild(help);
        info = new Sprite(camera.getCenterX(), camera.getCenterY(), camera.getWidth(), camera.getHeight(),
                resourcesManager.info_shop_region, vbom);
        this.attachChild(info);
        help.setVisible(true);
        info.setVisible(false);
    }

    private void createTouchRects() {
        final Rectangle helpTouch = new Rectangle(resourcesManager.sideLength*4, camera.getHeight()-resourcesManager.sideLength/2,
                resourcesManager.sideLength*6, resourcesManager.sideLength, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    setHelpVisible(true);
                    return true;
                } else {
                    return false;
                }
            }
        };

        final Rectangle infoTouch = new Rectangle(resourcesManager.sideLength*12, camera.getHeight()-resourcesManager.sideLength/2,
                resourcesManager.sideLength*6, resourcesManager.sideLength, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    setHelpVisible(false);
                    return true;
                } else {
                    return false;
                }
            }
        };

        attachChild(helpTouch);
        attachChild(infoTouch);
        registerTouchArea(helpTouch);
        registerTouchArea(infoTouch);
        helpTouch.setAlpha(0f);
        infoTouch.setAlpha(0f);
    }

    private void setHelpVisible(boolean visible) {
        helpVisible = visible;
        help.setVisible(helpVisible);
        info.setVisible(!helpVisible);
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