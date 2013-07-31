package at.irian.ankor.netty.server;

import at.irian.ankor.netty.protocol.JsonProtocolDecoder;
import at.irian.ankor.netty.protocol.JsonProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 *
 */
public class SocketServer {

    private int port;

    public SocketServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        // Configure the server.
        EventLoopGroup acceptEventLoop = acceptEventLoop();
        EventLoopGroup workerEventLoop = workerEventLoop();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(acceptEventLoop, workerEventLoop)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer());

            Channel ch = b.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            acceptEventLoop.shutdownGracefully();
            workerEventLoop.shutdownGracefully();
        }
    }

    /**
     * <p>Every time a new channel is created this object will initialize it.</p>
     * @return an object that takes care of initializing HTTP channels
     */
    protected ChannelInitializer<SocketChannel> channelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast("frameDecoder", new LineBasedFrameDecoder(80));
                p.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                p.addLast("jsonDecoder", new JsonProtocolDecoder());

                p.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
                p.addLast("jsonEncoder", new JsonProtocolEncoder());

                p.addLast("handler", new SocketChannelHandler());
            }
        };
    }

    /**
     * <p>Creates the event loop that we'll use to accept incoming connections. By default,
     * a multi-threaded implementation which is used for NIO selectors will be returned. This
     * effectively implements the messaging loop that other parts of Ankor implement manually.</p>
     * @return event loop that we'll use to accept incoming connections
     */
    protected EventLoopGroup acceptEventLoop() {
        return new NioEventLoopGroup();
    }

    /**
     * <p>Creates the event loop that we'll use to process incoming connections. By default,
     * a multi-threaded implementation which is used for NIO selectors will be returned. This
     * effectively implements the messaging loop that other parts of Ankor implement manually.</p>
     * @return event loop that we'll use to accept incoming connections
     */
    protected EventLoopGroup workerEventLoop() {
        return new NioEventLoopGroup();
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new SocketServer(port).run();
    }


}
