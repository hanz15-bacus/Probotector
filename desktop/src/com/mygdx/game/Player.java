package com.mygdx.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {
    private int x, y;
    private int speed = 5;
    private int bulletDelay = 20;
    private int bulletCooldown = 0;
    private int bulletLimit = 10;
    private int bulletsFired = 0;
    private int reloadTime = 100;
    private int reloadCooldown = 0;
    private int hp = 10000;
    private int bulletDamage = 100; // Set bullet damage to 100
    private List<Bullet> bullets = new ArrayList<>();

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        if (bulletCooldown > 0) {
            bulletCooldown--;
        }
        if (reloadCooldown > 0) {
            reloadCooldown--;
            if (reloadCooldown == 0) {
                bulletsFired = 0;
            }
        }
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            bullet.update();
            if (bullet.isOffScreen()) {
                it.remove();
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, 50, 50);
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        drawPlayerHP(g);
    }

    public void attack(int targetX, int targetY) {
        if (reloadCooldown > 0) {
            return;
        }
        if (bulletCooldown <= 0 && bulletsFired < bulletLimit) {
            Bullet bullet = new Bullet(x + 25, y + 25, targetX - (x + 25), targetY - (y + 25));
            bullet.setDamage(bulletDamage); // Set bullet damage to 100
            bullets.add(bullet);
            bulletCooldown = bulletDelay;
            bulletsFired++;
            if (bulletsFired >= bulletLimit) {
                reloadCooldown = reloadTime;
            }
            Game.mousePressed = false;
        }
    }

    void drawPlayerHP(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("Player HP: " + hp, 50, 50);
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }


    public int getX() {
        return x;
    }
}
