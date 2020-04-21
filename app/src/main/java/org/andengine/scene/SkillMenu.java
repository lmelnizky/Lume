package org.andengine.scene;

import org.andengine.base.BaseScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;

public class SkillMenu extends BaseScene {
    private float sideLength;

    @Override
    public void createScene() {
        sideLength = resourcesManager.sideLength;
        this.createBackground();
        this.createButtons();
    }

    public void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.skill_background_region, vbom));
        this.setBackground(spriteBackground);

        Text title = new Text(camera.getCenterX(), camera.getHeight()-sideLength, resourcesManager.bigFont,
                "Skill gym", vbom);
        this.attachChild(title);
    }

    public void createButtons() {
        Sprite[] upperButtons = new Sprite[4];
        Sprite[] lowerButtons = new Sprite[4];
        float firstX = sideLength*3.5f;
        float distance = sideLength*3;
        float firstRowY = sideLength*5.5f;
        float secondRowY = sideLength*2.5f;
        for (int i = 0; i < upperButtons.length; i++) {
            upperButtons[i] = new Sprite(firstX + i*distance, firstRowY, sideLength*2, sideLength*2.5f,
                    resourcesManager.hantel_region, vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                        SceneManager.getInstance().loadSkillGameScene(engine);
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            lowerButtons[i] = new Sprite(firstX + i*distance, secondRowY, sideLength*2, sideLength*2.5f,
                    resourcesManager.hantel_region, vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                        SceneManager.getInstance().loadSkillGameScene(engine);
                        return true;
                    } else {
                        return false;
                    }
                }
            };

            this.registerTouchArea(upperButtons[i]);
            this.registerTouchArea(lowerButtons[i]);
            this.attachChild(upperButtons[i]);
            this.attachChild(lowerButtons[i]);

        }
    }

    @Override
    public void onBackKeyPressed() {
        SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_SKILLMENU;
    }

    @Override
    public void disposeScene() {

    }
}
