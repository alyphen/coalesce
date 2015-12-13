package com.seventh_root.coalesce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LoginScreen extends ScreenAdapter {

    private Coalesce game;
    private Stage stage;
    private Table table;
    private SpriteBatch spriteBatch;
    private Label statusLabel;
    private TextField nameField;
    private TextField passwordField;
    private Button loginButton;

    public LoginScreen(Coalesce game) {
        this.game = game;
        Skin skin = game.getSkin();
        stage = new Stage();
        table = new Table();
        table.center();
        statusLabel = new Label(game.getNetworkManager().getStatusMessage(), skin);
        table.add(statusLabel).colspan(2).center().padBottom(16);
        table.row();
        table.add(new Label("Player name: ", skin)).width(128).left().padBottom(16);
        nameField = new TextField("", skin);
        nameField.setDisabled(true);
        table.add(nameField).width(256).padBottom(16);
        table.row();
        table.add(new Label("Password: ", skin)).width(128).left().padBottom(16);
        passwordField = new TextField("", skin);
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);
        passwordField.setDisabled(true);
        table.add(passwordField).width(256).padBottom(16);
        table.row();
        loginButton = new TextButton("Login", skin);
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Coalesce game = LoginScreen.this.game;
                if (nameField.getText().length() > 0 && passwordField.getText().length() > 0) {
                    game.getNetworkManager().sendMessage("P|" + nameField.getText() + "|" + passwordField.getText());
                }
            }
        });
        loginButton.setDisabled(true);
        table.add(loginButton).colspan(2).width(256).center().padBottom(16);
        table.setFillParent(true);
        stage.addActor(table);
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        statusLabel.setText(game.getNetworkManager().getStatusMessage());
        if (game.getNetworkManager().isConnected()) {
            nameField.setDisabled(false);
            passwordField.setDisabled(false);
            loginButton.setDisabled(false);
        } else {
            nameField.setDisabled(true);
            passwordField.setDisabled(true);
            loginButton.setDisabled(true);
        }
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        spriteBatch.begin();
        spriteBatch.draw(game.getLogo(), 0, 600 - game.getLogo().getHeight());
        spriteBatch.end();
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
    public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
    }
}
