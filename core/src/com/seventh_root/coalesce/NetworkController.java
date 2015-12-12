package com.seventh_root.coalesce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

import java.io.IOException;

public class NetworkController extends Controller {

    public NetworkController(Player player) {
        super(player);
        Net.Protocol protocol = Net.Protocol.TCP;
        String host = "seventh-root.com";
        int port = 30512;
        SocketHints hints = new SocketHints();
        hints.connectTimeout = 15000;
        hints.socketTimeout = 15000;
        Socket socket = Gdx.net.newClientSocket(protocol, host, port, hints);
        while (socket.isConnected()) {
            try {
                int b = socket.getInputStream().read();

            } catch (IOException exception) {
                Gdx.app.log("SEVERE", exception.getMessage(), exception);
            }
        }
    }

    @Override
    public void tick() {

    }

}
