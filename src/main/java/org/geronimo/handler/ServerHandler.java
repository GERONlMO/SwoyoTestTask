package org.geronimo.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import java.nio.charset.StandardCharsets;
import org.geronimo.command.CommandProcessor;

public class ServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private final CommandProcessor processor;

    public ServerHandler(CommandProcessor processor) {
        this.processor = processor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        String command = packet.content().toString(StandardCharsets.UTF_8);
        String response = processor.process(ctx.channel(), command);
        ctx.writeAndFlush(new DatagramPacket(
                io.netty.buffer.Unpooled.copiedBuffer(response.getBytes()),
                packet.sender()
        ));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}