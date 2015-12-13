package com.seventh_root.coalesce;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Level {

    public static final int BOX2D_SCALE = 1;

    private LevelScreen screen;

    private transient SpriteBatch spriteBatch;
    private transient ShapeRenderer shapeRenderer;
    private transient OrthographicCamera camera;

    private transient Texture growthOrbTexture;

    private transient World world;
    private Array<Track> tracks;
    private Array<GameObject> objects;
    private Player player1;
    private Player player2;
    private transient RayHandler rayHandler;
    private transient Array<Controller> controllers;
    private transient Array<Body> bodiesToDestroy;

    public Level(LevelScreen screen) {
        this.screen = screen;
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        camera = new OrthographicCamera();
        camera.setToOrtho(true, 800, 600);
        growthOrbTexture = new Texture(Gdx.files.internal("growth_orb.png"));
        Box2D.init();
        world = new World(new Vector2(0F, 98.1F), false);
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Player player = null;
                GrowthOrb growthOrb = null;
                if (contact.getFixtureA().getBody().getUserData() instanceof Player) {
                    player = (Player) contact.getFixtureA().getBody().getUserData();
                } else if (contact.getFixtureA().getBody().getUserData() instanceof GrowthOrb) {
                    growthOrb = (GrowthOrb) contact.getFixtureA().getBody().getUserData();
                }
                if (contact.getFixtureB().getBody().getUserData() instanceof Player) {
                    player = (Player) contact.getFixtureB().getBody().getUserData();
                } else if (contact.getFixtureB().getBody().getUserData() instanceof GrowthOrb) {
                    growthOrb = (GrowthOrb) contact.getFixtureB().getBody().getUserData();
                }
                if (player != null && growthOrb != null) {
                    bodiesToDestroy.add(growthOrb.getBody());
                    getObjects().removeValue(growthOrb, true);
                    player.setRadius(player.getRadius() + 8);
                    player.setSpeed(player.getSpeed() - 5);
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
        tracks = new Array<Track>();
        objects = new Array<GameObject>();
        rayHandler = new RayHandler(world);
        rayHandler.setShadows(false);
        controllers = new Array<Controller>();
        bodiesToDestroy = new Array<Body>();
    }

    public LevelScreen getScreen() {
        return screen;
    }

    public void init() {
        Array<Controller> potentialControllers = new Array<Controller>();
        potentialControllers.add(new KeyboardController());
        for (int i = 0; i < Controllers.getControllers().size; i++) {
            potentialControllers.add(new GamepadController(Controllers.getControllers().get(i)));
        }
        int nextAvailableControllerIndex = 0;
        for (GameObject object : getObjects()) {
            object.setLevel(this);
            object.init();
            if (object instanceof Player) {
                if (nextAvailableControllerIndex < potentialControllers.size) {
                    Controller nextAvailableController = potentialControllers.get(nextAvailableControllerIndex++);
                    nextAvailableController.setPlayer((Player) object);
                    controllers.add(nextAvailableController);
                }
            }
        }
        createLights();
    }

    public void destroy(Body body) {
        bodiesToDestroy.add(body);
    }

    public void createLights() {
        for (Track track : getTracks()) {
            track.createLights(getRayHandler());
        }
    }

    public void render(float delta) {
        for (Controller controller : getControllers()) {
            controller.tick();
        }
        getWorld().step(delta, 6, 2);
        for (GameObject object : getObjects()) {
            object.tick(delta);
        }
        Array<Body> bodiesRemoved = new Array<Body>();
        for (Body body : bodiesToDestroy) {
            Array<Body> bodies = new Array<Body>();
            getWorld().getBodies(bodies);
            if (bodies.contains(body, true)) {
                if (!getWorld().isLocked()) {
                    getWorld().destroyBody(body);
                    bodiesRemoved.add(body);
                }
            }
        }
        bodiesToDestroy.removeAll(bodiesRemoved, true);
        float targetX = (player1.getX() + player2.getX()) / 2;
        float targetY = (player1.getY() + player2.getY()) / 2;
        camera.position.set(targetX, targetY, 0);
        float dist = (float) Math.sqrt(Math.pow(player2.getX() - player1.getX(), 2) + Math.pow(player2.getY() - player1.getY(), 2));
        float targetZoom = dist / 400F;
        camera.zoom = targetZoom;
        camera.update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);
        for (Track track : getTracks()) {
            track.render(shapeRenderer);
        }
        for (GameObject object : getObjects()) {
            object.render(spriteBatch, shapeRenderer);
        }
        getRayHandler().setCombinedMatrix(camera);
        getRayHandler().updateAndRender();
    }

    public Texture getGrowthOrbTexture() {
        return growthOrbTexture;
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

    public Array<Controller> getControllers() {
        return controllers;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

}
