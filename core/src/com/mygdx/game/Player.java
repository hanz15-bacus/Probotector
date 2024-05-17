package com.mygdx.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
    private static final int PLAYER_HP = 10000; // Player's initial HP
    public static final int MOVE_SPEED = 5; // Player's movement speed
    private List<Bullet> bullets;
    private int hp;
    private int moveX = 0;
    private int moveY = 0;

    public Player(int x, int y) {
        super(x, y, 50, 50);
        this.bullets = new ArrayList<>();
        this.hp = PLAYER_HP; // Set player's initial HP
    }

    public void update() {
        x += moveX;
        y += moveY;

        // Ensure player stays within game boundaries
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > Game.WIDTH) x = Game.WIDTH - width; // Assuming game width is 800
        if (y + height > Game.HEIGHT) y = Game.HEIGHT - height; // Assuming game height is 600

        for (Bullet bullet : bullets) {
            bullet.update();
        }
        bullets.removeIf(bullet -> !bullet.isActive());
    }

    @Override
    public void draw(Object GameAssets) {

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

    public void setMoveX(int moveX) {
        this.moveX = moveX;
    }

    public void setMoveY(int moveY) {
        this.moveY = moveY;
    }
}
