package com.seventh_root.coalesce.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private CoalesceServer server;
    private Connection databaseConnection;

    private Map<String, Player> playersByUUID;
    private Map<String, Player> playersByName;

    public PlayerManager(CoalesceServer server, Connection databaseConnection) {
        this.server = server;
        this.databaseConnection = databaseConnection;
        playersByUUID = new HashMap<>();
        playersByName = new HashMap<>();
    }

    public void cachePlayer(Player player) {
        playersByUUID.put(player.getUUID().toString(), player);
        playersByName.put(player.getName(), player);
    }

    public void uncachePlayer(Player player) {
        playersByUUID.remove(player.getUUID().toString());
        playersByName.remove(player.getName());
    }

    public Player getByUUID(UUID uuid) throws SQLException {
        if (playersByUUID.containsKey(uuid.toString())) return playersByUUID.get(uuid.toString());
        if (databaseConnection != null) {
            PreparedStatement statement = databaseConnection.prepareStatement(
                    "SELECT `uuid`, `name`, `password_hash`, `password_salt`, `mmr`, `rating_deviation`, `volatility`, `number_of_results` FROM `player` WHERE `uuid` = ? LIMIT 1"
            );
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Player player = new Player(this, databaseConnection, UUID.fromString(resultSet.getString("uuid")), resultSet.getString("name"), resultSet.getString("password_hash"), resultSet.getString("password_salt"), resultSet.getInt("mmr"), resultSet.getDouble("rating_deviation"), resultSet.getDouble("volatility"), resultSet.getInt("number_of_results"));
                cachePlayer(player);
                return player;
            }
        }
        return null;
    }

    public Player getByName(String playerName) throws SQLException {
        if (playersByName.containsKey(playerName)) return playersByName.get(playerName);
        if (databaseConnection != null) {
            PreparedStatement statement = databaseConnection.prepareStatement(
                    "SELECT `uuid`, `name`, `password_hash`, `password_salt`, `mmr`, `rating_deviation`, `volatility`, `number_of_results` FROM `player` WHERE `name` = ? LIMIT 1"
            );
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Player(this, databaseConnection, UUID.fromString(resultSet.getString("uuid")), resultSet.getString("name"), resultSet.getString("password_hash"), resultSet.getString("password_salt"), resultSet.getInt("mmr"), resultSet.getDouble("rating_deviation"), resultSet.getDouble("volatility"), resultSet.getInt("number_of_results"));
            }
        }
        return null;
    }

    public CoalesceServer getServer() {
        return server;
    }
}
