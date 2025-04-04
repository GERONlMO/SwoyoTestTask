package org.geronimo.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private boolean isCreatingVote = false;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        String response = packet.content().toString(StandardCharsets.UTF_8);
        System.out.println("Сервер: " + response);

        if (response.startsWith("ОЖИДАНИЕ:")) {
            isCreatingVote = true;
            System.out.print("> ");
            String input = new Scanner(System.in).nextLine();
            ctx.channel().writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(input.getBytes()),
                    packet.sender()
            ));
        } else if (isCreatingVote) {
            System.out.print("> ");
            String input = new Scanner(System.in).nextLine();
            ctx.channel().writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(input.getBytes()),
                    packet.sender()
            ));
        }
    }
}