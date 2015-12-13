package com.seventh_root.coalesce.server;

import io.netty.channel.Channel;
import org.goochjs.glicko2.Rating;
import org.goochjs.glicko2.RatingCalculator;
import org.goochjs.glicko2.RatingPeriodResults;

import java.sql.SQLException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.logging.Level.SEVERE;

public class GameManager {

    private CoalesceServer server;

    private Map<String, ActiveGame> activeGames;
    private Map<Player, Integer> searching;

    private RatingCalculator ratingCalculator;

    public GameManager(CoalesceServer server) {
        this.server = server;
        activeGames = new ConcurrentHashMap<>();
        searching = new ConcurrentHashMap<>();
        ratingCalculator = new RatingCalculator();
    }

    public void addGame(ActiveGame game) {
        activeGames.put(game.getPlayer1().getUUID().toString(), game);
        activeGames.put(game.getPlayer2().getUUID().toString(), game);
    }

    public void finishGame(ActiveGame game, Player winner) {
        adjustMMR(game, winner);
        InactiveGame inactiveGame = new InactiveGame(server, server.getDatabaseConnection(), game, winner);
        try {
            inactiveGame.insert();
        } catch (SQLException exception) {
            server.getLogger().log(SEVERE, "Failed to insert game", exception);
        }
    }

    private void adjustMMR(ActiveGame game, Player winner) {
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();
        Rating player1Rating = new Rating(player1.getUUID().toString(), ratingCalculator, player1.getMMR(), player1.getRatingDeviation(), player1.getVolatility());
        player1Rating.setNumberOfResults(player1.getNumberOfResults());
        Rating player2Rating = new Rating(player2.getUUID().toString(), ratingCalculator, player2.getMMR(), player2.getRatingDeviation(), player2.getVolatility());
        player2Rating.setNumberOfResults(player2.getNumberOfResults());
        RatingPeriodResults ratingPeriodResults = new RatingPeriodResults();
        if (winner.getUUID().toString().equalsIgnoreCase(player1.getUUID().toString())) {
            ratingPeriodResults.addResult(player1Rating, player2Rating);
        } else if (winner.getUUID().toString().equalsIgnoreCase(player2.getUUID().toString())) {
            ratingPeriodResults.addResult(player2Rating, player1Rating);
        }
        ratingCalculator.updateRatings(ratingPeriodResults);
        player1.setMMR(player1Rating.getRating());
        player1.setRatingDeviation(player1Rating.getRatingDeviation());
        player1.setVolatility(player1Rating.getVolatility());
        player1.setNumberOfResults(player1Rating.getNumberOfResults());
        try {
            player1.update();
        } catch (SQLException exception) {
            server.getLogger().log(SEVERE, "Failed to update player MMR", exception);
        }
        player2.setMMR(player2Rating.getRating());
        player2.setRatingDeviation(player2Rating.getRatingDeviation());
        player2.setVolatility(player2Rating.getVolatility());
        player2.setNumberOfResults(player2Rating.getNumberOfResults());
        try {
            player2.update();
        } catch (SQLException exception) {
            server.getLogger().log(SEVERE, "Failed to update player MMR", exception);
        }
    }

    public ActiveGame getGame(Player player) {
        return activeGames.get(player.getUUID().toString());
    }

    public void search(Player player) {
        searching.put(player, 0);
    }

    public void onTick() {
        for (Player player : searching.keySet()) {
            if (getGame(player) == null) {
                searching.put(player, searching.get(player) + 1);
                for (Player potentialMatch : searching.keySet()) {
                    if (potentialMatch == player) continue;
                    if (Math.abs(potentialMatch.getMMR() - player.getMMR()) < Math.min(searching.get(player), searching.get(potentialMatch))) {
                        Channel player1Channel = server.getHandler().getChannel(player);
                        Channel player2Channel = server.getHandler().getChannel(potentialMatch);
                        ActiveGame game = new ActiveGame(player, potentialMatch, player1Channel, player2Channel);
                        StringBuilder levelBuilder = new StringBuilder();
                        Random random = new Random();
                        for (int i = 0; i < 100000; i += 64) {
                            int offset = random.nextInt(256);
                            if (offset < 240) {
                                if (random.nextInt(10) == 0) {
                                    offset = 240 + random.nextInt(16);
                                }
                            }
                            levelBuilder.append(offset).append("|");
                        }
                        String message = "G|" + levelBuilder.toString() + "\n";
                        player1Channel.writeAndFlush(message);
                        player2Channel.writeAndFlush(message);
                        addGame(game);
                        break;
                    }
                }
            }
        }
    }

}
