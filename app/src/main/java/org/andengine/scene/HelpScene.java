package org.andengine.scene;

import org.andengine.base.BaseScene;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
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
        createTouchRectsRight();
        createTouchRectLeft();
    }

    private void createBackground() {
        Color colorC = new Color(30/255, 177/255, 225/255);
        Background background = new Background(colorC);
        help = new Sprite(camera.getCenterX(), camera.getCenterY(), camera.getWidth(), camera.getHeight(),
                resourcesManager.help_shop_region, vbom);
        this.attachChild(help);
        info = new Sprite(camera.getCenterX(), camera.getCenterY(), camera.getWidth(), camera.getHeight(),
                resourcesManager.info_shop_region, vbom);
        this.attachChild(info);
        help.setVisible(true);
        info.setVisible(false);
    }

    private void addSigns() {
        float sideLength = resourcesManager.sideLength;
        float firstX;
        float lowerY, upperY;
        float distance = camera.getWidth()/4;
        firstX = sideLength*2;
        Sprite kimmelnitzSprite = new Sprite(firstX, upperY, sideLength*3, sideLength*3, resourcesManager.kimmelnitz_region, vbom);
    }

    private void removeSigns() {

    }

    private void createTouchRectsRight() {
        final Rectangle infoTouch = new Rectangle(resourcesManager.sideLength*12, camera.getHeight()-resourcesManager.sideLength/2,
                resourcesManager.sideLength*6, resourcesManager.sideLength, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    helpVisible = false;
                    setHelpVisible(helpVisible);
                    return true;
                } else {
                    return false;
                }
            }
        };

        attachChild(infoTouch);
        registerTouchArea(infoTouch);
        infoTouch.setAlpha(0f);
    }

    private void createTouchRectLeft() {
        final Rectangle helpTouch = new Rectangle(resourcesManager.sideLength*4, camera.getHeight()-resourcesManager.sideLength/2,
                resourcesManager.sideLength*6, resourcesManager.sideLength, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    helpVisible = true;
                    setHelpVisible(helpVisible);
                    return true;
                } else {
                    return false;
                }
            }
        };

        attachChild(helpTouch);
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