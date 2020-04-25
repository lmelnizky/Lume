package org.andengine.scene.OnlineScenes.ServerScene.Users;

import org.andengine.scene.OnlineScenes.ServerScene.Player;

import java.util.Collection;

public interface UserActions {
    void socketID(String id);
    void allUsers(Collection<Player> players);
    void newUser(Player player);
    void userDisconnected(Player player);
    void getRequest(String id, String username);
    void disconnect();
}
