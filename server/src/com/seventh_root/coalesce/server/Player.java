package com.seventh_root.coalesce.server;

import org.apache.commons.lang.RandomStringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static java.util.logging.Level.SEVERE;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

public class Player {

    private Connection databaseConnection;
    private PlayerManager playerManager;

    private UUID uuid;
    private String name;
    private String passwordHash;
    private String passwordSalt;
    private double mmr;
    private double ratingDeviation;
    private double volatility;
    private int numberOfResults;

    public Player(PlayerManager playerManager, Connection databaseConnection, String name, String password) throws SQLException {
        this.playerManager = playerManager;
        this.databaseConnection = databaseConnection;
        this.name = name;
        setPassword(password);
        this.mmr = 1500;
        this.ratingDeviation = 350;
        this.volatility = 0.06;
        this.numberOfResults = 0;
        insert();
    }

    public Player(PlayerManager playerManager, Connection databaseConnection, UUID uuid, String name, String passwordHash, String passwordSalt, double mmr) {
        this.playerManager = playerManager;
        this.databaseConnection = databaseConnection;
        this.uuid = uuid;
        this.name = name;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.mmr = mmr;
    }

    public Player(UUID uuid, String name, double mmr) {
        this.uuid = uuid;
        this.name = name;
        this.mmr = mmr;
    }

    public Connection getDatabaseConnection() {
        return databaseConnection;
    }

    public UUID getUUID() {
        return uuid;
    }

    private void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws SQLException {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPassword(String password) throws SQLException {
        passwordSalt = RandomStringUtils.randomAlphanumeric(32);
        passwordHash = sha256Hex(password + passwordSalt);
    }

    public boolean checkPassword(String password) {
        return sha256Hex(password + getPasswordSalt()).equals(getPasswordHash());
    }

    public double getMMR() {
        return mmr;
    }

    public void setMMR(double mmr) {
        this.mmr = mmr;
    }

    public double getRatingDeviation() {
        return ratingDeviation;
    }

    public void setRatingDeviation(double ratingDeviation) {
        this.ratingDeviation = ratingDeviation;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(int numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public void insert() throws SQLException {
        try (
        PreparedStatement statement = getDatabaseConnection().prepareStatement(
                "INSERT INTO `player`(`uuid`, `name`, `password_hash`, `password_salt`, `mmr`, `rating_deviation`, `volatility`, `number_of_results`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)"
        )) {
            setUUID(UUID.randomUUID());
            statement.setString(1, getUUID().toString());
            statement.setString(2, getName());
            statement.setString(3, getPasswordHash());
            statement.setString(4, getPasswordSalt());
            statement.setDouble(5, getMMR());
            statement.setDouble(6, getRatingDeviation());
            statement.setDouble(7, getVolatility());
            statement.setInt(8, getNumberOfResults());
            statement.executeUpdate();
            playerManager.cachePlayer(this);
        } catch (SQLException exception) {
            playerManager.getServer().getLogger().log(SEVERE, "Failed to insert player", exception);
        }
    }

    public void update() throws SQLException {
        try (PreparedStatement statement = getDatabaseConnection().prepareStatement(
                "UPDATE `player` SET `name` = ?, `password_hash` = ?, `password_salt` = ?, `mmr` = ?, `rating_deviation` = ?, `volatility` = ?, `number_of_results` = ? WHERE `uuid` = ?"
        )) {
            statement.setString(1, getName());
            statement.setString(2, getPasswordHash());
            statement.setString(3, getPasswordSalt());
            statement.setDouble(4, getMMR());
            statement.setDouble(5, getRatingDeviation());
            statement.setDouble(6, getVolatility());
            statement.setInt(7, getNumberOfResults());
            statement.setString(8, getUUID().toString());
            statement.executeUpdate();
        } catch (SQLException exception) {
            playerManager.getServer().getLogger().log(SEVERE, "Failed to update player", exception);
        }
    }

    public void delete() throws SQLException {
        try (PreparedStatement statement = getDatabaseConnection().prepareStatement(
                "DELETE FROM `player` WHERE `uuid` = ?"
        )) {
            statement.setString(1, getUUID().toString());
            statement.executeUpdate();
            playerManager.uncachePlayer(this);
        } catch (SQLException exception) {
            playerManager.getServer().getLogger().log(SEVERE, "Failed to delete player", exception);
        }
    }

}
