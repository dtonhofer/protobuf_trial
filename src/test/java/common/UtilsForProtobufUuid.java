package common;

import com.google.protobuf.ByteString;
import name.heavycarbon.protobuf_trial.protos.Uuid;
import name.heavycarbon.protobuf_trial.protos.UuidSimple;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

// ---
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

public abstract class UtilsForProtobufUuid {

    public static boolean isNilUuid(@NotNull UuidSimple uuid) {
        return uuid.getLow() == 0L && uuid.getHigh() == 0L;
    }

    public static boolean isNilUuid(@NotNull UUID uuid) {
        return uuid.getLeastSignificantBits() == 0L && uuid.getMostSignificantBits() == 0L;
    }

    private static long readLongBigEndianly(@NotNull ByteArrayInputStream bais) {
        long shift = 0;
        for (int i = 0; i < 8; i++) {
            int n = bais.read();
            if (n < 0) {
                System.err.println("No more data from ByteArrayInputStream at index " + i);
                return shift;
            } else {
                shift = shift | (long) (n) << i * 8;
            }
        }
        return shift;
    }

    public static @NotNull UuidSimple javaUuidToProtobufUuidSimple(@NotNull UUID javaUuid) {
        return UuidSimple.newBuilder()
                .setHigh(javaUuid.getMostSignificantBits())
                .setLow(javaUuid.getLeastSignificantBits())
                .build();
    }

    public static @NotNull Uuid javaUuidToProtobufUuid(@NotNull UUID javaUuid) {
        final var byteArray = uuidToBigEndianArray(javaUuid);
        final var byteString = ByteString.copyFrom(byteArray);
        return Uuid.newBuilder().setData(byteString).build();
    }

    public static @NotNull UUID protobufUuidToJavaUuid(@NotNull Uuid pbUuid) {
        ByteArrayInputStream bais = new ByteArrayInputStream(pbUuid.getData().toByteArray());
        final long low = readLongBigEndianly(bais);
        final long high = readLongBigEndianly(bais);
        return new UUID(high, low);
    }

    public static @NotNull UUID protobufUuidSimpleToJavaUuid(@NotNull UuidSimple pbUuid) {
        final long low = pbUuid.getLow();
        final long high = pbUuid.getHigh();
        return new UUID(high, low);
    }

    private static void writeLongBigEndianly(final long value, @NotNull ByteArrayOutputStream baos) {
        long shift = value;
        for (int i = 0; i < 8; i++) {
            baos.write((int) (shift & 0xFF));
            shift >>= 8;
        }
    }

    // UUID: [F][E][D][C][B][A][9][8],[7][6][5][4][3][2][1][0]
    // bytes: [0][1][2][3][4][5][6][7],[8][9][A][B][C][D][E][F] <-- highest index

    public static byte @NotNull [] uuidToBigEndianArray(@NotNull UUID uuid) {
        final var baos = new ByteArrayOutputStream(16);
        writeLongBigEndianly(uuid.getLeastSignificantBits(), baos); // least significant 8 byte first
        writeLongBigEndianly(uuid.getMostSignificantBits(), baos); // most significant 8 byte first
        return baos.toByteArray();
    }

    public static @NotNull String bytesToString(byte @NotNull [] byteArray) {
        StringBuilder buf = new StringBuilder();
        if (byteArray.length == 0) {
            buf.append("[empty]");
        } else {
            for (int i = 0; i < byteArray.length; i++) {
                if (i > 0) {
                    buf.append(".");
                }
                buf.append(String.format("%02x", byteArray[i]));
            }
        }
        return buf.toString();
    }
}
