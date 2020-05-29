package org.lume.scene.OnlineScenes.ServerScene.Game.Creator;

import android.util.Log;

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
    @Override
    public Sprite createSprite() {
        Log.i("MoveCreator", "createSprite Start");
        for (Player player : MultiplayerGameScene.getInstance().getMultiplayer().getPlayers()) {
            if (player.getId().equals(movedPlayersID)) {
                playerSprite = player.getSprite();
                xPosPlayer = (int) player.getCurrentPosition().x;
                yPosPlayer = (int) player.getCurrentPosition().y;

                switch (direction) {
                    case 'R':
                        if (player.getCurrentPosition().x < 3 &&
                                (player.getCurrentPosition().x + 1 != scene.opponentPlayer.getCurrentPosition().x || player.getCurrentPosition().y != scene.opponentPlayer.getCurrentPosition().y) &&
                                (player.getCurrentPosition().x + 1 != scene.xPosBomb || player.getCurrentPosition().y != scene.yPosBomb)) {
                            xPosPlayer++;
                            playerSprite.setPosition(playerSprite.getX() + sideLength, playerSprite.getY());
                        }
                        break;
                    case 'L':
                        if (player.getCurrentPosition().x > 1 &&
                                (player.getCurrentPosition().x - 1 != scene.opponentPlayer.getCurrentPosition().x || player.getCurrentPosition().y != scene.opponentPlayer.getCurrentPosition().y) &&
                                (player.getCurrentPosition().x - 1 != scene.xPosBomb || player.getCurrentPosition().y != scene.yPosBomb)) {
                            xPosPlayer--;
                            playerSprite.setPosition(playerSprite.getX() - sideLength, playerSprite.getY());
                        }
                        break;
                    case 'D':
                        if (player.getCurrentPosition().y > 1 &&
                                (player.getCurrentPosition().x != scene.opponentPlayer.getCurrentPosition().x || player.getCurrentPosition().y - 1 != scene.opponentPlayer.getCurrentPosition().y) &&
                                (player.getCurrentPosition().x != scene.xPosBomb || player.getCurrentPosition().y - 1 != scene.yPosBomb)) {
                            yPosPlayer--;
                            playerSprite.setPosition(playerSprite.getX(), playerSprite.getY() - sideLength);
                        }
                        break;
                    case 'U':
                        if (player.getCurrentPosition().y < 3 &&
                                (player.getCurrentPosition().x != scene.opponentPlayer.getCurrentPosition().x || player.getCurrentPosition().y + 1 != scene.opponentPlayer.getCurrentPosition().y) &&
                                (player.getCurrentPosition().x != scene.xPosBomb || player.getCurrentPosition().y + 1 != scene.yPosBomb)) {
                            yPosPlayer++;
                            playerSprite.setPosition(playerSprite.getX(), playerSprite.getY() + sideLength);
                        }
                        break;
                }

                this.setxPosPlayer(xPosPlayer);
                this.setyPosPlayer(yPosPlayer);

                player.updatePosition(new Vector2(xPosPlayer, yPosPlayer));
                if(movedPlayersID.equals(scene.localPlayer.getId())) {
                    scene.xPosLocal = xPosPlayer;
                    scene.yPosLocal = yPosPlayer;
                } else {
                    scene.xPosOpponent = xPosPlayer;
                    scene.yPosOpponent = yPosPlayer;
                }
                Log.i("MoveCreator", "before scene coincheck");
                scene.coinCheck();
                Log.i("MoveCreator", "after scene coincheck");
            }
        }
        System.out.println("ist der Gegener ausgeblendet?" + !playerSprite.isVisible());
        System.out.println("ist der Gegener ausgeblendet?" + !playerSprite.hasParent());
        System.out.println("    x:  " + playerSprite.getX() + " y:  " + playerSprite.getY());

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
