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
    //fields for createSprite
    private int xPosLume, yPosLume, xPosGrume, yPosGrume;
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

    //creates the coinSprite or changes its position
    //checks if it is the same as position of the players
    //call set xPosLume/Grume before createSprite
    @Override
    public Sprite createSprite() {
        Random randomGenerator = new Random();
        camera = ResourcesManager.getInstance().camera;
        VertexBufferObjectManager vbom = ResourcesManager.getInstance().vbom;

        do {
            xPosCoin = randomGenerator.nextInt(3) + 1; //TODO ist das nicht die Aufgabe des referees eine Zufällige Münze zu erzeugen? Also du bekommst dann die px und py des coins vom server, weil der referee die Daten hochgeladen hat
            yPosCoin = randomGenerator.nextInt(3) + 1;
        } while ((xPosCoin == xPosLume && yPosCoin == yPosLume) || (xPosCoin == xPosGrume && yPosCoin == yPosGrume));
        if (coinSprite == null) {
            coinSprite = new Sprite(camera.getCenterX() - sideLength + ((xPosCoin - 1) * sideLength),
                    camera.getCenterY() - sideLength + ((yPosCoin - 1) * sideLength),
                    sideLength * 7 / 8, sideLength * 7 / 8, ResourcesManager.getInstance().coin_region, vbom);
        } else {
            coinSprite.registerEntityModifier(new ScaleModifier(0.2f,0.7f,1f));
            coinSprite.setPosition(camera.getCenterX() - sideLength + ((xPosCoin - 1) * sideLength),
                    camera.getCenterY() - sideLength + ((yPosCoin - 1) * sideLength));
        }
        return coinSprite; //TODO Lukas Melnizky
    }
    //setter
    public void setxPosLume(int xPosLume) {this.xPosLume = xPosLume;}
    public void setyPosLume(int yPosLume) {this.yPosLume = yPosLume;}
    public void setxPosGrume(int xPosGrume) {this.xPosGrume = xPosGrume;}
    public void setyPosGrume(int yPosGrume) {this.yPosGrume = yPosGrume;}

    @Override
    public JSONObject getJSON() {
        return null; //TODO Martin Melnizky
    }
}
