package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.Iterator;

public class GameScreen implements Screen {

    final Drop game;

    Texture dropdogImage;
    Texture dropcatImage;
    Texture bucketImage;
    Texture ovImage;
    Sound dropdogSound;
    Sound deathdogSound;
    Sound dropcatSound;
    Sound deathcatSound;
    Music rainMusic;
    Music violinMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> dogdrops;
    Array<Rectangle> catdrops;
    long lastDropTime1;
    long lastDropTime2;
    int score;

    //settings values
    final int wh = 2340;
    final int ht = 1080;
    final int KR_size = 128;
    final int animal_size = 128;
    private int speed = 200;
    private int frequency = 1000000000;
    private int stage = -1;
    private boolean arecats = false;


    public GameScreen(final Drop game) {
        this.game = game;

        //texture - done
        dropdogImage = new Texture(Gdx.files.internal("dog.png"));
        dropcatImage = new Texture(Gdx.files.internal("cat.png"));
        bucketImage = new Texture(Gdx.files.internal("KR.png"));
        ovImage = new Texture(Gdx.files.internal("ov.png"));

        //music - done
        dropdogSound = Gdx.audio.newSound(Gdx.files.internal("dog.wav"));
        deathdogSound = Gdx.audio.newSound(Gdx.files.internal("d_dog.wav"));
        dropcatSound = Gdx.audio.newSound(Gdx.files.internal("cat.wav"));
        deathcatSound = Gdx.audio.newSound(Gdx.files.internal("d_cat.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        violinMusic = Gdx.audio.newMusic(Gdx.files.internal("violin.mp3"));
        rainMusic.setLooping(true);
        violinMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, wh, ht);

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = wh / 2 - KR_size / 2; // center the bucket horizontally
        bucket.y = 50; // bottom left corner of the bucket is 20 pixels above
        // the bottom screen edge
        bucket.width = KR_size;
        bucket.height = KR_size;

        // create the raindrops array and spawn the first raindrop
        dogdrops = new Array<Rectangle>();
        spawndogdrop();

        catdrops = new Array<Rectangle>();
        if(arecats) spawncatdrop();

    }

    private void spawndogdrop() {
        Rectangle dogdrop = new Rectangle();
        dogdrop.x = MathUtils.random(0, wh - animal_size);
        dogdrop.y = ht;
        dogdrop.width = animal_size;
        dogdrop.height = animal_size;
        dogdrops.add(dogdrop);
        lastDropTime1 = TimeUtils.nanoTime();
    }

    private void spawncatdrop() {
        Rectangle catdrop = new Rectangle();
        catdrop.x = MathUtils.random(0, wh - animal_size);
        catdrop.y = ht;
        catdrop.width = animal_size;
        catdrop.height = animal_size;
        catdrops.add(catdrop);
        lastDropTime2 = TimeUtils.nanoTime();
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        rainMusic.play();
        violinMusic.play();
    }

    private void setstage(int stage){
        switch (stage){
            case (0):
                ScreenUtils.clear(0.9f, 0.6f, 0.75f, 1);
                break;
            case (1):
                ScreenUtils.clear(1f, 0.5f, 0.6f, 1);
                arecats = true;
                frequency = 750000000;
                speed = 300;
                break;
            case (2):
                ScreenUtils.clear(1f, 0.1f, 0.1f, 1);
                frequency = 500000000;
                speed = 500;
                break;
            default:
                break;
        }
    }

    @Override
    public void render(float delta) {
        int k_random = MathUtils.random(-25, 25);
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        if(stage == -1) {
            stage = 0;
            setstage(stage);
        }

        game.batch.begin();
        game.font.getData().setScale(5);
        game.font.draw(game.batch, "Score: " + score, 20, ht - 20);
        game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
        for (Rectangle raindrop : dogdrops) {
            game.batch.draw(dropdogImage, raindrop.x, raindrop.y);
        }
        for (Rectangle raindrop : catdrops) {
            game.batch.draw(dropcatImage, raindrop.x, raindrop.y);
        }
        //if(raindrop.overlaps(bucket)) game.batch.draw(ovImage, raindrop.x, raindrop.y + 128);
        //why?
        game.batch.end();

        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = (int) (touchPos.x - KR_size / 2);
        }

        // make sure the bucket stays within the screen bounds
        if (bucket.x < 0)
            bucket.x = 0;
        if (bucket.x > wh - KR_size)
            bucket.x = wh - KR_size;

        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime1 > frequency * (double)(100+k_random)/100)
            spawndogdrop();

        if (TimeUtils.nanoTime() - lastDropTime2 > 2 * frequency * (double)(100+k_random)/100 && arecats)
            spawncatdrop();

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we increase the
        // value our drops counter and add a sound effect.

        Iterator<Rectangle> iter = dogdrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= speed * Gdx.graphics.getDeltaTime();
            if (raindrop.y + animal_size < 0) {
                deathdogSound.play();
                if(score > 5)
                    score -= 5;
                iter.remove();
            }
            if (raindrop.overlaps(bucket)) {
                score++;
                dropdogSound.play();
                iter.remove();
            }
        }

        Iterator<Rectangle> iter2 = catdrops.iterator();
        while (iter2.hasNext()) {
            Rectangle raindrop = iter2.next();
            raindrop.y -= 2 * speed * Gdx.graphics.getDeltaTime();
            if (raindrop.y + animal_size < 0) {
                deathcatSound.play();
                if(score > 25)
                    score -= 25;
                iter2.remove();
            }
            if (raindrop.overlaps(bucket)) {
                score+= 5;
                dropcatSound.play();
                iter2.remove();
            }
        }

        if (score > 0 && score < 25 && stage == 1) {
            stage = 0;
            setstage(stage);
        }
        if((score >= 25 && score < 100) && (stage == 0 || stage == 2)){
            stage = 1;
            setstage(stage);
        }
        if(score >= 100 && stage == 1){
            stage = 2;
            setstage(stage);
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
        dropdogImage.dispose();
        dropcatImage.dispose();
        bucketImage.dispose();
        ovImage.dispose();
        dropdogSound.dispose();
        dropcatSound.dispose();
        deathdogSound.dispose();
        deathcatSound.dispose();
        rainMusic.dispose();
        violinMusic.dispose();
    }
}
