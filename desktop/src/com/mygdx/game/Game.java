package com.mygdx.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private Player player;
    private List<Enemy> enemies = new ArrayList<>();
    static boolean mousePressed = false;
    private int mouseX, mouseY;

    public Game() {
        init();
    }

    private void init() {
        player = new Player(100, 100);
        spawnEnemies(1);
    }

    private void spawnEnemies(int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int x = WIDTH;
            int y = rand.nextInt(HEIGHT - 50);
            Enemy enemy = new Enemy(x, y, 1000, this);
            enemies.add(enemy);
            enemy.start(); // Start the enemy thread when spawned
        }
    }

    private void update() {
        player.update();
        List<Enemy> deadEnemies = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.update();
            } else {
                deadEnemies.add(enemy);
            }
        }
        enemies.removeAll(deadEnemies);

        if (enemies.isEmpty()) {
            spawnEnemies(new Random().nextInt(4) + 1);
        }

        handleCollisions();
    }

    private void handleCollisions() {
        List<Bullet> inactiveBullets = new ArrayList<>();

        for (Bullet bullet : player.getBullets()) {
            for (Enemy enemy : enemies) {
                if (bullet.intersects(enemy)) {
                    enemy.hit(100);
                    bullet.setInactive();
                    inactiveBullets.add(bullet);
                }
            }
        }

        player.getBullets().removeAll(inactiveBullets);

        // Remove defeated enemies and stop their threads
        List<Enemy> defeatedEnemies = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                defeatedEnemies.add(enemy);
                enemy.stop(); // Stop the enemy thread
            }
        }
        enemies.removeAll(defeatedEnemies);
    }

    private void draw(Graphics g) {
        player.draw(g);
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.draw(g);
                drawEnemyHP(g, enemy);
            }
        }
    }

    private void drawEnemyHP(Graphics g, Enemy enemy) {
        g.setColor(Color.BLACK);
        g.drawString("Enemy HP: " + enemy.getHP(), enemy.getX(), enemy.getY() - 10);
        System.out.println("HP: " + enemy.getHP());
    }

    public Player getPlayer() {
        return player;
    }

    public void start() {
        JFrame frame = new JFrame("Contra");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        Canvas canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
        canvas.createBufferStrategy(3);
        BufferStrategy bs = canvas.getBufferStrategy();
        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!mousePressed) {
                    mousePressed = true;
                    mouseX = e.getX();
                    mouseY = e.getY();
                    player.attack(mouseX, mouseY);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }
        });

        canvas.setFocusable(true);
        canvas.requestFocus();

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                update();
                delta--;
            }
            if (bs != null) {
                Graphics g = bs.getDrawGraphics();
                g.clearRect(0, 0, WIDTH, HEIGHT);
                draw(g);
                g.dispose();
                bs.show();
            }
            frames++;
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
