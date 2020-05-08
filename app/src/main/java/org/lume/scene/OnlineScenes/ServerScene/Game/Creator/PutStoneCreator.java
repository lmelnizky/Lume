package org.lume.scene.OnlineScenes.ServerScene.Game.Creator;

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
        return null;
    }
}
