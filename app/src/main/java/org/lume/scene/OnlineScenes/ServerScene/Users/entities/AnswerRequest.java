package org.lume.scene.OnlineScenes.ServerScene.Users.entities;

import org.lume.engine.handler.timer.ITimerCallback;
import org.lume.engine.handler.timer.TimerHandler;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.Text;
import org.lume.manager.ResourcesManager;
import org.lume.manager.SceneManager;
import org.lume.scene.OnlineScenes.ServerScene.Player;
import org.lume.scene.OnlineScenes.ServerScene.Users.LumeUserActions;
import org.lume.scene.OnlineScenes.ServerScene.Users.MultiplayerUsersScene;

import java.util.LinkedList;

import static org.lume.GameActivity.CAMERA_HEIGHT;
import static org.lume.GameActivity.CAMERA_WIDTH;

public class AnswerRequest extends Sprite {

    private MultiplayerUsersScene scene = MultiplayerUsersScene.getInstance();
    private String room;
    private Player fromPlayer;

    public AnswerRequest(Player fromPLayer, boolean angenommen, String room) {
        super(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, ResourcesManager.getInstance().inputtext_online_region, ResourcesManager.getInstance().vbom);
        super.setSize(CAMERA_WIDTH, CAMERA_HEIGHT);
        this.room = room;
        this.fromPlayer = fromPLayer;
        Text text = new Text(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 4 * 3, ResourcesManager.getInstance().standardFont,
                "1234567890123456789012345678901234567890123456789012345678901234567890", this.getVertexBufferObjectManager());
        if (angenommen)
            text.setText(fromPLayer.getUsername() + " has accepted your request :) Good luck!");
        if (!angenommen)
            text.setText(fromPLayer.getUsername() + " did not accept your request :( Luser!");
        this.attachChild(text);
        scene.attachChild(this);
        if (angenommen) {
            LinkedList<Player> players = new LinkedList();
            for (Player p : scene.getPlayers())
                if (p.getId().equals(fromPLayer.getId())) players.add(p);
            if (scene.getServer().getUserActions() instanceof LumeUserActions)
                players.add(((LumeUserActions) scene.getServer().getUserActions()).getLocalPLayer());
            scene.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {

                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    String[] list = new String[2];
                    list[0] = fromPLayer.getId();
                    list[1] = scene.getServer().id;
                    SceneManager.getInstance().loadMultiOnlineGameScene(ResourcesManager.getInstance().engine,players,scene.getServer(),room);
                    scene.getServer().createGameRoom(list, room);
                }
            }));
        }
    }
}
