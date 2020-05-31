package org.lume.scene.OnlineScenes.ServerScene.Users;

import android.util.Log;
import android.widget.Toast;

import org.lume.entity.Entity;
import org.lume.manager.ResourcesManager;
import org.lume.scene.OnlineScenes.ServerScene.Player;
import org.lume.scene.OnlineScenes.ServerScene.Users.entities.AnswerRequest;
import org.lume.scene.OnlineScenes.ServerScene.Users.entities.PlayersField;
import org.lume.scene.OnlineScenes.ServerScene.Users.entities.RequestPopUp;

import java.util.Collection;
import java.util.LinkedList;

public class LumeUserActions implements UserActions {
    private MultiplayerUsersScene scene;
    private Player localPLayer;
    private boolean listen = true;
    @Override
    public void socketID(String id) {
        if(listen) {
            scene = MultiplayerUsersScene.getInstance();
            Log.i("LumeUserActions", "socketID");
            localPLayer = new Player(id, scene.getServer().userName);
        }
    }
    @Override
    public void allUsers(Collection<Player> players) {
        if(listen) {
            scene = MultiplayerUsersScene.getInstance();
            Log.i("LumeUserActions", "allUsers");
            scene.setPlayers(new LinkedList<>(players));
            scene.updateScene();//TODO Attention! local player is part of players. If you don't want to show the localPlayer, you have to remove him!
        }
    }
    @Override
    public void newUser(Player player) {
        if(listen) {
            scene = MultiplayerUsersScene.getInstance();
            Log.i("LumeUserActions", "newUser");
            scene.getPlayers().add(player);
            scene.getPlayerEntities().add(new PlayersField(player));
            //updateScene and add the new User to the Scene
        }
    }
    @Override
    public void userDisconnected(String userID) {
        if(listen) {
            scene = MultiplayerUsersScene.getInstance();
            Log.i("LumeUserActions", "userDisconnected");
            Player player = null;
            for (Player p : ResourcesManager.getInstance().players)
                if (p.getId().equals(userID)) player = new Player(p.getId(), p.getUsername());
            ResourcesManager.getInstance().players.remove(player);
            for (Entity t : ResourcesManager.getInstance().playerEntities)
                if (t instanceof PlayersField)
                    if (((PlayersField) t).getPlayer().getId().equals(userID)) scene.detachChild(t);
        }
    }
    @Override
    public void getRequest(String id, String room) {
        if(listen) {
            scene = MultiplayerUsersScene.getInstance();
            Log.i("LumeUserActions", "getRequest");
            for (Player p : scene.getPlayers())
                if (p.getId().equals(id)) scene.getEntitiesList().add(new RequestPopUp(p, room));
        }
    }
    @Override
    public void disconnect() {
        if(listen) {
            scene = MultiplayerUsersScene.getInstance();
            Log.i("LumeUserActions", "disconnect");
            ResourcesManager.getInstance().activity.toastOnUiThread("Lost Connection!", Toast.LENGTH_LONG);
        }
    }
    @Override
    public void getAnswerRequest(boolean angenommen, String fromID, String room) {
        if (listen) {
            scene = MultiplayerUsersScene.getInstance();
            Log.i("LumeUserActions", "answerRequest");
            for (Player p : scene.getPlayers())
                if (p.getId().equals(fromID)) new AnswerRequest(p, angenommen, room);
        }
    }

    public Player getLocalPLayer() {return localPLayer;}

    public boolean isListen() {
        return listen;
    }

    public void setListen(boolean listen) {
        this.listen = listen;
    }
}
