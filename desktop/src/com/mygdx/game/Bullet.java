package com.mygdx.game;

import java.awt.*;

public class Bullet extends GameObject {
    private int speed = 10;
    private int directionX, directionY;
    private boolean active = true;
    private int damage; // Add damage variable

    public Bullet(int x, int y, int directionX, int directionY) {
        super(x, y, 10, 10);
        this.directionX = directionX;
        this.directionY = directionY;
        normalizeDirection();
    }

    private void normalizeDirection() {
        double length = Math.sqrt(directionX * directionX + directionY * directionY);
        if (length != 0) {
            directionX = (int) (directionX / length * speed);
            directionY = (int) (directionY / length * speed);
        }
    }

    @Override
    public void update() {
        if (active) {
            x += directionX;
            y += directionY;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (active) {
            g.setColor(Color.BLUE);
            g.fillOval(x, y, width, height);
        }
    }

    public boolean isOffScreen() {
        return x < 0 || x > Game.WIDTH || y < 0 || y > Game.HEIGHT;
    }

    public boolean intersects(Enemy enemy) {
        if (!active) {
            return false;
        }
        return x >= enemy.getX() && x <= enemy.getX() + enemy.getWidth() &&
                y >= enemy.getY() && y <= enemy.getY() + enemy.getHeight();
    }

    public void setInactive() {
        active = false;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean isActive() {

        return true;
    }
}
