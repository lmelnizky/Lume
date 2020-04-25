package org.andengine.scene.OnlineScenes.ServerScene;

import org.andengine.scene.OnlineScenes.ServerScene.Game.LumeGameActions;
import org.andengine.scene.OnlineScenes.ServerScene.Users.LumeUserActions;

import java.util.LinkedList;

public class Multiplayer {
    //variables
    private LinkedList<Player> players;
    private Server server;
    //constructor
    public Multiplayer(LinkedList<Player> players){this.players = players; create();}
    public Multiplayer(Server server, LinkedList<Player> players){this.server = server; this.players = players; create();}
    //methods
    private void create(){
        players = players == null ? new LinkedList<>() : players;
        server = server == null ? new Server(new LumeGameActions(), new LumeUserActions()): server;
        //TODO where should start your Player? where should start Player 2/3/4?
    }
    //getter
    public LinkedList<Player> getPlayers(){return players;}
    public Server getServer() {return server;}
}
