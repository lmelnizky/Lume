package org.andengine.scene;

import org.andengine.base.BaseScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.util.adt.align.HorizontalAlign;

/**
 * Created by Lukas on 04.07.2017.
 */

public class Worlds1to4Scene extends BaseScene {

    private Text w1, w2, w3, w4;
    private Text[] w1Levels, w2Levels, w3Levels, w4Levels;
    private Sprite changePage1;
    private Sprite showVideoSprite;

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
    }

    @Override
    public void onBackKeyPressed() {
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
        changePage1 = new Sprite(camera.getWidth()-80,camera.getHeight()/2,
                100,80, resourcesManager.change_page, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    if (activity.getCurrentWorld() >= 5) {
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
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(1, finalI + 1);
                        } else {
                            SceneManager.getInstance().loadWorld1Scene(engine, finalI + 1);
                        }
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
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(2, finalI + 1);
                        } else {
                            if (activity.getCurrentWorld() >= 2) {
                                SceneManager.getInstance().loadWorld2Scene(engine, finalI + 1);
                            } else {
                                activity.toastOnUiThread("Level locked");
                            }
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
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(3, finalI + 1);
                        } else {
                            if (activity.getCurrentWorld() >= 3) {
                                SceneManager.getInstance().loadWorld3Scene(engine, finalI + 1);
                            } else {
                                activity.toastOnUiThread("Level locked");
                            }
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
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(4, finalI + 1);
                        } else {
                            if (activity.getCurrentWorld() >= 4) {
                                SceneManager.getInstance().loadWorld4Scene(engine, finalI + 1);
                            } else {
                                activity.toastOnUiThread("Level locked");
                            }
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
