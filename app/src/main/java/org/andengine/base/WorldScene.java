package org.andengine.base;

import org.andengine.GameActivity;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.manager.ResourcesManager;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class WorldScene extends Scene {

    protected Engine engine;
    protected GameActivity activity;
    protected ResourcesManager resourcesManager;
    protected VertexBufferObjectManager vbom;
    protected BoundCamera camera;

    //constructor
    public WorldScene() {
        this.resourcesManager = ResourcesManager.getInstance();
        this.engine = resourcesManager.engine;
        this.activity = resourcesManager.activity;
        this.vbom = resourcesManager.vbom;
        this.camera = resourcesManager.camera;
    }

    public void createHUD() {

    }
    public abstract void createBackground();
    public abstract void createMusic();
    public abstract void createBoard();
    public abstract void createLume();
}
