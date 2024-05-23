package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Enemy extends GameObject implements Runnable {
    private static final int PLAYER_DAMAGE = 10; // Player damage when hit by enemy
    private int hp;
    private Player player;
    private List<EnemyBullet> bullets;
    private boolean canShoot;
    private Texture enemyTexture;

    public Enemy(int x, int y, int hp) {
        super(x, y, 50, 50);
        this.hp = hp;
        this.player = player;
        this.bullets = new ArrayList<>();
        this.canShoot = true;
        this.enemyTexture = new Texture("enemy.png");
    }

    @Override
    public void run() {
        while (isAlive()) {
            if (canShoot) {
                shoot();
                canShoot = false;
            }
            update();

            try {
                Thread.sleep(1000);
                canShoot = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void update() {
        if (x > 600) {
            x -= 2;
        }

        Iterator<EnemyBullet> it = bullets.iterator();
        while (it.hasNext()) {
            EnemyBullet bullet = it.next();
            bullet.update();
            if (!bullet.isActive()) {
                it.remove();
            } else if (bullet.intersects(player)) {
                player.hit(PLAYER_DAMAGE);
                bullet.setInactive();
            }
        }
    }

    @Override
    public void draw(Object GameAssets) {
        // Implement if necessary
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isAlive()) {
            batch.setColor(Color.GREEN);
            batch.draw(enemyTexture, x, y, width, height);
        }

        for (EnemyBullet bullet : bullets) {
            bullet.draw(batch);
        }
    }

    @Override
    public void dispose() {
        enemyTexture.dispose();
        for (EnemyBullet bullet : bullets) {
            bullet.dispose();
        }
    }

    private void shoot() {
        int targetX = player.getX();
        int targetY = player.getY();
        EnemyBullet bullet = new EnemyBullet(x + width / 2, y + height / 2, targetX, targetY);
        bullet.setDamage(50);
        bullets.add(bullet);
    }


    public List<EnemyBullet> getBullets() {
        return bullets;
    }

    public int getHP() {
        return hp;
    }

    public void hit(int damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            System.out.println("Enemy defeated!");
        }
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void start() {
        new Thread(this).start();
    }
}
