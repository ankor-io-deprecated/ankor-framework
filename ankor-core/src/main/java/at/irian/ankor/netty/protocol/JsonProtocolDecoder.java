package at.irian.ankor.netty.protocol;

import at.irian.ankor.messaging.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * <p>Decoder implementation that assumes we're receiving String messages in a JSON format. Any ChannelHandler
 * that follows this decoder in the pipeline will then only have to process deserialized objects already, i.e.
 * ChannelHandlers are completely unaware of the protocol in which messages are being passed from one to
 * another.</p>
 *
 * <p>Only use this decoder in combination with {@link io.netty.handler.codec.string.StringDecoder}.</p>
 *
 * @author Bernhard Huemer
 */
public class JsonProtocolDecoder extends MessageToMessageDecoder<String> {

    private final ObjectMapper mapper;

    public JsonProtocolDecoder() {
        mapper = JsonUtils.configureJsonMapper();
    }

    /**
     * <p>When processing messages in a channel pipeline this method will be called to decode raw String
     * messages, which we assume to contain JSON stuff, into actual Java objects representing those
     * messages.</p>
     *
     * @param ctx current execution context
     * @param msg the raw message
     * @param out collects results
     *
     * @throws Exception if an unknown error occurs
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, MessageList<Object> out) throws Exception {
        out.add(mapper.readValue(msg, Message.class));
    }

}
