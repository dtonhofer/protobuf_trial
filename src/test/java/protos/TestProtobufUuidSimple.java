package protos;

import com.google.protobuf.InvalidProtocolBufferException;
import common.UtilsForProtobufUuid;
import name.heavycarbon.protobuf_trial.protos.UuidSimple;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

// ---
// Test the "UuidSimple" protobuf message, which encapsulates two "unsigned 64 bit longs" to
// form a single UUID. This is a better idea than using an array of byte.
// ---

class TestProtobufUuidSimple {

    private final static int rounds = 100;

    @Test
    void testProtobufUuidSimple() throws InvalidProtocolBufferException {
        for (int round = 0; round < rounds; round++) {
            // select random UUID
            final var javaUuid = UUID.randomUUID();
            System.out.println("Round " + round + ": " + javaUuid);
            // serialize UUID to byte array that would be seen on the "protobuf wire"
            final byte[] pbBytes = UtilsForProtobufUuid.javaUuidToProtobufUuidSimple(javaUuid).toByteArray();
            System.out.println("   UUID serialized via protobuf: " + UtilsForProtobufUuid.bytesToString(pbBytes));
            // deserialize the byte array back into a protobuf message, then into a Java UUID
            final UUID javaUuidRestored;
            {
                final UuidSimple pbUuidBack = UuidSimple.parseFrom(ByteBuffer.wrap(pbBytes));
                javaUuidRestored = UtilsForProtobufUuid.protobufUuidSimpleToJavaUuid(pbUuidBack);
            }
            System.out.println("   UUID deserialized via protobuf: " + javaUuidRestored);
            assertThat(javaUuid).isEqualTo(javaUuidRestored);
        }
    }

    @Test
    void testProtobufUuidSimpleDefault() throws InvalidProtocolBufferException {
        final UuidSimple pbUuid = UuidSimple.newBuilder().getDefaultInstanceForType();
        final byte[] pbBytes = pbUuid.toByteArray();
        System.out.println("Default protobuf UUID serialized via protobuf: " + UtilsForProtobufUuid.bytesToString(pbBytes));
        final UUID javaUuid = UtilsForProtobufUuid.protobufUuidSimpleToJavaUuid(pbUuid);
        System.out.println("UUID derived from default protobuf UUID: " + javaUuid);
        // This is the "nil" UUID (it's not really valid)
        assertThat(javaUuid.getLeastSignificantBits()).isEqualTo(0L);
        assertThat(javaUuid.getMostSignificantBits()).isEqualTo(0L);
    }

}