package dev.retrotv.protector.client.netty;

import dev.retrotv.protector.client.ProtectorClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SocketTest {
    private static final Logger log = LogManager.getLogger(ProtectorClient.class);

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

    @Test
    @DisplayName("ProtectClient 테스트")
    void test_protect_client() throws Exception {
        ProtectorClient pc = new ProtectorClient("127.0.0.1", 8888);

        pc.run();
        String message = "NewData";
        String encryptedData = pc.encrypt(message);
        String originalMessage = pc.decrypt(encryptedData);
        pc.close();

        assertEquals(message, originalMessage);
    }

    @Test
    @DisplayName("ProtectClient 패스워드 암호화 테스트")
    void test_protect_password_client() throws Exception {
        ProtectorClient pc = new ProtectorClient("127.0.0.1", 8888);

        pc.run();
        String password = "password";
        String encryptedPassword = pc.passwordEncrypt(password);
        boolean result = pc.passwordMatch(password, encryptedPassword);
        pc.close();

        assertTrue(result);
    }
}
