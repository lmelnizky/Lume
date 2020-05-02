package org.andengine.scene.OnlineScenes.ServerScene.Game;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.manager.ResourcesManager;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.CoinCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.MoveCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Player;
import org.andengine.util.adt.color.Color;

import java.util.Random;

public class Referee {
    MultiplayerGameScene scene = MultiplayerGameScene.getInstance();

    //constructor
    public Referee(){
        create();
    }
    private void create(){
        scene.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (!waitingForStonesToDisappear && !worldFinished) {
                    createStones(level);
                    time -= (slowMotion) ? 0.3f : 1f;
                    int displayTime = (int) Math.round(time/60);
                    timeText.setText(String.valueOf(displayTime));
                    if (time <= 0 && !gameOverDisplayed) {
                        displayGameOverScene();
                    }
                    if (displayTime <= 5) {
                        timeText.setColor(Color.RED);
                    }
                }
            }

            @Override
            public void reset() {

            }
        });
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
