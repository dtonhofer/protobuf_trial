package name.heavycarbon.protobuf_trial.test.common;

import com.google.protobuf.InvalidProtocolBufferException;
import name.heavycarbon.protobuf_trial.protos.*;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UtilsForProtobufServerToClientMsgs {

    private static @NotNull ServerToClient.Builder buildServerToClientBuilder(@NotNull CommonData commonData) {
        return ServerToClient.newBuilder().setCommonStuff(commonData.toProtobufCommonStuff());
    }

    private static void verifyServerToClientMessage(@NotNull ServerToClient s2c) {
        if (s2c.getCommonStuff().getSeqNum() <= 0) {
            // The default value for the seqNum
            // protobuf may actually map an unsigned 32-bit value above 0x8000000
            // to a negative Java int; this problem is checked here also
            throw new IllegalArgumentException("seqNum must be greater than 0 but is " + s2c.getCommonStuff().getSeqNum());
        }
        // All the sub-messages declared in the .proto file are set.
        // Even if they are "default" and thus do not appear on the wire, they are recreated on de-serialization.
        assert s2c.getCommonStuff() != null;
        assert s2c.getPayloadType() != null;
        assert s2c.getLoginChallenge() != null;
        assert s2c.getLoginGenericFailure() != null;
        assert s2c.getGenericFailure() != null;
        switch (s2c.getPayloadType()) {
            case UNRECOGNIZED:
                // actually -1; the protobuf parser gave up
                throw new IllegalArgumentException("Payload type has value " + s2c.getPayloadTypeValue() + ", which is unlisted");
            case UNKNOWN:
                // the default, 0
                throw new IllegalArgumentException("Payload type has default value " + s2c.getPayloadTypeValue() + ", which should not be observed");
            case GENERIC_FAILURE:
                if (!s2c.hasGenericFailure()) {
                    throw new IllegalArgumentException("Payload type is GENERIC_FAILURE but the sub-message is not there");
                } else {
                    // there is nothing to test here really, but let's see whether this compiles
                    final GenericFailure subMsg = s2c.getGenericFailure();
                    final String msg = subMsg.getMsg();
                    final ClientToServer cause = subMsg.getCause();
                }
                break;
            case LOGIN_GENERIC_FAILURE:
                if (!s2c.hasLoginGenericFailure()) {
                    throw new IllegalArgumentException("Payload type is LOGIN_GENERIC_FAILURE but the sub-message is not there");
                } else {
                    // there is nothing to test here really, but let's see whether this compiles
                    final LoginGenericFailure subMsg = s2c.getLoginGenericFailure();
                    final String msg = subMsg.getMsg();
                    final ClientToServer cause = subMsg.getCause();
                }
                break;
            case LOGIN_CHALLENGE:
                if (!s2c.hasLoginChallenge()) {
                    throw new IllegalArgumentException("Payload type is LOGIN_CHALLENGE but the sub-message is not there");
                } else {
                    // there is nothing to test here really, but let's see whether this compiles
                    final LoginChallenge subMsg = s2c.getLoginChallenge();
                    final long product = subMsg.getProduct();
                }
                break;
            case LOGIN_REQUEST_ANSWER_FAILURE:
                if (!s2c.hasLoginRequestAnswerFailure()) {
                    throw new IllegalArgumentException("Payload type is LOGIN_REQUEST_ANSWER_FAILURE but the sub-message is not there");
                } else {
                    // there is nothing to test here really, but let's see whether this compiles
                    final LoginRequestAnswerFailure subMsg = s2c.getLoginRequestAnswerFailure();
                    final String msg = subMsg.getMsg();
                }
                break;
            case LOGIN_FOLLOWUP_ANSWER_OK:
                if (!s2c.hasLoginFollowupAnswerOk()) {
                    throw new IllegalArgumentException("Payload type is LOGIN_FOLLOWUP_ANSWER_OK but the sub-message is not there");
                } else {
                    // there is nothing to test here really, but let's see whether this compiles
                    final LoginFollowupAnswerOk subMsg = s2c.getLoginFollowupAnswerOk();
                    final String playerName = subMsg.getPlayerName();
                    final UUID playerUuid = UtilsForProtobufUuid.protobufUuidSimpleToJavaUuid(subMsg.getPlayerUuid());
                }
                break;
            case LOGIN_REQUEST_ANSWER_OK:
                // there is no payload/sub-msg
                break;
            case LOGIN_FOLLOWUP_ANSWER_FAILURE:
                // there is no payload/sub-msg
                break;
            case LOGIN_CHALLENGE_SOLVED_ANSWER_OK:
                // there is no payload/sub-msg
                break;
            case LOGIN_CHALLENGE_SOLVED_ANSWER_FAILURE:
                // there is no payload/sub-msg
                break;
            default:
                throw new IllegalArgumentException("Unrecognized payload type " + s2c.getPayloadType());
        }
        // The default value for the UUID is the "nil" UUID containing only 0.
        // It should not be encountered!
        if (UtilsForProtobufUuid.isNilUuid(s2c.getCommonStuff().getClientExchangeUuid())) {
            throw new IllegalArgumentException("Client Exchange UUID is nil");
        }
        if (UtilsForProtobufUuid.isNilUuid(s2c.getCommonStuff().getServerExchangeUuid())) {
            throw new IllegalArgumentException("Server Exchange UUID is nil");
        }
    }

    public static @NotNull ServerToClient deserialize(final byte[] pbBytes) throws InvalidProtocolBufferException {
        final ServerToClient s2cBack = ServerToClient.parseFrom(ByteBuffer.wrap(pbBytes));
        verifyServerToClientMessage(s2cBack);
        return s2cBack;
    }

    // This message is used when a failure occurs even outside any exchange

    public static @NotNull ServerToClient buildGenericFailure(@NotNull String msg, @NotNull ClientToServer cause, @NotNull CommonData commonData) {
        final ServerToClient.Builder builder = buildServerToClientBuilder(commonData);
        final var subMsg = GenericFailure.newBuilder().setMsg(msg).setCause(cause).build();
        return builder
                .setPayloadType(ServerToClient.PayloadType.GENERIC_FAILURE)
                .setGenericFailure(subMsg)
                .build();
    }

    // This message is used when a failure occurs in a "Login" exchange
    // Maybe one should make sure that the "cause" contains one of the "Login"
    // Client->Server messages? Otherwise, this would be a "generic failure"

    public static @NotNull ServerToClient buildLoginGenericFailure(@NotNull String msg, @NotNull ClientToServer cause, @NotNull CommonData commonData) {
        final ServerToClient.Builder builder = buildServerToClientBuilder(commonData);
        final var subMsg = LoginGenericFailure.newBuilder().setMsg(msg).setCause(cause).build();
        return builder
                .setPayloadType(ServerToClient.PayloadType.LOGIN_GENERIC_FAILURE)
                .setLoginGenericFailure(subMsg)
                .build();
    }

    public static @NotNull ServerToClient buildLoginChallenge(long product, @NotNull CommonData commonData) {
        final ServerToClient.Builder builder = buildServerToClientBuilder(commonData);
        final var subMsg = LoginChallenge.newBuilder().setProduct(product).build();
        return builder
                .setPayloadType(ServerToClient.PayloadType.LOGIN_CHALLENGE)
                .setLoginChallenge(subMsg)
                .build();
    }

    public static @NotNull ServerToClient buildLoginRequestAnswerOk(@NotNull CommonData commonData) {
        final ServerToClient.Builder builder = buildServerToClientBuilder(commonData);
        return builder
                .setPayloadType(ServerToClient.PayloadType.LOGIN_REQUEST_ANSWER_OK)
                .build();
    }

    public static @NotNull ServerToClient buildLoginRequestAnswerFailure(@NotNull String msg, @NotNull CommonData commonData) {
        final ServerToClient.Builder builder = buildServerToClientBuilder(commonData);
        final var subMsg = LoginRequestAnswerFailure.newBuilder().setMsg(msg).build();
        return builder
                .setPayloadType(ServerToClient.PayloadType.LOGIN_REQUEST_ANSWER_FAILURE)
                .setLoginRequestAnswerFailure(subMsg)
                .build();
    }

    public static @NotNull ServerToClient buildLoginFollowupAnswerOk(@NotNull UUID playerUuid, @NotNull String playerName, @NotNull CommonData commonData) {
        final ServerToClient.Builder builder = buildServerToClientBuilder(commonData);
        final var simplePlayerUuid = UtilsForProtobufUuid.javaUuidToProtobufUuidSimple(playerUuid);
        final var subMsg = LoginFollowupAnswerOk.newBuilder().setPlayerName(playerName).setPlayerUuid(simplePlayerUuid).build();
        return builder
                .setPayloadType(ServerToClient.PayloadType.LOGIN_FOLLOWUP_ANSWER_OK)
                .setLoginFollowupAnswerOk(subMsg)
                .build();
    }

    public static @NotNull ServerToClient buildLoginFollowupAnswerFailure(@NotNull CommonData commonData) {
        final ServerToClient.Builder builder = buildServerToClientBuilder(commonData);
        return builder
                .setPayloadType(ServerToClient.PayloadType.LOGIN_FOLLOWUP_ANSWER_FAILURE)
                .build();
    }

    public static @NotNull ServerToClient buildLoginChallengeSolvedAnswerOk(@NotNull CommonData commonData) {
        final ServerToClient.Builder builder = buildServerToClientBuilder(commonData);
        return builder
                .setPayloadType(ServerToClient.PayloadType.LOGIN_CHALLENGE_SOLVED_ANSWER_OK)
                .build();
    }

    public static @NotNull ServerToClient buildLoginChallengeSolvedAnswerFailure(@NotNull CommonData commonData) {
        final ServerToClient.Builder builder = buildServerToClientBuilder(commonData);
        return builder
                .setPayloadType(ServerToClient.PayloadType.LOGIN_CHALLENGE_SOLVED_ANSWER_FAILURE)
                .build();
    }
}
