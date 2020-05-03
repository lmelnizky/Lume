package org.lume.scene.OnlineScenes.ServerScene.Game.Creator;

import org.lume.entity.sprite.Sprite;
import org.json.JSONObject;

public abstract class Creator {
    public String room;
    public Creator(String room){this.room = room;}
    //dont use this because coinCreator needs position of players as arguments
    public abstract Sprite createSprite();
    public abstract JSONObject getJSON();
}
