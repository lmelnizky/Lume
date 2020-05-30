package org.lume.scene;

import org.lume.base.BaseScene;
import org.lume.entity.scene.background.Background;
import org.lume.entity.text.Text;
import org.lume.manager.SceneType;
import org.lume.util.adt.color.Color;

import static org.lume.GameActivity.CAMERA_HEIGHT;
import static org.lume.GameActivity.CAMERA_WIDTH;

public class LoadingScene extends BaseScene {
    private String param;
    @Override
    public void createScene() {
        setBackground(new Background(Color.WHITE));
        attachChild(new Text(camera.getCenterX(), camera.getHeight()/3, resourcesManager.smallFont, "Loading...", vbom));
        if(param != null){
            Text text = new Text(CAMERA_WIDTH/2, CAMERA_HEIGHT/4, resourcesManager.standardFont, param, vbom);
            super.attachChild(text);
        }
    }
    public LoadingScene(){}
    public LoadingScene(String param){
        super(true);
        this.param = param;
        createScene();
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