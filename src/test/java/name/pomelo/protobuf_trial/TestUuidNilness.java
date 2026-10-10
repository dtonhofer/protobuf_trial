package name.pomelo.protobuf_trial;

import com.google.protobuf.ByteString;
import lombok.val;
import name.pomelo.protobuf_trial.common.UtilsForProtobufUuid;
import name.pomelo.protobuf_trial.protos.Uuid_Bytes;
import name.pomelo.protobuf_trial.protos.Uuid_Longs;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUuidNilness {

    private final static ByteString ZERO_BYTE_STRING = ByteString.copyFrom(new byte[16]);

    @Test
    void nil_protobuf_UUID_based_on_longs_must_be_ACCEPTED_by_isNilUuid() {
        val nilUuid = Uuid_Longs.newBuilder().setLow(0L).setHigh(0L).build();
        assertTrue(UtilsForProtobufUuid.isNilUuid(nilUuid));
    }

    @Test
    void valid_protobuf_UUID_based_on_longs_must_be_REJECTED_by_isNilUuid() {
        val trueJavaUuid = UUID.randomUUID(); // Random type 4 UUID
        val trueUuid = Uuid_Longs.newBuilder()
                .setLow(trueJavaUuid.getLeastSignificantBits())
                .setHigh(trueJavaUuid.getMostSignificantBits())
                .build();
        assertFalse(UtilsForProtobufUuid.isNilUuid(trueUuid));
    }

    // ---

    @Test
    void nil_protobuf_UUID_based_on_bytes_must_be_ACCEPTED_by_isNilUuid() {
        val nilUuid = Uuid_Bytes.newBuilder().setData(ZERO_BYTE_STRING).build();
        assertTrue(UtilsForProtobufUuid.isNilUuid(nilUuid));
    }

    @Test
    void valid_protobuf_UUID_based_on_bytes_must_be_REJECTED_by_isNilUuid() {
        val trueJavaUuid = UUID.randomUUID(); // Random type 4 UUID
        // Building a Kotlin ByteArray, which is actually byte[]
        val byteArray = ByteBuffer.allocate(16)
                .putLong(trueJavaUuid.getMostSignificantBits())
                .putLong(trueJavaUuid.getLeastSignificantBits())
                .array();
        val byteString = ByteString.copyFrom(byteArray);
        val trueUuid = Uuid_Bytes.newBuilder().setData(byteString).build();
        assertFalse(UtilsForProtobufUuid.isNilUuid(trueUuid));
    }

    // ---

    @Test
    void nil_Java_UUID_must_be_ACCEPTED_by_isNilUuid() {
        val nilUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        assertTrue(UtilsForProtobufUuid.isNilUuid(nilUuid));
    }

    @Test
    void valid_Java_UUID_must_be_REJECTED_by_isNilUuid() {
        val nonNilUuid = UUID.randomUUID(); // Random type 4 UUID
        assertFalse(UtilsForProtobufUuid.isNilUuid(nonNilUuid));
    }

}
