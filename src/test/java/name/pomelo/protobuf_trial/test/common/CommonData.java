package name.pomelo.protobuf_trial.test.common;

import lombok.Getter;
import name.pomelo.protobuf_trial.protos.CommonStuff;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.UUID;

// ---
// A simple "record" class carrying a packet's sequence number and UUIDs,
// used in passing data around.
// ---

@Getter
public class CommonData {

    private final int seqNum;
    private final @Nullable UUID clientExchangeUUID;
    private final @Nullable UUID serverExchangeUUID;

    public CommonData(int seqNum, @Nullable UUID clientExchangeUUID, @Nullable UUID serverExchangeUUID) {
        if (seqNum <= 0) {
            throw new IllegalArgumentException("seqNum must be greater than 0 but is " + seqNum);
        }
        if (clientExchangeUUID != null && UtilsForProtobufUuid.isNilUuid(clientExchangeUUID)) {
            throw new IllegalArgumentException("clientExchangeUUID must be non-nil");
        }
        if (serverExchangeUUID != null && UtilsForProtobufUuid.isNilUuid(serverExchangeUUID)) {
            throw new IllegalArgumentException("serverExchangeUUID must be non-nil");
        }
        this.clientExchangeUUID = clientExchangeUUID;
        this.serverExchangeUUID = serverExchangeUUID;
        this.seqNum = seqNum;
    }

    public @NotNull CommonStuff toProtobufCommonStuff() {
        final var builder = CommonStuff.newBuilder().setSeqNum(getSeqNum());
        // We assume that the "builder" instance stays the same during the chain
        // i.e. we can always use builder "n" instead of having to use builder
        // "n", "n+1", "n+2" etc...
        if (clientExchangeUUID != null) {
            final var pbUuid = UtilsForProtobufUuid.javaUuidToProtobufUuidSimple(clientExchangeUUID);
            builder.setClientExchangeUuid(pbUuid);
        }
        if (serverExchangeUUID != null) {
            final var pbUuid = UtilsForProtobufUuid.javaUuidToProtobufUuidSimple(serverExchangeUUID);
            builder.setServerExchangeUuid(pbUuid);
        }
        return builder.build();
    }

    // ---
    // Generate random "CommonData" instance, with random UUIDs and
    // a random seqNum from [1,999].
    // ---

    private final static Random rand = new Random();

    public static CommonData generateRandomCommonData() {
        final UUID clientExchangeUUID = UUID.randomUUID();
        final UUID serverExchangeUUID = UUID.randomUUID();
        final int seqNum = rand.nextInt(1,1000); // must be > 0
        return new CommonData(seqNum, clientExchangeUUID, serverExchangeUUID);
    }
}
