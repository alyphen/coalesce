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

    public void startBoost(float pos, int radius) {
        getPlayer().setTrackPos(pos);
        getPlayer().setRadius(radius);
        getPlayer().startBoost();
    }

    public void stopBoost(float pos, int radius) {
        getPlayer().setTrackPos(pos);
        getPlayer().setRadius(radius);
        getPlayer().stopBoost();
    }

}
