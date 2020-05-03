package org.lume.scene.skillscenes;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.lume.base.SkillScene;
import org.lume.entity.primitive.Rectangle;
import org.lume.entity.scene.background.SpriteBackground;
import org.lume.entity.sprite.Sprite;
import org.lume.entity.text.AutoWrap;
import org.lume.entity.text.TickerText;
import org.lume.extension.physics.box2d.PhysicsConnector;
import org.lume.extension.physics.box2d.PhysicsFactory;
import org.lume.input.touch.TouchEvent;
import org.lume.object.Ball;
import org.lume.object.Circle;
import org.lume.opengl.texture.region.ITextureRegion;
import org.lume.util.adt.align.HorizontalAlign;
import org.lume.util.adt.color.Color;

import java.util.Date;

public class Skill22 extends SkillScene {
    private int survivedStones;

    public Skill22() {
        super(6);
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
                                moveGrume('R');
                            } else { //right to left
                                moveGrume('L');
                            }
                        } else { //vertical swipe
                            if (deltaY > 0) { //up to down
                                moveGrume('U');
                            } else { //down to up
                                moveGrume('D');
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
                char moveDir = 0;
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
                                moveDir = 'R';
                            } else { //right to left
                                moveDir = 'L';
                            }
                        } else { //vertical
                            if (deltaY > 0) { //up to down
                                moveDir = 'U';
                            } else { //down to up
                                moveDir = 'D';
                            }
                        }
                    } else { //TAP
                    }

                    moveLume(moveDir);

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
//        Ball ball;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/5;
        switch (direction) {
            case 1: //up to down
                x = lumeSprite.getX();
                y = lumeSprite.getY() - lumeSprite.getHeight();
                yVel = -speed;
                break;
            case 2: //right to left
                x = lumeSprite.getX() - lumeSprite.getWidth();
                y = lumeSprite.getY();
                xVel = -speed;
                break;
            case 3: //down to up
                x = lumeSprite.getX();
                y = lumeSprite.getY() + lumeSprite.getHeight();
                yVel = speed;
                break;
            case 4: //left to right
                x = lumeSprite.getX() + lumeSprite.getWidth();
                y = lumeSprite.getY();
                xVel = speed;
                break;
        }
        cannonball = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                for (Sprite crackyStone : crackyStones) {
                    final Circle cannonCircle, stoneCircle;
                    cannonCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);
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
                stones.removeAll(stonesToRemove);
                crackyStones.removeAll(crackyStonesToRemove);

                if (this.getX() < -sideLength || this.getY() < - sideLength ||
                        this.getX() > camera.getWidth()+sideLength || this.getY() > camera.getWidth()+sideLength) {
                    this.detachSelf();
                    this.dispose();
                }
            }
        };
        secondLayer.attachChild(cannonball);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, cannonball, BodyDef.BodyType.KinematicBody, FIXTURE_DEF);
//        ball = new Ball(cannonball, "cannonball");
//        body.setUserData(ball);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(cannonball, body, true, false));
    }

    public Sprite addBall(final boolean thorny, int direction, int position, float row, float speedFactor) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/15 * speedFactor;

        final Sprite stone;
        ITextureRegion textureRegion = (thorny) ? resourcesManager.thorny_stone_region : resourcesManager.cracky_stone_region;
        Ball ball;

        switch (direction) {
            case 1:
                x = camera.getCenterX() - sideLength + sideLength*position;
                y = camera.getHeight() - sideLength / 2 + sideLength*row;
                yVel = -speed;
                break;
            case 2:
                x = camera.getWidth() - sideLength / 2 + sideLength*row;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = -speed;
                break;
            case 3:
                x = camera.getCenterX()-sideLength + sideLength*position;
                y = sideLength / 2 - sideLength*row;
                yVel = speed;
                break;
            case 4:
                x = sideLength / 2 - sideLength*row;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = speed;
                break;
        }

//        stoneCircle = new Circle(x, y, sideLength*3/8);
        stone = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                final Circle lumeCircle, lamporghinaCircle, stoneCircle;
                lumeCircle = new Circle(lumeSprite.getX(), lumeSprite.getY(), lumeSprite.getWidth() / 2);
                lamporghinaCircle = new Circle(lamporghinaSprite.getX(), lamporghinaSprite.getY(), lamporghinaSprite.getWidth()/2);
                stoneCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);

                if (stoneCircle.collision(lumeCircle) && !gameOverDisplayed  && thorny) {
                    displayGameOverScene();
                } else if (stoneCircle.collision(lumeCircle) && !thorny) {
                    final Sprite crackyBallToRemove = this;

                    engine.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            crackyBallToRemove.detachSelf();
                            crackyBallToRemove.dispose();
                        }
                    });
                }
                if (stoneCircle.collision(lamporghinaCircle) && !gameOverDisplayed) {
                    displayGameOverScene();
                }
