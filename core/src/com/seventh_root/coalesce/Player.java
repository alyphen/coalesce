package com.seventh_root.coalesce;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody;

public class Player implements GameObject {

    private transient Level level;
    private int track;
    private float trackPos;
    private float x;
    private float y;
    private float yOffset;
    private int radius;
    private Color colour;
    private Color lineColour;
    private int speed;
    private transient Body body;

    public Player(int track, Color colour) {
        this.track = track;
        this.x = level.getTrack(track).getPoint(0).x;
        this.y = level.getTrack(track).getPoint(0).y;
        this.yOffset = 0F;
        this.radius = 4;
        this.colour = colour;
        this.lineColour = new Color(0.8F, 0.8F, 0.8F, 1F);
        this.speed = 120;
    }

    public Player() {
    }

    public void init() {
        createBody();
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Track getTrack() {
        return level.getTrack(track);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    public Color getColour() {
        return colour;
    }

    public Color getLineColour() {
        return lineColour;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public void tick(float delta) {
        trackPos += delta * speed;
        Vector2 pos = getTrack().getPointAt(trackPos);
        x = pos.x;
        y = pos.y + yOffset;
        body.getPosition().x = x;
        body.getPosition().y = y;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.set(Filled);
        shapeRenderer.setColor(getColour());
        shapeRenderer.circle(getX(), getY(), getRadius());
        shapeRenderer.set(Line);
        shapeRenderer.setColor(getLineColour());
        shapeRenderer.circle(getX(), getY(), getRadius());
    }

    private void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = StaticBody;
        bodyDef.position.set(getX(), getY());
        body = getLevel().getWorld().createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(getRadius());
        body.createFixture(shape, 0.0F);
    }

}
