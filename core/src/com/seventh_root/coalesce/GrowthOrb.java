package com.seventh_root.coalesce;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody;
import static com.seventh_root.coalesce.Level.BOX2D_SCALE;

public class GrowthOrb implements GameObject {

    private transient Level level;
    private transient Texture texture;
    private float x;
    private float y;
    private transient Body body;

    @Override
    public void init() {
        texture = getLevel().getGrowthOrbTexture();
        createBody();
    }

    @Override
    public void tick(float delta) {
        x = body.getPosition().x * BOX2D_SCALE;
        y = body.getPosition().y * BOX2D_SCALE;
    }

    @Override
    public void render(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        spriteBatch.begin();
        spriteBatch.draw(getTexture(), getX() - (getTexture().getWidth() / 2), getY() - (getTexture().getHeight() / 2));
        spriteBatch.end();
    }

    @Override
    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Body getBody() {
        return body;
    }

    private void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = DynamicBody;
        bodyDef.position.set(getX() / BOX2D_SCALE, getY() / BOX2D_SCALE);
        body = getLevel().getWorld().createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / BOX2D_SCALE);
        body.createFixture(shape, 1F);
        body.setGravityScale(0);
        body.setUserData(this);
    }
}
