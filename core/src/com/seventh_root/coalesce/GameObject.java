package com.seventh_root.coalesce;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface GameObject {

    void tick(float delta);

    void render(ShapeRenderer shapeRenderer);

    void setLevel(Level level);

}
