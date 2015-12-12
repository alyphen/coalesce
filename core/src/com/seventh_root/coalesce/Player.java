package com.seventh_root.coalesce;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line;

public class Player implements GameObject {

    private Track track;
    private float trackPos;
    private float x;
    private float y;
    private int radius;
    private Color colour;
    private Color lineColour;

    public Player(Track track, Color colour) {
        this.track = track;
        this.x = track.getPoint(0).x;
        this.y = track.getPoint(0).y;
        this.radius = 4;
        this.colour = colour;
        this.lineColour = new Color(0.8F, 0.8F, 0.8F, 1F);
    }

    public Track getTrack() {
        return track;
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

    @Override
    public void tick(float delta) {
        trackPos += delta * 10;
        Vector2 pos = getTrack().getPointAt(trackPos);
        x = pos.x;
        y = pos.y;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.set(Filled);
        shapeRenderer.setColor(getColour());
        shapeRenderer.circle(getX(), getY(), getRadius());
        shapeRenderer.set(Line);
        shapeRenderer.setColor(getLineColour());
        shapeRenderer.circle(getX(), getY(), getRadius());
    }

}
