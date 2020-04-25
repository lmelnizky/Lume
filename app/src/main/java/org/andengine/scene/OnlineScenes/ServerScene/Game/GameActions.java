package org.andengine.scene.OnlineScenes.ServerScene.Game;

import com.badlogic.gdx.math.Vector2;

import org.andengine.object.Ball;
import org.andengine.scene.OnlineScenes.ServerScene.Player;

public interface GameActions {
    void startGame(Player opponent);
    void playerMoved(Vector2 newPosition, Player player);
    void loadBall(Ball ball); //TODO is this the right class? for stones, ect.?
    void loadCoin(Object Coin); //TODO is there any class for coin?
    void opponentDisconnected();
    void youDisconnected();
}
