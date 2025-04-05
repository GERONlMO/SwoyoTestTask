package org.geronimo.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.geronimo.command.ServerCommandProcessor;
import org.geronimo.handler.ServerHandler;
import org.geronimo.command.ClientCommandProcessor;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import org.geronimo.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("Запуск сервера...");

        Map<String, Topic> topics = new ConcurrentHashMap<>();
        ClientCommandProcessor clientProcessor = new ClientCommandProcessor(topics);
        ServerCommandProcessor serverProcessor = new ServerCommandProcessor(topics);

        new Thread(() -> {
            logger.debug("Поток обработки консольных команд запущен");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    System.out.print("[Сервер] > ");
                    String cmd = scanner.nextLine();
                    logger.debug("Получена серверная команда: {}", cmd);
                    String response = serverProcessor.processCommand(cmd);
                    logger.info("Результат выполнения команды: {}", response);
                    System.out.println(response);
                } catch (Exception e) {
                    logger.error("Ошибка в потоке обработки команд", e);
                }
            }
        }).start();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) {
                            ch.pipeline().addLast(new ServerHandler(clientProcessor));
                        }
                    });

            ChannelFuture future = bootstrap.bind(8080).sync();
            logger.info("Сервер запущен на порту 8080");
            future.channel().closeFuture().await();
        } catch (Exception e) {
            logger.error("Критическая ошибка сервера", e);
        } finally {
            group.shutdownGracefully();
            logger.info("Сервер остановлен");
        }
    }
}