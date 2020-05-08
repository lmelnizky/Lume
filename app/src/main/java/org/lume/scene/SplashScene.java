package org.lume.scene;

import org.lume.base.BaseScene;
import org.lume.engine.camera.Camera;
import org.lume.entity.sprite.Sprite;
import org.lume.manager.SceneType;
import org.lume.opengl.util.GLState;

public class SplashScene extends BaseScene {
    private Sprite splash;

    @Override
    public void createScene() {
        splash = new Sprite(0, 0, resourcesManager.splash_region, vbom) {
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
        };

        splash.setScale(1.5f);
        splash.setPosition(resourcesManager.camera.getCenterX(), resourcesManager.camera.getCenterY());
        attachChild(splash);
    }

    @Override
    public void onBackKeyPressed() {
        return;
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_SPLASH;
    }

    @Override
    public void disposeScene() {
        splash.detachSelf();
        splash.dispose();
        this.detachSelf();
        this.dispose();
    }
}