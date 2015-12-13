package com.seventh_root.coalesce.server;

import io.netty.channel.Channel;

public class ActiveGame {

    private GameManager gameManager;

    private Player player1;
    private Player player2;
    private Channel player1Channel;
    private Channel player2Channel;
    private long timestamp;
    private int player1Score;
    private int player2Score;

    public ActiveGame(GameManager gameManager, Player player1, Player player2, Channel player1Channel, Channel player2Channel) {
        this.gameManager = gameManager;
        this.player1 = player1;
        this.player2 = player2;
        this.player1Channel = player1Channel;
        this.player2Channel = player2Channel;
        timestamp = System.currentTimeMillis();
        player1Score = -1;
        player2Score = -1;
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

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
        if (player2Score > -1) {
            if (player2Score > player1Score) {
                gameManager.finishGame(this, getPlayer2());
            } else if (player1Score > player2Score) {
                gameManager.finishGame(this, getPlayer1());
            }
        }
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
        if (player1Score > -1) {
            if (player2Score > player1Score) {
                gameManager.finishGame(this, getPlayer2());
            } else if (player1Score > player2Score) {
                gameManager.finishGame(this, getPlayer1());
            }
        }
    }

}
