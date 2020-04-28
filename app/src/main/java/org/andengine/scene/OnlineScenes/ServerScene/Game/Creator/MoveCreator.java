package org.andengine.scene.OnlineScenes.ServerScene.Game.Creator;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.manager.ResourcesManager;
import org.andengine.scene.OnlineScenes.ServerScene.Game.MultiplayerGameScene;
import org.andengine.scene.OnlineScenes.ServerScene.Player;
import org.json.JSONException;
import org.json.JSONObject;

public class MoveCreator extends Creator {
    //variables for JSON
    private boolean isLume;
    private int xPosPlayer, yPosPlayer;
    private char direction;
    private String movedPlayersID;
    //constants
    private final float sideLength =  ResourcesManager.getInstance().sideLength;
    //returnValue
    private Sprite playerSprite;
    //constructor
    public MoveCreator(String toPlayerID) {
        super(toPlayerID);
    }
    //constructor
    public MoveCreator(String room, char directionToMove, String movedPlayersID) {
        super(room);
        this.direction = directionToMove;
        this.movedPlayersID = movedPlayersID;
    }

    //call setPlayer and Opponent positions before calling this
    //call setSprite before calling this
    //call coinCheck() after this TODO (that's the job of the referee class)
    @Override
    public Sprite createSprite() {
        for (Player player : MultiplayerGameScene.getInstance().getMultiplayer().getPlayers()) {
            if (player.getId().equals(room)) {
                playerSprite = player.getSprite();
                xPosPlayer = (int) player.getCurrentPosition().x;
                yPosPlayer = (int) player.getCurrentPosition().y;

                switch (direction) {
                    case 'R':
                        xPosPlayer++;
                        playerSprite.setPosition(playerSprite.getX() + sideLength, playerSprite.getY());
                        break;
                    case 'L':
                        xPosPlayer--;
                        playerSprite.setPosition(playerSprite.getX() - sideLength, playerSprite.getY());
                        break;
                    case 'D':
                        yPosPlayer--;
                        playerSprite.setPosition(playerSprite.getX(), playerSprite.getY() - sideLength);
                        break;
                    case 'U':
                        yPosPlayer++;
                        playerSprite.setPosition(playerSprite.getX(), playerSprite.getY() + sideLength);
                        break;
                }
                this.setxPosPlayer(xPosPlayer);
                this.setyPosPlayer(yPosPlayer);
            }
        }
        return playerSprite;
    }

    //setter
    public void setPlayerSprite(Sprite playerSprite) {
        this.playerSprite = playerSprite;
    }
    public void setxPosPlayer(int x) {this.xPosPlayer = x;}
    public void setyPosPlayer(int y) {this.yPosPlayer = y;}
    public int getxPosPlayer() {
        return this.xPosPlayer;
    }
    public int getyPosPlayer() {
        return this.yPosPlayer;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject returnValue = new JSONObject();
        try {
            returnValue.put("room", room);
            returnValue.put("movedPlayer", movedPlayersID);
            returnValue.put("direction", direction);
        }catch(JSONException e){ e.printStackTrace();}
        return returnValue; //TODO Martin Melnizky
    }
    public static MoveCreator getCreatorFromJson(JSONObject o){
        try{return new MoveCreator(o.getString("room"), o.getString("direction").charAt(0), o.getString("movedPlayer"));} catch(JSONException e){e.printStackTrace();}
        return null;
    }
}