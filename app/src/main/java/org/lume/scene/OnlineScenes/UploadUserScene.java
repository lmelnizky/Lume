package org.lume.scene.OnlineScenes;

import android.util.Log;

import com.org.andengine.helperclasses.InputText;

import org.lume.OnlineUsers.GameState;
import org.lume.OnlineUsers.User;
import org.lume.OnlineUsers.UsernameLoaderManager;
import org.lume.OnlineUsers.World;
import org.lume.base.BaseScene;
import org.lume.entity.scene.background.SpriteBackground;
import org.lume.entity.sprite.ButtonSprite;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.Text;
import org.lume.manager.ResourcesManager;
import org.lume.manager.SceneType;
import org.lume.opengl.texture.region.TiledTextureRegion;
import org.lume.scene.MainMenuScene;

import java.util.LinkedList;

import static org.lume.GameActivity.CAMERA_HEIGHT;
import static org.lume.GameActivity.CAMERA_WIDTH;

public class UploadUserScene extends BaseScene implements ButtonSprite.OnClickListener {
    //variables
    private float sideLength;

    private InputText userNameInputText;
    private ButtonSprite confirmButton, noButton;
    private Text title;
    private Text confirmButtonHelpText, noHelpText;
    private Text usernameText;
    private String username;
    private UsernameLoaderManager uLM;
    private MainMenuScene parentScene;

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
        confirmButton = new ButtonSprite(CAMERA_WIDTH/2-sideLength*3, CAMERA_HEIGHT/4, ResourcesManager.getInstance().confirm_region, vbom, this);
        confirmButton.setSize(CAMERA_WIDTH/5,CAMERA_WIDTH/10);
        confirmButton.setEnabled(false);
        confirmButtonHelpText = new Text(confirmButton.getWidth()/2, confirmButton.getHeight()/2, ResourcesManager.getInstance().standardFont, "CONFIRM", vbom);
        usernameText = new Text(CAMERA_WIDTH/4, CAMERA_HEIGHT/4*3, ResourcesManager.getInstance().standardFont, "Username: (already used)(too long)", vbom);
        usernameText.setText("Username:");

        confirmButton.attachChild(confirmButtonHelpText);
        this.attachChild(userNameInputText);    this.attachChild(confirmButton);    this.attachChild(usernameText);
        this.registerTouchArea(confirmButton);  this.registerTouchArea(userNameInputText);

        noButton = new ButtonSprite(camera.getWidth()/2+sideLength*3, camera.getHeight()/4, ResourcesManager.getInstance().no_region,
                vbom, this);
        noButton.setSize(camera.getWidth()/7, camera.getWidth()/10);
        noButton.setEnabled(true);
        noHelpText = new Text(noButton.getWidth()/2, noButton.getHeight()/2, ResourcesManager.getInstance().standardFont, "NO", vbom);
        this.attachChild(noButton); this.registerTouchArea(noButton);
        noButton.attachChild(noHelpText);

        title = new Text(camera.getCenterX(), camera.getHeight()-sideLength, resourcesManager.smallFont, "DO YOU WANT AN ONLINE USER?", vbom);
        this.attachChild(title);
    }
    private void setUpULM(){
        uLM = new UsernameLoaderManager() {
            @Override
            public void startLoadingNames() {
                confirmButton.setEnabled(false);
                confirmButton.setVisible(false);
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
                    }
                    if (username.length() > 19) {
                        enabled = false;
                        usernameText.setText("Username:" + " (too long)");
                        activity.toastOnUiThread("This name has 20+ characters!", 0);
                    }
                    else {
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
        sideLength = resourcesManager.sideLength;
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.upload_background_region, vbom));
        this.setBackground(spriteBackground);
        setUpULM();
        setUpEntities();
    }

    @Override
    public void onBackKeyPressed() {
        this.disposeScene();
    }

    @Override
    public SceneType getSceneType() {
        return null;
    }

    @Override
    public void disposeScene() {
        this.detachSelf();
        this.dispose();
    }
    //override Methods from Interface(s)
    @Override
    public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if (pButtonSprite == confirmButton){
            Log.i("CONFIRMED", "CONFIRMED");
            username = userNameInputText.getText();

            User newUser = User.createUser(new GameState((activity.getCurrentHighscore()),
                    World.getWorld(activity.getCurrentWorld()), username));
            activity.setUserID(newUser.getiD());
            activity.setNameOnline(true);
            activity.setUserName(username);
            parentScene.createMenuScene();
        } else if (pButtonSprite == noButton) {
            activity.setWantsOnline(false);
            parentScene.createMenuScene();
        }
    }

    public void registerParentScene(MainMenuScene scene) {
        this.parentScene = scene;
    }
}
