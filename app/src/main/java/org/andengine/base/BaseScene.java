package org.andengine.base;

import android.app.Activity;

import org.andengine.GameActivity;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

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

    //---------------------------------------------
    // ABSTRACTION
    //---------------------------------------------

    public abstract void createScene();

    public abstract void onBackKeyPressed();

    public abstract SceneType getSceneType();

    public abstract void disposeScene();
}
