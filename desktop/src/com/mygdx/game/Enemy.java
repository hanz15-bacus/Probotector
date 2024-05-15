package com.mygdx.game;

import java.awt.*;

public class Enemy extends GameObject {
    private int hp;
    private Game game;

    public Enemy(int x, int y, int hp, Game game) {
        super(x, y, 50, 50);
        this.hp = hp;
        this.game = game;
    }

    @Override
    public void update() {
        if (x > 600) {
            x -= 2; // Move left until the enemy reaches x = 600
        }
    }

    @Override
    public void draw(Graphics g) {
        if (isAlive()) {
            g.setColor(Color.GREEN);
            g.fillRect(x, y, width, height);
        }
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

}