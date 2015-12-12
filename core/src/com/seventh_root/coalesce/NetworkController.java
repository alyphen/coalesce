package com.seventh_root.coalesce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String message;
        try {
            while ((message = in.readLine()) != null) {
                if (message.trim().equalsIgnoreCase("J")) {
                    getPlayer().jump();
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void tick() {

    }

}
