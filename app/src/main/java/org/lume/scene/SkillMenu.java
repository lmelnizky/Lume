package org.lume.scene;

import org.lume.base.BaseScene;
import org.lume.entity.scene.background.SpriteBackground;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.Text;
import org.lume.input.touch.TouchEvent;
import org.lume.manager.SceneManager;
import org.lume.manager.SceneType;
import org.lume.util.adt.color.Color;

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
        title.setColor(new Color(0.4f, 0.4f, 0.4f));
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
            int finalI = i;
            upperButtons[i] = new Sprite(firstX + finalI *distance, firstRowY, sideLength*2, sideLength*2.5f,
                    resourcesManager.hantel_region, vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                        SceneManager.getInstance().loadSkillGameScene(engine, 1+ finalI);
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            int finalI1 = i;
            lowerButtons[i] = new Sprite(firstX + finalI1 *distance, secondRowY, sideLength*2, sideLength*2.5f,
                    resourcesManager.hantel_region, vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                        SceneManager.getInstance().loadSkillGameScene(engine, 5+ finalI1);
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
