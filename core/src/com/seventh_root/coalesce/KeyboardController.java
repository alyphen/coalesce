package com.seventh_root.coalesce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class KeyboardController extends Controller {

    public KeyboardController(Player player) {
        super(player);
    }

    public void tick() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            getPlayer().setDetached(true);
            getPlayer().getBody().applyLinearImpulse(0, getPlayer().getGravityScale() * -1000000, getPlayer().getX(), getPlayer().getY(), true);
        }
    }

}
