package com.seventh_root.coalesce.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.System.currentTimeMillis;
import static java.util.logging.Level.SEVERE;

public class CoalesceServer {

    private static final long DELAY = 250L;
    private boolean running;

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new CoalesceServer().start();
            }
        }).start();
    }

    private CoalesceServerHandler handler;
    private Config config;
    private Connection databaseConnection;
    private Logger logger;
    private GameManager gameManager;
    private PlayerManager playerManager;

    public CoalesceServer() {
        logger = Logger.getLogger(getClass().getCanonicalName());
        loadConfig();
        try {
            databaseConnection = DriverManager.getConnection(
                    "jdbc:mysql://" + getConfig().getMap("database").get("url") + "/" + getConfig().getMap("database").get("database"),
                    (String) getConfig().getMap("database").get("user"),
                    (String) getConfig().getMap("database").get("password")
            );
        } catch (SQLException exception) {
            getLogger().log(SEVERE, "Failed to connect to database", exception);
        }
        gameManager = new GameManager(this);
        playerManager = new PlayerManager(getDatabaseConnection());
    }

    public Config getConfig() {
        return config;
    }

    public Connection getDatabaseConnection() {
        return databaseConnection;
    }

    public Logger getLogger() {
        return logger;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    private void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            handler = new CoalesceServerHandler(this);
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()),
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    handler
                            );
                        }
                    });
            Channel channel = bootstrap.bind(getConfig().getInt("port", 30512)).sync().channel();
            setRunning(true);
            long beforeTime, timeDiff, sleep;
            beforeTime = currentTimeMillis();
            while (isRunning()) {
                doTick();
                timeDiff = currentTimeMillis() - beforeTime;
                sleep = DELAY - timeDiff;
                if (sleep > 0) {
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException exception) {
                        getLogger().log(SEVERE, "Thread interrupted", exception);
                    }
                }
                beforeTime = currentTimeMillis();
            }
            channel.closeFuture().sync();
        } catch (InterruptedException exception) {
            getLogger().log(SEVERE, "Event loop group interrupted", exception);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void doTick() {
        getGameManager().onTick();
        getHandler().getChannels().writeAndFlush("K\n");
    }

    public void loadConfig() {
        File configFile = new File("./config.json");
        try {
            saveDefaultConfig(configFile);
        } catch (IOException exception) {
            getLogger().log(SEVERE, "Failed to create default config", exception);
        }
        try {
            config = Config.load(configFile);
        } catch (IOException exception) {
            getLogger().log(SEVERE, "Failed to load configuration", exception);
        }
    }

    public void saveDefaultConfig(File configFile) throws IOException {
        if (!configFile.exists()) {
            Config defaultConfig = new Config();
            defaultConfig.set("port", 30512);
            Map<String, Object> databaseSettings = new HashMap<>();
            databaseSettings.put("url", "localhost");
            databaseSettings.put("database", "coalesce");
            databaseSettings.put("user", "coalesce");
            databaseSettings.put("password", "secret");
            defaultConfig.set("database", databaseSettings);
            defaultConfig.save(configFile);
        }
    }

    public void saveDefaultConfig() throws IOException {
        saveDefaultConfig(new File("./config.json"));
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    public CoalesceServerHandler getHandler() {
        return handler;
    }

}
