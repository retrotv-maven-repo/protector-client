package dev.retrotv.protector.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LogManager.getLogger(NettyClientHandler.class);

    private final String[] msgArr;
    private int idx = 0;

    public NettyClientHandler(String[] msgArr) {
        this.msgArr = msgArr;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        log.info("채널 등록");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.info("채널 연결 종료");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("채널 접속 완료");
        sendMsg(ctx, idx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        int n = buf.readableBytes();

        log.info("msg size: {}", n);
        if (n > 0) {
            byte[] b = new byte[n];
            buf.readBytes(b);

            // 수신메시지 출력
            String receiveMsg = new String(b);
            log.info("수신된 메시지 > {}", receiveMsg);

            // 보낼 메시지가 없으면 연결 종료
            if (msgArr.length == ++idx) {
                log.debug("msgArr size: {}, idx: {}", msgArr.length, idx);
                sendMsg(ctx, "!CLOSE");
            } else {
                log.debug("msgArr size: {}, idx: {}", msgArr.length, idx);
                sendMsg(ctx, idx);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("메시지 수신 완료");
    }

    private void sendMsg(ChannelHandlerContext ctx, String msg) {
        ByteBuf messageBuffer = Unpooled.buffer();
        messageBuffer.writeBytes(msg.getBytes());
        ctx.writeAndFlush(messageBuffer);
        log.info("발송 메시지 > {}", msg);
    }

    private void sendMsg(ChannelHandlerContext ctx, int idx) {
        ByteBuf messageBuffer = Unpooled.buffer();
        messageBuffer.writeBytes(msgArr[idx].getBytes());
        ctx.writeAndFlush(messageBuffer);
        log.info("발송 메시지 > {}", msgArr[idx]);
    }
}
