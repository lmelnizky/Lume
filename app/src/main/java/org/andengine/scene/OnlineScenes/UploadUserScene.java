package org.andengine.scene.OnlineScenes;

import android.util.Log;

import com.org.andengine.helperclasses.InputText;

import org.andengine.OnlineUsers.GameState;
import org.andengine.OnlineUsers.User;
import org.andengine.OnlineUsers.UsernameLoaderManager;
import org.andengine.OnlineUsers.World;
import org.andengine.base.BaseScene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneType;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.color.Color;

import java.util.LinkedList;

import static org.andengine.GameActivity.CAMERA_HEIGHT;
import static org.andengine.GameActivity.CAMERA_WIDTH;

public class UploadUserScene extends BaseScene implements ButtonSprite.OnClickListener {
    //variables
    private InputText userNameInputText;
    private ButtonSprite confirmButton;
    private Text confirmButtonHelpText;
    private Text usernameText;
    private String username;
    private UsernameLoaderManager uLM;

    //private Methods
    private void setUpEntities(){
        userNameInputText = new InputText(
                CAMERA_WIDTH/4*3, CAMERA_HEIGHT/4*3, "Username", "What's your Nickname?",
                new TiledTextureRegion(ResourcesManager.getInstance().menuTextureAtlas, ResourcesManager.getInstance().inputtext_region),
                ResourcesManager.getInstance().standardFont,
                (int) CAMERA_WIDTH/8,(int) ResourcesManager.getInstance().standardFont.getLineHeight()/2 ,vbom, activity
        ){
            @Override
            public void setText(String text) {
                super.setText(text);
                if(!text.equals("")) {
                    confirmButton.setEnabled(true);
                } else {
                    confirmButton.setEnabled(false);
                }

                User.getUsersFromDatabase(uLM);
            }
        };
        userNameInputText.setSize(CAMERA_WIDTH/4, ResourcesManager.getInstance().standardFont.getLineHeight());
        confirmButton = new ButtonSprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/4, ResourcesManager.getInstance().confirm_region, vbom, this);
        confirmButton.setSize(CAMERA_WIDTH/5,CAMERA_WIDTH/10);
        confirmButton.setEnabled(false);
        confirmButtonHelpText = new Text(confirmButton.getWidth()/2, confirmButton.getHeight()/2, ResourcesManager.getInstance().standardFont, "CONFIRM", vbom);
        usernameText = new Text(CAMERA_WIDTH/4, CAMERA_HEIGHT/4*3, ResourcesManager.getInstance().standardFont, "Username: (already used)", vbom);
        usernameText.setText("Username:");

        confirmButton.attachChild(confirmButtonHelpText);
        this.attachChild(userNameInputText);    this.attachChild(confirmButton);    this.attachChild(usernameText);
        this.registerTouchArea(confirmButton);  this.registerTouchArea(userNameInputText);
    }
    private void setUpULM(){
        uLM = new UsernameLoaderManager() {
            @Override
            public void startLoadingNames() {
                confirmButton.setEnabled(false);
            }

            @Override
            public void finishLoadingNames(LinkedList<String> userNames) {
                boolean enabled = confirmButton.isEnabled();
                if(userNameInputText.getText().equals("")) enabled = false; else enabled = true;
                for(String username: userNames) {
                    if (username.equals(userNameInputText.getText())) {
                        enabled = false;
                        usernameText.setText("Username:" + " (already used)");
                        activity.toastOnUiThread("This name already exists!", 0);
                    } else {
                        usernameText.setText("Username:");
                    }
                }
                confirmButton.setEnabled(enabled);
                confirmButton.setVisible(enabled);
            }
        };
    }
    //override Methods from superclass
    @Override
    public void createScene() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.upload_background_region, vbom));
        this.setBackground(spriteBackground);
        setUpULM();
        setUpEntities();
    }

    @Override
    public void onBackKeyPressed() {
        this.back(); //TODO cannot go back to parent scene
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
            User.createUser(new GameState((activity.getCurrentWorld()-1)*40,
                    World.getWorld(activity.getCurrentWorld()), username));
            activity.setNameOnline(true);
            activity.setUserName(username);
        }
    }
}
