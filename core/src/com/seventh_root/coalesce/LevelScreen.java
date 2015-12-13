package com.seventh_root.coalesce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class LevelScreen extends ScreenAdapter {

    private Coalesce game;
    private Level level;
    private Music music;
    private Sound jumpDownSound;
    private Sound jumpUpSound;

    public LevelScreen(Coalesce game) {
        this.game = game;
        music = Gdx.audio.newMusic(Gdx.files.internal("ld34.ogg"));
        jumpDownSound = Gdx.audio.newSound(Gdx.files.internal("flute_descend.wav"));
        jumpUpSound = Gdx.audio.newSound(Gdx.files.internal("flute_ascend.wav"));
        //Json json = new Json();
        //level = json.fromJson(Level.class, fileHandle.readString());
    }

    @Override
    public void render(float delta) {
        level.render(delta);
    }

    @Override
    public void show() {
        music.setLooping(false);
        music.play();
    }

    @Override
    public void hide() {
        music.stop();
    }

    public Sound getJumpUpSound() {
        return jumpUpSound;
    }

    public Sound getJumpDownSound() {
        return jumpDownSound;
    }

    @Override
    public void dispose() {
        level.dispose();
        music.dispose();
        jumpDownSound.dispose();
        jumpUpSound.dispose();
    }

    public void loadLevel(Array<Integer> offsets) {
        level = new Level(this);
        Track track1 = new Track();
        track1.setColour(new Color(0.2509804F, 0F, 0F, 1F));
        Track track2 = new Track();
        track2.setColour(new Color(0F, 0F, 0.2509804F, 1F));
        int x = 0;
        for (int offset : offsets) {
            track1.addPoint(x, 16 + offset);
            track2.addPoint(x, 584 - offset);
            if (offset > 240) {
                GrowthOrb growthOrb = new GrowthOrb();
                growthOrb.setX(x);
                growthOrb.setY(300);
                level.addObject(growthOrb);
            }
            x += 64;
        }
        level.addTrack(track1);
        level.addTrack(track2);
        Player player1 = new Player(track1, new Color(0.7019608F, 0.3019608F, 0.3019608F, 1F));
        player1.setX(track1.getPoint(0).x);
        player1.setY(track1.getPoint(0).y);
        player1.setGravityScale(-1);
        level.addObject(player1);
        level.setPlayer1(player1);
        Player player2 = new Player(track2, new Color(0.29803923F, 0.3019608F, 0.69411767F, 1F));
        player2.setX(track1.getPoint(0).x);
        player2.setY(track2.getPoint(0).y);
        player2.setGravityScale(1);
        level.addObject(player2);
        level.setPlayer2(player2);
        level.init();
    }

    public Coalesce getGame() {
        return game;
    }

}
