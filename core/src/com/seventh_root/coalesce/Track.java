package com.seventh_root.coalesce;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static java.lang.Math.*;

public class Track {

    private Color colour;
    private Array<Vector2> points;

    public Track() {
        colour = new Color(1F, 1F, 1F, 1F);
        points = new Array<Vector2>();
    }

    public void createLights(RayHandler rayHandler) {
        for (Vector2 point : getPoints()) {
            new PointLight(rayHandler, 128, getColour(), 64, point.x, point.y);
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(colour);
        for (int i = 0; i < points.size - 1; i++) {
            shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
        }
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public Track addPoint(Vector2 point) {
        points.add(point);
        return this;
    }

    public Track addPoint(int x, int y) {
        addPoint(new Vector2(x, y));
        return this;
    }

    public Vector2 getPoint(int index) {
        return points.get(index);
    }

    public Array<Vector2> getPoints() {
        return points;
    }

    public Vector2 getPointAt(float trackPos) {
        double dist = 0;
        double prevDist = 0;
        for (int i = 0; i < getPoints().size - 1; i++) {
            Vector2 p1 = getPoint(i);
            Vector2 p2 = getPoint(i + 1);
            float b = (p2.y - p1.y);
            float c = (p2.x - p1.x);
            double a = Math.sqrt((b * b) + (c * c));
            dist += a;
            if (dist > trackPos) {
                float angle = (float) atan((p2.y - p1.y) / (p2.x - p1.x));
                float x = (float) (p1.x + ((trackPos - prevDist) * cos(angle)));
                float y = (float) (p1.y + ((trackPos - prevDist) * sin(angle)));
                return new Vector2(x, y);
            }
            prevDist = dist;
        }
        return getPoint(getPoints().size - 1);
    }

}
