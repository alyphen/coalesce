package com.seventh_root.coalesce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class NetworkManager {

    private Coalesce game;

    private Array<NetworkController> controllers;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    private String statusMessage;

    private boolean shutdown;
    private boolean connected;

    public NetworkManager(Coalesce game) {
        this.game = game;
        connected = false;
        controllers = new Array<NetworkController>();
        Net.Protocol protocol = Net.Protocol.TCP;
        //String host = "seventh-root.com";
        String host = "localhost";
        int port = 30512;
        SocketHints hints = new SocketHints();
        hints.connectTimeout = 15000;
        hints.socketTimeout = 15000;
        statusMessage = "Connecting...";
        try {
            socket = Gdx.net.newClientSocket(protocol, host, port, hints);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            statusMessage = "Connected.";
            connected = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String message;
                        while (!shutdown && socket.isConnected() && (message = in.readLine()) != null) {
                            if (message.toUpperCase().startsWith("J")) {
                                final float pos = Float.parseFloat(message.split("\\|", -1)[1]);
                                for (final NetworkController controller : controllers) {
                                    Gdx.app.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            controller.jump(pos);
                                        }
                                    });
                                }
                            } else if (message.toUpperCase().startsWith("E")) {
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        Coalesce game = NetworkManager.this.game;
                                        game.setScreen(game.getLoginScreen());
                                    }
                                });
                            } else if (message.toUpperCase().startsWith("L")) {
                                final String playerName = message.split("\\|", -1)[1];
                                final int mmr = Integer.parseInt(message.split("\\|", -1)[2]);
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        Coalesce game = NetworkManager.this.game;
                                        game.getMenuScreen().setPlayerName(playerName);
                                        game.getMenuScreen().setMMR(mmr);
                                        game.setScreen(game.getMenuScreen());
                                    }
                                });
                            } else if (message.toUpperCase().startsWith("F")) {
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        statusMessage = "Login unsuccessful";
                                    }
                                });
                            } else if (message.toUpperCase().startsWith("G")) {
                                controllers.clear();
                                String[] offsetStrings = message.substring(2).split("\\|", -1);
                                final Array<Integer> offsets = new Array<Integer>();
                                for (String offsetString : offsetStrings) {
                                    offsets.add(Integer.parseInt(offsetString));
                                }
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        Coalesce game = NetworkManager.this.game;
                                        game.getLevelScreen().loadLevel(offsets);
                                        game.setScreen(game.getLevelScreen());
                                    }
                                });
                            } else if (message.toUpperCase().startsWith("M")) {
                                final int newMMR = Integer.parseInt(message.split("\\|", -1)[1]);
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        Coalesce game = NetworkManager.this.game;
                                        game.getMenuScreen().setMMR(newMMR);
                                        game.setScreen(game.getMenuScreen());
                                        game.getMenuScreen().enableRankedButton();
                                    }
                                });
                            } else if (message.toUpperCase().startsWith("B")) {
                                String[] parts = message.split("\\|", -1);
                                if (parts[1].equalsIgnoreCase("S")) {
                                    final float pos = Float.parseFloat(message.split("\\|", -1)[2]);
                                    final int radius = Integer.parseInt(message.split("\\|", -1)[3]);
                                    for (final NetworkController controller : controllers) {
                                        Gdx.app.postRunnable(new Runnable() {
                                            @Override
                                            public void run() {
                                                controller.startBoost(pos, radius);
                                            }
                                        });
                                    }
                                } else {
                                    final float pos = Float.parseFloat(message.split("\\|", -1)[2]);
                                    final int radius = Integer.parseInt(message.split("\\|", -1)[3]);
                                    for (final NetworkController controller : controllers) {
                                        Gdx.app.postRunnable(new Runnable() {
                                            @Override
                                            public void run() {
                                                controller.stopBoost(pos, radius);
                                            }
                                        });
                                    }
                                }
                            } else if (message.toUpperCase().startsWith("T")) {
                                Coalesce game = NetworkManager.this.game;
                                String[] parts = message.split("\\|", -1);
                                game.getMenuScreen().appendChat(parts[1], parts[2]);
                            }
                        }
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                statusMessage = "Disconnected.";
                                connected = false;
                            }
                        });
                    } catch (final IOException exception) {
                        exception.printStackTrace();
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                statusMessage = exception.getMessage();
                                connected = false;
                            }
                        });
                    }
                }
            }).start();
        } catch (GdxRuntimeException exception) {
            statusMessage = exception.getMessage();
            connected = false;
        }
    }

    public void addController(NetworkController controller) {
        controllers.add(controller);
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.write(message + "\n");
            out.flush();
        }
    }

    public void dispose() {
        shutdown = true;
        if (in != null) {
            try {
                in.close();
            } catch (IOException exception) {
                Gdx.app.log("ERROR", "Failed to close input stream from socket", exception);
            }
        }
        if (out != null) {
            out.close();
        }
        if (socket != null) {
            socket.dispose();
        }
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public boolean isConnected() {
        return connected;
    }

}
