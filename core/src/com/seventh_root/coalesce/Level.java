package com.seventh_root.coalesce;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Level {

    private World world;
    private Array<Track> tracks;
    private Array<GameObject> objects;
    private RayHandler rayHandler;

    public Level() {
        world = new World(new Vector2(0F, 9.81F), true);
        tracks = new Array<Track>();
        objects = new Array<GameObject>();
        rayHandler = new RayHandler(world);
    }

    public void createLights() {
        for (Track track : getTracks()) {
            track.createLights(getRayHandler());
        }
    }

    public void render(float delta, OrthographicCamera camera, ShapeRenderer shapeRenderer) {
        for (GameObject object : getObjects()) {
            object.tick(delta);
        }
        for (Track track : getTracks()) {
            track.render(shapeRenderer);
        }
        for (GameObject object : getObjects()) {
            object.render(shapeRenderer);
        }
        getRayHandler().setCombinedMatrix(camera);
        getRayHandler().updateAndRender();
    }

    public Array<Track> getTracks() {
        return tracks;
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
