package org.andengine.scene.OnlineScenes.ServerScene.Game;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;

import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.object.Ball;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.BallCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.CannonCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.CoinCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.Creator;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.MoveCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Player;

//TODO write methods for server-events!
//this is the class, where you change the Multiplayer Scene in Runtime with the serverEvents.
public class LumeGameActions implements GameActions {
    //variables
        //scene
    private MultiplayerGameScene scene; // to work with the scene in the methods to add sprites or stuff like that
        //static

        //public

        //private
    private String[] ids;
    private String referee;
    //methods
        //constructor
    public LumeGameActions(){}

        //private methods

        //public methods

        //static methods

        //overwritten methods from superclass

        //overwritten methods from interface(s)
    @Override
    public void createdGame(String[] opponentsIDs, String refereeID) {
        Log.i("LumaGameActions", "createdGame");
        ids = opponentsIDs;
        referee = refereeID;
        //method is called when both player's connected to the server.
    }
    @Override
    public void playerMoved(MoveCreator creator) {
        Log.i("LumaGameActions", "playerMoved");
        creator.createSprite();
        //method is called when a player moved.
        if (scene.referee != null) {
            scene.referee.playerMoved();
        }
    }
    @Override
    public void loadBall(BallCreator creator) {
        Log.i("LumaGameActions", "new Ball");
        scene.attachChild(creator.createSprite());
        //method is called when the referee uploaded a new Ball
    }
    @Override
    public void loadCoin(CoinCreator creator) {
        Log.i("LumaGameActions", "loadCoin");
        creator.createSprite();
        //is called when the referee uploaded a new Coin
    }

    @Override
    public void loadCanon(CannonCreator creator) {
        creator.createSprite();
    }

    @Override
    public void lostLife(String playerID) {
        Log.i("LoseLife", "LoseLife");
        for (Player p : scene.getMultiplayer().getPlayers()) if (p.getId().equals(playerID)) {
            if(p.getId().equals(scene.localPlayer.getId())) {
                scene.lumeLives--;
                switch (scene.lumeLives) {
                    case 0:
                        //TODO Game ends!
                        break;
                    case 1:
                        scene.lumeHeart3.setVisible(false); scene.lumeHeart2.setVisible(false); scene.lumeHeart1.setVisible(true);
                        break;
                    case 2:
                        scene.lumeHeart3.setVisible(false); scene.lumeHeart2.setVisible(true); scene.lumeHeart1.setVisible(true);
                        break;
                    case 3:
                        scene.lumeHeart3.setVisible(true); scene.lumeHeart2.setVisible(true); scene.lumeHeart1.setVisible(true);
                        break;
                }
            }
            else{
                scene.grumeLives--;
                switch (scene.grumeLives) {
                    case 0:
                        //TODO Game ends!
                        break;
                    case 1:
                        scene.grumeHeart3.setVisible(false); scene.grumeHeart2.setVisible(false); scene.grumeHeart1.setVisible(true);
                        break;
                    case 2:
                        scene.grumeHeart3.setVisible(false); scene.grumeHeart2.setVisible(true); scene.grumeHeart1.setVisible(true);
                        break;
                    case 3:
                        scene.grumeHeart3.setVisible(true); scene.grumeHeart2.setVisible(true); scene.grumeHeart1.setVisible(true);
                        break;
                }
            }
        }
    }

    @Override
    public void opponentDisconnected() {
        Log.i("LumaGameActions", "opponent Disconnected from the service");
        //opponent lost connection to the server
    }

    @Override
    public void youDisconnected() {
        Log.i("LumaGameActions", "Lost connection to the server");
        //user lost connection
    }

    @Override
    public void startGame() {
        Log.i("LumaGameActions", "startGame");
        scene = MultiplayerGameScene.getInstance();
        if(referee.equals(scene.getMultiplayer().getServer().id)){ scene.referee = new Referee(); Log.i("LumeGameActions", "I'm a referee");}
    }
    //inner classes
        //public classes

        //enums

}
