package org.andengine.scene.OnlineScenes.ServerScene.Game;

import org.andengine.base.BaseScene;
import org.andengine.manager.SceneType;
import org.andengine.scene.OnlineScenes.ServerScene.Multiplayer;
import org.andengine.scene.OnlineScenes.ServerScene.Player;
import org.andengine.scene.OnlineScenes.ServerScene.Server;

import java.util.LinkedList;

//this is a singleton because only one instance should be initialized in runtime
//this class should not extend from the BaseScene class.. It should extend from PlayScene or stuff like that
public class MultiplayerGameScene extends BaseScene {
    //INSTANCE
    private static MultiplayerGameScene INSTANCE;
    //variables
    private Multiplayer multiplayer;
    //createScene method
    @Override
    public void createScene() {/*don't write code here, because the method is calling in the super constructor.. so in other classes, the getInstance() method will not work!!!(NullPointerException!)*/}
    //static methods
    public static void createInstance(LinkedList<Player> players, Server server){ INSTANCE = new MultiplayerGameScene(players, server); INSTANCE.create();}
    public static MultiplayerGameScene getInstance(){
        if(INSTANCE == null) throw new RuntimeException("you have to call create Instance before getInstance!!!");
        return INSTANCE;
    }
    public static void destroyInstance(){
        INSTANCE.disposeScene();
        INSTANCE = null;
    }
    //methods
    private void create(){
        //TODO write code here, it will run after the constructor
        multiplayer = new Multiplayer(null);
    }
    public void createGame(){
        //TODO should called in LumeGameActions, if all players connect to the server(startGame())
        //TODO create Game
    }
    //constructor
    private MultiplayerGameScene(LinkedList<Player> players){multiplayer = new Multiplayer(players);}
    private MultiplayerGameScene(LinkedList<Player> players, Server server){multiplayer = new Multiplayer(server,players);}
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
    public Multiplayer getMultiplayer() {return multiplayer;}
}