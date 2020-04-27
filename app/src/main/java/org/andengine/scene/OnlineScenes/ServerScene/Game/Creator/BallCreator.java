package org.andengine.scene.OnlineScenes.ServerScene.Game.Creator;

import org.andengine.entity.sprite.Sprite;
import org.json.JSONException;
import org.json.JSONObject;

public class BallCreator extends Creator {
    public BallCreator(String toPlayerID) {
        super(toPlayerID);
    }

    @Override
    public Sprite createSprite() {
        return null; //TODO Lukas Melnizky
    }

    @Override
    public JSONObject getJSON() {
        JSONObject returnValue = new JSONObject();
        try {
            returnValue.put("sendToID", toPlayerID);
            //put data into json
        }
        catch (JSONException e) {e.printStackTrace();}
        return returnValue; //TODO Martin Melnizky
    }
    public static Creator createCreator(JSONObject o){
        BallCreator returnValue = null;
        //TODO Martin Melnizky
        return returnValue;

    }
}
