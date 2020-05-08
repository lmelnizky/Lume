package org.lume.object;

/**
 * Created by lukas on 05.10.2016.
 */
public class Line {


    private float x1, x2, y1, y2, xm, ym;
    public Line() {
    }

    public Line(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.xm = (x2-x1)/2;
        this.ym = (y2-y1)/2;
    }

    public boolean collision(Circle circle) {
        boolean collided = false;
        double difference;
        float c1, c2;

        double square = Math.pow(Math.abs(circle.getX() - this.getX2()), 2) + Math.pow(Math.abs(circle.getY() - this.getY2()), 2);
        difference = Math.sqrt(square);
        if (difference <= circle.getRadius()) {
            collided = true;
        }
        return collided;
    }

    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY1() {
        return y1;
    }

    public void setY1(float y1) {
        this.y1 = y1;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    public float getXm() {
        return xm;
    }

    public void setXm(float xm) {
        this.xm = xm;
    }

    public float getYm() {
        return ym;
    }

    public void setYm(float ym) {
        this.ym = ym;
    }
}
