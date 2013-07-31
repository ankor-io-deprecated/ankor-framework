package at.irian.ankor.netty.protocol;

import at.irian.ankor.messaging.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * <p>Whenever someone wants to send messages, they'll go through a chain of encoders (as configured in
 * the channel pipeline). This particular encoder makes sure that Message objects will be encoded in a
 * JSON format.</p>
 *
 * <p>Only use this encoder in combination with {@link io.netty.handler.codec.string.StringEncoder}.</p>
 *
 * @author Bernhard Huemer
 */
public class JsonProtocolEncoder extends MessageToMessageEncoder<Message> {

    private final ObjectMapper mapper;

    public JsonProtocolEncoder() {
        mapper = JsonUtils.configureJsonMapper();
    }

    /**
     * <p>Converts the given message into JSON and passes it on to the next encoder
     * ({@link io.netty.handler.codec.string.StringEncoder}.</p>
     * @param ctx current context
     * @param msg the message that we're encoding
     * @param out collects results
     * @throws Exception if an I/O error occurs
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, MessageList<Object> out) throws Exception {
        out.add(mapper.writeValueAsString(msg));
    }

}
