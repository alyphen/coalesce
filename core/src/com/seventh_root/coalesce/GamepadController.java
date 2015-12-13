package com.seventh_root.coalesce;

public class GamepadController extends Controller {

    private com.badlogic.gdx.controllers.Controller controller;

    public GamepadController(Player player, com.badlogic.gdx.controllers.Controller controller) {
        super(player);
        this.controller = controller;
    }

    public GamepadController(com.badlogic.gdx.controllers.Controller controller) {
        this.controller = controller;
    }

    @Override
    public void tick() {
        if (controller.getButton(0)) {
            getPlayer().jump();
        }
        if (controller.getButton(2)) {
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
