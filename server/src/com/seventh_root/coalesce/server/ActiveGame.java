package com.seventh_root.coalesce.server;

import io.netty.channel.Channel;

public class ActiveGame {

    private Player player1;
    private Player player2;
    private Channel player1Channel;
    private Channel player2Channel;
    private long timestamp;

    public ActiveGame(Player player1, Player player2, Channel player1Channel, Channel player2Channel) {
        this.player1 = player1;
        this.player2 = player2;
        this.player1Channel = player1Channel;
        this.player2Channel = player2Channel;
        timestamp = System.currentTimeMillis();
    }

    public void sendMessageToPlayer2(String message) {
        player2Channel.writeAndFlush(message + "\n");
    }

    public void sendMessageToPlayer1(String message) {
        player1Channel.writeAndFlush(message + "\n");
    }

    public void sendMessageToOtherPlayer(Player sendingPlayer, String message) {
        if (sendingPlayer != player1) {
            sendMessageToPlayer1(message);
        } else if (sendingPlayer != player2) {
            sendMessageToPlayer2(message);
        }
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Channel getPlayer1Channel() {
        return player1Channel;
    }

    public Channel getPlayer2Channel() {
        return player2Channel;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
