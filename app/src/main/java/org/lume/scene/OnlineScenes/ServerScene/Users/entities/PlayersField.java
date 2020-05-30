package org.lume.scene.OnlineScenes.ServerScene.Users.entities;

import android.util.Log;

import org.lume.GameActivity;
import org.lume.engine.handler.timer.ITimerCallback;
import org.lume.engine.handler.timer.TimerHandler;
import org.lume.entity.sprite.ButtonSprite;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.Text;
import org.lume.manager.ResourcesManager;
import org.lume.scene.OnlineScenes.ServerScene.Player;
import org.lume.scene.OnlineScenes.ServerScene.Users.MultiplayerUsersScene;

import static org.lume.GameActivity.CAMERA_HEIGHT;
import static org.lume.GameActivity.CAMERA_WIDTH;

public class PlayersField extends Sprite {
    private MultiplayerUsersScene scene = MultiplayerUsersScene.getInstance();
    private Player player;
    private Text nameText, inviteText;
    private ButtonSprite inviteButton;
    public PlayersField(Player player) {
        super(CAMERA_WIDTH/2, 0,CAMERA_WIDTH,CAMERA_HEIGHT/10, ResourcesManager.getInstance().inputtext_online_region, ResourcesManager.getInstance().vbom);
        super.setY(CAMERA_HEIGHT-(CAMERA_HEIGHT/10*scene.getPlayers().indexOf(player))-CAMERA_HEIGHT/20);
        this.player = player;
        create();
    }
    private void create(){
        boolean thisIsMe = (player.getUsername().equals(ResourcesManager.getInstance().activity.getUserName()));
        inviteButton = new ButtonSprite(0,0,ResourcesManager.getInstance().inputtext_online_region, ResourcesManager.getInstance().vbom, scene);
        inviteButton.setSize(super.getHeight()*3.25f, super.getHeight()/1.5f);
        inviteButton.setPosition(super.getWidth()-inviteButton.getWidth()/1.25f, super.getHeight()/2);
        nameText = new Text(0,0, ResourcesManager.getInstance().standardFont, player.getUsername() + ((thisIsMe) ? " (me)" : ""), ResourcesManager.getInstance().vbom);
        nameText.setPosition(nameText.getLineAlignmentWidth()/2 + 30, this.getHeight()/2);
        inviteText = new Text(inviteButton.getWidth()/2, inviteButton.getHeight()/2, ResourcesManager.getInstance().standardFont, "INVITE", ResourcesManager.getInstance().vbom);
        inviteButton.attachChild(inviteText);
        this.attachChild(nameText);
        this.attachChild(inviteButton);
        scene.attachChild(this);    scene.registerTouchArea(inviteButton);
        inviteButton.setVisible(!thisIsMe);
        if (thisIsMe) {
            inviteButton.setAlpha(0.2f);
            this.setAlpha(0.2f);
        }
        Log.i("PlayersField", "createScene");
    }
    public void onClick(ButtonSprite button){
        Log.i("PlayersField", "OnClick");
        if(button == inviteButton){
            inviteButton.setEnabled(false);
            inviteButton.setVisible(false);
            String id = "";
            for(Player p: scene.getPlayers()) if(p.getId().equals(player.getId())) id = p.getId();
            scene.getServer().sendRequest(id);
            Log.i("PlayersField", "sended Request to " + id);
        }
    }

    public Player getPlayer() {return player;}
    public ButtonSprite getInviteButton(){return inviteButton;}
}
