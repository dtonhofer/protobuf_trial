package name.heavycarbon.protobuf_trial.test.testing;

import com.google.protobuf.InvalidProtocolBufferException;
import name.heavycarbon.protobuf_trial.protos.ClientToServer;
import name.heavycarbon.protobuf_trial.test.common.CommonData;
import name.heavycarbon.protobuf_trial.test.common.UtilsForProtobufUuid;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static name.heavycarbon.protobuf_trial.test.common.UtilsForProtobufClientToServerMsgs.*;

class TestProtobufClientToServerMsgs {

    private static void print(String subMsg, byte[] pbBytes) {
        System.out.println("ClientToServer with " + subMsg + " serialized via protobuf: " + UtilsForProtobufUuid.bytesToString(pbBytes));
    }

    @Test
    void testLoginFinish() throws InvalidProtocolBufferException {
        final ClientToServer c2s = buildLoginFinish(CommonData.generateRandomCommonData());
        assertThat(c2s.getPayloadType()).isEqualTo(ClientToServer.PayloadType.LOGIN_FINISH);
        final ClientToServer c2sBack;
        {
            final byte[] pbBytes = c2s.toByteArray();
            print("LoginFinish", pbBytes);
            c2sBack = deserialize(pbBytes);
        }
        printAsJson(c2sBack);
        assertThat(c2s).isEqualTo(c2sBack);
    }

    @Test
    void testLoginRequest() throws InvalidProtocolBufferException {
        final ClientToServer c2s = buildLoginRequest(CommonData.generateRandomCommonData());
        assertThat(c2s.getPayloadType()).isEqualTo(ClientToServer.PayloadType.LOGIN_REQUEST);
        final ClientToServer c2sBack;
        {
            final byte[] pbBytes = c2s.toByteArray();
            print("LoginRequest", pbBytes);
            c2sBack = deserialize(pbBytes);
        }
        printAsJson(c2sBack);
        assertThat(c2s).isEqualTo(c2sBack);

    }

    @Test
    void testLoginFollowup() throws InvalidProtocolBufferException {
        final ClientToServer c2s = buildLoginFollowup("john doe", CommonData.generateRandomCommonData());
        assertThat(c2s.getPayloadType()).isEqualTo(ClientToServer.PayloadType.LOGIN_FOLLOWUP);
        final ClientToServer c2sBack;
        {
            final byte[] pbBytes = c2s.toByteArray();
            print("LoginFollowup", pbBytes);
            c2sBack = deserialize(pbBytes);
        }
        printAsJson(c2sBack);
        assertThat(c2s).isEqualTo(c2sBack);
    }

    @Test
    void testLoginChallengeSolved() throws InvalidProtocolBufferException {
        final long a = 11;
        final long b = 23;
        final ClientToServer c2s = buildLoginChallengeSolved(a, b, CommonData.generateRandomCommonData());
        assertThat(c2s.getPayloadType()).isEqualTo(ClientToServer.PayloadType.LOGIN_CHALLENGE_SOLVED);
        final ClientToServer c2sBack;
        {
            final byte[] pbBytes = c2s.toByteArray();
            print("LoginChallengeSolved", pbBytes);
            c2sBack = deserialize(pbBytes);
        }
        printAsJson(c2sBack);
        assertThat(c2s).isEqualTo(c2sBack);
    }

    @Test
    void testMessagesAreDifferent() {
        final ClientToServer c2s1 = buildLoginRequest(CommonData.generateRandomCommonData());
        final ClientToServer c2s2 = buildLoginRequest(CommonData.generateRandomCommonData());
        assertThat(c2s1).isNotEqualTo(c2s2);
    }

    @Test
    void testMessagesAreEqual() {
        CommonData cd = CommonData.generateRandomCommonData();
        final ClientToServer c2s1 = buildLoginRequest(cd);
        final ClientToServer c2s2 = buildLoginRequest(cd);
        assertThat(c2s1).isEqualTo(c2s2);
    }


}
