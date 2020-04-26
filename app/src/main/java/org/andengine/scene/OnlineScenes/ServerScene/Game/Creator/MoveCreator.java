package org.andengine.scene.OnlineScenes.ServerScene.Game.Creator;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.manager.ResourcesManager;
import org.json.JSONObject;

public class MoveCreator extends Creator {
    private boolean isLume;
    private int xPosPlayer, yPosPlayer;
    private int xPosOpponent, yPosOpponent;
    private float sideLength;
    private char direction;

    private Sprite playerSprite;

    public MoveCreator(String toPlayerID) {
        super(toPlayerID);
    }

    public MoveCreator(String toPlayerID, char direction) {
        super(toPlayerID);
        this.direction = direction;
        sideLength = ResourcesManager.getInstance().sideLength;
    }



    //call setPlayer and Opponent positions before calling this
    //call setSprite before calling this
    //call coinCheck() after this
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

    public void setPlayerSprite(Sprite playerSprite) {
        this.playerSprite = playerSprite;
    }

    public void setxPosPlayer(int x) {
        this.xPosPlayer = x;
    }

    public void setyPosPlayer(int y) {
        this.yPosPlayer = y;
    }

    public void setxPosOpponent(int x) {
        this.xPosOpponent = x;
    }

    public void setyPosOpponent(int y) {
        this.yPosOpponent = y;
    }

    @Override
    public JSONObject getJSON() {
        return null;
    }
}
