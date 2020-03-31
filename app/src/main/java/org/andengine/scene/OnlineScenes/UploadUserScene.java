package org.andengine.scene.OnlineScenes;

import android.util.Log;
import android.widget.EditText;

import com.org.andengine.helperclasses.InputText;

import org.andengine.OnlineUsers.GameState;
import org.andengine.OnlineUsers.User;
import org.andengine.OnlineUsers.World;
import org.andengine.base.BaseScene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.text.Text;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneType;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.color.Color;

import static org.andengine.GameActivity.CAMERA_HEIGHT;
import static org.andengine.GameActivity.CAMERA_WIDTH;

public class UploadUserScene extends BaseScene implements ButtonSprite.OnClickListener {
    //variables
    private InputText userNameInputText;
    private ButtonSprite confirmButton;
    private Text confirmButtonHelpText;
    private Text usernameText;
    private String username;
    //private Methods
    private void setUpEntities(){
        userNameInputText = new InputText(
                CAMERA_WIDTH/4*3, CAMERA_HEIGHT/4*3, "Username", "What's your Nickname?",
                new TiledTextureRegion(ResourcesManager.getInstance().splashTextureAtlas, ResourcesManager.getInstance().splash_region), ResourcesManager.getInstance().smallFont,
                (int) CAMERA_WIDTH/8,(int) ResourcesManager.getInstance().smallFont.getLineHeight()/2 ,vbom, activity
        ){
            @Override
            public void setText(String text) {
                super.setText(text);
                if(!text.equals("")) confirmButton.setEnabled(true); else confirmButton.setEnabled(false);
            }
        };
        userNameInputText.setSize(CAMERA_WIDTH/4, ResourcesManager.getInstance().smallFont.getLineHeight());
        confirmButton = new ButtonSprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/4, ResourcesManager.getInstance().splash_region, vbom, this);
        confirmButton.setSize(CAMERA_WIDTH/5,CAMERA_WIDTH/10);
        confirmButton.setEnabled(false);
        confirmButtonHelpText = new Text(confirmButton.getWidth()/2, confirmButton.getHeight()/2, ResourcesManager.getInstance().smallFont, "CONFIRM", vbom);
        usernameText = new Text(CAMERA_WIDTH/4, CAMERA_HEIGHT/4*3, ResourcesManager.getInstance().smallFont, "Username:", vbom);

        confirmButton.attachChild(confirmButtonHelpText);
        this.attachChild(userNameInputText);    this.attachChild(confirmButton);    this.attachChild(usernameText);
        this.registerTouchArea(confirmButton);  this.registerTouchArea(userNameInputText);
    }
    //override Methods from superclass
    @Override
    public void createScene() {
        this.setBackground(new Background(Color.WHITE));
        setUpEntities();
    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public SceneType getSceneType() {
        return null;
    }

    @Override
    public void disposeScene() {

    }
    //override Methods from Interface(s)
    @Override
    public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if(pButtonSprite == confirmButton){
            Log.i("CONFIRMED", "CONFIRMED");
            username = userNameInputText.getText();
            User.createUser(new GameState(0, World.WORLD1, username));
        }
    }
}
