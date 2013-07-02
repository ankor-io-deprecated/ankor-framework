package at.irian.ankor.netty.server;

import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.netty.channel.ChannelBasedMessageSender;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 *
 */
public class SocketChannelHandler extends SimpleChannelInboundHandler<Message> {

    private MessageSender messageSender;

    /**
     * <p>The channel that we are using for this communcation was registered to the central event loop (i.e.
     * it will start receiving messages from now on). We'll use this callback to establish who we're talking
     * to from now on. Each channel will have its own instance of this channel handler.</p>
     * @param ctx information about the context
     * @throws Exception if an unknown error occurs
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        messageSender = new ChannelBasedMessageSender(ctx);
    }

    /**
     * <p>Callback method that will be called by Netty if a message has been received on this channel.</p>
     * @param ctx information about the context in which we are receiving this (from whom, etc.)
     * @param msg the message that we are receiving
     * @throws Exception if an unknown error occurs
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) throws Exception {
        // TODO: Register listeners somehow with this channel handler when initializing the channel
        // and then propagate this message to them. Also forward the message sender somehow.
    }

}
