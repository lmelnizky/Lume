package org.andengine.scene;

import org.andengine.base.BaseScene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.manager.SceneType;
import org.andengine.util.adt.color.Color;

public class TutorialScene extends BaseScene {
    @Override
    public void createScene() {
        setBackground(new Background(Color.WHITE));
        attachChild(new Text(camera.getCenterX(), camera.getHeight()*2/3, resourcesManager.smallFont, "Hahaha", vbom));
        attachChild(new Text(camera.getCenterX(), camera.getBoundsHeight()/3, resourcesManager.smallFont, "No seriously, find out yourself", vbom));
    }

    @Override
    public void onBackKeyPressed() {
        return;
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_LOADING;
    }

    @Override
    public void disposeScene() {

    }
}