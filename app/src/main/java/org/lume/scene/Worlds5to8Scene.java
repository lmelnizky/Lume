package org.lume.scene;

import org.lume.base.BaseScene;
import org.lume.entity.scene.background.SpriteBackground;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.Text;
import org.lume.entity.text.TextOptions;
import org.lume.input.touch.TouchEvent;
import org.lume.manager.SceneManager;
import org.lume.manager.SceneType;
import org.lume.util.adt.align.HorizontalAlign;

/**
 * Created by Lukas on 04.07.2017.
 */

public class Worlds5to8Scene extends BaseScene {

    private Text w5, w6, w7, w8;
    private Text[] w5Levels, w6Levels, w7Levels, w8Levels;
    private Sprite changePage2;

    private Sprite slowMotionSprite, noSnailSprite;

    private boolean slowMo = false;

    @Override
    public void createScene() {
        this.createBackground();
        this.createChangePageButton();
        this.initTextArrays();
        createTexts();
        createSlowMotionSprite();
    }

    @Override
    public void onBackKeyPressed() {
        disposeHUD();
        SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_LEVELS;
    }

    @Override
    public void disposeScene() {

    }

    private void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.chooseLevel_region, vbom));
        this.setBackground(spriteBackground);
    }

    private void createChangePageButton() {
        changePage2 = new Sprite(80,camera.getHeight()/2,
                100,80, resourcesManager.change_page, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    disposeHUD();
                    SceneManager.getInstance().loadWorlds1to4Scene(engine);
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.registerTouchArea(changePage2);
        this.attachChild(changePage2);
    }

    private void initTextArrays() {
        w5Levels = new Text[4];
        w6Levels = new Text[4];
        w7Levels = new Text[4];
        w8Levels = new Text[4];
    }

    private void createTexts() {
        createWorldTexts();

        int levelsPerWorld = 4;

        float startX1 = camera.getWidth()*5/32;
        float startX2 = camera.getWidth()*10/16;

        float startY1 = camera.getHeight()*6/9;
        float startY2 = camera.getHeight()*2/9;

        float gap = camera.getWidth()/14;
        float height = camera.getHeight()*5/12;

        for (int i = 0; i < levelsPerWorld; i++) {
            final int finalI = i;

            w5Levels[i] = new Text(startX1+(finalI*gap), startY1,
                    resourcesManager.worldNumberFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {

                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                            if (activity.getCurrentWorld() >= 5) {
                                disposeHUD();
                                SceneManager.getInstance().loadWorld5Scene(engine, finalI + 1);
                            } else {
                                activity.toastOnUiThread("Level locked");
                            }
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w5Levels[i]);
            this.attachChild(w5Levels[i]);


            w6Levels[i] = new Text(startX2+(finalI*gap), startY1,
                    resourcesManager.worldNumberFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                            if (activity.getCurrentWorld() >= 6) {
                                disposeHUD();
                                SceneManager.getInstance().loadWorld6Scene(engine, finalI + 1);
                            } else {
                                activity.toastOnUiThread("Level locked");
                            }
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w6Levels[i]);
            this.attachChild(w6Levels[i]);


            w7Levels[i] = new Text(startX1+(finalI*gap), startY2,
                    resourcesManager.worldNumberFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                            if (activity.getCurrentWorld() >= 7) {
                                disposeHUD();
                                SceneManager.getInstance().loadWorld7Scene(engine, finalI + 1);
                            } else {
                                activity.toastOnUiThread("Level locked");
                            }
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w7Levels[i]);
            this.attachChild(w7Levels[i]);


            w8Levels[i] = new Text(startX2+(finalI*gap), startY2,
                    resourcesManager.worldNumberFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                            if (activity.getCurrentWorld() >= 8) {
                                disposeHUD();
                                SceneManager.getInstance().loadWorld8Scene(engine, finalI + 1);
                            } else {
                                activity.toastOnUiThread("Level locked");
                            }
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w8Levels[i]);
            this.attachChild(w8Levels[i]);
        }
    }

    private void disposeHUD() {
        noSnailSprite.detachSelf();
        noSnailSprite.dispose();
        slowMotionSprite.detachSelf();
        slowMotionSprite.dispose();
        w5.detachSelf();w5.dispose();
        w6.detachSelf();w6.dispose();
        w7.detachSelf();w7.dispose();
        w8.detachSelf();w8.dispose();
        for (int i = 0; i < 4; i++) {
            w5Levels[i].detachSelf(); w5Levels[i].dispose();
            w6Levels[i].detachSelf(); w6Levels[i].dispose();
            w7Levels[i].detachSelf(); w7Levels[i].dispose();
            w8Levels[i].detachSelf(); w8Levels[i].dispose();
        }
        w5Levels = null; w6Levels = null; w7Levels = null; w8Levels = null;
    }

    private void createSlowMotionSprite() {
        slowMotionSprite = new Sprite(camera.getCenterX(), camera.getCenterY(), resourcesManager.sideLength*2.5f,
                resourcesManager.sideLength*2.5f, resourcesManager.snail_sign_region, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    slowMo = !slowMo;
                    slowMotionSprite.setVisible(slowMo);
                    noSnailSprite.setVisible(!slowMo);
                    activity.setSlowMotion(slowMo);
                    return true;
                } else {
                    return false;
                }
            }
        };
        noSnailSprite = new Sprite(camera.getCenterX(), camera.getCenterY(), resourcesManager.sideLength*2.5f,
                resourcesManager.sideLength*2.5f, resourcesManager.no_snail_sign_region, vbom);
        attachChild(slowMotionSprite);
        attachChild(noSnailSprite);
        registerTouchArea(slowMotionSprite);
        registerTouchArea(noSnailSprite);
        slowMotionSprite.setVisible(activity.isSlowMotion());
        noSnailSprite.setVisible(!activity.isSlowMotion());
    }

    private void createWorldTexts() {
        float x1 = camera.getWidth()/4;
        float x2 = camera.getWidth()*3/4;
        float y1 = camera.getHeight()*8/9;
        float y2 = camera.getHeight()*4/9;

        w5 = new Text(x1, y1, resourcesManager.smallFont, "World 5", new TextOptions(HorizontalAlign.CENTER), vbom);
        w6 = new Text(x2, y1, resourcesManager.smallFont, "World 6", new TextOptions(HorizontalAlign.CENTER), vbom);
        w7 = new Text(x1, y2, resourcesManager.smallFont, "World 7", new TextOptions(HorizontalAlign.CENTER), vbom);
        w8 = new Text(x2, y2, resourcesManager.smallFont, "World 8", new TextOptions(HorizontalAlign.CENTER), vbom);

        this.attachChild(w5);
        this.attachChild(w6);
        this.attachChild(w7);
        this.attachChild(w8);
    }
}
