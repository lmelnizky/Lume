package org.andengine.scene.OnlineScenes.ServerScene.Users;

import android.util.Log;

import org.andengine.base.BaseScene;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneType;
import org.andengine.scene.OnlineScenes.ServerScene.Game.LumeGameActions;
import org.andengine.scene.OnlineScenes.ServerScene.Player;
import org.andengine.scene.OnlineScenes.ServerScene.Server;
import org.andengine.scene.OnlineScenes.ServerScene.Users.entities.PlayersField;
import org.andengine.scene.OnlineScenes.ServerScene.Users.entities.RequestPopUp;
import org.andengine.util.adt.color.Color;

import java.util.LinkedList;
import java.util.List;

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
        this.setBackground(new Background(Color.BLUE));
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
    public void addRequestPopUp(String id){
        Log.i("MultiPlayerUserScene", "addRequestPopUp");
        for(Player p: players) if(p.getId().equals(id)) entities.add(new RequestPopUp(p));
    }
    public void updateScene(){
        Log.i("MultiPlayerUserScene", "updateScene");
        for(Player p: players) entities.add(new PlayersField(p));
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
