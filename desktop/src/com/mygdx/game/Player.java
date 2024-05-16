package com.mygdx.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
    private static final int PLAYER_HP = 10000; // Player's initial HP
    private List<Bullet> bullets;
    private int hp;

    public Player(int x, int y) {
        super(x, y, 50, 50);
        this.bullets = new ArrayList<>();
        this.hp = PLAYER_HP; // Set player's initial HP
    }

    public void update() {
        for (Bullet bullet : bullets) {
            bullet.update();
        }
        bullets.removeIf(bullet -> !bullet.isActive());
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }

    public void attack(int targetX, int targetY) {
        Bullet bullet = new Bullet(x + width / 2, y + height / 2, targetX, targetY);
        bullets.add(bullet);
        System.out.println("Player Attacking");
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void hit(int damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            System.out.println("Player defeated!");
        }
    }

    public int getHP() {
        return hp;
    }
}
