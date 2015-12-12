package com.seventh_root.coalesce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Json;

public class LevelScreen extends ScreenAdapter {

    private ShapeRenderer shapeRenderer;
    private Level level;
    private OrthographicCamera camera;

    public LevelScreen(FileHandle fileHandle) {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        Json json = new Json();
        level = json.fromJson(Level.class, fileHandle.readString());
        level.init();
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
