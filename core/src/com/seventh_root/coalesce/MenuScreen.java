package com.seventh_root.coalesce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class MenuScreen extends ScreenAdapter {

    private Coalesce game;
    private Stage stage;
    private Table table;
    private String playerName;
    private int mmr;
    private SpriteBatch spriteBatch;

    private Label playerNameLabel;
    private Button logoutButton;
    private Label mmrLabel;
    private SelectBox<String> controllerSelectBox;
    //private Table matchesTable;
    //private TextArea chatTextArea;
    //private TextField chatTextField;
    //private Button chatButton;
    //private Button localButton;
    //private Button unrankedButton;
    private Button rankedButton;

    public MenuScreen(Coalesce game) {
        this.game = game;
        Skin skin = game.getSkin();
        stage = new Stage();
        table = new Table();
        table.top().padTop(128);
        playerNameLabel = new Label("", skin);
        table.add(playerNameLabel).width(256).left().padLeft(16).padRight(128).padTop(16).padBottom(16);
        mmrLabel = new Label("mmr", skin);
        table.add(mmrLabel).width(256).right().padLeft(128).padRight(16).padTop(16).padBottom(16);
        table.row();
        logoutButton = new TextButton("Logout", skin);
        table.add(logoutButton).width(256).left().padLeft(16);
        controllerSelectBox = new SelectBox<String>(skin);
        Array<String> gamepadOptions = new Array<String>();
        gamepadOptions.add("Keyboard");
        for (int i = 0; i < Controllers.getControllers().size; i++) {
            Controller controller = Controllers.getControllers().get(i);
            gamepadOptions.add(i + "| " + controller.getName());
        }
        controllerSelectBox.setItems(gamepadOptions);
        table.add(controllerSelectBox).width(256).right().padLeft(128).padRight(16);
        table.row();
        rankedButton = new TextButton("Ranked", skin);
        rankedButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Coalesce game = MenuScreen.this.game;
                rankedButton.setDisabled(true);
                game.getNetworkManager().sendMessage("S");
            }
        });
        table.add(rankedButton).height(64).width(128).colspan(3).center().padTop(16).padBottom(16);
        table.setFillParent(true);
        stage.addActor(table);
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        playerNameLabel.setText("Logged in as: " + playerName);
        mmrLabel.setText(mmr + "mmr");
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        spriteBatch.begin();
        spriteBatch.draw(game.getLogo(), 0, 600 - game.getLogo().getHeight());
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        if (!game.getMenuMusic().isPlaying()) {
            game.getMenuMusic().setLooping(true);
            game.getMenuMusic().play();
        }
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        game.getMenuMusic().stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setMMR(int mmr) {
        this.mmr = mmr;
    }

    public String getSelectedController() {
        return controllerSelectBox.getSelected();
    }

}
