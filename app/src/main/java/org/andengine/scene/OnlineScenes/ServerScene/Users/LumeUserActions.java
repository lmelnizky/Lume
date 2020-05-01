package org.andengine.scene.OnlineScenes.ServerScene.Users;

import android.util.Log;

import org.andengine.entity.Entity;
import org.andengine.scene.OnlineScenes.ServerScene.Player;
import org.andengine.scene.OnlineScenes.ServerScene.Users.entities.AnswerRequest;
import org.andengine.scene.OnlineScenes.ServerScene.Users.entities.PlayersField;
import org.andengine.scene.OnlineScenes.ServerScene.Users.entities.RequestPopUp;

import java.util.Collection;
import java.util.LinkedList;

public class LumeUserActions implements UserActions {
    private MultiplayerUsersScene scene = MultiplayerUsersScene.getInstance();
    private Player localPLayer;
    @Override
    public void socketID(String id) {
        Log.i("LumeUserActions", "socketID");
        localPLayer = new Player(id,scene.getServer().userName);
    }
    @Override
    public void allUsers(Collection<Player> players) {
        Log.i("LumeUserActions", "allUsers");
        scene.setPlayers(new LinkedList<>(players));
        scene.updateScene();//TODO Attention! local player is part of players. If you don't want to show the localPlayer, you have to remove him!
    }
    @Override
    public void newUser(Player player) {
        Log.i("LumeUserActions", "newUser");
        scene.getPlayers().add(player);
        scene.getPlayerEntities().add(new PlayersField(player));
        //updateScene and add the new User to the Scene
    }
    @Override
    public void userDisconnected(String userID) {
        Log.i("LumeUserActions", "userDisconnected");
        Player player = null;
        for(Player p: scene.getPlayers()) if(p.getId().equals(userID)) player = new Player(p.getId(), p.getUsername());
        scene.getPlayers().remove(player);
        for(Entity t: scene.getPlayerEntities()) if(t instanceof PlayersField) if(((PlayersField) t).getPlayer().getId().equals(userID)) scene.detachChild(t);
    }
    @Override
    public void getRequest(String id, String room) {
        Log.i("LumeUserActions", "getRequest");
        for(Player p: scene.getPlayers()) if(p.getId().equals(id)) scene.getEntitiesList().add(new RequestPopUp(p, room));
    }
    @Override
    public void disconnect() {
        Log.i("LumeUserActions", "disconnect");
    }
    @Override
    public void getAnswerRequest(boolean angenommen, String fromID, String room) {
        Log.i("LumeUserActions", "answerRequest");
        for(Player p: scene.getPlayers()) if(p.getId().equals(fromID)) new AnswerRequest(p, angenommen, room);
    }

    public Player getLocalPLayer() {return localPLayer;}
}
