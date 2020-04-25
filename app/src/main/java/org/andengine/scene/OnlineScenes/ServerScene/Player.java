package org.andengine.scene.OnlineScenes.ServerScene;

import com.badlogic.gdx.math.Vector2;

import org.andengine.entity.sprite.Sprite;
import org.andengine.manager.ResourcesManager;
import org.andengine.opengl.texture.region.TextureRegion;

public class Player {
    //variables
    private Vector2 previousPosition, currentPosition;
    private Sprite sprite;
    private boolean referee;
    private String id;
    private String username;
    //constructors
    public Player(Vector2 position, TextureRegion textureRegion, boolean referee, String id, String username){
        sprite = new Sprite(position.x, position.y, textureRegion, ResourcesManager.getInstance().vbom);
        this.currentPosition = position;
        this.referee = referee;
        this.id = id;
        this.username = username;
    }
    public Player(boolean referee, String id, String username){
        this.referee = referee;
        this.id = id;
        this.username = username;
    }
    public Player(String id, String username){
        this.id = id;
        this.username = username;
    }
    private Player(){}
    //methods for overwrite
    public void update(){}
    //methods
    public void updatePosition(Vector2 newPosition){
        if(currentPosition.x == newPosition.x && currentPosition.y == newPosition.y) return;
        if(this.currentPosition != null) this.previousPosition = this.currentPosition;
        this.currentPosition = newPosition;
    }
    //getter
    public Vector2 getPreviousPosition() {return previousPosition;}
    public Vector2 getCurrentPosition() {return currentPosition;}
    public Sprite getSprite() {return sprite;}
    public boolean isReferee() {return referee;}
    public String getId() {return id;}
    public String getUsername() {return username;}
    //setter
    public void setSprite(Sprite sprite) {this.sprite = sprite;}
    public void setReferee(boolean referee) {this.referee = referee;}
}
