package org.geronimo.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private volatile boolean isAwaitingInput = false;
    private final Scanner scanner = new Scanner(System.in);
    private final ExecutorService inputExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        String response = packet.content().toString(StandardCharsets.UTF_8);
        System.out.println("Сервер: " + response);

        if (response.startsWith("ОЖИДАНИЕ:") || response.startsWith("Доступные варианты:")) {
            isAwaitingInput = true;
            inputExecutor.submit(() -> sendUserInput(ctx, packet.sender()));
        }
    }

    private void sendUserInput(ChannelHandlerContext ctx, InetSocketAddress sender) {
        if (!isAwaitingInput) return;

        System.out.print("> ");
        String input = scanner.nextLine();
        ctx.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer(input.getBytes(StandardCharsets.UTF_8)),
                sender
        ));
        isAwaitingInput = false;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        inputExecutor.shutdown();
    }
}