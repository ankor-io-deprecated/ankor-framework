package at.irian.ankor.netty.channel;

import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageSender;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 */
public class ChannelBasedMessageSender<T> implements MessageSender {

    private ChannelHandlerContext ctx;

    public ChannelBasedMessageSender(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void sendMessage(Message msg) {
        ctx.write(msg); // Encoders will take care of serializing this
    }

    @Override
    public void flush() {
        // Doesn't make sense with NIO
    }

}
