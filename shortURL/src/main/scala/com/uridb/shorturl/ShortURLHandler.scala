package com.uridb.shorturl

import com.typesafe.scalalogging.LazyLogging
import com.uridb.shorturl.model.URLInfo
import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.HttpHeaderNames._
import io.netty.handler.codec.http.HttpMethod._
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http._
import io.netty.util.CharsetUtil

class ShortURLHandler extends SimpleChannelInboundHandler[FullHttpRequest] with LazyLogging {


  override def messageReceived(ctx: ChannelHandlerContext, req: FullHttpRequest): Unit = {
    if (!req.decoderResult().isSuccess) {
      sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST))
      return ()
    }

    //    val cookieString = req.headers().get(HttpHeaderNames.COOKIE).toString
    //    val cookies:java.util.Set[Cookie] = ServerCookieDecoder.decode(cookieString)
    //    val decoderQuery = new QueryStringDecoder(req.uri())
    //    val uriAttributes:java.util.Map[String, java.util.List[String]] = decoderQuery.parameters()

    val requestUri = req.uri()
    requestUri match {
      case "/" if (req.method() == GET) =>
        val res = new DefaultFullHttpResponse(HTTP_1_1, MOVED_PERMANENTLY)
        res.headers().set(LOCATION, Config.returnHost + "index.html")
        sendHttpResponse(ctx, req, res)
      case _ if (req.method() == GET && requestUri.startsWith("/") && ShortURL.isValid(requestUri.substring(1))) =>
        //decode
        val num = ShortURL.decode(req.uri().substring(1))
        //query database
        ComponentRegistry.urlInfoService.findByUrlId(num) match {
          case Some(urlInfo) =>
            val res = new DefaultFullHttpResponse(HTTP_1_1, MOVED_PERMANENTLY)
            res.headers().set(LOCATION, urlInfo.url)
            sendHttpResponse(ctx, req, res)
          case None =>
            val res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND)
            sendHttpResponse(ctx, req, res)
        }
      case "/" if (req.method() == POST) =>
        val requestedURL = req.content().toString(CharsetUtil.UTF_8)
        if (URLUtil.isValidURL(requestedURL)) {
          val savedUrlInfo = ComponentRegistry.urlInfoService.save(URLInfo(0, requestedURL))
          savedUrlInfo match {
            case Some(urlInfo) =>
              val shortURL = ShortURL.encode(urlInfo.urlId)
              val content = Unpooled.copiedBuffer((Config.returnHost + shortURL).getBytes)
              val res = new DefaultFullHttpResponse(HTTP_1_1, OK, content)
              res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8")
              sendHttpResponse(ctx, req, res)
            case None =>
              val res = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR)
              res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8")
              sendHttpResponse(ctx, req, res)
          }
        } else {
          val res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST)
          sendHttpResponse(ctx, req, res)
        }

      case _ =>
        val content = Unpooled.copiedBuffer("<html><header><title></title></header><body>Unimplemented</body></html>".getBytes)
        val res = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR, content)
        res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8")
        //HttpHeaderUtil.setContentLength(res, content.readableBytes())
        sendHttpResponse(ctx, req, res)
    }

  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.error(cause.getMessage, cause)
    ctx.close()
  }

  def sendHttpResponse(ctx: ChannelHandlerContext, req: FullHttpRequest, res: FullHttpResponse): Unit = {
    //    if (res.status().code() != 200 && res.status().code() != 301) {
    //      val buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8)
    //      res.content().writeBytes(buf)
    //      buf.release()
    //      HttpHeaderUtil.setContentLength(res, res.content().readableBytes())
    //    }
    if (res.content() != null)
      HttpHeaderUtil.setContentLength(res, res.content().readableBytes())
    else
      HttpHeaderUtil.setContentLength(res, 0)
    // Send the response and close the connection if necessary.
    val f = ctx.channel().writeAndFlush(res)
    if (!HttpHeaderUtil.isKeepAlive(req) || res.status().code() != 200) {
      f.addListener(ChannelFutureListener.CLOSE)
    }
  }

}
