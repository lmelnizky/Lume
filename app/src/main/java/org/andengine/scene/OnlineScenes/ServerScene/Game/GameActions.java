package org.andengine.scene.OnlineScenes.ServerScene.Game;

import com.badlogic.gdx.math.Vector2;

import org.andengine.object.Ball;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.BallCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.CoinCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.MoveCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Player;

public interface GameActions {
    void createdGame(String[] opponentsID, String refereeID);
    void playerMoved(MoveCreator moveCreator);
    void loadBall(BallCreator ballCreator); //TODO is this the right class? for stones, ect.?
    void loadCoin(CoinCreator coinCreator); //TODO is there any class for coin?
    void opponentDisconnected();
    void youDisconnected();
    void startGame();
}
