package org.andengine.scene.OnlineScenes.ServerScene.Game.Creator;

import com.badlogic.gdx.math.Vector2;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.manager.ResourcesManager;
import org.andengine.opengl.vbo.VertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.json.JSONObject;

import java.util.Random;

public class CoinCreator extends Creator {
    //variables
    //variables for json
    private int xPosCoin, yPosCoin;
    //constants
    private final float sideLength =  ResourcesManager.getInstance().sideLength;
    //base stuff
    private BoundCamera camera;
    private Sprite coinSprite;
    //constructor
    public CoinCreator(String toPlayerID) {
        super(toPlayerID);
    }
    public CoinCreator(String toPlayerID, int xPosCoin, int yPosCoin) {
        super(toPlayerID);
        this.xPosCoin = xPosCoin;
        this.yPosCoin = yPosCoin;
    }

    //call set xPosLume/Grume before createSprite
    @Override
    public Sprite createSprite() {
        camera = ResourcesManager.getInstance().camera;
        VertexBufferObjectManager vbom = ResourcesManager.getInstance().vbom;

            coinSprite = new Sprite(camera.getCenterX() - sideLength + ((xPosCoin - 1) * sideLength),
                    camera.getCenterY() - sideLength + ((yPosCoin - 1) * sideLength),
                    sideLength * 7 / 8, sideLength * 7 / 8, ResourcesManager.getInstance().coin_region, vbom);
        return coinSprite; //TODO Lukas Melnizky
    }

    @Override
    public JSONObject getJSON() {
        return null; //TODO Martin Melnizky
    }
}
