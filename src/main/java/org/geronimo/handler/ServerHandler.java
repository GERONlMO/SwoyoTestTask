package org.geronimo.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import java.nio.charset.StandardCharsets;
import org.geronimo.command.ClientCommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private final ClientCommandProcessor processor;

    public ServerHandler(ClientCommandProcessor processor) {
        this.processor = processor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        try {
            String command = packet.content().toString(StandardCharsets.UTF_8);
            logger.debug("Получен пакет от {}: {}", packet.sender(), command);

            String response = processor.processCommand(ctx.channel(), command);

            logger.debug("Отправка ответа {}: {}", packet.sender(), response);
            ctx.writeAndFlush(new DatagramPacket(
                    io.netty.buffer.Unpooled.copiedBuffer(response.getBytes()),
                    packet.sender()
            ));
        } catch (Exception e) {
            logger.error("Ошибка обработки пакета", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Сетевая ошибка", cause);
        ctx.close();
    }
}