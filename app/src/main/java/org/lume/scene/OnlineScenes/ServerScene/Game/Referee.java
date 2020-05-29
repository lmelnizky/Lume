package org.lume.scene.OnlineScenes.ServerScene.Game;

import android.util.Log;

import org.lume.engine.handler.IUpdateHandler;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.BallCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.CoinCreator;
import org.lume.scene.OnlineScenes.ServerScene.Player;

import java.util.Date;

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
                createStones();
            }

            @Override
            public void reset() {

            }
        });
        //coinCheck();
    }

    public void createStones() {
        long age;
        long interval = 5000;
        age = (new Date()).getTime() - scene.stoneTime;
        if (scene.firstStonesInLevel) interval = 6000;
        if (age >= interval) {

            if (scene.firstStonesInLevel) scene.getMultiplayer().getServer().emit(createCoin()); //create first coin
            scene.firstStonesInLevel = false;
            scene.variant = scene.randomGenerator.nextInt(3) + 1;
            //showStonesToScreen(scene.variant);
            //showStonesToScreen(scene.variant);
            //TODO test
            scene.stoneTime = new Date().getTime();
        }
    }

    public void showStonesToScreen(int variant) {
        short direction, direction2, randomRow, row2;
        switch (variant) {
            case 1:
                direction = (short) (scene.randomGenerator.nextInt(2) + 1); //1 or 2
                direction2 = (short) (direction+2);
                randomRow = (short) (scene.randomGenerator.nextInt(3));
                row2 = (short) (randomRow+1);
                if (row2 > 2) row2 = 0;
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),true, direction, randomRow));
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),true, direction2, randomRow));

                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),false, direction, row2));
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),false, direction2, row2));
                break;
            case 2:
                direction = (short) (scene.randomGenerator.nextInt(4) + 1); //1 - 4
                direction2 = (short) (direction+1);
                if (direction2 > 4) direction2 = 1;
                randomRow = (short) (scene.randomGenerator.nextInt(2)*2);//0 or 2
                row2 = (short) (scene.randomGenerator.nextInt(2)*2);//0 or 2
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),true, direction, randomRow));
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),true, direction2, row2));
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),false, direction2, (short)1));
                break;
            case 3:
                direction = (short) (scene.randomGenerator.nextInt(2) + 1); //1 or 2
                direction2 = (short) (direction+2);
                randomRow = (short) (scene.randomGenerator.nextInt(3));//0 or 2
                row2 = (short) (randomRow+1);
                if (row2 > 2) row2 = 0;
                short row3 = (short) (row2+1);
                if (row3 > 2) row3 = 0;
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),true, direction, randomRow));
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),true, direction, row2));
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),true, direction2, randomRow));
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),true, direction2, row2));
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),false, direction, row3));
                scene.getMultiplayer().getServer().emit(new BallCreator(scene.getMultiplayer().getRoom(),false, direction2, row3));
                break;
        }
    }

    public void playerMoved() {

    }

    public void coinCheck() {
        for (Player p : scene.getMultiplayer().getPlayers()) {
            int xPosPlayer, yPosPlayer;
            xPosPlayer = (int) p.getCurrentPosition().x;
            yPosPlayer = (int) p.getCurrentPosition().y;
            if (xPosPlayer == scene.xPosCoin && yPosPlayer == scene.yPosCoin) {
                scene.getMultiplayer().getServer().emit(createCoin());
            }
        }
    }

    public CoinCreator createCoin() {
        Log.i("Referee", "Start createCoin()");
        boolean oasch = false;
        do {
            oasch = false;
            scene.xPosCoin = scene.randomGenerator.nextInt(3) + 1;
            scene.yPosCoin = scene.randomGenerator.nextInt(3) + 1;
            for (Player p : scene.getMultiplayer().getPlayers()) {
                if (scene.xPosCoin == p.getCurrentPosition().x && scene.yPosCoin == p.getCurrentPosition().y) oasch = true;
            }
        } while (oasch);
        Log.i("Referee", "before return, new values: " + String.valueOf(scene.xPosCoin) + ", " + String.valueOf(scene.yPosCoin));

        return new CoinCreator(scene.getMultiplayer().getRoom(),scene.xPosCoin, scene.yPosCoin);
    }
}
