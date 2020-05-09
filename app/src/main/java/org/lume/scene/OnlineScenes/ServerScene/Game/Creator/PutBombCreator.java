package org.lume.scene.OnlineScenes.ServerScene.Game.Creator;



import org.json.JSONException;
import org.json.JSONObject;
import org.lume.engine.camera.BoundCamera;
import org.lume.engine.handler.timer.ITimerCallback;
import org.lume.engine.handler.timer.TimerHandler;
import org.lume.entity.sprite.Sprite;
import org.lume.manager.ResourcesManager;
import org.lume.scene.OnlineScenes.ServerScene.Game.MultiplayerGameScene;

public class PutBombCreator extends Creator {

    private int xPosBomb, yPosBomb;
    private float sideLength = ResourcesManager.getInstance().sideLength;
    private String playerId;

    private BoundCamera camera = ResourcesManager.getInstance().camera;
    private ResourcesManager resourcesManager = ResourcesManager.getInstance();
    private MultiplayerGameScene scene = MultiplayerGameScene.getInstance();

    public PutBombCreator(String room, int xPos, int yPos, String playerId) {
        super(room);
        this.playerId = playerId;
        this.xPosBomb = xPos;
        this.yPosBomb = yPos;
    }

    @Override
    public Sprite createSprite() {
        if (scene.bombLaid) { //put bomb on swipe
            scene.bombLaid = false;
            scene.lumeCanBomb = false;
            scene.bombing = true;
            scene.bombSprite.setPosition(camera.getCenterX() - sideLength + ((xPosBomb - 1) * sideLength),
                    camera.getCenterY() - sideLength + ((yPosBomb - 1) * sideLength));

            scene.registerUpdateHandler(new TimerHandler(0.5f, false, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
//                    scene.bombSprite.detachSelf();
//                    scene.bombSprite.dispose();
//                    scene.bombSprite = null;
                    scene.bombSprite.setVisible(false);
                    scene.createRedBomb(xPosBomb, yPosBomb);
                }
            }));
            scene.xPosBomb = xPosBomb;
            scene.yPosBomb = yPosBomb;
        } else { //lay bomb first time on tap
            if (playerId.equals(scene.localPlayer.getId())) {
                scene.bombLaid = true;
                scene.bombScoreLume = 0;
                //scene.lumeBomb = new Sprite(camera.getCenterX() - sideLength * 3, camera.getHeight() - sideLength / 2,
                        //sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.bomb_sign00_region, resourcesManager.vbom);
//                resourcesManager.engine.runOnUpdateThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //scene.removeLumeBomb();
//                        //scene.gameHUD.attachChild(scene.lumeBomb);
//                    }
//                });
            } else {
                scene.bombLaid = true;
                scene.grumeCanBomb = false;
                scene.bombScoreGrume = 0;
                //scene.grumeBomb = new Sprite(camera.getCenterX() - sideLength * 3, camera.getHeight() - sideLength / 2,
                        //sideLength * 3 / 4, sideLength * 3 / 4, resourcesManager.bomb_sign00_region, resourcesManager.vbom);
//                resourcesManager.engine.runOnUpdateThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //scene.removeGrumeBomb();
//                        //scene.gameHUD.attachChild(scene.grumeBomb);
//                    }
//                });
            }

            if (scene.bombSprite == null) {
                scene.bombSprite = new Sprite(camera.getCenterX() - sideLength + ((xPosBomb - 1) * sideLength), camera.getCenterY() - sideLength + ((yPosBomb - 1) * sideLength),
                        sideLength * 3/4, sideLength * 3/4, resourcesManager.bomb_normal_region, resourcesManager.vbom);
                scene.attachChild(scene.bombSprite);
            } else {
                scene.bombSprite.setPosition(camera.getCenterX() - sideLength + ((xPosBomb - 1) * sideLength), camera.getCenterY() - sideLength + ((yPosBomb - 1) * sideLength));
                scene.bombSprite.setVisible(true);
            }

            //scene.attachChild(scene.bombSprite);

            scene.xPosBomb = xPosBomb;
            scene.yPosBomb = yPosBomb;
        }

        return scene.bombSprite;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject o = new JSONObject();
        try{o.put("room", room);
        o.put("ID", playerId);
        o.put("xBomb", xPosBomb);
        o.put("yBomb",yPosBomb);}
        catch (JSONException e){e.printStackTrace();}
        return o;
    }
    public static PutBombCreator getCreatorFromJson(JSONObject o){
        PutBombCreator c = null;
        try{
            c = new PutBombCreator(
                    o.getString("room"),
                    o.getInt("xBomb"),
                    o.getInt("yBomb"),
                    o.getString("ID")
                    );
        }catch (JSONException e){e.printStackTrace();}

        return c;
    }
}
