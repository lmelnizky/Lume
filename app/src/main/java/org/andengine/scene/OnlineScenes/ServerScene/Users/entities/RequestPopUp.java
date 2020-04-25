package org.andengine.scene.OnlineScenes.ServerScene.Users.entities;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.manager.ResourcesManager;
import org.andengine.scene.OnlineScenes.ServerScene.Player;
import org.andengine.scene.OnlineScenes.ServerScene.Users.MultiplayerUsersScene;

import static org.andengine.GameActivity.CAMERA_HEIGHT;
import static org.andengine.GameActivity.CAMERA_WIDTH;

public class RequestPopUp extends Sprite implements ButtonSprite.OnClickListener {
    //variables
    private Player requestFrom;
    private MultiplayerUsersScene scene = MultiplayerUsersScene.getInstance();
    //constructor
    public RequestPopUp(Player requestFrom) {
        super(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, ResourcesManager.getInstance().arrow_region, ResourcesManager.getInstance().vbom); //TODO set the textureRegion to a grey texture
        super.setAlpha(200/255);
        this.requestFrom = requestFrom;
    }
    public void create(){ //TODO this method have to called external!

    }
    @Override
    public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {

    }
}
