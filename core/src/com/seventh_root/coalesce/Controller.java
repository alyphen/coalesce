package com.seventh_root.coalesce;

public abstract class Controller {

    private Player player;

    public Controller(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract void tick();

}
