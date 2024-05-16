package com.mygdx.game;

import java.awt.*;

public class Enemy extends GameObject implements Runnable {
    private int hp;
    private Game game;
    private Thread thread;

    public Enemy(int x, int y, int hp, Game game) {
        super(x, y, 50, 50);
        this.hp = hp;
        this.game = game;
        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        while (isAlive()) {
            update();
            try {
                Thread.sleep(20); // Adjust sleep time as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }
}
