package org.andengine.scene.OnlineScenes.ServerScene.Users.entities;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.scene.OnlineScenes.ServerScene.Player;
import org.andengine.scene.OnlineScenes.ServerScene.Users.LumeUserActions;
import org.andengine.scene.OnlineScenes.ServerScene.Users.MultiplayerUsersScene;

import java.util.LinkedList;

import static org.andengine.GameActivity.CAMERA_HEIGHT;
import static org.andengine.GameActivity.CAMERA_WIDTH;

public class AnswerRequest extends Sprite {

    private MultiplayerUsersScene scene = MultiplayerUsersScene.getInstance();
    private String room;
    private Player fromPlayer;

    public AnswerRequest(Player fromPLayer, boolean angenommen, String room) {
        super(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, ResourcesManager.getInstance().inputtext_region, ResourcesManager.getInstance().vbom);
        super.setSize(CAMERA_WIDTH, CAMERA_HEIGHT);
        this.room = room;
        this.fromPlayer = fromPLayer;
        Text text = new Text(CAMERA_WIDTH/2, CAMERA_HEIGHT/4*3, ResourcesManager.getInstance().smallFont,
                "1234567890123456789012345678901234567890123456789012345678901234567890", this.getVertexBufferObjectManager());
        if(angenommen)text.setText(fromPLayer.getUsername() + " hat deine Anfrage angenommen:) Viel Spaß!");
        if(!angenommen)text.setText(fromPLayer.getUsername() + " hat deine Anfrage nicht angenommen:( Lusche!");
        this.attachChild(text);
        scene.attachChild(this);
        if(angenommen)scene.registerUpdateHandler(new TimerHandler(3f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                LinkedList<Player> players = new LinkedList();
                for(Player p: scene.getPlayers()) if(p.getId().equals(fromPLayer.getId())) players.add(p);
                if(scene.getServer().getUserActions() instanceof LumeUserActions)players.add(((LumeUserActions) scene.getServer().getUserActions()).getLocalPLayer());
                SceneManager.getInstance().loadMultiOnlineGameScene(ResourcesManager.getInstance().engine, players, scene.getServer());
                String[] list = new String[1];
                list[0] = fromPLayer.getId();
                scene.getServer().createGameRoom(list, room);
            }
        }));
    }
}
