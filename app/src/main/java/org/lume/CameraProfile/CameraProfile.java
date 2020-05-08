package org.lume.CameraProfile;

import org.lume.entity.IEntity;
import org.lume.entity.scene.ITouchArea;
import org.lume.entity.scene.Scene;
import org.lume.entity.sprite.ButtonSprite;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.Text;
import org.lume.manager.ResourcesManager;
import org.lume.opengl.texture.Texture;

import static org.lume.GameActivity.CAMERA_HEIGHT;
import static org.lume.GameActivity.CAMERA_WIDTH;

public class CameraProfile extends Sprite {
    //variables
    private CameraProfileManager cPM;
    private Scene parentScene;
    private ButtonSprite.OnClickListener oLS;

    private Texture result;
    private ButtonSprite makePhoto;
    private CameraLumeSprite showCamera;

    //constructor
    public CameraProfile(Scene scene, CameraProfileManager cPM, ButtonSprite.OnClickListener oLS) {
        super(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, CAMERA_WIDTH, CAMERA_HEIGHT, null, ResourcesManager.getInstance().vbom); //TODO set transparent Textureregion!
        this.cPM = cPM;
        parentScene = scene;
        this.oLS = oLS;
        setUpParentScene();
    }
    //private Methods
    private void setUpParentScene(){
        //set the alpha of all Scene Entities to 75%    So the User know that he can't click on the other Scene Entities.
        for(int childCounter = 0; childCounter<parentScene.getChildCount(); childCounter++){
            IEntity s = parentScene.getChildByIndex(childCounter);
            s.setAlpha(0.75f);
        }
        //next we have to unregister all Touch-areas to get sure that the user can't interact with the scene.
        for(ITouchArea touchArea : parentScene.getTouchAreas()) parentScene.unregisterTouchArea(touchArea);
        //now we cleaned the parent scene. next we have to add new entities to the scene.

        //after setup the cameraSprite we can add a button to the scene.
        setUpButton();
    }
    private void setUpButton(){
        makePhoto = new ButtonSprite(CAMERA_WIDTH/2,CAMERA_HEIGHT/8, null, ResourcesManager.getInstance().vbom, oLS);//TODO please put a simple texture into constructor for making a picture
        Text childText = new Text(makePhoto.getWidth()/2, makePhoto.getHeight(), null, "Make\nFoto", this.getVertexBufferObjectManager()); //TODO please create the standard font from the device! you get the font from the font-factory
        makePhoto.attachChild(childText);
        parentScene.registerTouchArea(makePhoto);
        parentScene.attachChild(makePhoto);
    }
    public void onClick(){
        //TODO this method must called when the makePhoto Button is pressed!!!
    }
}
