package org.lume.object;

public class Circle {
	
	private float x, y;
	private float radius;

	public Circle() {
		x = 0;
		y = 0;
		radius = 0;
	}
	
	public Circle(float x, float y) {
		this.x = x;
		this.y = y;
		radius = 0;
	}
	
	public Circle(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	public boolean collision(Circle secondCircle) {
		boolean collided = false;
		double difference;
		float x2, y2;
		float x1 = this.getX();
		float y1 = this.getY();
		x2 = secondCircle.getX();
		y2 = secondCircle.getY();
		double square = Math.pow(Math.abs(x2 - x1), 2) + Math.pow(Math.abs(y2 - y1), 2);
		difference = Math.sqrt(square);
		if (difference <= this.getRadius() + secondCircle.getRadius()) {
			collided = true;
		}
		return collided;
	}
	
	public float getX() {
		return this.x;
	}
	
	public void setX(float newX) {
		this.x = newX;
	}
	
	public float getY() {
		return this.y;
	}
	
	public void setY(float newY) {
		this.y = newY;
	}
	
	public float getRadius() {
		return this.radius;
	}
	
	public void setRadius(float newRadius) {
		this.radius = newRadius;
	}

}
