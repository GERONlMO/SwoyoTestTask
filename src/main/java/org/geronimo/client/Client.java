package org.geronimo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.geronimo.handler.ClientHandler;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ClientHandler());

            Channel channel = bootstrap.bind(0).sync().channel();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                if ("exit".equalsIgnoreCase(input)) break;
                channel.writeAndFlush(new DatagramPacket(
                        Unpooled.copiedBuffer(input.getBytes()),
                        new InetSocketAddress("localhost", 8080)
                ));
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}