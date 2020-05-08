package org.lume.scene.OnlineScenes.ServerScene.Game.Creator;

import org.json.JSONException;
import org.json.JSONObject;
import org.lume.entity.sprite.Sprite;

public class PutStoneCreator extends Creator {
    private int xPos, yPos;
    private String playerId;

    public PutStoneCreator(String room, int posX, int posY, String playerId) {
        super(room);
        this.playerId = playerId;
        this.xPos = posX;
        this.yPos = posY;
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
        o.put("x", xPos);
        o.put("y", yPos);}
        catch (JSONException e){e.printStackTrace();}
        return o;
    }
    public static PutStoneCreator getCreatorFromJson(JSONObject o){
        PutStoneCreator c = null;
        try{
            new PutBombCreator(
                    o.getString("room"),
                    o.getInt("x"),
                    o.getInt("y"),
                    o.getString("ID")
            );
        }catch (JSONException e){e.printStackTrace();}

        return c;
    }
}
