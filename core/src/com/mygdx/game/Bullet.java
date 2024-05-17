package com.mygdx.game;

import java.awt.*;

public class Bullet extends GameObject {
    private int targetX, targetY;
    private boolean active;
    private double speed = 10.0;

    public Bullet(int startX, int startY, int targetX, int targetY) {
        super(startX, startY, 5, 5);
        this.targetX = targetX;
        this.targetY = targetY;
        this.active = true;
        calculateVelocity();
    }

    private double vx, vy;

    private void calculateVelocity() {
        double angle = Math.atan2(targetY - y, targetX - x);
        vx = speed * Math.cos(angle);
        vy = speed * Math.sin(angle);
    }

    public void update() {
        if (active) {
            x += vx;
            y += vy;
            // Deactivate the bullet if it goes off screen
            if (x < 0 || x > Game.WIDTH || y < 0 || y > Game.HEIGHT) {
                active = false;
            }
        }
    }

    @Override
    public void draw(Object GameAssets) {

    }

    public void draw(Graphics g) {
        if (active) {
            g.setColor(Color.BLACK);
            g.fillRect(x, y, width, height);
        }
    }

    public void setInactive() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public boolean intersects(Enemy enemy) {
        Rectangle bulletRect = new Rectangle(x, y, width, height);
        Rectangle enemyRect = new Rectangle(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
        return bulletRect.intersects(enemyRect);
    }
}