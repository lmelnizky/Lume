package org.andengine.CameraProfile;

import android.graphics.Camera;
import android.view.View;
import android.view.ViewGroup;

import org.andengine.engine.camera.CameraFactory;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.manager.ResourcesManager;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import static org.andengine.GameActivity.CAMERA_HEIGHT;
import static org.andengine.GameActivity.CAMERA_WIDTH;

public class CameraLumeSprite extends Sprite {
    //variables

    //constructor
    public CameraLumeSprite() {
        super(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, null, ResourcesManager.getInstance().vbom);
        View mBackgroundView = new View(ResourcesManager.getInstance().activity);
        ResourcesManager.getInstance().activity.addContentView(mBackgroundView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
    }
}
