package com.seventh_root.coalesce;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class LevelScreen extends ScreenAdapter {

    private Level level;

    public LevelScreen(FileHandle fileHandle) {
        Json json = new Json();
        level = json.fromJson(Level.class, fileHandle.readString());
        level.init();
    }

    @Override
    public void render(float delta) {
        level.render(delta);
    }

}
