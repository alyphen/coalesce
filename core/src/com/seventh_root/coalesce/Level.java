package com.seventh_root.coalesce;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Level {

    private transient ShapeRenderer shapeRenderer;
    private transient OrthographicCamera camera;

    private transient World world;
    private Array<Track> tracks;
    private Array<GameObject> objects;
    private transient RayHandler rayHandler;

    public Level() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        camera = new OrthographicCamera();
        camera.setToOrtho(true, 800, 600);
        Box2D.init();
        world = new World(new Vector2(0F, 0F), true);
        tracks = new Array<Track>();
        objects = new Array<GameObject>();
        rayHandler = new RayHandler(world);
        rayHandler.setShadows(false);
    }

    public void init() {
        for (GameObject object : getObjects()) {
            object.setLevel(this);
            object.init();
        }
        createLights();
    }

    public void createLights() {
        for (Track track : getTracks()) {
            track.createLights(getRayHandler());
        }
    }

    public void render(float delta) {
        getWorld().step(delta, 6, 2);
        for (GameObject object : getObjects()) {
            object.tick(delta);
        }
        camera.update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin();
        for (Track track : getTracks()) {
            track.render(shapeRenderer);
        }
        for (GameObject object : getObjects()) {
            object.render(shapeRenderer);
        }
        shapeRenderer.end();
        getRayHandler().setCombinedMatrix(camera);
        getRayHandler().updateAndRender();
    }

    public World getWorld() {
        return world;
    }

    public Array<Track> getTracks() {
        return tracks;
    }

    public Track getTrack(int index) {
        return getTracks().get(index);
    }

    public void addTrack(Track track) {
        getTracks().add(track);
    }

    public Array<GameObject> getObjects() {
        return objects;
    }

    public void addObject(GameObject object) {
        getObjects().add(object);
    }

    public RayHandler getRayHandler() {
        return rayHandler;
    }

}
