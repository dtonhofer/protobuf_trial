package name.pomelo.protobuf_trial.test.testing;

import com.google.protobuf.InvalidProtocolBufferException;
import name.pomelo.protobuf_trial.test.common.UtilsForProtobufUuid;
import name.pomelo.protobuf_trial.protos.Uuid;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

// ---
// Test the "Uuid" protobuf message, which encapsulates a byte array to form a single UUID.
// It turns out that idea needs a lot of extra support.
//
// For Java UUID see:
//
// https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/UUID.html
//
// Wikipedia on UUID:
//
// https://en.wikipedia.org/wiki/Universally_unique_identifier
//
// For Protobuf Java API see:
//
// https://protobuf.dev/reference/java/api-docs/com/google/protobuf/CodedOutputStream
// https://protobuf.dev/reference/java/api-docs/com/google/protobuf/ByteString.html
// ---

class TestProtobufUuid {

    // initial Apollo format of UUID (if I do not mix up MSB and LSB?)
    //
    // MSB -> NN.NN.NN.NN..NN.NN.NN.FA..RR.RR.TL.TL..TH.TH.TH.TH <- LSB
    // TH = time high
    // TL = time low
    // RR = reserved
    // FA = family   0....... The legacy Apollo NCS UUID (only values 0-13 used in FA)
    //               10...... OSF DCE UUID
    //               110..... Microsoft COM/DCOM UUID
    //               111..... Reserved for future definition
    // NN = node

    private final static int rounds = 100;

    @Test
    void testProtobufUuid() throws InvalidProtocolBufferException {
        for (int round = 0; round < rounds; round++) {
            // select random UUID
            final var javaUuid = UUID.randomUUID();
            System.out.println("Round " + round + ": " + javaUuid);
            {
                byte[] bytes = UtilsForProtobufUuid.uuidToBigEndianArray(javaUuid);
                System.out.println("   UUID as byte array (big endian): " + UtilsForProtobufUuid.bytesToString(bytes));
            }
            // serialize UUID to byte array that would be seen on the "protobuf wire"
            final byte[] pbBytes = UtilsForProtobufUuid.javaUuidToProtobufUuid(javaUuid).toByteArray();
            System.out.println("   UUID serialized via protobuf: " + UtilsForProtobufUuid.bytesToString(pbBytes));
            // deserialize the byte array back into a protobuf message, then into a Java UUID
            final UUID javaUuidRestored;
            {
                final Uuid pbUuidBack = Uuid.parseFrom(ByteBuffer.wrap(pbBytes));
                javaUuidRestored = UtilsForProtobufUuid.protobufUuidToJavaUuid(pbUuidBack);
            }
            System.out.println("   UUID deserialized via protobuf: " + javaUuidRestored);
            assertThat(javaUuid).isEqualTo(javaUuidRestored);
        }
    }

    @Test
    void testProtobufUuidDefault() {
        final Uuid pbUuid = Uuid.newBuilder().getDefaultInstanceForType();
        final byte[] pbBytes = pbUuid.toByteArray();
        System.out.println("Default protobuf UUID serialized via protobuf: " + UtilsForProtobufUuid.bytesToString(pbBytes));
        final UUID javaUuid = UtilsForProtobufUuid.protobufUuidToJavaUuid(pbUuid);
        System.out.println("UUID derived from default protobuf UUID: " + javaUuid);
        // This is the "nil" UUID (it's not really valid)
        assertThat(javaUuid.getLeastSignificantBits()).isEqualTo(0L);
        assertThat(javaUuid.getMostSignificantBits()).isEqualTo(0L);
    }

}