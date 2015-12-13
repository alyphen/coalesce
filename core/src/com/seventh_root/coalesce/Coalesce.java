package com.seventh_root.coalesce;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Coalesce extends Game {

    private Texture logo;
    private LoginScreen loginScreen;
    private MenuScreen menuScreen;
    private LevelScreen levelScreen;
    private NetworkManager networkManager;
    private Music menuMusic;
    private Skin skin;

    @Override
    public void create() {
        networkManager = new NetworkManager(this);
        logo = new Texture(Gdx.files.internal("logo.png"));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("ld34_var1.ogg"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        loginScreen = new LoginScreen(this);
        menuScreen = new MenuScreen(this);
        levelScreen = new LevelScreen(this);
        setScreen(loginScreen);
    }

    @Override
    public void dispose() {
        getNetworkManager().dispose();
        getLoginScreen().dispose();
        getLevelScreen().dispose();
        getMenuMusic().dispose();
        skin.dispose();
        logo.dispose();
    }

    public LoginScreen getLoginScreen() {
        return loginScreen;
    }

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    public LevelScreen getLevelScreen() {
        return levelScreen;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public Texture getLogo() {
        return logo;
    }

    public Music getMenuMusic() {
        return menuMusic;
    }

    public Skin getSkin() {
        return skin;
    }

}
