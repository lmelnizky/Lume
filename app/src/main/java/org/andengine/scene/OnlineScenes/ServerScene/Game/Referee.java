package org.andengine.scene.OnlineScenes.ServerScene.Game;

import org.andengine.manager.ResourcesManager;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.CoinCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.MoveCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Player;

import java.util.Random;

public class Referee {
    MultiplayerGameScene scene = MultiplayerGameScene.getInstance();

    //constructor
    public Referee(){

    }

    public void playerMoved() {

    }

    private void coinCheck() {
        for (Player p : scene.getMultiplayer().getPlayers()) {
            int xPosPlayer, yPosPlayer;
            xPosPlayer = (int) p.getCurrentPosition().x;
            yPosPlayer = (int) p.getCurrentPosition().y;
            if (xPosPlayer == scene.xPosCoin && yPosPlayer == scene.yPosCoin) {
                scene.getMultiplayer().getServer().emit(createCoin());
            }
        }
    }

    private CoinCreator createCoin() {
        boolean oasch = false;
        do {
            scene.xPosCoin = scene.randomGenerator.nextInt(3) + 1;
            scene.yPosCoin = scene.randomGenerator.nextInt(3) + 1;
            for (Player p : scene.getMultiplayer().getPlayers()) {
                if (scene.xPosCoin == p.getCurrentPosition().x && scene.yPosCoin == p.getCurrentPosition().y) oasch = true;
            }
        } while (oasch);

        return new CoinCreator(scene.getMultiplayer().getRoom(),scene.xPosCoin, scene.yPosCoin);
    }
}
