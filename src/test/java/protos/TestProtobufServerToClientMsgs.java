package protos;

import com.google.protobuf.InvalidProtocolBufferException;
import common.UtilsForProtobufUuid;
import name.heavycarbon.protobuf_trial.protos.ClientToServer;
import name.heavycarbon.protobuf_trial.protos.ServerToClient;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static common.UtilsForProtobufClientToServerMsgs.buildLoginFollowup;
import static common.UtilsForProtobufClientToServerMsgs.printAsJson;
import static common.UtilsForProtobufServerToClientMsgs.*;
import static protos.Common.generateCommonData;

class TestProtobufServerToClientMsgs {

    private static void print(String subMsg, byte[] pbBytes) {
        System.out.println("ServerToClient with " + subMsg + " serialized via protobuf: " + UtilsForProtobufUuid.bytesToString(pbBytes));
    }

    private static @NotNull ServerToClient serializeAndDeserialize(@NotNull String desc, final @NotNull ServerToClient in) throws InvalidProtocolBufferException {
        final byte[] pbBytes = in.toByteArray();
        print(desc, pbBytes);
        final ServerToClient s2cBack = deserialize(pbBytes);
        printAsJson(s2cBack);
        return s2cBack;
    }

    @Test
    void testGenericFailure() throws InvalidProtocolBufferException {
        ClientToServer cause = buildLoginFollowup("John Doe", generateCommonData());
        final ServerToClient s2c = buildGenericFailure("This didn't goo too well", cause, generateCommonData());
        assertThat(s2c.getPayloadType()).isEqualTo(ServerToClient.PayloadType.GENERIC_FAILURE);
        final ServerToClient s2cBack = serializeAndDeserialize("GenericFailure", s2c);
        assertThat(s2c).isEqualTo(s2cBack);
    }

    @Test
    void testLoginGenericFailure() throws InvalidProtocolBufferException {
        ClientToServer cause = buildLoginFollowup("John Doe", generateCommonData());
        final ServerToClient s2c = buildLoginGenericFailure("This didn't goo too well", cause, generateCommonData());
        assertThat(s2c.getPayloadType()).isEqualTo(ServerToClient.PayloadType.LOGIN_GENERIC_FAILURE);
        final ServerToClient s2cBack = serializeAndDeserialize("LoginGenericFailure", s2c);
        assertThat(s2c).isEqualTo(s2cBack);
    }

    @Test
    void testLoginChallenge() throws InvalidProtocolBufferException {
        final int product = 11 * 13;
        final ServerToClient s2c = buildLoginChallenge(product, generateCommonData());
        assertThat(s2c.getPayloadType()).isEqualTo(ServerToClient.PayloadType.LOGIN_CHALLENGE);
        final ServerToClient s2cBack = serializeAndDeserialize("LoginChallenge", s2c);
        assertThat(s2c).isEqualTo(s2cBack);
    }

    @Test
    void testLoginRequestAnswerOk() throws InvalidProtocolBufferException {
        final ServerToClient s2c = buildLoginRequestAnswerOk(generateCommonData());
        assertThat(s2c.getPayloadType()).isEqualTo(ServerToClient.PayloadType.LOGIN_REQUEST_ANSWER_OK);
        final ServerToClient s2cBack = serializeAndDeserialize("LoginRequestAnswerOk", s2c);
        assertThat(s2c).isEqualTo(s2cBack);
    }

    @Test
    void testLoginRequestAnswerFailure() throws InvalidProtocolBufferException {
        final ServerToClient s2c = buildLoginRequestAnswerFailure("Some problem occurred", generateCommonData());
        assertThat(s2c.getPayloadType()).isEqualTo(ServerToClient.PayloadType.LOGIN_REQUEST_ANSWER_FAILURE);
        final ServerToClient s2cBack = serializeAndDeserialize("LoginRequestAnswerFailure", s2c);
        assertThat(s2c).isEqualTo(s2cBack);
    }

    @Test
    void testLoginFollowupAnswerOk() throws InvalidProtocolBufferException {
        final ServerToClient s2c = buildLoginFollowupAnswerOk(UUID.randomUUID(), "player name", generateCommonData());
        assertThat(s2c.getPayloadType()).isEqualTo(ServerToClient.PayloadType.LOGIN_FOLLOWUP_ANSWER_OK);
        final ServerToClient s2cBack = serializeAndDeserialize("LoginRequestAnswerOk", s2c);
        assertThat(s2c).isEqualTo(s2cBack);
    }

    @Test
    void testLoginFollowupAnswerFailure() throws InvalidProtocolBufferException {
        final ServerToClient s2c = buildLoginFollowupAnswerFailure(generateCommonData());
        assertThat(s2c.getPayloadType()).isEqualTo(ServerToClient.PayloadType.LOGIN_FOLLOWUP_ANSWER_FAILURE);
        final ServerToClient s2cBack = serializeAndDeserialize("LoginFollowupAnswerFailure", s2c);
        assertThat(s2c).isEqualTo(s2cBack);
    }

    @Test
    void testLoginChallengeSolvedAnswerOk() throws InvalidProtocolBufferException {
        final ServerToClient s2c = buildLoginChallengeSolvedAnswerOk(generateCommonData());
        assertThat(s2c.getPayloadType()).isEqualTo(ServerToClient.PayloadType.LOGIN_CHALLENGE_SOLVED_ANSWER_OK);
        final ServerToClient s2cBack = serializeAndDeserialize("LoginChallengeSolvedAnswerOk", s2c);
        assertThat(s2c).isEqualTo(s2cBack);
    }

    @Test
    void testLoginChallengeSolvedAnswerFailure() throws InvalidProtocolBufferException {
        final ServerToClient s2c = buildLoginChallengeSolvedAnswerFailure(generateCommonData());
        assertThat(s2c.getPayloadType()).isEqualTo(ServerToClient.PayloadType.LOGIN_CHALLENGE_SOLVED_ANSWER_FAILURE);
        final ServerToClient s2cBack = serializeAndDeserialize("LoginChallengeSolvedAnswerFailure", s2c);
        assertThat(s2c).isEqualTo(s2cBack);
    }

}
