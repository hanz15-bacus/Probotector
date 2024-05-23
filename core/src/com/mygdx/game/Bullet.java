package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bullet extends GameObject {
    private int targetX, targetY;
    private boolean active;
    private double speed = 10.0;
    private double vx, vy;
    private Texture texture;

    public Bullet(int startX, int startY, int targetX, int targetY) {
        super(startX, startY, 5, 5);
        this.targetX = targetX;
        this.targetY = targetY;
        this.active = true;
        calculateVelocity();
        texture = new Texture(Gdx.files.internal("bullet.png")); // Ensure you have this texture in your assets folder
    }

    private void calculateVelocity() {
        double angle = Math.atan2(targetY - y, targetX - x);
        vx = speed * Math.cos(angle);
        vy = speed * Math.sin(angle);
    }

    @Override
    public void update() {
        if (active) {
            x += vx;
            y += vy;
            // Deactivate the bullet if it goes off screen
            if (x < 0 || x > Gdx.graphics.getWidth() || y < 0 || y > Gdx.graphics.getHeight()) {
                active = false;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {

        if (active) {
            batch.setColor(1, 0, 0, 1);
            batch.draw(texture, x, y, width, height);
        }
    }

    @Override
    public void draw(Object GameAssets) {
        // Unused method, consider removing it if not necessary
    }

    @Override
    public void dispose() {
        texture.dispose();
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
        return bulletRect.overlaps(enemyRect);
    }
}
