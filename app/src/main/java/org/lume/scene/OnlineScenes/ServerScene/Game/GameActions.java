package org.lume.scene.OnlineScenes.ServerScene.Game;

import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.BallCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.CannonCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.CoinCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.MoveCreator;

public interface GameActions {
    void createdGame(String[] opponentsID, String refereeID);
    void playerMoved(MoveCreator moveCreator);
    void loadBall(BallCreator ballCreator); //TODO is this the right class? for stones, ect.?
    void loadCoin(CoinCreator coinCreator);
    void loadCanon(CannonCreator creator);
    void lostLife(String playerID);
    //TODO is there any class for coin?
    void opponentDisconnected();
    void youDisconnected();
    void startGame();
}
