package org.lume.base;

import org.lume.GameActivity;
import org.lume.engine.Engine;
import org.lume.engine.camera.BoundCamera;
import org.lume.entity.scene.Scene;
import org.lume.manager.ResourcesManager;
import org.lume.manager.SceneType;
import org.lume.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Lukas on 15.05.2017.
 */

public abstract class BaseScene extends Scene {

    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------

    protected Engine engine;
//    protected Activity activity;
    protected GameActivity activity;
    protected ResourcesManager resourcesManager;
    protected VertexBufferObjectManager vbom;
    protected BoundCamera camera;

    //---------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------

    public BaseScene() {
        this.resourcesManager = ResourcesManager.getInstance();
        this.engine = resourcesManager.engine;
        this.activity = resourcesManager.activity;
        this.vbom = resourcesManager.vbom;
        this.camera = resourcesManager.camera;
        createScene();
    }
    public BaseScene(boolean onlineUsers) {
        this.resourcesManager = ResourcesManager.getInstance();
        this.engine = resourcesManager.engine;
        this.activity = resourcesManager.activity;
        this.vbom = resourcesManager.vbom;
        this.camera = resourcesManager.camera;
        //createScene();
    }

    //---------------------------------------------
    // ABSTRACTION
    //---------------------------------------------

    public abstract void createScene();

    public abstract void onBackKeyPressed();

    public abstract SceneType getSceneType();

    public abstract void disposeScene();
}
