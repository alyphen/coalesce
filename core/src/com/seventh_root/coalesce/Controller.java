package com.seventh_root.coalesce;

public abstract class Controller {

    private Player player;

    public Controller(Player player) {
        this.player = player;
    }

    public Controller() {

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public abstract void tick();

}
