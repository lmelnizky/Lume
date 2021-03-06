package org.lume.scene.OnlineScenes.ServerScene.Game;

import android.util.Log;

import org.lume.entity.sprite.Sprite;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.BallCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.CannonCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.CoinCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.MoveCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.PutBombCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.PutStoneCreator;

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
        Sprite s = creator.createSprite();
        //if (!s.hasParent()) scene.attachChild(s);
        //attachChild is said in CoinCreator
    }

    @Override
    public void loadCanon(CannonCreator creator) {
        creator.createSprite();
    }

    @Override
    public void lostLife(String playerID) {
        scene.loseLife(playerID);
    }

    @Override
    public void putBomb(PutBombCreator creator) {
        Sprite s = creator.createSprite();
        if(!s.hasParent()) scene.attachChild(s);
    }

    @Override
    public void putStone(PutStoneCreator creator) {

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
