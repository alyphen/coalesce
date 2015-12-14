package com.seventh_root.coalesce.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;
import java.sql.SQLException;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

@Sharable
public class CoalesceServerHandler extends SimpleChannelInboundHandler<String> {

    private CoalesceServer server;
    private ChannelGroup channels;

    private static final AttributeKey<Player> PLAYER_ATTRIBUTE_KEY = AttributeKey.valueOf("PLAYER");

    public CoalesceServerHandler(CoalesceServer server) {
        this.server = server;
        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        server.getLogger().log(INFO, "Player connected from " + ipAddress(ctx.channel()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Player player = ctx.channel().attr(PLAYER_ATTRIBUTE_KEY).get();
        if (player != null) {
            server.getGameManager().stopSearching(player);
        }
        server.getLogger().log(INFO, "Player disconnected from " + ipAddress(ctx.channel()));
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, String request) {
        boolean close = false;
        Player player = ctx.channel().attr(PLAYER_ATTRIBUTE_KEY).get();
        if (request.toUpperCase().startsWith("P")) {
            String[] parts = request.split("\\|");
            String playerName = parts[1];
            String password = parts[2];
            try {
                player = server.getPlayerManager().getByName(playerName);
                if (player != null) {
                    if (player.checkPassword(password)) {
                        ctx.channel().attr(PLAYER_ATTRIBUTE_KEY).set(player);
                        ctx.writeAndFlush("L|" + player.getName() + "|" + (int) Math.round(player.getMMR()) + "\n");
                        server.getLogger().info("Successful login to " + player.getName() + " from " + ipAddress(ctx.channel()));
                    } else {
                        ctx.writeAndFlush("F\n");
                        server.getLogger().info("Unsuccessful login to " + player.getName() + " from " + ipAddress(ctx.channel()));
                    }
                } else {
                    player = new Player(server.getPlayerManager(), server.getDatabaseConnection(), playerName, password);
                    ctx.channel().attr(PLAYER_ATTRIBUTE_KEY).set(player);
                    ctx.writeAndFlush("L|" + player.getName() + "|" + (int) Math.round(player.getMMR()) + "\n");
                    server.getLogger().info("Successful sign up as " + player.getName() + " from " + ipAddress(ctx.channel()));
                }
            } catch (SQLException exception) {
                server.getLogger().log(SEVERE, "Failed to retrieve player", exception);
                ctx.writeAndFlush("F\n");
            }
        } else if (request.toUpperCase().startsWith("S")) {
            server.getGameManager().search(player);
            server.getLogger().info(player.getName() + " started searching for ranked games (" + player.getMMR() + "mmr)");
        } else if (request.toUpperCase().startsWith("J")) {
            server.getGameManager().getGame(player).sendMessageToOtherPlayer(player, request.trim().toUpperCase());
        } else if (request.toUpperCase().startsWith("E")) {
            String[] parts = request.split("\\|");
            int score = Integer.parseInt(parts[1]);
            ActiveGame game = server.getGameManager().getGame(player);
            if (game != null) {
                if (game.getPlayer1().getUUID().toString().equalsIgnoreCase(player.getUUID().toString())) {
                    game.setPlayer1Score(score);
                } else {
                    game.setPlayer2Score(score);
                }
            }
        } else if (request.toUpperCase().startsWith("B")) {
            server.getGameManager().getGame(player).sendMessageToOtherPlayer(player, request.trim().toUpperCase());
        } else if (request.toUpperCase().startsWith("T")) {
            String[] parts = request.split("\\|");
            server.getLogger().info("Chat message from " + parts[1] + ": " + parts[2]);
            channels.writeAndFlush(request + "\n");
        } else if (request.toUpperCase().startsWith("Q")) {
            close = true;
        }
        if (close) {
            ctx.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public Channel getChannel(Player player) {
        for (Channel channel : channels) {
            if (channel.attr(PLAYER_ATTRIBUTE_KEY).get().getUUID().toString().equalsIgnoreCase(player.getUUID().toString())) {
                return channel;
            }
        }
        return null;
    }

    public ChannelGroup getChannels() {
        return channels;
    }

    private String ipAddress(Channel channel) {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().toString();
    }

}
