package dev.retrotv.protector.client.netty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class SocketTest {
    private static final Logger log = LogManager.getLogger(NettyClient.class);

    @Test
    @DisplayName("Socket 연결하고 데이터 보내기")
    void test_socket_connect_send_message() throws IOException {
        try (Socket socket = new Socket("127.0.0.1", 8888)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            // PrintWriter out = new PrintWriter(socket.getOutputStream());

            String outputMessage = "!ENCRYPT 123456789\n";
            out.write(outputMessage);
            out.flush();

            String inputMessage = in.readLine();

            String encryptedData = null;
            if ("!SUCCESS".equals(inputMessage)) {
                encryptedData = in.readLine();
                System.out.println(inputMessage);
            }

            if (encryptedData != null) {
                outputMessage = "!DECRYPT " + encryptedData;
                out.write(outputMessage);
                out.flush();
            }

            inputMessage = in.readLine();
            System.out.println(inputMessage);

            if ("!SUCCESS".equals(inputMessage)) {
                inputMessage = in.readLine();
                System.out.println(inputMessage);
            }

            outputMessage = "!CLOSE";
            out.write(outputMessage);
            out.flush();

            in.close();
            out.close();
        }
    }
}
