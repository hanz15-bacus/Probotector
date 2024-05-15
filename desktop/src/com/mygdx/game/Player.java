package com.mygdx.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player extends GameObject {
    private List<Bullet> bullets;

    public Player(int x, int y) {
        super(x, y, 50, 50);
        bullets = new ArrayList<>();
    }

    public void update() {
        for (Bullet bullet : bullets) {
            bullet.update();
        }
        bullets.removeIf(bullet -> !bullet.isActive());
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, 50, 50);
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            bullet.draw(g);
            if (!bullet.isActive()) {
                it.remove(); // Use Iterator's remove method
            }
        }
    }

    public void attack(int targetX, int targetY) {
        Bullet bullet = new Bullet(x + width / 2, y + height / 2, targetX, targetY);
        bullets.add(bullet);
    }

    public List<Bullet> getBullets() {
        return bullets;
    }
}
