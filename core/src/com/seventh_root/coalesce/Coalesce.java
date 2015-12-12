package com.seventh_root.coalesce;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Coalesce extends Game {

    @Override
    public void create() {
        setScreen(new LevelScreen(Gdx.files.internal("level1.json")));
    }

}
