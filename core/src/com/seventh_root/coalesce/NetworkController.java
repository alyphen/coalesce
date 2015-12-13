package com.seventh_root.coalesce;

public class NetworkController extends Controller {

    public NetworkController(Player player) {
        super(player);
    }

    @Override
    public void tick() {

    }

    public void jump(float pos) {
        getPlayer().setTrackPos(pos);
        getPlayer().jump();
    }

}
