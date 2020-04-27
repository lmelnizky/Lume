package org.andengine.scene.OnlineScenes.ServerScene.Game.Creator;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.manager.ResourcesManager;
import org.json.JSONObject;

public class MoveCreator extends Creator {
    //variables for JSON
    private boolean isLume;
    private int xPosPlayer, yPosPlayer;
    private int xPosOpponent, yPosOpponent;
    private char direction;
    //constants
    private final float sideLength =  ResourcesManager.getInstance().sideLength;
    //returnValue
    private Sprite playerSprite;
    //constructor
    public MoveCreator(String toPlayerID) {
        super(toPlayerID);
    }
    //constructor
    public MoveCreator(String toPlayerID, char direction) {
        super(toPlayerID);
        this.direction = direction;
    }

    //call setPlayer and Opponent positions before calling this
    //call setSprite before calling this
    //call coinCheck() after this TODO (that's the job of the referee class)
    @Override
    public Sprite createSprite() {
        switch (direction) {
            case 'R':
                if (xPosPlayer < 3) {
                    if (xPosPlayer+1 != xPosOpponent || yPosPlayer != yPosOpponent) {
                        xPosPlayer++;
                        playerSprite.setPosition(playerSprite.getX() + sideLength, playerSprite.getY());
                    }
                }
                break;
            case 'L':
                if (xPosPlayer > 1) {
                    if (xPosPlayer-1 != xPosOpponent || yPosPlayer != yPosOpponent) {
                        xPosPlayer--;
                        playerSprite.setPosition(playerSprite.getX() - sideLength, playerSprite.getY());
                    }
                }
                break;
            case 'D':
                if (yPosPlayer > 1) {
                    if (xPosPlayer != xPosOpponent || yPosPlayer-1 != yPosOpponent) {
                        yPosPlayer--;
                        playerSprite.setPosition(playerSprite.getX(), playerSprite.getY() - sideLength);
                    }
                }
                break;
            case 'U':
                if (yPosPlayer < 3) {
                    if (xPosPlayer != xPosOpponent || yPosPlayer+1 != yPosOpponent) {
                        yPosPlayer++;
                        playerSprite.setPosition(playerSprite.getX(), playerSprite.getY() + sideLength);
                    }
                }
                break;
        }
        this.setxPosPlayer(xPosPlayer);
        this.setyPosPlayer(yPosPlayer);
        return playerSprite;
    }
    //setter
    public void setPlayerSprite(Sprite playerSprite) {
        this.playerSprite = playerSprite;
    }
    public void setxPosPlayer(int x) {this.xPosPlayer = x;}
    public void setyPosPlayer(int y) {this.yPosPlayer = y;}
    public void setxPosOpponent(int x) {this.xPosOpponent = x;}
    public void setyPosOpponent(int y) {this.yPosOpponent = y;}
    @Override
    public JSONObject getJSON() {
        return null;
    }
}
