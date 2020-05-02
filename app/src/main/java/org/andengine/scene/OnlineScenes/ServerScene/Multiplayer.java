package org.andengine.scene.OnlineScenes.ServerScene;

import org.andengine.scene.OnlineScenes.ServerScene.Game.LumeGameActions;
import org.andengine.scene.OnlineScenes.ServerScene.Users.LumeUserActions;

import java.util.LinkedList;

public class Multiplayer {
    //variables
    private LinkedList<Player> players;
    private Server server;
    private String room;
    //constructor
    public Multiplayer(LinkedList<Player> players){this.players = players; create();}
    public Multiplayer(Server server, LinkedList<Player> players,String room){this.server = server; this.players = players; this.room = room; create();}
    //methods
    private void create(){
        players = players == null ? new LinkedList<>() : players;
        server = server == null ? new Server(new LumeGameActions(), new LumeUserActions(), ""): server; //TODO username from shared prefs
        //TODO where should start your Player? where should start Player 2/3/4?
    }
    //getter
    public LinkedList<Player> getPlayers(){return players;}
    public Server getServer() {return server;}
    public String getRoom() {return room;}
    public void setRoom(String room) {this.room = room;}
}
