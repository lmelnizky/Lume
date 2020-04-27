package org.andengine.scene.OnlineScenes.ServerScene.Game.Creator;

import org.andengine.entity.sprite.Sprite;
import org.json.JSONObject;

public abstract class Creator {
    //dont use this because coinCreator needs position of players as arguments
    public abstract Sprite createSprite();
    public abstract JSONObject getJSON();
}
