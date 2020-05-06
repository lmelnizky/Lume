package org.lume.scene.OnlineScenes.ServerScene.Users;

import android.util.Log;

import org.lume.base.BaseScene;
import org.lume.entity.Entity;
import org.lume.entity.scene.ITouchArea;
import org.lume.entity.scene.background.Background;
import org.lume.entity.sprite.ButtonSprite;
import org.lume.manager.ResourcesManager;
import org.lume.manager.SceneManager;
import org.lume.manager.SceneType;
import org.lume.scene.OnlineScenes.ServerScene.Game.LumeGameActions;
import org.lume.scene.OnlineScenes.ServerScene.Player;
import org.lume.scene.OnlineScenes.ServerScene.Server;
import org.lume.scene.OnlineScenes.ServerScene.Users.entities.PlayersField;
import org.lume.scene.OnlineScenes.ServerScene.Users.entities.RequestPopUp;
import org.lume.util.adt.color.Color;

import java.util.LinkedList;

//this is a singleton because only one instance should be initialized in runtime
//In this class you create the layout for the Scene. In LumePLayerActions you add all the Users to the Scene
public class MultiplayerUsersScene extends BaseScene implements ButtonSprite.OnClickListener {
    //INSTANCE
    private static MultiplayerUsersScene INSTANCE;
    //variables
    private LinkedList<Entity> entities = new LinkedList<>();
    private LinkedList<Entity> playerEntities = new LinkedList<>();
    private LinkedList<ITouchArea> touchAreas = new LinkedList<>();
    private LinkedList<Player> players;
    private Server server;
    //createScene method
    @Override
    public void createScene() {/*don't write code here, because the method is calling in the super constructor.. so in other classes, the getInstance() method will not work!!!(NullPointerException!)*/}
    //static methods
    public static MultiplayerUsersScene getInstance(){
        if(INSTANCE == null){ResourcesManager.getInstance().loadMenuTextures(); INSTANCE = new MultiplayerUsersScene();    INSTANCE.create();}
        return INSTANCE;
    }
    public static void destroyInstance(){
        INSTANCE.disposeScene();
        INSTANCE = null;
    }
    //methods
    private void create(){
        Log.i("MultiPlayerUserScene", "create");
        //TODO write code here, it will run after the constructor
        server = new Server(new LumeGameActions(), new LumeUserActions(), activity.getUserName());
        createChildren();
        manageChildren();
        this.setBackground(new Background(30/255, 178/255, 266/255));
    }
    private void createChildren(){
        Log.i("MultiPlayerUserScene", "createChildren");
        //TODO add children to the scene
    }
    private void manageChildren(){
        Log.i("MultiPlayerUserScene", "manageChildren");
        for(Entity e: entities) this.attachChild(e);
        for(ITouchArea area: touchAreas)this.registerTouchArea(area);
    }
    public void addRequestPopUp(String id, String room){}
    public void updateScene(){
        Log.i("MultiPlayerUserScene", "updateScene");
        for(Player p: players) playerEntities.add(new PlayersField(p));
    }
    //constructor
    private MultiplayerUsersScene(){}
    //override methods from superclass
    @Override
    public void onBackKeyPressed() {
        SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType() {return SceneType.SCENE_ONLINEUSERS;}

    @Override
    public void disposeScene() {
        //don't call the static destroy method, it will be recursive :)
    }
    //getter and setter
    public LinkedList<Entity> getEntitiesList() {return entities;}
    public LinkedList<ITouchArea> getTouchAreasList() {return touchAreas;}
    public LinkedList<Player> getPlayers() {return players;}
    public void setPlayers(LinkedList<Player> players) {this.players = players;}
    public LinkedList<Entity> getPlayerEntities() {return playerEntities;}

    public Server getServer() {return server;}

    @Override
    public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        Log.i("OnClick","OnClick");
        if(pButtonSprite.getParent() instanceof RequestPopUp) ((RequestPopUp) pButtonSprite.getParent()).onClick(pButtonSprite, pTouchAreaLocalX, pTouchAreaLocalY);
        if(pButtonSprite.getParent() instanceof PlayersField) ((PlayersField) pButtonSprite.getParent()).onClick(pButtonSprite);
    }
}
