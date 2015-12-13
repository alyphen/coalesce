package com.seventh_root.coalesce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class KeyboardController extends Controller {

    public KeyboardController(Player player) {
        super(player);
    }

    public KeyboardController() {

    }

    public void tick() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            getPlayer().jump();
            getPlayer().getLevel().getScreen().getGame().getNetworkManager().sendMessage("J|" + getPlayer().getTrackPos());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (!getPlayer().isBoost()) {
                getPlayer().startBoost();
            }
        } else {
            if (getPlayer().isBoost()) {
                getPlayer().stopBoost();
            }
        }
    }

}
