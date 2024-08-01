package name.heavycarbon.protobuf_trial.test.common;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import name.heavycarbon.protobuf_trial.protos.*;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class UtilsForProtobufClientToServerMsgs {

    public static void printAsJson(@NotNull MessageOrBuilder mob) throws InvalidProtocolBufferException {
        JsonFormat.Printer printer = JsonFormat.printer();
        System.out.println("In JSON:");
        System.out.println(printer.print(mob));
    }

    private static void verifyClientToServerMessage(@NotNull ClientToServer c2s) {
        if (c2s.getCommonStuff().getSeqNum() <= 0) {
            // The default value for the seqNum
            // protobuf may actually map an unsigned 32-bit value above 0x8000000
            // to a negative Java int; this problem is checked here also
            throw new IllegalArgumentException("seqNum must be greater than 0 but is " + c2s.getCommonStuff().getSeqNum());
        }
        // All the sub-messages declared in the .proto file are set.
        // Even if they are "default" and thus do not appear on the wire, they are recreated on de-serialization.
        assert c2s.getCommonStuff() != null;
        assert c2s.getPayloadType() != null;
        assert c2s.getLoginFollowup() != null;
        assert c2s.getLoginChallengeSolved() != null;
        switch (c2s.getPayloadType()) {
            case UNRECOGNIZED:
                // actually -1; the protobuf parser gave up
                throw new IllegalArgumentException("Payload type has value " + c2s.getPayloadTypeValue() + ", which is unlisted");
            case UNKNOWN:
                // the default, 0
                throw new IllegalArgumentException("Payload type has default value " + c2s.getPayloadTypeValue() + ", which should not be observed");
            case LOGIN_CHALLENGE_SOLVED:
                if (!c2s.hasLoginChallengeSolved()) {
                    throw new IllegalArgumentException("Payload type is LOGIN_CHALLENGE_SOLVED but the sub-message is not there");
                } else {
                    // there is nothing to test here really
                    final LoginChallengeSolved subMsg = c2s.getLoginChallengeSolved();
                    final long a = subMsg.getA();
                    final long b = subMsg.getB();
                }
                break;
            case LOGIN_FOLLOWUP:
                if (!c2s.hasLoginFollowup()) {
                    throw new IllegalArgumentException("Payload type is LOGIN_FOLLOWUP but the sub-message is not there");
                } else {
                    final LoginFollowup subMsg = c2s.getLoginFollowup();
                    final String username = subMsg.getUsername();
                    if (username.isEmpty()) {
                        throw new IllegalArgumentException("Payload type is LOGIN_FOLLOWUP but there is no valid username");
                    }
                }
                break;
            case LOGIN_REQUEST:
                // there is no payload/sub-msg
                break;
            case LOGIN_FINISH:
                // there is no payload/sub-msg
                break;
            default:
                throw new IllegalArgumentException("Unrecognized payload type " + c2s.getPayloadType());
        }
        // The default value for the UUID is the "nil" UUID containing only 0.
        // It should not be encountered!
        if (UtilsForProtobufUuid.isNilUuid(c2s.getCommonStuff().getClientExchangeUuid())) {
            throw new IllegalArgumentException("Client Exchange UUID is nil");
        }
        if (UtilsForProtobufUuid.isNilUuid(c2s.getCommonStuff().getServerExchangeUuid())) {
            throw new IllegalArgumentException("Server Exchange UUID is nil");
        }
    }

    private static @NotNull ClientToServer.Builder buildCclientToServerBuilder(@NotNull CommonData commonData) {
        return ClientToServer.newBuilder().setCommonStuff(commonData.toProtobufCommonStuff());
    }

    public static @NotNull ClientToServer deserialize(final byte[] pbBytes) throws InvalidProtocolBufferException {
        final ClientToServer c2sBack = ClientToServer.parseFrom(ByteBuffer.wrap(pbBytes));
        verifyClientToServerMessage(c2sBack);
        return c2sBack;
    }

    public static @NotNull ClientToServer buildLoginFollowup(@NotNull String username, @NotNull CommonData commonData) {
        final ClientToServer.Builder builder = buildCclientToServerBuilder(commonData);
        final var subMsg = LoginFollowup.newBuilder().setUsername(username).build();
        return builder
                .setPayloadType(ClientToServer.PayloadType.LOGIN_FOLLOWUP)
                .setLoginFollowup(subMsg)
                .build();
    }

    public static @NotNull ClientToServer buildLoginRequest(@NotNull CommonData commonData) {
        final ClientToServer.Builder builder = buildCclientToServerBuilder(commonData);
        return builder
                .setPayloadType(ClientToServer.PayloadType.LOGIN_REQUEST)
                .build();
    }

    public static @NotNull ClientToServer buildLoginFinish(@NotNull CommonData commonData) {
        final ClientToServer.Builder builder = buildCclientToServerBuilder(commonData);
        return builder
                .setPayloadType(ClientToServer.PayloadType.LOGIN_FINISH)
                .build();
    }

    public static @NotNull ClientToServer buildLoginChallengeSolved(long a, long b, @NotNull CommonData commonData) {
        final ClientToServer.Builder builder = buildCclientToServerBuilder(commonData);
        final var subMsg = LoginChallengeSolved.newBuilder().setA(a).setB(b).build();
        return builder
                .setPayloadType(ClientToServer.PayloadType.LOGIN_CHALLENGE_SOLVED)
                .setLoginChallengeSolved(subMsg)
                .build();
    }

}

