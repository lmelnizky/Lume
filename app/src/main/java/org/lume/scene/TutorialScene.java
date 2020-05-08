package org.lume.scene;

import org.lume.base.BaseScene;
import org.lume.entity.scene.background.Background;
import org.lume.entity.text.Text;
import org.lume.manager.SceneType;
import org.lume.util.adt.color.Color;

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