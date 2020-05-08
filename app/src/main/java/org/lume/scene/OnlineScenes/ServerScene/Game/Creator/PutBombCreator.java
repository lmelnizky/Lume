package org.lume.scene.OnlineScenes.ServerScene.Game.Creator;

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
        return null;
    }
}