//                if (this.getX() < -sideLength || this.getY() < -sideLength ||
//                        this.getX() > camera.getWidth() + sideLength || this.getY() > camera.getWidth() + sideLength) {
//                    if (!thorny) crackyStones.remove(this);
//                    this.detachSelf();
//                    this.dispose();
//                }


                if (this.getX() < sideLength) {
                    stonesToRemove.add(this);
                    if (!thorny) crackyStonesToRemove.add(this);

                    survivedStones++;
                    if (survivedStones >= 14) addToScore(10);

                    engine.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Sprite sprite : stonesToRemove) {
                                sprite.detachSelf();
                                sprite.dispose();
                            }
                            crackyStonesToRemove.clear();
                            stonesToRemove.clear();
                        }

                    });
                }

                crackyStones.removeAll(crackyStonesToRemove);
                stones.removeAll(stonesToRemove);

            }
        };
        secondLayer.attachChild(stone);

        //animate cannons
        animateCannon(direction, position);

        final Body body = PhysicsFactory.createCircleBody(physicsWorld, stone, BodyDef.BodyType.KinematicBody, FIXTURE_DEF);
        if (thorny) {
        } else {
            crackyStones.add(stone);
        }
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(stone, body, true, false));
        ball = new Ball(direction, null, body, stone, thorny, false, speedFactor);
        stone.setUserData(ball);
        stones.add(stone);
        return stone;
    }

    @Override
    public void addShootandMoveSign() {
        shootSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.lamporghina_sign_region, vbom);
        moveSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.helmet_sign_region, vbom);
    }

    public void createLamporghina() {
        xPosGrume = 1;
        yPosGrume = 3;
        lamporghinaSprite = new Sprite(camera.getCenterX() - sideLength, camera.getCenterY() + sideLength, sideLength*3/4, sideLength*3/4, resourcesManager.lamporghina_region, vbom);
        secondLayer.attachChild(lamporghinaSprite);
    }

    //moves the player in X or Y direction
    public void moveLume(char direction) {
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

    //moves the player in X or Y direction
    public void moveGrume(char direction) {
        switch (direction) {
            case 'R':
                if (xPosGrume < 3 && !(xPosLume == xPosGrume+1 && yPosLume == yPosGrume)) {
                    xPosGrume++;
                    lamporghinaSprite.setPosition(lamporghinaSprite.getX() + sideLength, lamporghinaSprite.getY());
                }
                break;
            case 'L':
                if (xPosGrume > 1 && !(xPosLume == xPosGrume-1 && yPosLume == yPosGrume)) {
                    xPosGrume--;
                    lamporghinaSprite.setPosition(lamporghinaSprite.getX() - sideLength, lamporghinaSprite.getY());
                }
                break;
            case 'D':
                if (yPosGrume > 1 && !(xPosLume == xPosGrume && yPosLume == yPosGrume-1)) {
                    yPosGrume--;
                    lamporghinaSprite.setPosition(lamporghinaSprite.getX(), lamporghinaSprite.getY() - sideLength);
                }
                break;
            case 'U':
                if (yPosGrume < 3 && !(xPosLume == xPosGrume && yPosLume == yPosGrume+1)) {
                    yPosGrume++;
                    lamporghinaSprite.setPosition(lamporghinaSprite.getX(), lamporghinaSprite.getY() + sideLength);
                }
                break;
        }
        coinCheck();
    }

    @Override
    public void showText() {
        createLamporghina();
        String tvText0 = "Take care aBout your lamporghina! Rescue her through all these stones!";
        if (kimmelnitzText == null) {
            //sideLength*6.6f
            kimmelnitzText = new TickerText(sideLength*6.6f, camera.getHeight() / 6, resourcesManager.smallFont, tvText0,
                    new TickerText.TickerTextOptions(AutoWrap.WORDS, sideLength*13.2f, HorizontalAlign.CENTER,60), resourcesManager.vbom);
            secondLayer.attachChild(kimmelnitzText);
            kimmelnitzText.setAlpha(0.7f);
            //activity.createTypingText(tvText0, kimmelnitzText, false);
        }
    }

    @Override
    public void createStones() {
        int direction = randomGenerator.nextInt(4) + 1;
        long interval = (long) 700;
        long age = (new Date()).getTime() - stoneTime;
        if (firstStonesInLevel) interval = 1000;
        if (age >= interval && firstStonesInLevel) {
            firstStonesInLevel = false;
            this.showStonesToScreen(direction, false);
            stoneTime = new Date().getTime();
        }
    }

    private void showStonesToScreen(int directionVariant, final boolean thornyFirst) {
        int randomRow = randomGenerator.nextInt(3); //values 0 to 2
        int row2 = randomRow+1;
        if (row2 > 2) row2 = 0;
        float ratio = resourcesManager.screenRatio;
        float factor = (directionVariant%2 == 0) ? ratio : 1f;
        float distance = 2.0f;
        addBall(true, 2, 0, 0, 0.5f*ratio);
        addBall(true, 2, 1, 0, 0.5f*ratio);
        addBall(false, 2, 2, 0, 0.5f*ratio);

        addBall(true, 2, 0, 1*distance, 0.5f*ratio);
        addBall(false, 2, 1, 1*distance, 0.5f*ratio);
        addBall(true, 2, 2, 1*distance, 0.5f*ratio);

        addBall(false, 2, 0, 2*distance, 0.5f*ratio);
        addBall(true, 2, 1, 2*distance, 0.5f*ratio);
        addBall(true, 2, 2, 2*distance, 0.5f*ratio);

        addBall(true, 2, 0, 3.5f*distance, 0.5f*ratio);
        addBall(true, 2, 1, 3.5f*distance, 0.5f*ratio);
        addBall(false, 2, 2, 3.5f*distance, 0.5f*ratio);

        addBall(true, 2, 0, 4.5f*distance, 0.5f*ratio);
        addBall(false, 2, 1, 4.5f*distance, 0.5f*ratio);
        addBall(true, 2, 2, 4.5f*distance, 0.5f*ratio);

        addBall(true, 2, 0, 5.5f*distance, 0.5f*ratio);
        addBall(true, 2, 1, 5.5f*distance, 0.5f*ratio);
        addBall(false, 2, 2, 5.5f*distance, 0.5f*ratio);

        addBall(false, 2, 0, 7f*distance, 0.5f*ratio);
        addBall(true, 2, 1, 7f*distance, 0.5f*ratio);
        addBall(true, 2, 2, 7f*distance, 0.5f*ratio);
    }

}
