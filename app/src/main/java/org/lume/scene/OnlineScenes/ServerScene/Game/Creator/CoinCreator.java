package org.lume.scene.OnlineScenes.ServerScene.Game.Creator;

import org.lume.engine.camera.BoundCamera;
import org.lume.entity.modifier.ScaleModifier;
import org.lume.entity.sprite.Sprite;
import org.lume.manager.ResourcesManager;
import org.lume.opengl.vbo.VertexBufferObjectManager;
import org.lume.scene.OnlineScenes.ServerScene.Game.MultiplayerGameScene;
import org.json.JSONException;
import org.json.JSONObject;

public class CoinCreator extends Creator {
    //variables
    //variables for json
    private int xPosCoin, yPosCoin;
    //constants
    private final float sideLength =  ResourcesManager.getInstance().sideLength;
    //base stuff
    private BoundCamera camera;
    private Sprite coinSprite = MultiplayerGameScene.getInstance().coinSprite;
    //constructor
    public CoinCreator(String room, int xPosCoin, int yPosCoin) {
        super(room);
        this.xPosCoin = xPosCoin;
        this.yPosCoin = yPosCoin;
    }

    //call set xPosLume/Grume before createSprite
    @Override
    public Sprite createSprite() {
        camera = ResourcesManager.getInstance().camera;
        VertexBufferObjectManager vbom = ResourcesManager.getInstance().vbom;

        if (coinSprite == null) {
            coinSprite = new Sprite(camera.getCenterX() - sideLength + ((xPosCoin - 1) * sideLength),
                    camera.getCenterY() - sideLength + ((yPosCoin - 1) * sideLength),
                    sideLength * 7 / 8, sideLength * 7 / 8, ResourcesManager.getInstance().coin_region, vbom);
        } else {
            coinSprite.registerEntityModifier(new ScaleModifier(0.2f,0.7f,1f));
            coinSprite.setPosition(camera.getCenterX() - sideLength + ((xPosCoin - 1) * sideLength), camera.getCenterY() - sideLength + ((yPosCoin - 1) * sideLength));
        }


        return coinSprite; //TODO Lukas Melnizky
    }

    @Override
    public JSONObject getJSON() {
        JSONObject returnValue = new JSONObject();
        try {
            returnValue.put("room", room);
            returnValue.put("px", xPosCoin);
            returnValue.put("py", yPosCoin);
        }catch(JSONException e){ e.printStackTrace();}
        return returnValue; //TODO Martin Melnizky
    }
    public static CoinCreator getCreatorFromJson(JSONObject o){
        try{return new CoinCreator( o.getString("room"), o.getInt("px"), o.getInt("py"));} catch(JSONException e){e.printStackTrace();}
        return null;
    }
}
