package dev.retrotv.protector.client.netty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SocketTest {

    @Test
    @DisplayName("Socket 연결하고 데이터 보내기")
    void test_socket_connect_send_message() throws InterruptedException {
        String[] message = { "Hello! ", "This ", "is ", "Message!" };
        new NettyClient("127.0.0.1", 8888, message).run();
    }
}
