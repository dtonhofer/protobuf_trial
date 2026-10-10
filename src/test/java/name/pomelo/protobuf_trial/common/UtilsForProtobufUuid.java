package name.pomelo.protobuf_trial.common;

import com.google.protobuf.ByteString;
import lombok.val;
import name.pomelo.protobuf_trial.protos.Uuid_Bytes;
import name.pomelo.protobuf_trial.protos.Uuid_Longs;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

// ---
// For Java UUID see:
//
// https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/UUID.html
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

    public enum Endianness {
        BIG_ENDIAN,
        LITTLE_ENDIAN;

        public ByteOrder toByteOrder() {
            if (this == Endianness.BIG_ENDIAN) {
                return ByteOrder.BIG_ENDIAN;
            } else {
                return ByteOrder.LITTLE_ENDIAN;
            }
        }
    }

    // ---

    public static boolean isNilUuid(@NotNull Uuid_Longs uuid) {
        return uuid.getLow() == 0L && uuid.getHigh() == 0L;
    }

    private static final ByteString NIL_BYTES = ByteString.copyFrom(new byte[16]);

    public static boolean isNilUuid(@NotNull Uuid_Bytes uuid) {
        return NIL_BYTES.equals(uuid.getData());
    }

    public static boolean isNilUuid(@NotNull UUID uuid) {
        return uuid.getLeastSignificantBits() == 0L && uuid.getMostSignificantBits() == 0L;
    }

    // ---

    public static Long readLong_UsingByteArray(@NotNull ByteArrayInputStream istream, @NotNull Endianness endianness) throws IOException {
        val barr = new byte[8];
        val count = istream.read(barr);
        if (count < 0) {
            // nothing could be read at all; this happens in case of "default" (nil) UUID
            System.err.println("Could not read enough data from ByteArrayInputStream: read " + count + " bytes");
        }
        else if (count != 8) {
            throw new IllegalArgumentException("Could not read enough data from ByteArrayInputStream: read " + count + " bytes");
        }
        return ByteBuffer.wrap(barr).order(endianness.toByteOrder()).getLong();
    }


    private static void writeLong_UsingByteArray(long l, @NotNull ByteArrayOutputStream baos, @NotNull Endianness endianness) throws IOException {
        val bytes = ByteBuffer.allocate(8).order(endianness.toByteOrder()).putLong(l).array();
        baos.write(bytes);
    }

    public static @NotNull Uuid_Longs javaUuid_to_Protobuf_Uuid_Longs(@NotNull UUID javaUuid) {
        return Uuid_Longs.newBuilder()
                .setHigh(javaUuid.getMostSignificantBits())
                .setLow(javaUuid.getLeastSignificantBits())
                .build();
    }

    public static @NotNull Uuid_Bytes javaUuid_to_Protobuf_Uuid_Bytes(@NotNull UUID javaUuid, @NotNull Endianness endianness) throws IOException {
        final var byteArray = uuidToArray(javaUuid, endianness);
        final var byteString = ByteString.copyFrom(byteArray);
        return Uuid_Bytes.newBuilder().setData(byteString).build();
    }

    public static @NotNull UUID protobuf_Uuid_Bytes_to_JavaUuid(@NotNull Uuid_Bytes pbUuid, @NotNull Endianness endianness) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(pbUuid.getData().toByteArray());
        final long low = readLong_UsingByteArray(bais, endianness);
        final long high = readLong_UsingByteArray(bais, endianness);
        return new UUID(high, low);
    }

    public static @NotNull UUID protobuf_Uuid_Longs_to_JavaUuid(@NotNull Uuid_Longs pbUuid) {
        return new UUID(pbUuid.getHigh(), pbUuid.getLow());
    }

    public static byte @NotNull [] uuidToArray(@NotNull UUID uuid, @NotNull Endianness endianness) throws IOException {
        final var baos = new ByteArrayOutputStream(16);
        writeLong_UsingByteArray(uuid.getLeastSignificantBits(), baos, endianness); // least significant 8 byte first
        writeLong_UsingByteArray(uuid.getMostSignificantBits(), baos, endianness); // most significant 8 byte first
        return baos.toByteArray();
    }

    public static @NotNull String bytesToString(byte @NotNull [] byteArray) {
        StringBuilder buf = new StringBuilder();
        if (byteArray.length == 0) {
            buf.append("[empty]");
        } else {
            buf.append(String.format("%02x", byteArray[0]));
            for (int i = 1; i < byteArray.length; i++) {
                buf.append(".");
                buf.append(String.format("%02x", byteArray[i]));
            }
        }
        return buf.toString();
    }
}
