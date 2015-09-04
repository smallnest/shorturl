package com.uridb.shorturl

import java.util.UUID
import java.util.concurrent.Executors

import com.typesafe.scalalogging.LazyLogging
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.{PooledByteBufAllocator, ByteBufAllocator}
import io.netty.channel.socket.SocketChannel
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.channel.epoll.{EpollServerSocketChannel, EpollEventLoopGroup, Epoll}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.cors.{CorsConfig, CorsHandler}
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler
import io.netty.handler.codec.http.{HttpObjectAggregator, HttpServerCodec}


object ShortURLServer extends App with LazyLogging{
  val bossGroup = if (Epoll.isAvailable()) new EpollEventLoopGroup() else new NioEventLoopGroup()
  val workerGroup = if (Epoll.isAvailable()) new EpollEventLoopGroup() else new NioEventLoopGroup()

  try {
    val bootstrap = new ServerBootstrap()
    bootstrap.group(bossGroup, workerGroup)
      .option[Integer](ChannelOption.SO_BACKLOG, 1024)
      .option[java.lang.Boolean](ChannelOption.SO_REUSEADDR, true)
      //.option[java.lang.Boolean](EpollChannelOption.SO_REUSEPORT, true)
      .option[Integer](ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE)
      .childOption[ByteBufAllocator](ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
      .childOption[java.lang.Boolean](ChannelOption.SO_REUSEADDR, true)
      .childOption[Integer](ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE)

    if (Epoll.isAvailable())
      bootstrap.channel(classOf[EpollServerSocketChannel])
    else
      bootstrap.channel(classOf[NioServerSocketChannel])

    bootstrap.childHandler(new ChannelInitializer[SocketChannel]() {
      override def initChannel(socketChannel: SocketChannel): Unit = {
        val corsConfig = CorsConfig.withAnyOrigin().allowNullOrigin().build()
        val pipeline = socketChannel.pipeline()
        pipeline.addLast(new HttpServerCodec())
        pipeline.addLast(new HttpObjectAggregator(65536))
        if (Config.enableCORS)
          pipeline.addLast(new CorsHandler(corsConfig));
        pipeline.addLast(new ShortURLHandler())
      }
    })

    val channel = bootstrap.bind(Config.port).sync().channel()
    logger.info("started")
    channel.closeFuture().sync()

  }
  finally {
    bossGroup.shutdownGracefully()
    workerGroup.shutdownGracefully()
  }
}
