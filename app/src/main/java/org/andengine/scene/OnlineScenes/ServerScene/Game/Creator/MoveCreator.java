package org.andengine.scene.OnlineScenes.ServerScene.Game.Creator;

import org.andengine.entity.sprite.Sprite;
import org.json.JSONObject;

public class MoveCreator extends Creator {
    public MoveCreator(String toPlayerID) {
        super(toPlayerID);
    }

    @Override
    public Sprite createSprite() {
        return null;
    }

    @Override
    public JSONObject getJSON() {
        return null;
    }
}
