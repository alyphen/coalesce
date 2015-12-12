package com.seventh_root.coalesce;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody;
import static com.seventh_root.coalesce.Level.BOX2D_SCALE;

public class Player implements GameObject {

    private transient Level level;
    private int track;
    private float trackPos;
    private float x;
    private float y;
    private int radius;
    private Color colour;
    private Color lineColour;
    private int speed;
    private transient Body body;
    private float gravityScale;
    private boolean detached;

    public Player(int track, Color colour) {
        this.track = track;
        this.x = level.getTrack(track).getPoint(0).x;
        this.y = level.getTrack(track).getPoint(0).y;
        this.radius = 4;
        this.colour = colour;
        this.lineColour = new Color(0.8F, 0.8F, 0.8F, 1F);
        this.speed = 120;
        this.gravityScale = 1;
        this.detached = false;
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

    public void setRadius(int radius) {
        this.radius = radius;
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

    public float getGravityScale() {
        return gravityScale;
    }

    public boolean isDetached() {
        return detached;
    }

    public void setDetached(boolean detached) {
        this.detached = detached;
        if (detached) {
            body.setLinearVelocity(0, 0);
            body.setAngularVelocity(0);
        }
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public void tick(float delta) {
        if (!isDetached()) {
            trackPos += delta * speed;
            Vector2 pos = getTrack().getPointAt(trackPos);
            body.setGravityScale(0F);
            body.setTransform(pos.x / BOX2D_SCALE, pos.y / BOX2D_SCALE, 0);
        } else {
            body.setGravityScale(getGravityScale());
            if (Math.abs(y - getTrack().getPointAt(trackPos).y) < 4 && Math.signum(body.getLinearVelocity().y) == Math.signum(getGravityScale())) {
                setDetached(false);
            }
        }
        x = body.getPosition().x * BOX2D_SCALE;
        y = body.getPosition().y * BOX2D_SCALE;
    }

    @Override
    public void render(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        shapeRenderer.begin();
        shapeRenderer.set(Filled);
        shapeRenderer.setColor(getColour());
        shapeRenderer.circle(getX(), getY(), getRadius());
        shapeRenderer.set(Line);
        shapeRenderer.setColor(getLineColour());
        shapeRenderer.circle(getX(), getY(), getRadius());
        shapeRenderer.end();
    }

    private void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = DynamicBody;
        bodyDef.position.set(getX() / BOX2D_SCALE, getY() / BOX2D_SCALE);
        body = getLevel().getWorld().createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(getRadius() / BOX2D_SCALE);
        body.createFixture(shape, 1F);
        body.setUserData(this);
    }

    public Body getBody() {
        return body;
    }

    public void jump() {
        if (Math.abs(y - getTrack().getPointAt(trackPos).y) < 4) {
            setDetached(true);
            getBody().applyLinearImpulse(0, getGravityScale() * -1000000, getX(), getY(), true);
        }
    }
}
