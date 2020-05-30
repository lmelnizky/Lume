package org.lume.scene.OnlineScenes.ServerScene.Users;

import android.util.Log;

import org.lume.base.BaseScene;
import org.lume.entity.Entity;
import org.lume.entity.scene.ITouchArea;
import org.lume.entity.scene.background.Background;
import org.lume.entity.scene.background.SpriteBackground;
import org.lume.entity.sprite.ButtonSprite;
import org.lume.entity.sprite.Sprite;
import org.lume.manager.ResourcesManager;
import org.lume.manager.SceneManager;
import org.lume.manager.SceneType;
import org.lume.scene.OnlineScenes.ServerScene.Game.LumeGameActions;
import org.lume.scene.OnlineScenes.ServerScene.Player;
import org.lume.scene.OnlineScenes.ServerScene.Server;
import org.lume.scene.OnlineScenes.ServerScene.Users.entities.AnswerRequest;
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
//    private LinkedList<Entity> entities = new LinkedList<>();
//    private LinkedList<Entity> playerEntities = new LinkedList<>();
//    private LinkedList<ITouchArea> touchAreas = new LinkedList<>();
//    private LinkedList<Player> players = new LinkedList<Player>();

    private Server server;

    private SpriteBackground background;
    //createScene method
    @Override
    public void createScene() {
        /*don't write code here, because the method is calling in the super constructor.. so in other classes, the getInstance() method will not work!!!(NullPointerException!)*/
    }
    //static methods
    public static MultiplayerUsersScene getInstance(){
        if(INSTANCE == null) {
            Log.i("MultiplayerUsersScene", "Make new instance");
            INSTANCE = new MultiplayerUsersScene();
            INSTANCE.create();
        }
        return INSTANCE;
    }
    public static void destroyInstance() {
        if (INSTANCE != null) INSTANCE.disposeScene();
    }
    //methods
    private void create(){
        Log.i("MultiPlayerUserScene", "create");
        //TODO write code here, it will run after the constructor
        if (resourcesManager.server == null) {
            resourcesManager.server = new Server(new LumeGameActions(), new LumeUserActions(), activity.getUserName());
            resourcesManager.entities = new LinkedList<>();
            resourcesManager.playerEntities = new LinkedList<>();
            resourcesManager.touchAreas = new LinkedList<>();
            resourcesManager.players = new LinkedList<Player>();
        } else {
            resourcesManager.server.addMe();
        }
        server = resourcesManager.server;
        createChildren();
        manageChildren();
        background = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.online_background_region, vbom));
        this.setBackground(background);
    }
    private void createChildren(){
        Log.i("MultiPlayerUserScene", "createChildren");
        //TODO add children to the scene
    }
    private void manageChildren(){
        Log.i("MultiPlayerUserScene", "manageChildren");
        for(Entity e: resourcesManager.entities) this.attachChild(e);
        for(ITouchArea area: resourcesManager.touchAreas)this.registerTouchArea(area);
    }
    public void addRequestPopUp(String id, String room){}

    public void updateScene(){
        Log.i("MultiPlayerUserScene", "updateScene");
        for(Player p: resourcesManager.players) resourcesManager.playerEntities.add(new PlayersField(p));
    }
    //constructor
    private MultiplayerUsersScene(){}
    //override methods from superclass
    @Override
    public void onBackKeyPressed() {
//        server.getSocket().disconnect();
//        server.getSocket().close();
//        server = null;
        //this.disposeScene();
        resourcesManager.server.deleteMe();
//        for (int i = 0; i < resourcesManager.entities.size(); i++) {
//            resourcesManager.entities.get(i).detachSelf();
//            resourcesManager.entities.get(i).dispose();
//        }
//        resourcesManager.entities = null;
//        for (int i = 0; i < resourcesManager.playerEntities.size(); i++) {
//            resourcesManager.playerEntities.get(i).detachSelf();
//            resourcesManager.playerEntities.get(i).dispose();
//        }
//        resourcesManager.playerEntities = null;
//        for (int i = 0; i < resourcesManager.touchAreas.size(); i++) {
//            unregisterTouchArea(resourcesManager.touchAreas.get(i));
//        }
//        resourcesManager.touchAreas = null;
//        resourcesManager.players = null;
        resourcesManager.entities.removeAll(resourcesManager.entities);
        resourcesManager.playerEntities.removeAll(resourcesManager.playerEntities);
        resourcesManager.players.removeAll(resourcesManager.players);
        SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType() {return SceneType.SCENE_ONLINEUSERS;}

    @Override
    public void disposeScene() {
        this.detachSelf();
        this.dispose();
        if (INSTANCE != null) INSTANCE = null;
    }
    //getter and setter
    public LinkedList<Entity> getEntitiesList() {return resourcesManager.entities;}
    public LinkedList<ITouchArea> getTouchAreasList() {return resourcesManager.touchAreas;}
    public LinkedList<Player> getPlayers() {return resourcesManager.players;}
    public void setPlayers(LinkedList<Player> players) {resourcesManager.players = players;}
    public LinkedList<Entity> getPlayerEntities() {return resourcesManager.playerEntities;}

    public Server getServer() {return server;}

    @Override
    public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        Log.i("MultiplayerUsersScene","OnClick");
        if(pButtonSprite.getParent() instanceof RequestPopUp) ((RequestPopUp) pButtonSprite.getParent()).onClick(pButtonSprite, pTouchAreaLocalX, pTouchAreaLocalY);
        if(pButtonSprite.getParent() instanceof PlayersField) ((PlayersField) pButtonSprite.getParent()).onClick(pButtonSprite);
        if(pButtonSprite.getParent() instanceof AnswerRequest) ((AnswerRequest) pButtonSprite.getParent()).onClick(pButtonSprite);
    }
}
