package org.andengine.scene.OnlineScenes.ServerScene.Users;

import org.andengine.scene.OnlineScenes.ServerScene.Player;

import java.util.Collection;
import java.util.LinkedList;

public class LumeUserActions implements UserActions {
    private MultiplayerUsersScene scene = MultiplayerUsersScene.getInstance();
    private LinkedList<Player> onlinePLayers;
    private Player localPLayer;
    @Override
    public void socketID(String id) {
        localPLayer = new Player(id, ""); //TODO get the username from the shared prefs
    }
    @Override
    public void allUsers(Collection<Player> players) {
        onlinePLayers = new LinkedList<>(players); //TODO Attention! local player is part of players. If you don't want to show the localPlayer, you have to remove him!
    }
    @Override
    public void newUser(Player player) {
        onlinePLayers.add(player);
        //updateScene and add the new User to the Scene
    }
    @Override
    public void userDisconnected(Player player) {
        onlinePLayers.remove(player);
    }
    @Override
    public void getRequest(String id, String userName) {
        scene.addRequestPopUp();
    }
    @Override
    public void disconnect() {

    }
}
