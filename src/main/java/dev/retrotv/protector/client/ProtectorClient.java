package dev.retrotv.protector.client;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ProtectorClient {
    private static final Logger log = LogManager.getLogger(ProtectorClient.class);

    private final String address;
    private final int port;
    private final String msg;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ProtectorClient(@NonNull String address, @NonNull int port, String msg) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("port는 1~65535 값만 허용됩니다.");
        }

        this.address = address;
        this.port = port;
        this.msg = msg;
    }

    public void run() throws IOException {
        log.debug("Server address: {}", address);
        log.debug("Server port: {}", port);
        log.trace("Send msg: {}", msg);

        socket = new Socket(address, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }
}
