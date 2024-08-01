package common;

import lombok.Getter;
import name.heavycarbon.protobuf_trial.protos.CommonStuff;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static common.UtilsForProtobufUuid.isNilUuid;

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
        if (clientExchangeUUID != null && isNilUuid(clientExchangeUUID)) {
            throw new IllegalArgumentException("clientExchangeUUID must be non-nil");
        }
        if (serverExchangeUUID != null && isNilUuid(serverExchangeUUID)) {
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
}
