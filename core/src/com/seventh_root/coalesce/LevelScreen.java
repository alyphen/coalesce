package com.seventh_root.coalesce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LevelScreen extends ScreenAdapter {

    private ShapeRenderer shapeRenderer;
    private Level level;
    private OrthographicCamera camera;

    public LevelScreen(FileHandle fileHandle) {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        //Json json = new Json();
        //level = json.fromJson(Level.class, fileHandle.readString());
        level = new Level();
        Track track1 = new Track();
        track1.setColour(new Color(0.2509804F, 0F, 0F, 1F));
        track1.addPoint(16, 16)
                .addPoint(64, 16)
                .addPoint(96, 32);
        level.addTrack(track1);
        Track track2 = new Track();
        track2.setColour(new Color(0F, 0F, 0.2509804F, 1F));
        track2.addPoint(16, 480)
                .addPoint(64, 480)
                .addPoint(96, 496);
        level.addTrack(track2);
        level.addObject(new Player(track1, new Color(0.7019608F, 0.3019608F, 0.3019608F, 1F)));
        level.addObject(new Player(track2, new Color(0.29803923F, 0.3019608F, 0.69411767F, 1F)));
        level.createLights();
        camera = new OrthographicCamera();
        camera.setToOrtho(true, 800, 600);
    }

    @Override
    public void render(float delta) {
        camera.update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin();
        level.render(delta, camera, shapeRenderer);
        shapeRenderer.end();
    }

}
