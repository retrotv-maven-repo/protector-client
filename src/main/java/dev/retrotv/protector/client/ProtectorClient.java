package dev.retrotv.protector.client;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ProtectorClient {
    private static final Logger log = LogManager.getLogger(ProtectorClient.class);

    private static final String SUCCESS = "!SUCCESS";
    private static final String CLOSE = "!CLOSE";

    // 하위 문자열 상수들은 공백이 포함되어 있음!
    private static final String DATA_START = "!DATASTART ";
    private static final String DATA_END = " !DATAEND";
    private static final String ENCRYPT = "!ENCRYPT ";
    private static final String DECRYPT = "!DECRYPT ";
    private static final String PASSWORD = "!PASSWORD ";
    private static final String PASSWORD_MATCH = "!PASSWORDMATCH ";

    private final String address;
    private final int port;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ProtectorClient(@NonNull String address, @NonNull int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("port는 1~65535 값만 허용됩니다.");
        }

        this.address = address;
        this.port = port;
    }

    public void run() throws IOException {
        log.debug("Server address: {}", address);
        log.debug("Server port: {}", port);

        socket = new Socket(address, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    public String encrypt(String data) throws IOException {
        out.write(ENCRYPT + dataPadding(data));
        out.flush();

        if (SUCCESS.equals(in.readLine())) {
            return in.readLine();
        } else {
            throw new IOException();
        }
    }

    public String decrypt(String encryptedData) throws IOException {
        out.write(DECRYPT + dataPadding(encryptedData));
        out.flush();

        if (SUCCESS.equals(in.readLine())) {
            return in.readLine();
        } else {
            throw new IOException();
        }
    }

    public String passwordEncrypt(String password) throws IOException {
        out.write(PASSWORD + dataPadding(password));
        out.flush();

        if (SUCCESS.equals(in.readLine())) {
            return in.readLine();
        } else {
            throw new IOException();
        }
    }

    public boolean passwordMatch(String password, String encryptedPassword) throws IOException {
        out.write(PASSWORD_MATCH + dataPadding(password) + " " + dataPadding(encryptedPassword));
        out.flush();

        if (SUCCESS.equals(in.readLine())) {
            return "true".equals(in.readLine());
        } else {
            throw new IOException();
        }
    }

    public void close() throws IOException {
        out.write(CLOSE);
        out.flush();

        out.close();
        in.close();
        socket.close();
    }

    private String dataPadding(String data) {
        return DATA_START + data + DATA_END;
    }
}
