package dev.retrotv.protector.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class NettyClient {
    private static final Logger log = LogManager.getLogger(NettyClient.class);

    private final Bootstrap bs = new Bootstrap();
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(1);
    private final String address;
    private final int port;
    private final String[] msgArr;

    public NettyClient(@NonNull String address, int port, String[] msgArr) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("port는 1~65535 값만 허용됩니다.");
        }

        this.address = address;
        this.port = port;
        this.msgArr = msgArr;
    }

    public void run() throws InterruptedException {
        log.debug("Netty address: {}", address);
        log.debug("Netty port: {}", port);
        log.debug("Message size: {}", msgArr.length);

        bs.group(workerGroup)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.SO_KEEPALIVE, true)
          .handler(new ChannelInitializer<SocketChannel>() {

              @Override
              protected void initChannel(SocketChannel ch) {
                  ch.pipeline().addLast("clientHandler", new NettyClientHandler(msgArr));
              }
          });

        log.debug("Bootstrap 생성");

        try {
            ChannelFuture f = bs.connect(new InetSocketAddress(address, port)).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
