package org.andengine.scene.OnlineScenes.ServerScene.Users;

import org.andengine.scene.OnlineScenes.ServerScene.Player;

import java.util.Collection;

public interface UserActions {
    void socketID(String id);
    void allUsers(Collection<Player> players);
    void newUser(Player player);
    void userDisconnected(String playerID);
    void getRequest(String id);
    void disconnect();
    void answerRequest(boolean angenommen, String fromID);
}
