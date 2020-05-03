package org.lume.scene.OnlineScenes.ServerScene.Users;

import org.lume.scene.OnlineScenes.ServerScene.Player;

import java.util.Collection;

public interface UserActions {
    void socketID(String id);
    void allUsers(Collection<Player> players);
    void newUser(Player player);
    void userDisconnected(String playerID);
    void getRequest(String id, String room);
    void disconnect();
    void getAnswerRequest(boolean angenommen, String fromID, String room);
}
