package com.seventh_root.coalesce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class MenuScreen extends ScreenAdapter {

    private Music music;
    private Stage stage;
    private Table table;

    public MenuScreen(Coalesce game) {
        music = Gdx.audio.newMusic(Gdx.files.internal("ld34_var1.ogg"));
        stage = new Stage();
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void show() {
        music.setLooping(true);
        music.play();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        music.stop();
    }
}
