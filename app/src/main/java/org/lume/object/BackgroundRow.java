package org.lume.object;

import org.lume.entity.Entity;
import org.lume.entity.primitive.Rectangle;
import org.lume.manager.ResourcesManager;
import org.lume.util.adt.color.Color;

import java.util.Random;

public class BackgroundRow extends Entity {
    //attributes
    private int sideLength;

    private Rectangle[] outerRectangles, innerRectangles;
    private Color[] colors;
    private Random randomGenerator;

    /*
    default constructor,
      creates 16 squares with
   random colour and alpha value
     */
    public BackgroundRow() {
        sideLength = ResourcesManager.getInstance().screenHeight/9;
        outerRectangles = new Rectangle[16];
        innerRectangles = new Rectangle[16];
        colors = new Color[16];
        randomGenerator = new Random();
        this.setWidth(sideLength*16);
        this.setHeight(sideLength);
        generateRandomRects();
    }

    public BackgroundRow(float x, float y) {
        this.setX(x);
        this.setY(y);
        sideLength = ResourcesManager.getInstance().screenHeight/9;
        outerRectangles = new Rectangle[16];
        innerRectangles = new Rectangle[16];
        colors = new Color[16];
        randomGenerator = new Random();
        this.setWidth(sideLength*16);
        this.setHeight(sideLength);
        generateRandomRects();
    }

    private void generateRandomRects() {
        float x, y, innerFactor;
        float greyRGB, alpha;
        for (int i = 0; i < outerRectangles.length; i++) {
            x = i*sideLength + sideLength/2;
            y = sideLength/2;
            innerFactor = 1.513f;
            greyRGB = randomGenerator.nextFloat()*0.7f; //not too dark
            alpha = randomGenerator.nextFloat()/3; //very transparent

            outerRectangles[i] = new Rectangle(x, y, sideLength, sideLength,
                    ResourcesManager.getInstance().vbom);
            innerRectangles[i] = new Rectangle(x, y, sideLength/innerFactor, sideLength/innerFactor,
                    ResourcesManager.getInstance().vbom);
            //random grey with random alpha
            colors[i] = new Color(greyRGB, greyRGB, greyRGB, alpha);
            outerRectangles[i].setColor(colors[i]);
            colors[i].set(greyRGB*0.7f, greyRGB*0.7f, greyRGB*0.7f, alpha);
            innerRectangles[i].setColor(colors[i]);
            this.attachChild(outerRectangles[i]);
            this.attachChild(innerRectangles[i]);
        }
    }
}
