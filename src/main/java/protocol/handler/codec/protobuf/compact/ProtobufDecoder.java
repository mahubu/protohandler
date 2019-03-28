package protocol.handler.codec.protobuf.compact;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import protocol.message.Messages;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Multi-purpose 'Google Protocol Buffers' decoder. Use an internal cache for saving/retrieving parser associated with a given message.<br/>
 * Based on {@link io.netty.handler.codec.protobuf.ProtobufDecoder}.
 */
@ChannelHandler.Sharable
public class ProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final Cache<Short, Parser<? extends MessageLiteOrBuilder>> CACHE = CacheBuilder.newBuilder().initialCapacity(16).expireAfterAccess(1, TimeUnit.HOURS).build();
    private static final String GET_PARSER_METHOD = "parser";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws IOException {
        final short uid = msg.readShort();

        final byte[] array;
        final int offset;
        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        } else {
            array = ByteBufUtil.getBytes(msg, msg.readerIndex(), msg.readableBytes(), false);
            offset = 0;
        }

        final Parser<? extends MessageLiteOrBuilder> parser = getParser(uid);
        out.add(parser.parseFrom(array, offset, array.length));
    }

    private Parser<? extends MessageLiteOrBuilder> getParser(final short uid) {
        Parser<? extends MessageLiteOrBuilder> parser = CACHE.getIfPresent(uid);
        if (parser == null) {
            try {
                final Class<?> clazz = Messages.get(uid);
                final MethodHandles.Lookup lookup = MethodHandles.lookup();
                final MethodHandle handle = lookup.findStatic(clazz, GET_PARSER_METHOD, MethodType.methodType(Parser.class));
                parser = (Parser<? extends MessageLiteOrBuilder>) handle.invokeExact();
            } catch (final Throwable e) {
                throw new IllegalStateException("Invalid protobuf class uid: " + uid, e);
            }
            CACHE.put(uid, parser);
        }
        return parser;
    }
}
