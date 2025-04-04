package org.geronimo.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.geronimo.handler.ServerHandler;
import org.geronimo.command.CommandProcessor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.geronimo.model.Topic;

public class Server {
    public static void main(String[] args) throws InterruptedException {
        Map<String, Topic> topics = new ConcurrentHashMap<>();
        CommandProcessor processor = new CommandProcessor(topics);
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioDatagramChannel.class) // Используем UDP
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) {
                            ch.pipeline().addLast(new ServerHandler(processor));
                        }
                    });

            ChannelFuture future = bootstrap.bind(8080).sync();
            future.channel().closeFuture().await();
        } finally {
            group.shutdownGracefully();
        }
    }
}