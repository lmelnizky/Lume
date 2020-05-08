package org.lume.CameraProfile;

import android.view.View;
import android.view.ViewGroup;

import org.lume.entity.sprite.Sprite;
import org.lume.manager.ResourcesManager;

import static org.lume.GameActivity.CAMERA_HEIGHT;
import static org.lume.GameActivity.CAMERA_WIDTH;

public class CameraLumeSprite extends Sprite {
    //variables

    //constructor
    public CameraLumeSprite() {
        super(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, null, ResourcesManager.getInstance().vbom);
        View mBackgroundView = new View(ResourcesManager.getInstance().activity);
        ResourcesManager.getInstance().activity.addContentView(mBackgroundView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
    }
}
