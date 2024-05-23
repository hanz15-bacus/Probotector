package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class EnemyBullet {
    private static final int SPEED = 5;
    private static final int DAMAGE = 10;
    private int x, y;
    private double vx, vy;
    private boolean activeBullet;
    private Texture bulletTexture;
    private int width;
    private int height;

    public EnemyBullet(int x, int y, int targetX, int targetY) {
        this.x = x;
        this.y = y;
        this.width = 5;  // Example width
        this.height = 5; // Example height
        this.activeBullet = true;
        this.bulletTexture = new Texture("bullet.png"); // Ensure this texture exists in the assets folder
        calculateVelocity(targetX, targetY);
    }

    private void calculateVelocity(int targetX, int targetY) {
        double angle = Math.atan2(targetY - y, targetX - x);
        vx = SPEED * Math.cos(angle);
        vy = SPEED * Math.sin(angle);
    }

    public int getDamage() {
        return DAMAGE;
    }

    public boolean isActive() {
        return activeBullet;
    }

    public void setInactive() {
        this.activeBullet = false;
    }

    public void update() {
        if (activeBullet) {
            x += vx;
            y += vy;

            // Deactivate bullet if it goes off-screen
            if (x < 0 || x > Gdx.graphics.getWidth() || y < 0 || y > Gdx.graphics.getHeight()) {
                activeBullet = false;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (activeBullet) {
            batch.begin();
            batch.draw(bulletTexture, x, y, width, height);
            batch.end();
        }
    }

    public boolean intersects(Player player) {
        Rectangle bulletRect = new Rectangle(x, y, width, height);
        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        return bulletRect.overlaps(playerRect);
    }

    public void dispose() {
        bulletTexture.dispose();
    }

    public void setDamage(int i) {
    }
}
