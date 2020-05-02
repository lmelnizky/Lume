package org.andengine.scene.skillscenes;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.base.SkillScene;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.TickerText;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.input.touch.TouchEvent;
import org.andengine.object.Ball;
import org.andengine.object.Circle;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import java.util.Date;

public class Skill21 extends SkillScene {
    public Skill21() {
        super(5);
    }

    @Override
    public void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world0_region, vbom));
        this.setBackground(spriteBackground);
    }

    @Override
    public void createHalves() {

        shootLeft = new Rectangle(camera.getCenterX() - resourcesManager.screenWidth / 4, camera.getCenterY(),
                resourcesManager.screenWidth / 2, resourcesManager.screenHeight, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    shootX1 = x;
                    shootY1 = y;
                    return true;
                } else if (touchEvent.isActionUp()) {
                    shootX2 = x;
                    shootY2 = y;
                    float deltaX = shootX2 - shootX1;
                    float deltaY = shootY2 - shootY1;
                    //checks if it was a swipe
                    if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE || Math.abs(deltaY) > SWIPE_MIN_DISTANCE) {
                        //checks if it was horizontal or vertical
                        if (Math.abs(deltaX) > Math.abs(deltaY)) { //horizontal swipe
                            if (deltaX > 0) { //left to right
                                createCannonball(4);
                            } else { //right to left
                                createCannonball(2);
                            }
                        } else { //vertical swipe
                            if (deltaY > 0) { //up to down
                                createCannonball(3);
                            } else { //down to up
                                createCannonball(1);
                            }
                        }
                    }


                    return true;
                } else {
                    return false;
                }
            }

            ;
        };

        swipeRight = new Rectangle(camera.getCenterX() + resourcesManager.screenWidth / 4, camera.getCenterY(),
                resourcesManager.screenWidth / 2, resourcesManager.screenHeight, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    swipeX1 = x;
                    swipeY1 = y;
                    return true;
                } else if (touchEvent.isActionUp()) {
                    swipeX2 = x;
                    swipeY2 = y;
                    float deltaX = swipeX2 - swipeX1;
                    float deltaY = swipeY2 - swipeY1;
                    //checks if it was a swipe and not a tap
                    if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE || Math.abs(deltaY) > SWIPE_MIN_DISTANCE) {
                        //horizontal or vertical check
                        if (Math.abs(deltaX) > Math.abs(deltaY)) { //horizontal
                            if (deltaX > 0) { //left to right
                                movePlayer('R');
                            } else { //right to left
                                movePlayer('L');
                            }
                        } else { //vertical
                            if (deltaY > 0) { //up to down
                                movePlayer('U');
                            } else { //down to up
                                movePlayer('D');
                            }
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }

            ;
        };

        shootLeft.setColor(Color.GREEN);
        swipeRight.setColor(Color.RED);
        shootLeft.setAlpha(0.2f);
        swipeRight.setAlpha(0.2f);
        this.registerTouchArea(shootLeft);
        this.registerTouchArea(swipeRight);
        secondLayer.attachChild(shootLeft);
        secondLayer.attachChild(swipeRight);
    }

    private void createCannonball(int direction) {
        ITextureRegion textureRegion = resourcesManager.cannonball_region;
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        final Sprite cannonball;
        Ball ball;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
//        float speed = 1.4f*sideLength/5;
        float speed = (level == 4) ? resourcesManager.screenWidth*4/400 :
                resourcesManager.screenWidth*6/400;
        switch (direction) {
            case 1: //up to down
                x = lumeSprite.getX();
                y = lumeSprite.getY() - lumeSprite.getHeight()-1;
                yVel = -speed;
                break;
            case 2: //right to left
                x = lumeSprite.getX() - lumeSprite.getWidth()-1;
                y = lumeSprite.getY();
                xVel = -speed;
                break;
            case 3: //down to up
                x = lumeSprite.getX();
                y = lumeSprite.getY() + lumeSprite.getHeight()+1;
                yVel = speed;
                break;
            case 4: //left to right
                x = lumeSprite.getX() + lumeSprite.getWidth()+1;
                y = lumeSprite.getY();
                xVel = speed;
                break;
        }
        cannonball = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {

                final Circle cannonCircle, lumeCircle;
                cannonCircle = new Circle(this.getX(), this.getY(), this.getWidth()/2);
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth()/2);

                if (cannonCircle.collision(lumeCircle) && !gameOverDisplayed) {
                    displayGameOverScene();
                    score = 0;
                }

                for (Sprite crackyStone : crackyStones) { //loop for cracky stones
                    final Circle stoneCircle;
                    stoneCircle = new Circle(crackyStone.getX(), crackyStone.getY(), crackyStone.getWidth() / 2);
                    if (cannonCircle.collision(stoneCircle)) {
                        crackyStonesToRemove.add(crackyStone);
                        stonesToRemove.add(crackyStone);
                        cannonBallsToRemove.add(this);
                        final Sprite cannonBalltoRemove = this;

                        engine.runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                if (cannonBallsToRemove.size()+crackyStonesToRemove.size() > 0) {
                                    if (cannonBallsToRemove.size() > 0) {
                                        cannonBalltoRemove.detachSelf();
                                        cannonBalltoRemove.dispose();
                                    }

                                    cannonBallsToRemove.clear();
                                    for (Sprite sprite : crackyStonesToRemove) {
                                        sprite.detachSelf();
                                        sprite.dispose();
                                    }
                                    stonesToRemove.clear();
                                    crackyStonesToRemove.clear();
                                }
                            }
                        });
                    }
                }
                for (Sprite mirrorStone : mirrorStones) {
                    final Circle stoneCircle; //making this circle smaller so that ball rebounds later
                    stoneCircle = new Circle(mirrorStone.getX(), mirrorStone.getY(), mirrorStone.getWidth() / 8);

                    if (cannonCircle.collision(stoneCircle)) {
                        mirrorStonesToRemove.add(mirrorStone);
                        stonesToRemove.add(mirrorStone);

                        this.setPosition(mirrorStone.getX(), mirrorStone.getY());
                        rebound(this); //makes the cannonball rebound in the opposite direction

                        engine.runOnUpdateThread(new Runnable() { //destroys the stone
                            @Override
                            public void run() {
                                if (mirrorStonesToRemove.size() > 0) {
                                    for (Sprite sprite : mirrorStonesToRemove) {
                                        sprite.detachSelf();
                                        sprite.dispose();
                                    }
                                    stonesToRemove.clear();
                                    mirrorStonesToRemove.clear();
                                }
                            }
                        });
                    }
                }
                stones.removeAll(stonesToRemove);
                crackyStones.removeAll(crackyStonesToRemove);
                mirrorStones.removeAll(mirrorStonesToRemove);

                if (this.getX() < -sideLength || this.getY() < - sideLength ||
                        this.getX() > camera.getWidth()+sideLength || this.getY() > camera.getWidth()+sideLength) {
                    this.detachSelf();
                    this.dispose();
                }
            }
        };
        secondLayer.attachChild(cannonball);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, cannonball, BodyDef.BodyType.KinematicBody, FIXTURE_DEF);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(cannonball, body, true, false));
        ball = new Ball();
        ball.setDirection(direction);
        ball.setBody(body);
        cannonball.setUserData(ball);
    }

    public Sprite addBall(final char type, int direction, int position, float speedFactor) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/15 * speedFactor;

        final Sprite stone;
        ITextureRegion textureRegion = null;
        switch (type) {
            case 'T':
                textureRegion = resourcesManager.thorny_stone_region;
                break;
            case 'C':
                textureRegion = resourcesManager.cracky_stone_region;
                break;
            case 'M':
                textureRegion = resourcesManager.cracky_mirror;
                break;
        }
        Ball ball;

        switch (direction) {
            case 1:
                x = camera.getCenterX() - sideLength + sideLength*position;
                y = camera.getHeight() - sideLength / 2;
                yVel = -speed;
                break;
            case 2:
                x = camera.getWidth() - sideLength / 2;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = -speed;
                break;
            case 3:
                x = camera.getCenterX()-sideLength + sideLength*position;
                y = sideLength / 2;
                yVel = speed;
                break;
            case 4:
                x = sideLength / 2;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = speed;
                break;
        }

