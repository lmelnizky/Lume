package org.lume.scene.OnlineScenes.ServerScene.Game.Creator;



import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.lume.entity.sprite.Sprite;

public class PutBombCreator extends Creator {

    private int xPosBomb, yPosBomb;
    private String playerId;

    public PutBombCreator(String room, int xPos, int yPos, String playerId) {
        super(room);
        this.playerId = playerId;
        this.xPosBomb = xPos;
        this.yPosBomb = yPos;
    }

    @Override
    public Sprite createSprite() {
        return null;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject o = new JSONObject();
        try{o.put("room", room);
        o.put("ID", playerId);
        o.put("xBomb", xPosBomb);
        o.put("yBomb",yPosBomb);}
        catch (JSONException e){e.printStackTrace();}
        return o;
    }
    public static PutBombCreator getCreatorFromJson(JSONObject o){
        PutBombCreator c = null;
        try{
            new PutBombCreator(
                    o.getString("room"),
                    o.getInt("xBomb"),
                    o.getInt("yBomb"),
                    o.getString("ID")
                    );
        }catch (JSONException e){e.printStackTrace();}

        return c;
    }
}
