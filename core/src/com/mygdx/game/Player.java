package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
    private static final int PLAYER_HP = 10000; // Player's initial HP
    public static final int MOVE_SPEED = 5; // Player's movement speed
    private List<Bullet> bullets;
    private int hp;
    private int moveX = 0;
    private int moveY = 0;
    private Texture playerTexture;
    private float bulletCooldown = 0;
    private static final float MAX_BULLET_COOLDOWN = 0.2f;
    private boolean movingToMiddle = true;
    private int score;

    private Animation<TextureRegion> idleDownAnimation;
    private Animation<TextureRegion> idleLeftAnimation;
    private Animation<TextureRegion> idleRightAnimation;
    private Animation<TextureRegion> idleUpAnimation;
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private float stateTime;

    public Player(int x, int y) {
        super(x, y, 32, 32); // Set the width and height to be larger
        this.bullets = new ArrayList<>();
        this.hp = PLAYER_HP;
        this.playerTexture = new Texture(Gdx.files.internal("player/Player.png"));
        this.score = 0;
        this.stateTime = 0f;
        initializeAnimations();
    }

    private void initializeAnimations() {
        TextureRegion[][] tmp = TextureRegion.split(playerTexture, 16, 16);

        idleDownAnimation = new Animation<>(0.1f, tmp[0][0]);
        idleLeftAnimation = new Animation<>(0.1f, tmp[0][1]);
        idleRightAnimation = new Animation<>(0.1f, tmp[0][2]);
        idleUpAnimation = new Animation<>(0.1f, tmp[0][3]);

        walkDownAnimation = new Animation<>(0.1f, tmp[0][4], tmp[0][5], tmp[0][6], tmp[0][7]);
        walkLeftAnimation = new Animation<>(0.1f, tmp[0][8], tmp[0][9], tmp[0][10], tmp[0][11]);
        walkRightAnimation = new Animation<>(0.1f, tmp[0][12], tmp[0][13], tmp[0][14], tmp[0][15]);
        walkUpAnimation = new Animation<>(0.1f, tmp[0][16], tmp[0][17], tmp[0][18], tmp[0][19]);
    }

    @Override
    public void update() {
        stateTime += Gdx.graphics.getDeltaTime();

        if (movingToMiddle) {
            moveTowardsMiddle();
        } else {
            handleInput();
            x += moveX;
            y += moveY;

            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (x + width > Gdx.graphics.getWidth()) x = Gdx.graphics.getWidth() - width;
            if (y + height > Gdx.graphics.getHeight()) y = Gdx.graphics.getHeight() - height;

            for (Bullet bullet : bullets) {
                bullet.update();
            }
            bullets.removeIf(bullet -> !bullet.isActive());
            bulletCooldown -= Gdx.graphics.getDeltaTime();
        }
    }

    public void increaseScore(int points) {
        this.score += points;
    }

    private void moveTowardsMiddle() {
        double angle = Math.atan2(Gdx.graphics.getHeight() / 2 - y, Gdx.graphics.getWidth() / 2 - x);
        double vx = MOVE_SPEED * Math.cos(angle);
        double vy = MOVE_SPEED * Math.sin(angle);
        x += vx;
        y += vy;

        if (Math.abs(x - Gdx.graphics.getWidth() / 2) < MOVE_SPEED && Math.abs(y - Gdx.graphics.getHeight() / 2) < MOVE_SPEED) {
            movingToMiddle = false;
        }
    }

    public int getScore() {
        return score;
    }

    private void handleInput() {
        moveX = 0;
        moveY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveX = -MOVE_SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveX = MOVE_SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveY = MOVE_SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveY = -MOVE_SPEED;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && bulletCooldown <= 0) {
            int targetX = Gdx.input.getX();
            int targetY = Gdx.graphics.getHeight() - Gdx.input.getY();
            shoot(targetX, targetY);
        }
    }

    private void shoot(int targetX, int targetY) {
        Bullet bullet = new Bullet(x + width / 2, y + height / 2, targetX, targetY);
        bullets.add(bullet);
        bulletCooldown = MAX_BULLET_COOLDOWN;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(2, 2, 1, 1);
        //batch.draw(playerTexture, x, y, width, height);
        TextureRegion currentFrame = getCurrentFrame();
        batch.draw(currentFrame, x, y, width, height);
        for (Bullet bullet : bullets) {
            bullet.draw(batch);
        }
    }

    @Override
    public void draw(Object GameAssets) {

    }

    private TextureRegion getCurrentFrame() {
        if (moveX == 0 && moveY == 0) {
            return idleDownAnimation.getKeyFrame(stateTime, true);
        } else {
            if (moveY > 0) {
                return walkUpAnimation.getKeyFrame(stateTime, true);
            } else if (moveY < 0) {
                return walkDownAnimation.getKeyFrame(stateTime, true);
            } else if (moveX > 0) {
                return walkRightAnimation.getKeyFrame(stateTime, true);
            } else {
                return walkLeftAnimation.getKeyFrame(stateTime, true);
            }
        }
    }

    @Override
    public void dispose() {
        playerTexture.dispose();
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
    }

    public void attack(int targetX, int targetY) {
        Bullet bullet = new Bullet(x + width / 2, y + height / 2, targetX, targetY);
        bullets.add(bullet);
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
