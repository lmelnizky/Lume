package org.andengine.scene;

import org.andengine.base.BaseScene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

/**
 * Created by Lukas on 04.07.2017.
 */

public class LevelsScene extends BaseScene {

    private Text w1, w2, w3, w4;
    private Text[] w1Levels, w2Levels, w3Levels, w4Levels;

    @Override
    public void createScene() {
        this.createBackground();
        this.initTextArrays();
        createTexts();
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

    private void initTextArrays() {
        w1Levels = new Text[4];
        w2Levels = new Text[4];
        w3Levels = new Text[4];
        w4Levels = new Text[4];
    }

    private void createTexts() {
        createWorldTexts();

        int levelsPerWorld = 4;

        float startX1 = camera.getWidth()*3/16;
        float startX2 = camera.getWidth()*5/8;

        float startY1 = camera.getHeight()*6/9;
        float startY2 = camera.getHeight()*2/9;

        float gap = camera.getWidth()/16;
        float height = camera.getHeight()*5/12;

        for (int i = 0; i < levelsPerWorld; i++) {
            final int finalI = i;


            w1Levels[i] = new Text(startX1+(finalI*gap), startY1,
                    resourcesManager.smallFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                        SceneManager.getInstance().loadWorld1Scene(engine, finalI + 1);
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w1Levels[i]);
            this.attachChild(w1Levels[i]);


            w2Levels[i] = new Text(startX2+(finalI*gap), startY1,
                    resourcesManager.smallFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                        SceneManager.getInstance().loadWorld2Scene(engine, finalI + 1);
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w2Levels[i]);
            this.attachChild(w2Levels[i]);


            w3Levels[i] = new Text(startX1+(finalI*gap), startY2,
                    resourcesManager.smallFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                        SceneManager.getInstance().loadWorld3Scene(engine, finalI + 1);
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w3Levels[i]);
            this.attachChild(w3Levels[i]);


            w4Levels[i] = new Text(startX2+(finalI*gap), startY2,
                    resourcesManager.smallFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {
                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                    if (touchEvent.isActionDown()) {
                        SceneManager.getInstance().loadWorld4Scene(engine, finalI + 1);
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            this.registerTouchArea(w4Levels[i]);
            this.attachChild(w4Levels[i]);
        }

//        for (int i = 0; i < w2Levels.length; i++) {
//            final int finalI = i;
//            w2Levels[i] = new Text(startX2+(finalI*gap), startY1,
//                    resourcesManager.smallFont, String.valueOf(finalI +1), new TextOptions(HorizontalAlign.CENTER), vbom) {
//                public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
//                    if (touchEvent.isActionDown()) {
//                        SceneManager.getInstance().loadWorld2Scene(engine, finalI + 1);
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//            };
//            this.registerTouchArea(w2Levels[i]);
//            this.attachChild(w2Levels[i]);
//        }

//        w1L1 = new Text(camera.getWidth()/4, height, resourcesManager.smallFont, "1", new TextOptions(HorizontalAlign.CENTER), vbom) {
//            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
//                if (touchEvent.isActionDown()) {
//                    SceneManager.getInstance().loadWorld1Scene(engine, 1);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        };
//
//        w1L2 = new Text(w1L1.getX() + gap, height, resourcesManager.smallFont, "2", new TextOptions(HorizontalAlign.CENTER), vbom) {
//            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
//                if (touchEvent.isActionDown()) {
//                    SceneManager.getInstance().loadWorld1Scene(engine, 2);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        };
//
//        w1L3 = new Text(w1L2.getX() + gap, height, resourcesManager.smallFont, "3", new TextOptions(HorizontalAlign.CENTER), vbom) {
//            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
//                if (touchEvent.isActionDown()) {
//                    SceneManager.getInstance().loadWorld1Scene(engine, 3);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        };
//
//        w1L4 = new Text(w1L3.getX() + gap, height, resourcesManager.smallFont, "4", new TextOptions(HorizontalAlign.CENTER), vbom) {
//            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
//                if (touchEvent.isActionDown()) {
//                    SceneManager.getInstance().loadWorld1Scene(engine, 4);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        };

//        world2 = new Text(camera.getCenterX(), camera.getHeight()/6, resourcesManager.smallFont, "W2", new TextOptions(HorizontalAlign.CENTER), vbom) {
//            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
//                if (touchEvent.isActionDown()) {
//                    SceneManager.getInstance().loadWorld2Scene(engine, 0);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        };

//        w1L1.setColor(Color.BLUE);
//        w1L2.setColor(Color.BLUE);
//        w1L3.setColor(Color.BLUE);
//        w1L4.setColor(Color.BLUE);
//        world2.setColor(Color.RED);

//        this.registerTouchArea(w1L1);
//        this.registerTouchArea(w1L2);
//        this.registerTouchArea(w1L3);
//        this.registerTouchArea(w1L4);
//        this.registerTouchArea(world2);
//
//        this.attachChild(w1L1);
//        this.attachChild(w1L2);
//        this.attachChild(w1L3);
//        this.attachChild(w1L4);
//        this.attachChild(world2);
    }

    private void createWorldTexts() {
        float x1 = camera.getWidth()/4;
        float x2 = camera.getWidth()*3/4;
        float y1 = camera.getHeight()*8/9;
        float y2 = camera.getHeight()*4/9;

        w1 = new Text(x1, y1, resourcesManager.smallFont, "W1", new TextOptions(HorizontalAlign.CENTER), vbom);
        w2 = new Text(x2, y1, resourcesManager.smallFont, "W2", new TextOptions(HorizontalAlign.CENTER), vbom);
        w3 = new Text(x1, y2, resourcesManager.smallFont, "W3", new TextOptions(HorizontalAlign.CENTER), vbom);
        w4 = new Text(x2, y2, resourcesManager.smallFont, "W4", new TextOptions(HorizontalAlign.CENTER), vbom);

        this.attachChild(w1);
        this.attachChild(w2);
        this.attachChild(w3);
        this.attachChild(w4);
    }
}
