package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DesktopLauncher {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	private Player player;
	private Enemy enemy;
	private static boolean mousePressed = false;
	private int mouseX, mouseY;

	public DesktopLauncher() {
		init();
	}

	private void init() {
		player = new Player(100, 100); // Initialize player at position (100, 100)
		enemy = new Enemy(600, 100, 100); // Initialize enemy at position (600, 100) with 100 HP
	}

	public void update() {
		player.update();
		enemy.update();
		handleCollisions();
	}

	private void handleCollisions() {
		for (Bullet bullet : player.bullets) {
			if (bullet.intersects(enemy)) {
				enemy.hit(10); // Decrease enemy's HP by 10
				bullet.setInactive(); // Deactivate the bullet
			}
		}
	}

	public void draw(Graphics g) {
		player.draw(g);
		enemy.draw(g);
	}

	private static void gameLoop(DesktopLauncher game) {
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
				game.mousePressed = true;
				game.mouseX = e.getX();
				game.mouseY = e.getY();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				game.mousePressed = false;
			}
		});

		canvas.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (game.mousePressed) {
					game.mouseX = e.getX();
					game.mouseY = e.getY();
				}
			}
		});

		canvas.setFocusable(true);
		canvas.requestFocus();

		while (true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				game.update();
				if (game.mousePressed) {
					game.player.attack(game.mouseX, game.mouseY);
				}
				delta--;
			}
			if (bs != null) {
				Graphics g = bs.getDrawGraphics();
				g.clearRect(0, 0, WIDTH, HEIGHT); // Clear the screen
				game.draw(g);
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

	static class Player {
		private int x, y;
		private int speed = 5;
		private int bulletDelay = 20; // Delay between each bullet shot
		private int bulletCooldown = 0; // Cooldown counter for shooting bullets
		private int bulletLimit = 10; // Maximum bullets before reload
		private int bulletsFired = 0; // Bullets fired before reload
		private int reloadTime = 100; // Time to reload
		private int reloadCooldown = 0; // Cooldown counter for reloading
		private List<Bullet> bullets = new ArrayList<>();

		public Player(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void update() {
			// Update player position based on input or game logic
			if (bulletCooldown > 0) {
				bulletCooldown--;
			}
			if (reloadCooldown > 0) {
				reloadCooldown--;
				if (reloadCooldown == 0) {
					bulletsFired = 0; // Reset bullets fired after reload
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
		}

		public void attack(int mouseX, int mouseY) {
			if (reloadCooldown > 0) {
				return; // Cannot attack during reload
			}
			if (bulletCooldown <= 0 && bulletsFired < bulletLimit) {
				System.out.println("Player attacks!");
				int directionX = mouseX - (x + 25);
				int directionY = mouseY - (y + 25);
				Bullet bullet = new Bullet(x + 25, y + 25, directionX, directionY); // Center bullet at player's position
				bullets.add(bullet);
				bulletCooldown = bulletDelay;
				bulletsFired++;
				if (bulletsFired >= bulletLimit) {
					reloadCooldown = reloadTime; // Start reload
				}
				// Reset mousePressed to prevent continuous firing
				mousePressed = false;
			}
		}
	}

	static class Bullet {
		private int x, y;
		private int speed = 10;
		private int directionX, directionY;
		private boolean active = true;

		public Bullet(int x, int y, int directionX, int directionY) {
			this.x = x;
			this.y = y;
			this.directionX = directionX;
			this.directionY = directionY;
			normalizeDirection();
		}

		private void normalizeDirection() {
			double length = Math.sqrt(directionX * directionX + directionY * directionY);
			if (length != 0) {
				directionX = (int) (directionX / length * speed);
				directionY = (int) (directionY / length * speed);
			}
		}

		public void update() {
			// Update bullet position based on direction
			if (active) {
				x += directionX;
				y += directionY;
			}
		}

		public void draw(Graphics g) {
			if (active) {
				g.setColor(Color.BLUE);
				g.fillOval(x, y, 10, 10); // Draw a simple bullet circle
			}
		}

		public boolean isOffScreen() {
			return x < 0 || x > DesktopLauncher.WIDTH || y < 0 || y > DesktopLauncher.HEIGHT;
		}

		public boolean intersects(Enemy enemy) {
			if (!active) {
				return false;
			}
			return x >= enemy.getX() && x <= enemy.getX() + enemy.getWidth() &&
					y >= enemy.getY() && y <= enemy.getY() + enemy.getHeight();
		}

		public void setInactive() {
			active = false;
		}
	}

	static class Enemy {
		private int x, y;
		private int width, height;
		private int hp;

		public Enemy(int x, int y, int hp) {
			this.x = x;
			this.y = y;
			this.hp = hp;
			this.width = 50; // Adjust the width and height according to your needs
			this.height = 50;
		}

		public void update() {
			// Update enemy position or perform other actions
		}

		public void draw(Graphics g) {
			g.setColor(Color.GREEN);
			g.fillRect(x, y, width, height);
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public int getHP() {
			return hp;
		}

		public void hit(int damage) {
			hp -= damage;
			if (hp <= 0) {
				// Enemy defeated, perform any necessary actions
				System.out.println("Enemy defeated!");
			}
		}
	}

	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("My GDX Game");
		new Lwjgl3Application(new MyGdxGame(), config);
		DesktopLauncher game = new DesktopLauncher();
		gameLoop(game);
	}
}

