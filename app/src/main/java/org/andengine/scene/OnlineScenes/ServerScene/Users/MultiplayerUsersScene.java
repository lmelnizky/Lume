package org.andengine.scene.OnlineScenes.ServerScene.Users;

import org.andengine.base.BaseScene;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.manager.SceneType;

import java.util.LinkedList;

//this is a singleton because only one instance should be initialized in runtime
//In this class you create the layout for the Scene. In LumePLayerActions you add all the Users to the Scene
public class MultiplayerUsersScene extends BaseScene {
    //INSTANCE
    private static MultiplayerUsersScene INSTANCE;
    //variables
    private LinkedList<Entity> entities = new LinkedList<>();
    private LinkedList<ITouchArea> touchAreas = new LinkedList<>();
    //createScene method
    @Override
    public void createScene() {/*don't write code here, because the method is calling in the super constructor.. so in other classes, the getInstance() method will not work!!!(NullPointerException!)*/}
    //static methods
    public static MultiplayerUsersScene getInstance(){
        if(INSTANCE == null){ INSTANCE = new MultiplayerUsersScene();    INSTANCE.create();}
        return INSTANCE;
    }
    public static void destroyInstance(){
        INSTANCE.disposeScene();
        INSTANCE = null;
    }
    //methods
    private void create(){
        //TODO write code here, it will run after the constructor
        createChildren();
        manageChildren();
    }
    private void createChildren(){
        //TODO add children to the scene
    }
    private void manageChildren(){
        for(Entity e: entities) this.attachChild(e);
        for(ITouchArea area: touchAreas)this.registerTouchArea(area);
    }
    public void addRequestPopUp(){

    }
    //constructor
    private MultiplayerUsersScene(){}
    //override methods from superclass
    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public SceneType getSceneType() {
        return null;
    }

    @Override
    public void disposeScene() {
        //don't call the static destroy method, it will be recursive :)
    }
    //getter

    public LinkedList<Entity> getEntitiesList() {
        return entities;
    }

    public LinkedList<ITouchArea> getTouchAreasList() {
        return touchAreas;
    }
}
