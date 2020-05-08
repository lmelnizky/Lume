package org.lume.scene.OnlineScenes.ServerScene.Game.Creator;

import org.json.JSONException;
import org.json.JSONObject;
import org.lume.entity.sprite.Sprite;

public class LoseLifeCreator extends Creator {
    private String playerId;

    public LoseLifeCreator(String room, String playerId) {
        super(room);
        this.playerId = playerId;
    }

    @Override
    public Sprite createSprite() {
        return null;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room", room);
            jsonObject.put("playerId", playerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static LoseLifeCreator getCreatorFromJSON(JSONObject jsonObject) {
        try {
            return new LoseLifeCreator(jsonObject.getString("room"), jsonObject.getString("playerId"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getPlayerIdFromJSON(JSONObject jsonObject) {
        try {
            return jsonObject.getString("playerId");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
