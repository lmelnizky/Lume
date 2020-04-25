package org.andengine.scene.OnlineScenes.ServerScene.Game.Creator;

import org.andengine.entity.sprite.Sprite;
import org.json.JSONObject;

public class CoinCreator extends Creator {
    public CoinCreator(String toPlayerID) {
        super(toPlayerID);
    }

    @Override
    public Sprite createSprite() {
        //
        return null; //TODO Lukas Melnizky
    }

    @Override
    public JSONObject getJSON() {
        return null; //TODO Martin Melnizky
    }
}
