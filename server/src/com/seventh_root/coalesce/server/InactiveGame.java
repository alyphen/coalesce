package com.seventh_root.coalesce.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static java.util.logging.Level.SEVERE;

public class InactiveGame {

    private CoalesceServer server;

    private UUID uuid;
    private Player player1;
    private Player player2;
    private Player winner;
    private long timestamp;

    public InactiveGame(CoalesceServer server, ActiveGame activeGame, Player winner) {
        this.server = server;
        this.uuid = UUID.randomUUID();
        this.player1 = activeGame.getPlayer1();
        this.player2 = activeGame.getPlayer2();
        this.winner = winner;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getWinner() {
        return winner;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void insert() throws SQLException {
        Connection databaseConnection = server.getDatabaseConnection();
        try (PreparedStatement statement = databaseConnection.prepareStatement("INSERT INTO `game`(`uuid`, `player1_uuid`, `player2_uuid`, `winner_uuid`, `timestamp`) VALUES(?, ?, ?, ?, ?)")) {
            statement.setString(1, getUUID().toString());
            statement.setString(2, getPlayer1().getUUID().toString());
            statement.setString(3, getPlayer2().getUUID().toString());
            statement.setString(4, getWinner().getUUID().toString());
            statement.setLong(5, getTimestamp());
            statement.executeUpdate();
        } catch (SQLException exception) {
            server.getLogger().log(SEVERE, "Failed to insert game", exception);
        } finally {
            databaseConnection.close();
        }
    }

    public void update() throws SQLException {
        Connection databaseConnection = server.getDatabaseConnection();
        try (PreparedStatement statement = databaseConnection.prepareStatement("UPDATE `game` SET `player1_uuid` = ?, `player2_uuid` = ?, `winner_uuid` = ?, `timestamp` = ? WHERE `uuid` = ?")) {
            statement.setString(1, getPlayer1().getUUID().toString());
            statement.setString(2, getPlayer2().getUUID().toString());
            statement.setString(3, getWinner().getUUID().toString());
            statement.setLong(4, getTimestamp());
            statement.setString(5, getUUID().toString());
            statement.executeUpdate();
        } catch (SQLException exception) {
            server.getLogger().log(SEVERE, "Failed to update game", exception);
        } finally {
            databaseConnection.close();
        }
    }

    public void delete() throws SQLException {
        Connection databaseConnection = server.getDatabaseConnection();
        try (PreparedStatement statement = databaseConnection.prepareStatement("DELETE FROM `game` WHERE `uuid` = ?")) {
            statement.setString(1, getUUID().toString());
            statement.executeUpdate();
        } catch (SQLException exception) {
            server.getLogger().log(SEVERE, "Failed to delete game", exception);
        } finally {
            databaseConnection.close();
        }
    }

}
