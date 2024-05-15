package com.mygdx.game;

import java.awt.*;

public class Bullet extends GameObject {
    private int speed = 10;
    private double directionX, directionY;
    private boolean active = true;
    private int damage;

    public Bullet(int x, int y, int targetX, int targetY) {
        super(x, y, 10, 10);
        setDirection(targetX, targetY);
    }

    private void setDirection(int targetX, int targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length != 0) {
            directionX = (dx / length) * speed;
            directionY = (dy / length) * speed;
        }
    }

    @Override
    public void update() {
        if (active) {
            x += directionX;
            y += directionY;
            if (isOffScreen()) {
                active = false;
            }
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

}
