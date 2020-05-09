package org.lume.scene.OnlineScenes.ServerScene.Game.Creator;

import com.badlogic.gdx.math.Vector2;

import org.lume.entity.sprite.Sprite;
import org.lume.manager.ResourcesManager;
import org.lume.scene.OnlineScenes.ServerScene.Game.MultiplayerGameScene;
import org.lume.scene.OnlineScenes.ServerScene.Player;
import org.json.JSONException;
import org.json.JSONObject;

public class MoveCreator extends Creator {
    //variables for JSON
    private boolean isLume;
    private int xPosPlayer, yPosPlayer;
    public char direction;
    public String movedPlayersID;
    //constants
    private final float sideLength =  ResourcesManager.getInstance().sideLength;
    //returnValue
    private Sprite playerSprite;
    private MultiplayerGameScene scene = MultiplayerGameScene.getInstance();
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
            if (player.getId().equals(movedPlayersID)) {
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

                player.updatePosition(new Vector2(xPosPlayer, yPosPlayer));
                scene.coinCheck();
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
            returnValue.put("direction", String.valueOf(direction));
        }catch(JSONException e){ e.printStackTrace();}
        return returnValue; //TODO Martin Melnizky
    }
    public static MoveCreator getCreatorFromJson(JSONObject o){
        try{
            char s = o.getString("direction").charAt(0);
            System.out.println(s);
        }catch (JSONException e){ e.printStackTrace();}
        try{return new MoveCreator(o.getString("room"), o.getString("direction").charAt(0), o.getString("movedPlayer"));} catch(JSONException e){e.printStackTrace();}
        return null;
    }
}
