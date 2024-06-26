package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;
    Texture loadingImg;
    Stage stage;
    BitmapFont font;
    TextButton.TextButtonStyle buttonStyle;
    private int score = 0;
    private int mapWidth, mapHeight;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private Player player;
    private List<Enemy> enemies = new ArrayList<>();
    private boolean gameStarted = false;
    private boolean loading = true;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private FitViewport viewport;

    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("WHITE.jpg");
        loadingImg = new Texture("tank-loading.png");

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;

        // set background image for button
        Texture startButtonTexture = new Texture("start-button.png"); // replace with your own start button image
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(startButtonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(startButtonTexture));
        buttonStyle.checked = new TextureRegionDrawable(new TextureRegion(startButtonTexture));

        TextButton startButton = new TextButton(null, buttonStyle);
        startButton.setPosition(Gdx.graphics.getWidth() / 2f - startButton.getWidth() / 2f, Gdx.graphics.getHeight() / 4f - startButton.getHeight() / 2f);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loading = false;
                gameStarted = true;
            }
        });
        stage.addActor(startButton);

        player = new Player(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        spawnEnemies(3);

        // Load the map
        map = new TmxMapLoader().load("mapper/my-realmap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.update();
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 1, 1, 1);

        if (loading) {
            renderLoadingScreen();
        } else if (!gameStarted) {
            batch.begin();
            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            stage.draw();
            batch.end();
        } else {
            update();
            camera.update();
            mapRenderer.setView(camera);
            mapRenderer.render();

            batch.begin();
            draw();
            batch.end();
        }
    }

    private void renderLoadingScreen() {
        batch.begin();
        batch.draw(loadingImg, 0, 0, WIDTH, HEIGHT);
        batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        loadingImg.dispose();
        stage.dispose();
        font.dispose();
        map.dispose();
        mapRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.setToOrtho(false, width, height);
        mapRenderer.setView(camera);
    }

    private void spawnEnemies(int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int x = WIDTH;
            int y = rand.nextInt(HEIGHT - 50);
            Enemy enemy = new Enemy(x, y, 1000, player);
            enemy.start();
            enemies.add(enemy);
        }
    }

    private void update() {
        player.update();
        updateCameraPosition();
        camera.update();

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.isAlive()) {
                enemy.update();
            } else {
                enemyIterator.remove();
            }
        }

        if (enemies.isEmpty()) {
            spawnEnemies(new Random().nextInt(4) + 1);
        }

        handleCollisions();
    }

    private void updateCameraPosition() {
        camera.position.set(player.getX(), player.getY(), 0);

        // Ensure the camera doesn't go out of bounds
        float halfViewportWidth = camera.viewportWidth * 0.5f;
        float halfViewportHeight = camera.viewportHeight * 0.5f;

        camera.position.x = Math.max(halfViewportWidth, camera.position.x);
        camera.position.x = Math.min(mapWidth - halfViewportWidth, camera.position.x);

        camera.position.y = Math.max(halfViewportHeight, camera.position.y);
        camera.position.y = Math.min(mapHeight - halfViewportHeight, camera.position.y);
    }

    private void handleCollisions() {
        List<Bullet> inactiveBullets = new ArrayList<>();
        for (Bullet bullet : player.getBullets()) {
            for (Enemy enemy : enemies) {
                if (bullet.intersects(enemy)) {
                    enemy.hit(100);
                    bullet.setInactive();
                    inactiveBullets.add(bullet);
                    if (!enemy.isAlive()) {
                        score += 200; // Increment the score when an enemy is defeated
                    }
                }
            }
        }
        player.getBullets().removeAll(inactiveBullets);
    }

    private void draw() {
        player.draw(batch);
        font.draw(batch, "Player HP: " + player.getHP(), 10, 20);
        font.draw(batch, "Score: " + player.getScore(), 10, 40);
        font.draw(batch, "Score: " + score, 10, 60);
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.draw(batch);
                font.draw(batch, "Enemy HP: " + enemy.getHP(), enemy.getX(), enemy.getY() - 10);
            }
        }
    }
}
