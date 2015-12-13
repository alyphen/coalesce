package com.seventh_root.coalesce;

import com.badlogic.gdx.Game;

public class Coalesce extends Game {

    @Override
    public void create() {
        //setScreen(new MenuScreen(this));
        setScreen(new LevelScreen(/*Gdx.files.internal("level1.json")*/));
    }

}
