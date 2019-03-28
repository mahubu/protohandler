package protocol.handler.codec.protobuf.compact;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import protocol.message.Messages;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Multi-purpose 'Google Protocol Buffers' encoder.<br/>
 * Based on {@link io.netty.handler.codec.protobuf.ProtobufEncoder}.
 */
@ChannelHandler.Sharable
public class ProtobufEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) {
        byte[] payload;
        if (msg instanceof MessageLite) {
            payload = ((MessageLite) msg).toByteArray();
        } else if (msg instanceof MessageLite.Builder) {
            payload = ((MessageLite.Builder) msg).build().toByteArray();
        } else {
            throw new IllegalStateException("Invalid protobuf message: " + msg);
        }

        final short uid = Messages.get(msg.getClass());
        final ByteBuf buffer = Unpooled.copiedBuffer(Shorts.toByteArray(uid), payload);
        out.add(buffer);
    }
}
