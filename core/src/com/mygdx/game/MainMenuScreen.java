package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Drop game;
    final int wh = 2340;
    final int ht = 1080;
    //2340×1080 - samsung m31

    OrthographicCamera camera;

    public MainMenuScreen(final Drop game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, wh, ht);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.95f, 0.85f, 0.90f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.font.getData().setScale(10);
        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Drop!!! ", 100, ht/2 + 75);
        game.font.draw(game.batch, "Tap anywhere to begin!", 100, ht/2 - 75);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