//        stoneCircle = new Circle(x, y, sideLength*3/8);
        stone = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                final Circle lumeCircle, stoneCircle;
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
                stoneCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);

                if (stoneCircle.collision(lumeCircle) && !gameOverDisplayed) {
                    displayGameOverScene();
                    score = 0;
                }
//                if (this.getX() < -sideLength || this.getY() < -sideLength ||
//                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
//                    if (!thorny) crackyStones.remove(this);
//                    this.detachSelf();
//                    this.dispose();
//                }


                if (this.getX() < -sideLength || this.getY() < -sideLength ||
                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
                    stonesToRemove.add(this);
                    if (type == 'C') crackyStonesToRemove.add(this);
                    if (type == 'M') mirrorStonesToRemove.add(this);

                    engine.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Sprite sprite : stonesToRemove) {
                                sprite.detachSelf();
                                sprite.dispose();
                            }
                            crackyStonesToRemove.clear();
                            mirrorStonesToRemove.clear();
                            stonesToRemove.clear();
                        }

                    });
                }

                crackyStones.removeAll(crackyStonesToRemove);
                mirrorStones.removeAll(mirrorStonesToRemove);
                stones.removeAll(stonesToRemove);

            }
        };
        secondLayer.attachChild(stone);

        //animate cannon
        animateCannon(direction, position);

        final Body body = PhysicsFactory.createCircleBody(physicsWorld, stone, BodyDef.BodyType.KinematicBody, FIXTURE_DEF);
        if (type == 'C') {
            crackyStones.add(stone);
        } else if (type == 'M') mirrorStones.add(stone);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(stone, body, true, false));
        ball = new Ball(direction, null, body, stone, (type == 'T'), false, speedFactor);
        stone.setUserData(ball);
        stones.add(stone);
        return stone;
    }

    @Override
    public void addShootandMoveSign() {
        shootSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*6/8, resourcesManager.cracky_mirror_sign_region, vbom);
        moveSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.move_normal_sign_region, vbom);
    }

    //moves the player in X or Y direction
    public void movePlayer(char direction) {
        switch (direction) {
            case 'R':
                if (xPosLume < 3) {
                    xPosLume++;
                    lumeSprite.setPosition(lumeSprite.getX() + sideLength, lumeSprite.getY());
                }
                break;
            case 'L':
                if (xPosLume > 1) {
                    xPosLume--;
                    lumeSprite.setPosition(lumeSprite.getX() - sideLength, lumeSprite.getY());
                }
                break;
            case 'D':
                if (yPosLume > 1) {
                    yPosLume--;
                    lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() - sideLength);
                }
                break;
            case 'U':
                if (yPosLume < 3) {
                    yPosLume++;
                    lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() + sideLength);
                }
                break;
        }
        coinCheck();
    }

    @Override
    public void showText() {
        String tvText0 = "Mirror stones make trouBle! How can you deal with this situation, bro?";
        if (kimmelnitzText == null) {
            //sideLength*6.6f
            kimmelnitzText = new TickerText(sideLength*6.6f, camera.getHeight() / 6, resourcesManager.smallFont, tvText0,
                    new TickerText.TickerTextOptions(AutoWrap.WORDS, sideLength*13.2f, HorizontalAlign.CENTER,30), resourcesManager.vbom);
            secondLayer.attachChild(kimmelnitzText);
            kimmelnitzText.setAlpha(0.7f);
            activity.createTypingText(tvText0, kimmelnitzText, false);
        }
    }

    @Override
    public void createStones() {
        long interval = (long) 6000;
        long age = (new Date()).getTime() - stoneTime;
        if (firstStonesInLevel) interval = 1000;
        if (age >= interval) {
            if (firstStonesInLevel) createCoin();
            firstStonesInLevel = false;
            this.showStonesToScreen(0, false); //always same variant
            stoneTime = new Date().getTime();
        }
    }

    private void showStonesToScreen(int directionVariant, final boolean thornyFirst) {
        int randomRow = randomGenerator.nextInt(3); //values 0 to 2
        int row2 = randomRow+1;
        if (row2 > 2) row2 = 0;
        float ratio = resourcesManager.screenRatio;
        float factor = (directionVariant%2 == 0) ? ratio : 1f;
        addBall('M', 2, 0, 0.6f*factor);
        addBall('M', 2, 1, 0.6f*factor);
        addBall('M', 2, 2, 0.6f*factor);

        addBall('M', 1, 0, 0.6f);
        addBall('M', 1, 1, 0.6f);
        addBall('M', 1, 2, 0.6f);
    }

    private void rebound(Sprite cannonBall) {
        int direction = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = (level == 4) ? resourcesManager.screenWidth*4/400 :
                resourcesManager.screenWidth*6/400;
        Ball ball = (Ball) cannonBall.getUserData();
        Body body = ball.getBody();
        switch (ball.getDirection()) {
            case 1:
                yVel = speed;
                direction = 3;
                break;
            case 2:
                xVel = speed;
                direction = 4;
                break;
            case 3:
                yVel = -speed;
                direction = 1;
                break;
            case 4:
                xVel = -speed;
                direction = 2;
                break;
        }
        body.setLinearVelocity(xVel, yVel);
        ball.setDirection(direction);
        ball.setBody(body);
    }

}
