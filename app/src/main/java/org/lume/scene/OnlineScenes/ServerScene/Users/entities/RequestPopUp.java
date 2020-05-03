package org.lume.scene.OnlineScenes.ServerScene.Users.entities;

import org.lume.engine.handler.timer.ITimerCallback;
import org.lume.engine.handler.timer.TimerHandler;
import org.lume.entity.sprite.ButtonSprite;
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

public class RequestPopUp extends Sprite{
    //variables
    private Player requestFrom;
    private Text yesText, noText, questionText;
    private ButtonSprite yes, no;
    private MultiplayerUsersScene scene = MultiplayerUsersScene.getInstance();
    private String room;
    //constructor
    public RequestPopUp(Player requestFrom, String room) {
        super(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, ResourcesManager.getInstance().inputtext_region, ResourcesManager.getInstance().vbom); //TODO set the textureRegion to a grey texture
        //super.setAlpha(200/255);
        this.room = room;
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
            LinkedList<Player> players = new LinkedList();
            for(Player p: scene.getPlayers()) if(p.getId().equals(requestFrom.getId())) players.add(p);
            if(scene.getServer().getUserActions() instanceof LumeUserActions)players.add(((LumeUserActions) scene.getServer().getUserActions()).getLocalPLayer());
            scene.getServer().sendAnswer(true, requestFrom.getId(), room);
            scene.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    SceneManager.getInstance().loadMultiOnlineGameScene(ResourcesManager.getInstance().engine, players, scene.getServer(), room);
                }
            }));
        }
        if(pButtonSprite == no){
            scene.getServer().sendAnswer(false, requestFrom.getId(), room);
        }
    }
    public Player getRequestFrom() {return requestFrom;}
    public void setRequestFrom(Player requestFrom) {this.requestFrom = requestFrom;}
    public Text getYesText() {return yesText;}
    public void setYesText(Text yesText) {this.yesText = yesText;}
    public Text getNoText() {return noText;}
    public void setNoText(Text noText) {this.noText = noText;}
    public Text getQuestionText() {return questionText;}
    public void setQuestionText(Text questionText) {this.questionText = questionText;}
    public ButtonSprite getYes() {return yes;}
    public void setYes(ButtonSprite yes) {this.yes = yes;}
    public ButtonSprite getNo() {return no;}
    public void setNo(ButtonSprite no) {this.no = no;}
    public MultiplayerUsersScene getScene() {return scene;}
    public void setScene(MultiplayerUsersScene scene) {this.scene = scene;}
    public String getRoom() {return room;}
    public void setRoom(String room) {this.room = room;}
}
