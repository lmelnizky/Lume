package org.lume.scene;

import org.lume.base.BaseScene;
import org.lume.entity.scene.background.SpriteBackground;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.Text;
import org.lume.entity.text.TextOptions;
import org.lume.input.touch.TouchEvent;
import org.lume.manager.ResourcesManager;
import org.lume.manager.SceneManager;
import org.lume.manager.SceneType;
import org.lume.util.adt.align.HorizontalAlign;

/**
 * Created by Lukas on 04.07.2017.
 */

public class Worlds1to4Scene extends BaseScene {

    private Text w1, w2, w3, w4;
    private Text[] w1Levels, w2Levels, w3Levels, w4Levels;
    private Sprite changePage1;
    private Sprite showVideoSprite;
    private Sprite slowMotionSprite, noSnailSprite;

    private boolean slowMo = false;

    private float startX1 = camera.getWidth()*5/32; //was 3
    private float startX2 = camera.getWidth()*10/16;

    private float startY1 = camera.getHeight()*6/9;
    private float startY2 = camera.getHeight()*2/9;

    @Override
    public void createScene() {
        this.createBackground();
        this.createChangePageButton();
        this.initTextArrays();
        createTexts();
        showVideoButton();
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

    private void disposeHUD() {
        noSnailSprite.detachSelf();
        noSnailSprite.dispose();
        slowMotionSprite.detachSelf();
        slowMotionSprite.dispose();
        w1.detachSelf();w1.dispose();
        w2.detachSelf();w2.dispose();
        w3.detachSelf();w3.dispose();
        w4.detachSelf();w4.dispose();
        for (int i = 0; i < 4; i++) {
            w1Levels[i].detachSelf(); w1Levels[i].dispose();
            w2Levels[i].detachSelf(); w2Levels[i].dispose();
            w3Levels[i].detachSelf(); w3Levels[i].dispose();
            w4Levels[i].detachSelf(); w4Levels[i].dispose();
        }
        w1Levels = null; w2Levels = null; w3Levels = null; w4Levels = null;
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

    private void createChangePageButton() {
        changePage1 = new Sprite(camera.getWidth()-80,camera.getHeight()/2,
                100,80, resourcesManager.change_page, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    if (activity.getCurrentWorld() >= 5) {
                        disposeHUD();
                        SceneManager.getInstance().loadWorlds5to8Scene(engine);
                    } else {
                        activity.toastOnUiThread("Worlds locked");
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.registerTouchArea(changePage1);
        this.attachChild(changePage1);
    }

    private void initTextArrays() {
        w1Levels = new Text[4];
        w2Levels = new Text[4];
        w3Levels = new Text[4];
        w4Levels = new Text[4];
    }

    private void createTexts() {
        createWorldTexts();

        startX1 = camera.getWidth()*5/32; //was 3
        startX2 = camera.getWidth()*10/16;

        startY1 = camera.getHeight()*6/9;
        startY2 = camera.getHeight()*2/9;

        int levelsPerWorld = 4;

        float gap = camera.getWidth()/14; //was 16
        float height = camera.getHeight()*5/12;

        for (int i = 0; i < levelsPerWorld; i++) {
            final int finalI = i;

            startX1 -= camera.getWidth()/28;

            w1Levels[i] = new Text(startX1+(finalI*gap), startY1,
                    resourcesManager.worldNumberFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {

                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                        disposeHUD();
                        SceneManager.getInstance().loadWorld1Scene(engine, finalI + 1);
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w1Levels[i]);
            this.attachChild(w1Levels[i]);

            startX1 = camera.getWidth()*5/32; //was 3

            w2Levels[i] = new Text(startX2+(finalI*gap), startY1,
                    resourcesManager.worldNumberFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                            if (activity.getCurrentWorld() >= 2) {
                                disposeHUD();
                                SceneManager.getInstance().loadWorld2Scene(engine, finalI + 1);
                            } else {
                                activity.toastOnUiThread("Level locked");
                            }
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w2Levels[i]);
            this.attachChild(w2Levels[i]);


            w3Levels[i] = new Text(startX1+(finalI*gap), startY2,
                    resourcesManager.worldNumberFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                            if (activity.getCurrentWorld() >= 3) {
                                disposeHUD();
                                SceneManager.getInstance().loadWorld3Scene(engine, finalI + 1);
                            } else {
                                activity.toastOnUiThread("Level locked");
                            }
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w3Levels[i]);
            this.attachChild(w3Levels[i]);


            w4Levels[i] = new Text(startX2+(finalI*gap), startY2,
                    resourcesManager.worldNumberFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                            if (activity.getCurrentWorld() >= 4) {
                                disposeHUD();
                                SceneManager.getInstance().loadWorld4Scene(engine, finalI + 1);
                            } else {
                                activity.toastOnUiThread("Level locked");
                            }
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w4Levels[i]);
            this.attachChild(w4Levels[i]);
        }
    }

    private void showVideoButton() {
        startX1 -= camera.getWidth()/28;
        showVideoSprite = new Sprite(startX1+4.3f*camera.getWidth()/14, startY1+camera.getHeight()/55, camera.getHeight()/9, camera.getHeight()/9*1.12f,
                ResourcesManager.getInstance().video_show_region, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {

                    activity.showWalkthroughDialog();
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.attachChild(showVideoSprite);
        this.registerTouchArea(showVideoSprite);
    }

    private void createWorldTexts() {
        float x1 = camera.getWidth()/4;
        float x2 = camera.getWidth()*3/4;
        float y1 = camera.getHeight()*8/9;
        float y2 = camera.getHeight()*4/9;

        w1 = new Text(x1, y1, resourcesManager.smallFont, "World 1", new TextOptions(HorizontalAlign.CENTER), vbom);
        w2 = new Text(x2, y1, resourcesManager.smallFont, "World 2", new TextOptions(HorizontalAlign.CENTER), vbom);
        w3 = new Text(x1, y2, resourcesManager.smallFont, "World 3", new TextOptions(HorizontalAlign.CENTER), vbom);
        w4 = new Text(x2, y2, resourcesManager.smallFont, "World 4", new TextOptions(HorizontalAlign.CENTER), vbom);

        this.attachChild(w1);
        this.attachChild(w2);
        this.attachChild(w3);
        this.attachChild(w4);
    }
}
