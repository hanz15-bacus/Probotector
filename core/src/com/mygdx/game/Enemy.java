package com.mygdx.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Enemy extends GameObject implements Runnable {
    private static final int PLAYER_DAMAGE = 10; // Player damage when hit by enemy
    private int hp;
    private Game game;
    private List<EnemyBullet> bullets;
    private boolean canShoot;

    public Enemy(int x, int y, int hp, Game game) {
        super(x, y, 50, 50);
        this.hp = hp;
        this.game = game;
        this.bullets = new ArrayList<>();
        this.canShoot = true;
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
            } else if (bullet.intersects(game.getPlayer())) {
                game.getPlayer().hit(PLAYER_DAMAGE);
                bullet.setInactive();
            }
        }
    }

    @Override
    public void draw(Object GameAssets) {

    }

    @Override
    public void draw(Graphics g) {
        if (isAlive()) {
            g.setColor(Color.GREEN);
            g.fillRect(x, y, width, height);
        }

        for (EnemyBullet bullet : bullets) {
            bullet.draw(g);
        }
    }

    private void shoot() {
        int targetX = game.getPlayer().getX() + game.getPlayer().getWidth() / 2;
        int targetY = game.getPlayer().getY() + game.getPlayer().getHeight() / 2;
        EnemyBullet bullet = new EnemyBullet(x + width / 2, y + height / 2, targetX, targetY);
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