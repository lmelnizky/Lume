package org.andengine.scene.OnlineScenes.ServerScene.Users.entities;

import android.widget.Button;

import com.google.android.gms.common.util.concurrent.NamedThreadFactory;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.scene.OnlineScenes.ServerScene.Player;
import org.andengine.scene.OnlineScenes.ServerScene.Users.LumeUserActions;
import org.andengine.scene.OnlineScenes.ServerScene.Users.MultiplayerUsersScene;
import org.andengine.util.adt.dictionary.Dictionary;

import java.util.LinkedList;

import static org.andengine.GameActivity.CAMERA_HEIGHT;
import static org.andengine.GameActivity.CAMERA_WIDTH;

public class RequestPopUp extends Sprite{
    //variables
    private Player requestFrom;
    private Text yesText, noText, questionText;
    private ButtonSprite yes, no;
    private MultiplayerUsersScene scene = MultiplayerUsersScene.getInstance();
    //constructor
    public RequestPopUp(Player requestFrom) {
        super(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, ResourcesManager.getInstance().inputtext_region, ResourcesManager.getInstance().vbom); //TODO set the textureRegion to a grey texture
        //super.setAlpha(200/255);
        super.setSize(CAMERA_WIDTH/2, CAMERA_HEIGHT/2);
        this.requestFrom = requestFrom;
        create();
    }
    private void create(){
        questionText = new Text(getWidth()/2,getHeight()/4*3, ResourcesManager.getInstance().smallFont, requestFrom.getUsername() + " möchte mit dir spielen", ResourcesManager.getInstance().vbom);
        yes = new ButtonSprite(getWidth()/4, getHeight()/4, ResourcesManager.getInstance().inputtext_region,ResourcesManager.getInstance().vbom, scene);
        no = new ButtonSprite(getWidth()/4*3,getHeight()/4, ResourcesManager.getInstance().inputtext_region, ResourcesManager.getInstance().vbom, scene);
        yes.setSize(getWidth()/10,getHeight()/10);
        no.setSize(getWidth()/10, getHeight()/10);
        yesText = new Text(yes.getWidth()/2,yes.getHeight()/2,ResourcesManager.getInstance().smallFont,"YES", ResourcesManager.getInstance().vbom);
        noText = new Text(no.getWidth()/2,no.getHeight()/2, ResourcesManager.getInstance().smallFont,"NO", ResourcesManager.getInstance().vbom);
        this.attachChild(questionText);
        yes.attachChild(yesText);
        no.attachChild(noText);
        this.attachChild(yes);  scene.registerTouchArea(yes);
        this.attachChild(no);   scene.registerTouchArea(no);
        scene.attachChild(this);
    }
    public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if(pButtonSprite == yes){
            scene.getServer().sendAnswer(true, requestFrom.getId());
            scene.registerUpdateHandler(new TimerHandler(3f, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    LinkedList<Player> players = new LinkedList();
                    for(Player p: scene.getPlayers()) if(p.getId().equals(requestFrom.getId())) players.add(p);
                    if(scene.getServer().getUserActions() instanceof LumeUserActions)players.add(((LumeUserActions) scene.getServer().getUserActions()).getLocalPLayer());
                    SceneManager.getInstance().loadMultiOnlineGameScene(ResourcesManager.getInstance().engine, players, scene.getServer());
                }
            }));
        }
        if(pButtonSprite == no){
            scene.getServer().sendAnswer(false, requestFrom.getId());
        }
    }
}
