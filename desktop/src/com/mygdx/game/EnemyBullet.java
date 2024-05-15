package com.mygdx.game;

import com.badlogic.gdx.physics.bullet.Bullet;

import java.awt.*;

public class EnemyBullet extends Bullet {
    private static final int DAMAGE = 10;

    public EnemyBullet(int x, int y, int targetX, int targetY) {
        super();
        // Set other properties if needed
    }


    public int getDamage() {
        return DAMAGE;
    }

    public boolean isActive() {
        return true;
    }

    public void update() {
    }

    public void draw(Graphics g) {
    }
}

