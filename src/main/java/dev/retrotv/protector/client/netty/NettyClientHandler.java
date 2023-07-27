package dev.retrotv.protector.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LogManager.getLogger(NettyClientHandler.class);

    private final String msg;
    private int idx = 0;

    public NettyClientHandler(String msg) {
        this.msg = msg;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("채널 접속 완료");
        sendMsg(ctx, msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        int n = buf.readableBytes();

        byte[] b = new byte[n];
        buf.readBytes(b);

        // 수신메시지 출력
        String receiveMsg = new String(b, StandardCharsets.UTF_8);
    }

    private void sendMsg(ChannelHandlerContext ctx, String msg) {
        ByteBuf messageBuffer = Unpooled.buffer();
        messageBuffer.writeBytes(msg.getBytes());
        ctx.writeAndFlush(messageBuffer);
        log.info("발송 메시지 > {}", msg);
    }
}
