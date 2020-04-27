package org.andengine.scene.OnlineScenes.ServerScene.Game;

import com.badlogic.gdx.math.Vector2;

import org.andengine.object.Ball;
import org.andengine.scene.OnlineScenes.ServerScene.Player;

//TODO write methods for server-events!
//this is the class, where you change the Multiplayer Scene in Runtime with the serverEvents.
public class LumeGameActions implements GameActions {
    //variables
        //scene
    private MultiplayerGameScene scene = MultiplayerGameScene.getInstance(); // to work with the scene in the methods to add sprites or stuff like that
        //static

        //public

        //private

    //methods
        //constructor
    public LumeGameActions(){}

        //private methods

        //public methods

        //static methods

        //overwritten methods from superclass

        //overwritten methods from interface(s)
    @Override
    public void startGame(String[] opponentsIDs, String refereeID) {
        //method is called when both player's connected to the server.
    }
    @Override
    public void playerMoved(Vector2 newPosition, Player player) {
        for (Player p : scene.getMultiplayer().getPlayers()) {
            if (p.getId().equals(player.getId())) {
                p.getSprite().setPosition(newPosition.x, newPosition.y);
            }
        }
        //method is called when a player moved.
    }
    @Override
    public void loadBall(Ball ball) {
        //method is called when the referee uploaded a new Ball
    }
    @Override
    public void loadCoin(Object coin) {
        //is called when the referee uploaded a new Coin
    }
    @Override
    public void opponentDisconnected() {
        //opponent lost connection to the server
    }

    @Override
    public void youDisconnected() {
        //user lost connection
    }
    //inner classes
        //public classes

        //enums

}
